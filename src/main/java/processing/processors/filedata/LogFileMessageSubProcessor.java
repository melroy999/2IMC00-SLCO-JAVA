package processing.processors.filedata;

import processing.processors.IProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A processor that gathers statistical data about messages within the log files.
 */
public class LogFileMessageSubProcessor implements IProcessor {
    // A reference to the data object.
    protected final MessageData entry = new MessageData();

    /**
     * A data class holding statistical data for the target class.
     */
    static class MessageData {
        // Count the number of times that each event occurs within the log.
        protected final Map<String, Long> count = new HashMap<>();
    }

    /**
     * Initialize the processor using the given information.
     *
     * @param nrOfFiles The number of log files to be processed.
     */
    @Override
    public void initialize(int nrOfFiles) {
        // No initialization required.
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
        String target = data[0];
        entry.count.merge(thread + "." + target, 1L, Long::sum);
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.printf("[%s] Reporting global log message counts.%n", this.getClass().getSimpleName());
        String[] targets = entry.count.keySet().toArray(new String[0]);
        Arrays.sort(targets);
        for(String target : targets) {
            System.out.printf("[%s] - %s: %s%n", this.getClass().getSimpleName(), target, entry.count.get(target));
        }
    }
}
