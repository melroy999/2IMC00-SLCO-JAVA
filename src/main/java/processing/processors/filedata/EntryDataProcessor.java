package processing.processors.filedata;

import processing.processors.IProcessor;

import java.util.*;

/**
 * A processor that gathers statistical data about base entries for each individual log file, without message context.
 */
public class EntryDataProcessor implements IProcessor {
    // Keep entries for each individual log file.
    private EntryData[] files;

    // Keep track of the last encountered timestamp.
    private long lastTimestamp = -1;

    private String lastMessage = "";

    static class EntryData {
        // The number of log entries contained within the log file.
        private int nrOfLines = 0;

        // The first and last unix milli timestamps encountered in the log.
        private long startTime = -1;
        private long endTime = 0;

        // Time gaps found within the entries.
        private final List<String> gaps = new ArrayList<>();

        // Track the number of entries per time unit (milliseconds).
        private final Map<Long, Integer> timeUnitToCount = new HashMap<>();

        @Override
        public String toString() {
            return "FileData{" +
                    "nrOfLines=" + nrOfLines +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", duration=" + (endTime - startTime) +
                    ", rate=" + nrOfLines / (endTime - startTime) +
                    ", activity=" + (float) timeUnitToCount.size() / (endTime - startTime + 1) +
                    ", missing_ranges=[" + String.join(", ", gaps) + "]" +
                    '}';
        }
    }

    public EntryDataProcessor() {
        // Empty default constructor.
    }

    public EntryDataProcessor(int nrOfFiles) {
        initialize(nrOfFiles);
    }

    /**
     * Initialize the processor using the given information.
     *
     * @param nrOfFiles The number of log files to be processed.
     */
    @Override
    public void initialize(int nrOfFiles) {
        files = new EntryData[nrOfFiles];
        for(int i = 0; i < nrOfFiles; i++) {
            files[i] = new EntryData();
        }
    }

    /**
     * Process the given log entry.
     *
     * @param fileNumber The log file number.
     * @param timestamp  The unix timestamp the log entry is created on.
     * @param thread     The name of the thread the log entry is from.
     * @param data       The data contained within the body of the log entry.
     */
    @Override
    public void process(int fileNumber, long timestamp, String thread, String[] data) {
        // Load the appropriate entry.
        EntryData entry = files[fileNumber];

        // Update the start and end times.
        if(entry.startTime == -1) {
            entry.startTime = timestamp;
        }
        entry.endTime = timestamp;

        // Increment the count for the time unit and the total number of lines.
        entry.timeUnitToCount.merge(timestamp, 1, Integer::sum);
        entry.nrOfLines++;

        // Detect gaps in the data flow.
        if(lastTimestamp != -1 && timestamp - lastTimestamp > 1) {
            entry.gaps.add(String.format(
                    "[%s, %s]: %s",
                    lastTimestamp + 1 - entry.startTime,
                    timestamp - 1 - entry.startTime,
                    timestamp - lastTimestamp - 2)
            );
        }

        // Keep track of the last encountered timestamp.
        if(lastTimestamp > timestamp) {
            throw new Error("Warning: order preservation assumption violated.");
        }
        lastTimestamp = timestamp;
        lastMessage = String.format("%s %s %s %s", fileNumber, timestamp, thread, String.join(" ", data));
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.println("[EntryDataProcessor] Reporting log file characteristics.");
        for(int i = 0; i < files.length; i++) {
            EntryData entry = files[i];
            System.out.printf("[EntryDataProcessor] - File %s: %s%n", i, entry);
        }
    }

    public EntryData[] getFiles() {
        return files;
    }
}
