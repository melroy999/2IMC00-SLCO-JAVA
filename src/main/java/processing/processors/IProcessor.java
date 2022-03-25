package processing.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
     *  @param path The path to the folder in which the gathered results can be stored.
     * @param gson The preconfigured gson formatter to be used.
     */
    void reportResults(String path, Gson gson);

    /**
     * Register the appropriate serializer overrides to serialize the processor's data.
     *
     * @param builder The gson builder that will be used to generate the json models.
     */
    void registerJsonSerializers(GsonBuilder builder);
}
