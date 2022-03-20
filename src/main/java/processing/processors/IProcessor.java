package processing.processors;

import java.time.Instant;

/**
 * An interface for classes that process log entries.
 */
public interface IProcessor {
    /**
     * Process the given log entry.
     *
     * @param timestamp The unix timestamp the log entry is created on.
     * @param thread The name of the thread the log entry is from.
     * @param data The data contained within the body of the log entry.
     */
    void process(long timestamp, String thread, String[] data);

    /**
     * Notify the processor that the next data entries are within a new measurement interval.
     *
     * @param start The start of the logging period in milliseconds.
     * @param end The end of the logging period in milliseconds.
     */
    void closeInterval(long start, long end);

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    void reportResults(String path);
}
