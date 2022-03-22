package processing.processors;

/**
 * An interface for classes that process log entries.
 */
public interface IProcessor {
    /**
     * Initialize the processor using the given information.
     *
     * @param nrOfFiles The number of log files to be processed.
     */
    void initialize(int nrOfFiles);

    /**
     * Process the given log entry.
     *
     * @param fileNumber The log file number.
     * @param timestamp  The unix timestamp the log entry is created on.
     * @param thread     The name of the thread the log entry is from.
     * @param data       The data contained within the body of the log entry.
     */
    void process(int fileNumber, long timestamp, String thread, String[] data);

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    void reportResults(String path);
}
