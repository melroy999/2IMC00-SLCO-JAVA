package processing.processors.filedata;

import processing.processors.IProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A processor that gathers statistical data about messages within the log files.
 */
public class LogFileMessageProcessor extends LogFileMessageSubProcessor {
    // TODO: graph giving information on message sequences.

    // The timestamp of the first entry.
    private long first = Long.MAX_VALUE;

    // The desired intervals in which the data should be divided and grouped.
    private final int interval;

    // The (offset from start) interval to take measurements in.
    private final int start;
    private final int duration;

    // Keep data for each interval slot.
    private final LogFileMessageSubProcessor[] intervals;


    public LogFileMessageProcessor() {
        this(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    public LogFileMessageProcessor(int interval, int start, int end) {
        this.interval = interval;
        this.start = start;
        this.duration = end - start;
        this.intervals = new LogFileMessageSubProcessor[this.duration / interval];
        for(int i = 0; i < this.intervals.length; i++) {
            this.intervals[i] = new LogFileMessageSubProcessor();
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
        // Find the timestamp of the first encountered entry.
        first = Long.min(first, timestamp);

        // Check if the timestamp is within the desired measurement range.
        if(timestamp >= first + start && timestamp < first + start + duration) {
            // Process global data.
            super.process(fileNumber, timestamp, thread, data);

            // Determine the interval slot the entry is part of for further processing by its sub-processor.
            LogFileMessageSubProcessor processor = intervals[(int) ((timestamp - first - start) / interval)];
            processor.process(fileNumber, timestamp, thread, data);
        }
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.printf(
                "[%s] The following data is within the time window [%s, %s) with interval %s.%n",
                this.getClass().getSimpleName(),
                start,
                start + duration,
                interval
        );

        // Report global data.
        super.reportResults(path);

        // Report data per interval if appropriate.
        if(intervals.length > 1) {
            for(int i = 0; i < intervals.length; i++) {
                System.out.printf(
                        "[%s] Reporting log message counts for interval [%s, %s).%n",
                        this.getClass().getSimpleName(),
                        this.interval * i,
                        this.interval * (i + 1)
                );
                MessageData entry = intervals[i].entry;
                String[] targets = entry.count.keySet().toArray(new String[0]);
                Arrays.sort(targets);
                for(String target : targets) {
                    System.out.printf(
                            "[%s] - %s: %s%n", this.getClass().getSimpleName(), target, entry.count.get(target)
                    );
                }
            }
        }
    }
}
