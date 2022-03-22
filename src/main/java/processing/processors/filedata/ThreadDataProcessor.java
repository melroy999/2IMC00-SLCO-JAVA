package processing.processors.filedata;

import processing.processors.IProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A processor that gathers statistical data about each individual thread's entries.
 */
public class ThreadDataProcessor implements IProcessor {
    // Track the number of files.
    private int nrOfFiles;

    // Track file data for each individual thread.
    private final Map<String, EntryDataProcessor> threadsMap = new HashMap<>();

    /**
     * Initialize the processor using the given information.
     *
     * @param nrOfFiles The number of log files to be processed.
     */
    @Override
    public void initialize(int nrOfFiles) {
        this.nrOfFiles = nrOfFiles;
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
        // Load the sub-processor and let it process the entry instead.
        EntryDataProcessor processor = threadsMap.computeIfAbsent(thread, k -> new EntryDataProcessor(nrOfFiles));
        processor.process(fileNumber, timestamp, thread, data);
    }

    /**
     * Report the data gathered by the processor.
     *
     * @param path The path to the folder in which the gathered results can be stored.
     */
    @Override
    public void reportResults(String path) {
        System.out.println("[ThreadDataProcessor] Reporting log file characteristics.");

        // Create a pre-defined order to print in.
        String[] threads = threadsMap.keySet().toArray(new String[0]);
        Arrays.sort(threads);

        // Print all entries grouped by file.
        for(int i = 0; i < nrOfFiles; i++) {
            System.out.printf("[ThreadDataProcessor] - File %s: %n", i);
            for(String thread : threads) {
                EntryDataProcessor processor = threadsMap.get(thread);
                EntryDataProcessor.EntryData entry = processor.getFiles()[i];
                System.out.printf("[ThreadDataProcessor]    - %s: %s%n", thread, entry);
            }
        }
    }
}
