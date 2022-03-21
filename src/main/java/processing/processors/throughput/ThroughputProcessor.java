package processing.processors.throughput;

import processing.processors.IProcessor;

import java.util.Arrays;
import java.util.Map;

/**
 * A processor that gathers data on the throughput aspect of the logging, with the primary goal of finding odd behavior.
 */
public class ThroughputProcessor implements IProcessor {
    // Object containing all gathered data.
    ThroughputData throughputData = new ThroughputData();

    /**
     * Process the given log entry.
     *
     * @param timestamp The unix timestamp the log entry is created on.
     * @param thread    The name of the thread the log entry is from.
     * @param data      The data contained within the body of the log entry.
     */
    @Override
    public void process(long timestamp, String thread, String[] data) {
        throughputData.incrementEntryCount(thread, timestamp);
    }

    /**
     * Notify the processor that the next data entries are within a new measurement interval.
     *
     * @param start The start of the logging period in milliseconds.
     * @param end   The end of the logging period in milliseconds.
     */
    @Override
    public void closeInterval(long start, long end) {
        // The throughput processor does not support intervals, since it actively investigates time-based anomalies.
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        String[] threads = throughputData.getThreadData().keySet().toArray(new String[0]);
        Arrays.sort(threads);
        for(String thread : threads) {
            System.out.printf("[ThroughputProcessor] Analysis for thread '%s': %n", thread);
            ThreadData data = throughputData.getThreadData().get(thread);

            // Find unexpected gaps in the data flow.
            int count = 0;
            Map<Long, Integer> entriesPerTimeUnit = data.getEntriesPerTimeUnit();
            Long[] timestamps = entriesPerTimeUnit.keySet().toArray(new Long[0]);
            long lastTimestamp = timestamps[0];
            for(long timestamp : timestamps) {
                if(timestamp > lastTimestamp + 1) {
                    // Generally, multiple operations are executed per time unit (ms).
                    // Hence, any gap larger than that is deemed unexpected behavior.
                    if(timestamp - lastTimestamp >= 20) {
                        System.out.printf(
                                "[ThroughputProcessor] - Interruption of duration %s ms found before timestamp %s.%n",
                                timestamp - lastTimestamp,
                                timestamp
                        );
                        System.out.printf(
                                "[ThroughputProcessor]   Number of entries (before and after time gap): %s, %s\n",
                                entriesPerTimeUnit.get(lastTimestamp),
                                entriesPerTimeUnit.get(timestamp)
                        );
                    }
                    count++;
                }
                lastTimestamp = timestamp;
            }
            System.out.printf("[ThroughputProcessor] Thread '%s' had %s unexpected gaps.%n", thread, count);
        }
    }
}
