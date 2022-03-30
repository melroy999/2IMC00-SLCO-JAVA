package processing.processors.filedata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import processing.processors.IProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * A processor that gathers statistical data about base entries for each individual log file, without message context.
 */
public class LogFileEntrySubProcessor implements IProcessor {
    // Keep entries for each individual log file.
    protected LogFileData[] files;

    // Keep global data.
    protected final LogFileData global = new LogFileData();

    /**
     * A data class containing static context-free information on the contents of the log file.
     */
    static class LogFileData {
        // The number of log entries contained within the log file.
        protected int lines = 0;

        // The first and last unix milli timestamps encountered in the log.
        protected long start = Long.MAX_VALUE;
        protected long end = 0;

        // Track the number of entries per time unit (milliseconds).
        protected final Map<Long, Integer> count = new HashMap<>();
    }

    protected LogFileEntrySubProcessor() {
        // Empty constructor.
    }

    protected LogFileEntrySubProcessor(int nrOfFiles) {
        initialize(nrOfFiles);
    }

    /**
     * Initialize the processor using the given information.
     *
     * @param nrOfFiles The number of log files to be processed.
     */
    @Override
    public void initialize(int nrOfFiles) {
        files = new LogFileData[nrOfFiles];
        for(int i = 0; i < nrOfFiles; i++) {
            files[i] = new LogFileData();
        }
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
        // Load the appropriate entry.
        LogFileData entry = files[fileNumber];

        // Update the start and end times.
        entry.start = Long.min(entry.start, timestamp);
        entry.end = Long.max(entry.end, timestamp);

        // Increment the count for the time unit and the total number of lines.
        entry.count.merge(timestamp, 1, Integer::sum);
        entry.lines++;

        // Update global data.
        global.start = Long.min(global.start, timestamp);
        global.end = Long.max(global.end, timestamp);
        global.count.merge(timestamp, 1, Integer::sum);
        global.lines++;
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
        object.add("log_data", gson.toJsonTree(this));
    }

    /**
     * Find gaps in the data.
     *
     * @return A list of json objects containing information on the gaps present in the log entry's data.
     */
    public List<JsonObject> getDataGaps(LogFileData entry) {
        if(entry.count.size() == entry.end - entry.start + 1) {
            // No gaps to report.
            return new ArrayList<>();
        }

        // Sort the gathered timestamps and find the gaps.
        Long[] timestamps = entry.count.keySet().toArray(new Long[0]);
        Arrays.sort(timestamps);
        long last = timestamps[0];

        // Detect gaps in the data flow.
        List<JsonObject> gaps = new ArrayList<>();
        for(long timestamp : timestamps) {
            if(last != -1 && timestamp - last > 1) {
                JsonObject range = new JsonObject();
                range.addProperty("start", last + 1 - entry.start);
                range.addProperty("end", timestamp - 1 - entry.start);
                range.addProperty("duration", timestamp - last - 1);
                gaps.add(range);
            }
            last = timestamp;
        }
        return gaps;
    }

    /**
     * Register the appropriate serializer overrides to serialize the processor's data.
     *
     * @param builder The gson builder that will be used to generate the json models.
     */
    public void registerJsonSerializers(GsonBuilder builder) {
        JsonSerializer<LogFileEntrySubProcessor> processorSerializer = (src, type, context) -> {
            // Bypass the processor and report the data instead.
            JsonObject root = new JsonObject();
            root.add("files", context.serialize(src.files));
            root.add("global", context.serialize(src.global));
            return root;
        };
        builder.registerTypeAdapter(LogFileEntrySubProcessor.class, processorSerializer);

        JsonSerializer<LogFileData> dataSerializer = (src, type, context) -> {
            // Add all information, including the gaps in the data.
            JsonObject root = new JsonObject();
            root.addProperty("lines", src.lines);
            root.addProperty("start", src.start);
            root.addProperty("end", src.end);
            root.addProperty("duration", src.end - src.start + 1);
            root.addProperty("rate", (double) src.lines / (src.end - src.start + 1));
            root.addProperty("activity", (double) src.count.size() / (src.end - src.start + 1));
            root.add("gaps", context.serialize(getDataGaps(src)));
            root.add("count", context.serialize(src.count));

            return root;
        };
        builder.registerTypeAdapter(LogFileData.class, dataSerializer);
    }
}
