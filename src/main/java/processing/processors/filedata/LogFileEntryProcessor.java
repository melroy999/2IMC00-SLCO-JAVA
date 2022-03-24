package processing.processors.filedata;

import java.util.Arrays;
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
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.printf("[%s] Reporting log file characteristics.%n", this.getClass().getSimpleName());

        // Report global data.
        super.reportResults(path);

        // Create a pre-defined order for the thread names.
        String[] threads = this.threads.keySet().toArray(new String[0]);
        Arrays.sort(threads);

        // Print all entries grouped by file.
        for(int i = 0; i < files.length; i++) {
            System.out.printf("[%s] - File %s: %n", this.getClass().getSimpleName(), i);
            for(String thread : threads) {
                LogFileEntrySubProcessor processor = this.threads.get(thread);
                LogFileEntrySubProcessor.LogFileData entry = processor.getFiles()[i];
                System.out.printf("[%s]    - %s: %s%n", this.getClass().getSimpleName(), thread, entry);
            }
        }
    }

}
