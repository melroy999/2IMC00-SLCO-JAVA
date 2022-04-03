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
    private transient String last_event;

    // The last transition entry encountered.
    private transient String last_transition_event;

    /**
     * A data class holding statistical data for the target class.
     */
    static class MessageData {
        // Count the number of times that each event occurs within the log.
        protected final Map<String, Long> event_count = new HashMap<>();

        // Track the order of events to detect concurrency.
        protected final Map<String, Long> succession_graph = new HashMap<>();

        // Track the order of transition events to detect concurrency.
        protected final Map<String, Long> transition_succession_graph = new HashMap<>();
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
        entry.event_count.merge(target, 1L, Long::sum);

        // Add an edge to the succession graph.
        if(last_event != null) {
            entry.succession_graph.merge(last_event + "~" + target, 1L, Long::sum);
        }
        last_event = target;

        // Add an edge to the transition succession graph if appropriate.
        if(target.contains(".T")) {
            if(last_transition_event != null) {
                entry.transition_succession_graph.merge(last_transition_event + "~" + target, 1L, Long::sum);
            }
            last_transition_event = target;
        }
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
