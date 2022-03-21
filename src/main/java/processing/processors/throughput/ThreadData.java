package processing.processors.throughput;

import java.util.LinkedHashMap;
import java.util.Map;

public class ThreadData {
    // Measure the number of entries received per time unit (milliseconds).
    private final Map<Long, Integer> entriesPerTimeUnit = new LinkedHashMap<>();

    public void incrementEntryCount(long timestamp) {
        entriesPerTimeUnit.put(timestamp, entriesPerTimeUnit.getOrDefault(timestamp, 0) + 1);
    }

    public Map<Long, Integer> getEntriesPerTimeUnit() {
        return entriesPerTimeUnit;
    }
}