package processing.processors.filedata;

import com.google.gson.*;
import processing.processors.IProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * A processor that gathers statistical data about messages within the log files.
 */
public class LogFileMessageSubProcessor implements IProcessor {
    // A reference to the data object.
    protected final MessageData entry = new MessageData();

    // The last entry encountered.
    private transient String last;

    /**
     * A data class holding statistical data for the target class.
     */
    static class MessageData {
        // Count the number of times that each event occurs within the log.
        protected final Map<String, Long> count = new HashMap<>();

        // Track how often a message is followed by another message to track concurrency.
        protected final Map<String, Long> graph = new HashMap<>();
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
        // Append the thread to the target to ensure uniqueness of the key.
        String target = thread + "." + data[0];

        // Increment the appropriate counter.
        entry.count.merge(target, 1L, Long::sum);

        // Add an edge to the graph if appropriate.
        if(last != null) {
            entry.graph.merge(last + "~" + target, 1L, Long::sum);
        }
        last = target;
    }

    /**
     * Post-process the data before conversion to json.
     */
    @Override
    public void postProcess() {

    }


    /**
     * Add the data gathered by the processor to the given json object.
     *
     * @param path  The path to the folder in which the gathered results can be stored.
     * @param gson  The preconfigured gson formatter to be used.
     * @param object The json object to store the information in.
     */
    public void addResults(String path, Gson gson, JsonObject object) {
        object.add("message_data", gson.toJsonTree(this));
    }

    /**
     * Register the appropriate serializer overrides to serialize the processor's data.
     *
     * @param builder The gson builder that will be used to generate the json models.
     */
    public void registerJsonSerializers(GsonBuilder builder) {
        JsonSerializer<LogFileMessageSubProcessor> serializer = (src, type, context) -> {
            // Bypass the processor and report the data instead.
            return context.serialize(src.entry);
        };
        builder.registerTypeAdapter(LogFileMessageSubProcessor.class, serializer);
    }
}
