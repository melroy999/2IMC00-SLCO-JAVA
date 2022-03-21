package processing.processors.throughput;

import java.util.HashMap;
import java.util.Map;

public class ThroughputData {
    // Keep data for each individual thread.
    private final Map<String, ThreadData> threadData = new HashMap<>();

    public ThreadData getThreadData(String thread) {
        if(!threadData.containsKey(thread)) {
            threadData.put(thread, new ThreadData());
        }
        return threadData.get(thread);
    }

    public void incrementEntryCount(String thread, long timestamp) {
        ThreadData data = getThreadData(thread);
        data.incrementEntryCount(timestamp);
    }

    public Map<String, ThreadData> getThreadData() {
        return threadData;
    }
}
