package processing.processors;

import java.time.Instant;

/**
 * A log processor that checks if the entries in the given log have the expected order.
 */
public class LogOrderCheck implements IProcessor {
    // Variables used to track the next expected value to encounter.
    private static final int NUMBER_OF_THREADS = 8;
    private int expectedValue = 0;

    /**
     * Process the given log entry.
     *
     * @param message The message contained within the log entry.
     */
    @Override
    public void process(String message) {
        int value = Integer.parseInt(message);
        if(value != expectedValue) {
            throw new Error("Order violation");
        }
        expectedValue = (expectedValue + 1) % NUMBER_OF_THREADS;
    }
}
