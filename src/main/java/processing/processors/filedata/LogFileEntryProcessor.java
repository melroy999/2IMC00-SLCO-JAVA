package processing.processors.filedata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class LogFileEntryProcessor extends LogFileEntrySubProcessor {
    // Track file data for each individual thread.
    private final Map<String, LogFileEntrySubProcessor> threads = new HashMap<>();

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
        // Process global data.
        super.process(fileNumber, timestamp, thread, data);

        // Load the appropriate sub-processor associated with the thread and let it process the entry.
        LogFileEntrySubProcessor processor = threads.computeIfAbsent(
                thread, k -> new LogFileEntrySubProcessor(files.length)
        );
        processor.process(fileNumber, timestamp, thread, data);
    }

    public LogFileEntryProcessor() {
        // Empty constructor.
    }

    /**
     * Register the appropriate serializer overrides to serialize the processor's data.
     *
     * @param builder The gson builder that will be used to generate the json models.
     */
    @Override
    public void registerJsonSerializers(GsonBuilder builder) {
        super.registerJsonSerializers(builder);

        // Format the data differently.
        JsonSerializer<LogFileEntryProcessor> serializer = (src, type, context) -> {
            // Have the root level differentiate global and thread data.
            JsonObject root = new JsonObject();
            root.add("global", context.serialize(src, LogFileEntrySubProcessor.class));
            root.add("threads", context.serialize(src.threads));
            return root;
        };
        builder.registerTypeAdapter(LogFileEntryProcessor.class, serializer);
    }
}
