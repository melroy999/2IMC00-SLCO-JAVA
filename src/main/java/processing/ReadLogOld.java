package processing;

import processing.processors.IProcessor;
import processing.processors.LogOrderCheck;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class ReadLogOld {
    // The path to the log file.
    private final String targetFolder;

    // The processors.
    private final IProcessor[] processors;

    // Control the warmup grace period.
    private final long warmupGracePeriodDuration;
    private long startTime = -1;
    private boolean hasWarmedUp = false;

    // Keep track of the number of lines processed.
    private int linesProcessed = 0;
    private int linesInWarmup = 0;

    // The log messages often repeat--hence, keep a mapping to cache results with.
    private static final Map<String, String[]> MESSAGE_CACHE = new HashMap<>();

    public ReadLogOld(String targetFolder, IProcessor[] processors, long warmupGracePeriodDuration) {
        this.targetFolder = targetFolder;
        this.processors = processors;
        this.warmupGracePeriodDuration = warmupGracePeriodDuration;
    }

    /**
     * Java needs time to optimize code during runtime--hence, include a warm-up check.
     *
     * @param timestamp The timestamp as a string.
     * @return true if the grace period is over, false otherwise.
     */
    public boolean hasWarmedUp(String timestamp) {
        // Get the current timestamp as an instant object.
        long unixMilli = Long.parseLong(timestamp);

        // Skip calculations if already warmed up.
        if(hasWarmedUp) {
            return true;
        }

        if (startTime == -1) {
            startTime = unixMilli;
        }

        // Check how much time has passed and set the appropriate flags.
        if (unixMilli - startTime >= warmupGracePeriodDuration) {
            System.out.printf(
                    "Warmup grace period of %s milliseconds concluded, starting log processing.%n",
                    warmupGracePeriodDuration
            );
            hasWarmedUp = true;
            return true;
        }
        linesInWarmup++;
        return false;
    }

    /**
     * Process the given line by passing on the relevant data to each of the processors.
     *
     * @param line The line to be processed.
     */
    public void processLine(String line) {
        // Split the string in two on the first occurrence of a space character.
        int index = line.indexOf(" ");
        String[] components = new String[] {line.substring(0, index), line.substring(index + 1)};

        // Split the header component on an underscore to find the timestamp and thread name.
        index = components[0].indexOf("_");
        String timestamp = components[0].substring(0, index);
        String thread = components[0].substring(index + 1);

        // Process the line.
        if(hasWarmedUp(timestamp)) {
            if(!MESSAGE_CACHE.containsKey(components[1])) {
                MESSAGE_CACHE.put(components[1], components[1].split(" "));
            }
            String[] data = MESSAGE_CACHE.get(components[1]);
            for(IProcessor processor : processors) {
                processor.process(thread, data);
            }
        }
        linesProcessed++;
    }

    /**
     * Process the target compressed file by reading it line by line.
     *
     * @param i The currently targeted compressed file number.
     */
    public void processCompressedFile(int i) throws IOException {
        File file = Paths.get("logs", targetFolder, "data." + i + ".gz").toFile();
        try (
                GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }
    }

    /**
     * Process the remaining plain and uncompressed file at the target path by reading it line by line.
     */
    public void processPlainFile() throws IOException {
        try (
                FileInputStream inputStream = new FileInputStream(
                        Paths.get("logs", targetFolder, "data.log").toString()
                );
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)
        ) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                processLine(line);
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
    }

    /**
     * Process the log files.
     */
    public void processFiles() throws IOException {
        // Process each of the log files in order--note that counting is started at 1 instead of 0.
        System.out.printf("Processing log files contained within the 'logs/%s' folder.%n", targetFolder);
        int nrOfFiles = Objects.requireNonNull(Paths.get("logs", targetFolder).toFile().list()).length;
        for(int i = 1; i < nrOfFiles; i++) {
            processCompressedFile(i);
        }

        // The last file is uncompressed and should be processed differently.
        processPlainFile();
        System.out.printf(
                "Processed %s lines, with %s lines occurring within the warmup phase.%n",
                linesProcessed, linesInWarmup
        );
    }

    /**
     * Report all the gathered results.
     */
    public void reportResults() {
        // TODO: Create a valid path to store the results in.
        System.out.println("Reporting the results gathered by the processors.");
        String path = "";
        for(IProcessor processor : processors) {
            processor.reportResults(path);
        }
        System.out.printf("Finished the analysis of log files contained within the 'logs/%s' folder.%n", targetFolder);
    }

    public static void main(String[] args) {
        ReadLogOld operation = new ReadLogOld(
                "B_2022-03-17T12.27.13.538460200Z",
                new IProcessor[]{
                        new LogOrderCheck()
                },
                0
        );

        try {
            Instant instant = Instant.now();
            operation.processFiles();
            operation.reportResults();
            System.out.println(Duration.between(instant, Instant.now()).toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
