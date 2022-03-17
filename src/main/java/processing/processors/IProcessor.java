package processing.processors;

import java.time.Instant;

/**
 * An interface for classes that process log entries.
 */
public interface IProcessor {
    /**
     * Process the given log entry.
     *
     * @param thread The name of the thread the log entry is from.
     * @param data The data contained within the body of the log entry.
     */
    void process(String thread, String[] data);

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    void reportResults(String path);
}
