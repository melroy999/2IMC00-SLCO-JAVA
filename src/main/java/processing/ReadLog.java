package processing;

import processing.processors.IProcessor;
import processing.processors.LogOrderCheck;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class ReadLog {
    // The path to the log file.
    private final String targetFolder;

    // The processors.
    private final IProcessor[] processors;

    // Control the warmup grace period.
    private final long warmupGracePeriodDuration;
    private Instant startTime;
    private boolean hasWarmedUp = false;

    // Keep track of the number of lines processed.
    private int linesProcessed = 0;
    private int linesInWarmup = 0;

    public ReadLog(String targetFolder, IProcessor[] processors, long warmupGracePeriodDuration) {
        this.targetFolder = targetFolder;
        this.processors = processors;
        this.warmupGracePeriodDuration = warmupGracePeriodDuration;
    }

    /**
     * Java needs time to optimize code during runtime--hence, include a warm-up check.
     *
     * @param header The header containing the timestamp.
     * @return true if the grace period is over, false otherwise.
     */
    public boolean hasWarmedUp(String header) {
        // Skip calculations if already warmed up.
        if(hasWarmedUp) {
            return true;
        }

        // Parse the header and get the current timestamp.
        String[] headerComponents = header.substring(1).replaceAll("]", "").split("\\[");
        Instant instant = Instant.ofEpochMilli(Long.parseLong(headerComponents[1]));
        if (startTime == null) {
            startTime = instant;
        }

        // Check how much time has passed and set the appropriate flags.
        if (Duration.between(startTime, instant).toMillis() >= warmupGracePeriodDuration) {
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
        String[] components = line.split(" ", 2);
        String header = components[0];
        if(hasWarmedUp(header)) {
            String[] data = components[1].split(" ");
            for(IProcessor processor : processors) {
                processor.process(header, data);
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
        ReadLog operation = new ReadLog(
                "B_2022-03-15T22.48.35.032837800Z",
                new IProcessor[]{
                        new LogOrderCheck()
                },
                0
        );

        try {
            operation.processFiles();
            operation.reportResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
