package processing.processors;

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
     * @param timestamp The unix timestamp the log entry is created on.
     * @param thread The name of the thread the log entry is from.
     * @param data The data contained within the body of the log entry.
     */
    @Override
    public void process(long timestamp, String thread, String[] data) {
        String message = data[0];
        int value = Integer.parseInt(message);
        if(value != expectedValue) {
            throw new Error("Order violation");
        }
        expectedValue = (expectedValue + 1) % NUMBER_OF_THREADS;
    }

    /**
     * Notify the processor that the next data entries are within a new measurement interval.
     *
     * @param start The start of the logging period in milliseconds.
     * @param end The end of the logging period in milliseconds.
     */
    public void closeInterval(long start, long end) {
        // Do nothing.
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.println("Log order verification successful.");
    }
}
