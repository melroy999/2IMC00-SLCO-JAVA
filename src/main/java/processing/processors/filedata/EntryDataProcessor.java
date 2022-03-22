package processing.processors.filedata;

import processing.processors.IProcessor;

import java.util.*;

/**
 * A processor that gathers statistical data about base entries for each individual log file, without message context.
 */
public class EntryDataProcessor implements IProcessor {
    // Keep entries for each individual log file.
    private EntryData[] files;

    static class EntryData {
        // The number of log entries contained within the log file.
        private int nrOfLines = 0;

        // The first and last unix milli timestamps encountered in the log.
        private long startTime = -1;
        private long endTime = 0;

        // Track the number of entries per time unit (milliseconds).
        private final Map<Long, Integer> timeUnitToCount = new HashMap<>();

        /**
         * Get a comma separated list containing timestamp ranges in which no data is is available.
         *
         * @return A comma separated list containing timestamp ranges in which no data is is available.
         */
        public String getMissingRanges() {
            List<String> ranges = new ArrayList<>();
            long previous = startTime;
            Long[] timestamps = timeUnitToCount.keySet().toArray(new Long[0]);
            Arrays.sort(timestamps);
            for(long timestamp : timestamps) {
                if(timestamp - previous > 1) {
                    ranges.add(String.format(
                            "[%s, %s]: %s",
                            previous + 1 - startTime,
                            timestamp - 1 - startTime,
                            timestamp - previous - 2)
                    );
                }
                previous = timestamp;
            }
            return String.join(", ", ranges);
        }

        @Override
        public String toString() {
            return "FileData{" +
                    "nrOfLines=" + nrOfLines +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", duration=" + (endTime - startTime) +
                    ", rate=" + nrOfLines / (endTime - startTime) +
                    ", activity=" + (float) timeUnitToCount.size() / (endTime - startTime) +
                    ", missing_ranges=[" + getMissingRanges() + "]" +
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
