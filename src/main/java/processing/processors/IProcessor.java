package processing.processors;

import java.time.Instant;

/**
 * An interface for classes that process log entries.
 */
public interface IProcessor {
    /**
     * Process the given log entry.
     *
     * @param message The message contained within the log entry.
     */
    void process(String message);
}
