package processing;

import processing.processors.IProcessor;
import processing.processors.TransitionCountProcessor;
import processing.processors.throughput.ThroughputProcessor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
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
    private long startTime = -1;
    private long logProcessingStartTime = -1;
    private boolean hasWarmedUp = false;

    // Control the data interval system.
    private final long intervalDuration;
    private long intervalStartTime = -1;

    // Keep track of the number of lines processed.
    private int linesProcessed = 0;
    private int linesInWarmup = 0;

    // The log messages often repeat--hence, keep a mapping to cache results with.
    private static final Map<String, String[]> MESSAGE_CACHE = new HashMap<>();

    public ReadLog(
            String targetFolder, IProcessor[] processors, long warmupGracePeriodDuration, long intervalDuration
    ) {
        this.targetFolder = targetFolder;
        this.processors = processors;
        this.warmupGracePeriodDuration = warmupGracePeriodDuration;
        this.intervalDuration = intervalDuration;
    }

    /**
     * Java needs time to optimize code during runtime--hence, include a warm-up check.
     *
     * @param timestamp The unix timestamp.
     * @return true if the grace period is over, false otherwise.
     */
    public boolean hasWarmedUp(long timestamp) {
        // Skip calculations if already warmed up.
        if(hasWarmedUp) {
            return true;
        }

        if (startTime == -1) {
            startTime = timestamp;
        }

        // Check how much time has passed and set the appropriate flags.
        if (timestamp - startTime >= warmupGracePeriodDuration) {
            System.out.printf(
                    "Warmup grace period of %s milliseconds concluded, starting log processing.%n",
                    warmupGracePeriodDuration
            );
            logProcessingStartTime = timestamp;
            hasWarmedUp = true;
            return true;
        }
        linesInWarmup++;
        return false;
    }

    /**
     * Check if the processors should proceed to the next interval.
     *
     * @param timestamp The current timestamp.
     */
    public void checkCloseInterval(long timestamp) {
        if(intervalDuration == -1) {
            // Skip if no interval is given.
            return;
        }

        if (intervalStartTime == -1) {
            intervalStartTime = timestamp;
        }

        // Check if the processors should roll over to the next interval.
        if (timestamp - intervalStartTime > intervalDuration) {
            long start = intervalStartTime - logProcessingStartTime;
            intervalStartTime += intervalDuration;
            long end = intervalStartTime - logProcessingStartTime;
            System.out.printf("Closing measurement interval [%s, %s).%n", start, end);
            for(IProcessor processor : processors) {
                processor.closeInterval(start, end);
            }
        }
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
        long timestamp = Long.parseLong(components[0].substring(0, index));
        String thread = components[0].substring(index + 1);

        // Process the line.
        if(hasWarmedUp(timestamp)) {
            // Check if the processors need to roll over to the next measurement interval.
            checkCloseInterval(timestamp);

            // Cache messages, since they are often identical.
            if(!MESSAGE_CACHE.containsKey(components[1])) {
                MESSAGE_CACHE.put(components[1], components[1].split(" "));
            }

            // Call the processors.
            String[] data = MESSAGE_CACHE.get(components[1]);
            for(IProcessor processor : processors) {
                processor.process(timestamp, thread, data);
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
                "Elevator[T=60s]_2022-03-21T15.27.30.020620900Z",
                new IProcessor[]{
                        new TransitionCountProcessor(),
                        new ThroughputProcessor()
                },
                10000,
                10000
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

    // TODO:
    //  - An observation that is made is that the big pauses seems to be closely correlated to log files being generated
    //  during logging--the longer pauses occur at regular intervals, and there are roughly as many log files.
    //  - Increasing the log file rollover policy's size increases the pause duration by an equivalent amount. This does
    //  seem to imply that the bottleneck is caused by the RollingRandomAccessFile appender. Moving to a faster storage
    //  medium does not cause a meaningful speed in performance (about 25%, SSD to m.2), which implies that the primary
    //  cause is the compression of the log files during rollover.
    //  - The operating system, and windows defender in particular, has been investigated as an alternative culprit.
    //  Excluding the project folder did result in fewer unexpected gaps (700 to approximately 200). Nevertheless, the
    //  bottleneck described above persists, which implies that there are multiple causes of the irregularities.
    //      - This behavior is difficult to reproduce--the count varies wildly, with values observed in [90, 400]. Gaps
    //      do seem to occur more regularly when other tasks are performed on the same machine.
    //      - The same test methodology has been repeated with the folder not being excluded: this yielded ~220 gaps,
    //      which implies that excluding the folder in windows defender does not make a noticeable difference.
    //  - Another aspect that is important to note is that the timestamp is generated at the moment of writing. Hence,
    //  it could theoretically be possible that the program does run continuously, with the pauses occurring within the
    //  logging aspect. For this to be true, all threads would need to halt at the same moment.
    // TODO:
    //  - Verify if all threads have the same time gaps.
}
