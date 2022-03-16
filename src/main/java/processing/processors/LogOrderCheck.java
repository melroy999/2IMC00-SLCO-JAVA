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
     * @param header The header of the log entry.
     * @param data The data contained within the body of the log entry.
     */
    @Override
    public void process(String header, String[] data) {
        String message = data[0];
        int value = Integer.parseInt(message);
        if(value != expectedValue) {
            throw new Error("Order violation");
        }
        expectedValue = (expectedValue + 1) % NUMBER_OF_THREADS;
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
