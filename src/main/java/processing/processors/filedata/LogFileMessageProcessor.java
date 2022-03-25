package processing.processors.filedata;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;

/**
 * A processor that gathers statistical data about messages within the log files.
 */
public class LogFileMessageProcessor extends LogFileMessageSubProcessor {
    // The timestamp of the first entry.
    private long first = Long.MAX_VALUE;

    // The desired intervals in which the data should be divided and grouped.
    private final int interval;

    // The (offset from start) interval to take measurements in.
    private final int start;
    private final int end;

    // Keep data for each interval slot.
    private final LogFileMessageSubProcessor[] intervals;

    public LogFileMessageProcessor(int interval, int start, int end) {
        this.interval = interval;
        this.start = start;
        this.end = end;
        this.intervals = new LogFileMessageSubProcessor[(end - start) / interval];
        for(int i = 0; i < this.intervals.length; i++) {
            this.intervals[i] = new LogFileMessageSubProcessor();
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
        // Find the timestamp of the first encountered entry.
        first = Long.min(first, timestamp);

        // Check if the timestamp is within the desired measurement range.
        if(timestamp >= first + start && timestamp < first + end) {
            // Process global data.
            super.process(fileNumber, timestamp, thread, data);

            // Determine the interval slot the entry is part of for further processing by its sub-processor.
            LogFileMessageSubProcessor processor = intervals[(int) ((timestamp - first - start) / interval)];
            processor.process(fileNumber, timestamp, thread, data);
        }
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
        JsonSerializer<LogFileMessageProcessor> serializer = (src, type, context) -> {
            // Have the root level contain the chosen settings and a pointer to the global and interval data.
            JsonObject root = new JsonObject();
            root.addProperty("interval", interval);
            root.addProperty("start", start);
            root.addProperty("end", end);
            root.add("global", context.serialize(entry));

            // Have the interval data include the chosen intervals.
            JsonObject[] intervalObjects = new JsonObject[intervals.length];
            for(int i = 0; i < intervalObjects.length; i++) {
                LogFileMessageSubProcessor processor = intervals[i];
                intervalObjects[i] = new JsonObject();
                intervalObjects[i].addProperty("start", i * interval);
                intervalObjects[i].addProperty("end", (i + 1) * interval);
                intervalObjects[i].add("count", context.serialize(processor.entry.count));
                intervalObjects[i].add("graph", context.serialize(processor.entry.graph));
            }
            root.add("intervals", context.serialize(intervalObjects));
            return root;
        };
        builder.registerTypeAdapter(LogFileMessageProcessor.class, serializer);
    }
}
