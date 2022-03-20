package processing.processors;

import java.util.*;

/**
 * Measure statistics on how often a particular transition is fired.
 */
public class TransitionCounter implements IProcessor {
    // Objects holding the data for each individual thread.
    private Map<String, ThreadData> threadDataMap = new HashMap<>();

    // Data measured within the given interval.
    private final Map<String, Map<String, ThreadData>> intervalMap = new HashMap<>();
    private final List<String> intervals = new ArrayList<>();

    /**
     * Data container for data gathered for a given thread.
     */
    static class ThreadData {
        // The thread the collected data is for.
        private final String thread;

        // Each state machine runs on a unique thread--which state machine and class are associated with this one?
        private final String class_name;
        private final String state_machine_name;

        // The data entries for the transitions/decision structures contained within, grouped by starting state.
        private final Map<String, Map<String, DataEntry>> stateToData = new HashMap<>();

        public ThreadData(String thread, String[] data) {
            this.thread = thread;
            this.class_name = data[1];
            this.state_machine_name = data[2];
        }

        /**
         * Get the data associated with the given state.
         *
         * @param state The state to get the data of.
         * @return A mapping containing information gathered for the state.
         */
        private Map<String, DataEntry> getStateData(String state) {
            if(!stateToData.containsKey(state)) {
                stateToData.put(state, new HashMap<>());
            }
            return stateToData.get(state);
        }

        /**
         * Get the data associated with the given state's decision structure.
         *
         * @param state The target state.
         * @return A DecisionStructureEntry object containing numerical data on the behavior of the decision structure.
         */
        public DecisionStructureEntry getDecisionStructureData(String state) {
            Map<String, DataEntry> stateData = getStateData(state);
            if(!stateData.containsKey("__D__")) {
                stateData.put("__D__", new DecisionStructureEntry(state));
            }
            return (DecisionStructureEntry) stateData.get("__D__");
        }

        /**
         * Get the data associated with the given transition.
         *
         * @param source The source state of the transition.
         * @param target The target state of the transition.
         * @param id The identifier of the transition.
         * @return A TransitionEntry object containing numerical data on the behavior of the transition.
         */
        public TransitionEntry getTransitionData(String source, String target, String id) {
            Map<String, DataEntry> stateData = getStateData(source);
            if(!stateData.containsKey(id)) {
                stateData.put(id, new TransitionEntry(source, target, id));
            }
            return (TransitionEntry) stateData.get(id);
        }

        public void reportData() {
            // Print all of the gathered data.
            System.out.println(thread + ": " + class_name + "." + state_machine_name);
            String[] states = stateToData.keySet().toArray(new String[0]);
            Arrays.sort(states);
            for(String state : states) {
                Map<String, DataEntry> stateData = stateToData.get(state);
                String[] targets = stateData.keySet().toArray(new String[0]);
                Arrays.sort(targets);
                for(String target : targets) {
                    System.out.println("\t - " + stateData.get(target));
                }
            }
        }
    }

    interface DataEntry {}

    /**
     * Track the appropriate data for the target transition.
     */
    static class TransitionEntry implements DataEntry {
        // The transition the decision structure belongs to.
        private final String source;
        private final String target;
        private final String id;

        // The number of times the transition has been fired.
        private int entryCount = 0;

        // The number of times the transition has been executed successfully.
        private int successCount = 0;

        // The number of times the transition blocked due to a failing guard statement.
        private int failureCount = 0;

        public TransitionEntry(String source, String target, String id) {
            this.source = source;
            this.target = target;
            this.id = id;
        }

        @Override
        public String toString() {
            return "TransitionEntry{" +
                    "source=" + source +
                    ", target=" + target +
                    ", id=" + id +
                    ", entryCount=" + entryCount +
                    ", successCount=" + successCount +
                    ", failureCount=" + failureCount +
                    ", success_rate=" + (successCount / (float) entryCount) +
                    '}';
        }
    }

    static class DecisionStructureEntry implements DataEntry {
        // The state the decision structure belongs to.
        private final String state;

        // The number of times the decision structure has been entered.
        private int entryCount = 0;

        // The number of times the decision structure failed to pick a transition.
        private int failureCount = 0;

        public DecisionStructureEntry(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "DecisionStructureData{" +
                    "state=" + state +
                    ", entryCount=" + entryCount +
                    ", failureCount=" + failureCount +
                    ", success_rate=" + (1 - failureCount / (float) entryCount) +
                    '}';
        }
    }

    /**
     * Process the given log entry.
     *
     * @param timestamp The unix timestamp the log entry is created on.
     * @param thread The name of the thread the log entry is from.
     * @param data   The data contained within the body of the log entry.
     */
    @Override
    public void process(long timestamp, String thread, String[] data) {
        // Find the data container associated with the thread.
        if(!threadDataMap.containsKey(thread)) {
            threadDataMap.put(thread, new ThreadData(thread, data));
        }
        ThreadData threadData = threadDataMap.get(thread);

        // Determine which type of log message has been received.
        String identifier = data[0];
        if(identifier.contains("D.")) {
            processDecisionStructureEvent(threadData, data);
        } else if (identifier.contains("T.")) {
            processTransitionEvent(threadData, data);
        } else {
            throw new Error("Unexpected identifier encountered");
        }
    }

    /**
     * Notify the processor that the next data entries are within a new measurement interval.
     *
     * @param start The start of the logging period in milliseconds.
     * @param end The end of the logging period in milliseconds.
     */
    public void closeInterval(long start, long end) {
        String interval = "[" + start + ", " + end + ")";
        intervalMap.put(interval, threadDataMap);
        intervals.add(interval);
        threadDataMap = new HashMap<>();
    }

    /**
     * Process the given log event as a decision structure event.
     *
     * @param threadData The data associated with the thread the log event originates from.
     * @param data The data contained within the log entry's message.
     */
    public void processDecisionStructureEvent(ThreadData threadData, String[] data) {
        // Which state is targeted?
        String state = data[3];

        // Find the associated data entry.
        DecisionStructureEntry entry = threadData.getDecisionStructureData(state);

        // Determine which type of log message has been received.
        String identifier = data[0];
        switch (identifier) {
            case "D.O" -> entry.entryCount++;
            case "D.CF" -> entry.failureCount++;
            // default -> throw new Error("Unexpected identifier encountered");
        }
    }

    /**
     * Process the given log event as a transition event.
     *
     * @param threadData The data associated with the thread the log event originates from.
     * @param data The data contained within the log entry's message.
     */
    public void processTransitionEvent(ThreadData threadData, String[] data) {
        // Which transition is targeted?
        String source = data[4];
        String target = data[5];
        String id = data[3];

        // Find the associated data entry.
        TransitionEntry entry = threadData.getTransitionData(source, target, id);

        // Determine which type of log message has been received.
        String identifier = data[0];
        switch (identifier) {
            case "T.O" -> entry.entryCount++;
            case "T.CS" -> entry.successCount++;
            case "T.CF" -> entry.failureCount++;
            // default -> throw new Error("Unexpected identifier encountered");
        }
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        for(String interval : intervals) {
            System.out.printf("[TransitionCounter] Data for interval %s:%n", interval);
            Map<String, ThreadData> threadDataMap = intervalMap.get(interval);
            String[] keys = threadDataMap.keySet().toArray(new String[0]);
            Arrays.sort(keys);
            for(String key : keys) {
                threadDataMap.get(key).reportData();
            }
        }
    }
}
