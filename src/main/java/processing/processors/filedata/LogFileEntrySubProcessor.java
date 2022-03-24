package processing.processors.filedata;

import processing.processors.IProcessor;

import java.util.*;

/**
 * A processor that gathers statistical data about base entries for each individual log file, without message context.
 */
public class LogFileEntrySubProcessor implements IProcessor {
    // Keep entries for each individual log file.
    protected LogFileData[] files;

    // The last recorded timestamp.
    private long last = -1;

    /**
     * A data class containing static context-free information on the contents of the log file.
     */
    static class LogFileData {
        // The number of log entries contained within the log file.
        protected int lines = 0;

        // The first and last unix milli timestamps encountered in the log.
        protected long start = Long.MAX_VALUE;
        protected long end = 0;

        // Track the number of entries per time unit (milliseconds).
        protected final Map<Long, Integer> count = new HashMap<>();

        @Override
        public String toString() {
            return "FileData{" +
                    "nrOfLines=" + lines +
                    ", startTime=" + start +
                    ", endTime=" + end +
                    ", duration=" + (end - start) +
                    ", rate=" + lines / (end - start) +
                    ", activity=" + (float) count.size() / (end - start + 1) +
                    getGaps() +
                    '}';
        }

        /**
         * Find gaps in the data.
         *
         * @return A comma separated list containing each of the gaps as an inclusive range, including the gap's length.
         */
        public String getGaps() {
            if(count.size() == end - start + 1) {
                // No gaps to report.
                return "";
            }

            // Sort the gathered timestamps and find the gaps.
            Long[] timestamps = count.keySet().toArray(new Long[0]);
            Arrays.sort(timestamps);
            long last = timestamps[0];

            // Detect gaps in the data flow.
            List<String> gaps = new ArrayList<>();
            for(long timestamp : timestamps) {
                if(last != -1 && timestamp - last > 1) {
                    gaps.add(String.format(
                            "[%s, %s]: %s",
                            last + 1 - start,
                            timestamp - 1 - start,
                            timestamp - last - 2)
                    );
                }
                last = timestamp;
            }
            return String.format(", missing_ranges=[%s]", String.join(", ", gaps));
        }
    }

    protected LogFileEntrySubProcessor() {
        // Empty constructor.
    }

    protected LogFileEntrySubProcessor(int nrOfFiles) {
        initialize(nrOfFiles);
    }

    /**
     * Initialize the processor using the given information.
     *
     * @param nrOfFiles The number of log files to be processed.
     */
    @Override
    public void initialize(int nrOfFiles) {
        files = new LogFileData[nrOfFiles];
        for(int i = 0; i < nrOfFiles; i++) {
            files[i] = new LogFileData();
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
        LogFileData entry = files[fileNumber];

        // Update the start and end times.
        entry.start = Long.min(entry.start, timestamp);
        entry.end = Long.max(entry.end, timestamp);

        // Increment the count for the time unit and the total number of lines.
        entry.count.merge(timestamp, 1, Integer::sum);
        entry.lines++;
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.printf("[%s] Reporting global log file characteristics.%n", this.getClass().getSimpleName());
        for(int i = 0; i < files.length; i++) {
            LogFileData entry = files[i];
            System.out.printf("[%s] - File %s: %s%n", this.getClass().getSimpleName(), i, entry);
        }
    }

    public LogFileData[] getFiles() {
        return files;
    }
}
