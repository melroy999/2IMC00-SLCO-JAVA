package processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import processing.processors.IProcessor;
import processing.processors.filedata.LogFileEntryProcessor;
import processing.processors.filedata.LogFileMessageProcessor;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class LogReader {
    // The path to the log file.
    private final String targetFolder;

    // The number of log files that need to be processed.
    private final int nrOfLogFiles;

    // The processors.
    private final IProcessor[] processors;

    // The current log file number. Note that the count starts at zero instead of one.
    // Moreover, the non-compressed file should be empty and is hence not included in this count.
    private int currentLogFileId = 0;

    // The model information encoded as a JSON string.
    private String modelInformation;

    public LogReader(String targetFolder, IProcessor[] processors) {
        // Gather target data.
        this.targetFolder = targetFolder;
        this.nrOfLogFiles = Objects.requireNonNull(Paths.get("logs", targetFolder).toFile().list()).length - 1;

        // Initialize processors.
        this.processors = processors;
        for(IProcessor processor : processors) {
            processor.initialize(this.nrOfLogFiles);
        }
    }

    /**
     * Process the given line by passing on the relevant data to each of the processors.
     *
     * @param line The line to be processed.
     */
    public void processLine(String line) {
        // Split the string in two on the first occurrence of a space character.
        int index = line.indexOf(" ");
        String[] components = new String[] {line.substring(0, index), line.substring(index + 1)};

        // Split the header component on an underscore to find the timestamp and thread name.
        index = components[0].indexOf("_");
        long timestamp = Long.parseLong(components[0].substring(0, index));
        String thread = components[0].substring(index + 1);

        // Check if the message contains special information.
        if(components[1].startsWith("JSON")) {
            modelInformation = components[1].substring(5);
        } else {
            // Call the processors.
            String[] data = components[1].split(" ");
            for (IProcessor processor : processors) {
                processor.process(currentLogFileId, timestamp, thread, data);
            }
        }
    }

    /**
     * Process the target compressed file by reading it line by line.
     *
     * @param i The currently targeted compressed file number.
     */
    public void processCompressedFile(int i) throws IOException {
        File file = Paths.get("logs", targetFolder, "data." + i + ".gz").toFile();
        System.out.printf("[LogReader] Processing log file: '%s'%n", file.getPath());
        try (
                GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }
    }

    /**
     * Process the remaining plain and uncompressed file at the target path by reading it line by line.
     */
    public void processPlainFile() throws IOException {
        File file = Paths.get("logs", targetFolder, "data.log").toFile();
        System.out.printf("[LogReader] Checking for empty log file: '%s'%n", file.getPath());
        try (
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            if(br.readLine() != null) {
                throw new Error(
                        "[LogReader] File '" + file.getPath() + "' should be empty."
                );
            }
        }
    }

    /**
     * Process the log files.
     */
    public void processFiles() throws IOException {
        System.out.printf("[LogReader] Processing log files contained within the 'logs/%s' folder.%n", targetFolder);

        // The last file should be empty--throw an error if not.
        processPlainFile();

        // Process each of the log files in order--note that counting is started at 1 instead of 0.
        for(currentLogFileId = 0; currentLogFileId < nrOfLogFiles; currentLogFileId++) {
            processCompressedFile(currentLogFileId + 1);
        }

        System.out.println("[LogReader] Processing completed.");
    }

    /**
     * Report all the gathered results.
     */
    public void reportResults() {
        System.out.println("[LogReader] Reporting the results gathered by the processors.");
        File file = Paths.get("results", targetFolder).toFile();
        if(file.mkdirs()) {
            System.out.println("[LogReader] Results folder already exists. Results will be overwritten.");
        }

        // Post-process the models and prepare the gson builder.
        GsonBuilder builder = new GsonBuilder();
        for(IProcessor processor : processors) {
            processor.postProcess();
            processor.registerJsonSerializers(builder);
        }

        // Report the results.
        Gson gson = builder.setPrettyPrinting().create();
        JsonObject root = new JsonObject();
        JsonObject model = gson.fromJson(modelInformation, JsonObject.class);
        root.add("model", model);
        for(IProcessor processor : processors) {
            processor.addResults(file.getPath(), gson, root);
        }

        // Report the results to a file.
        File dataFile = Paths.get(file.getPath(), "results.json").toFile();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            writer.write(gson.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the log reader operations.
     */
    public void execute() {
        try {
            Instant instant = Instant.now();
            processFiles();
            reportResults();
            System.out.printf(
                    "[LogReader] Processing and reporting of all %s log files in folder '%s' completed in %s ms.%n",
                    nrOfLogFiles,
                    targetFolder,
                    Duration.between(instant, Instant.now()).toMillis()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Process the given model result entry.
     *
     * @param path A path to a model result entry that contains count based measurements.
     */
    public static void processModel(String path) {
        LogReader operation = new LogReader(
                path,
                new IProcessor[]{
                        new LogFileEntryProcessor(),
                        new LogFileMessageProcessor(5000, 10000, 50000)
                }
        );
        operation.execute();
    }

    public static void main(String[] args) {
        String[] targetFolders = {
            "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s,URP]/logging/2022-04-03T17.58.29.572240Z",
            "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s,URP]/logging/2022-04-03T17.59.39.116260200Z",
            "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s,URP]/logging/2022-04-03T18.01.30.914282Z"
        };

        for(String targetFolder : targetFolders) {
            processModel(targetFolder);
        }
    }

    // TODO:
    //  - An observation that is made is that the big pauses seems to be closely correlated to log files being generated
    //  during logging--the longer pauses occur at regular intervals, and there are roughly as many log files.
    //  - Increasing the log file rollover policy's size increases the pause duration by an equivalent amount. This does
    //  seem to imply that the bottleneck is caused by the RollingRandomAccessFile appender. Moving to a faster storage
    //  medium does not cause a meaningful speed in performance (about 25%, SSD to m.2), which implies that the primary
    //  cause is the compression of the log files during rollover, or just a bottleneck in the logging itself.
    //  - The operating system, and windows defender in particular, has been investigated as an alternative culprit.
    //  Excluding the project folder did result in fewer unexpected gaps (700 to approximately 200). Nevertheless, the
    //  bottleneck described above persists, which implies that there are multiple causes of the irregularities.
    //      - This behavior is difficult to reproduce--the count varies wildly, with values observed in [90, 400]. Gaps
    //      do seem to occur more regularly when other tasks are performed on the same machine.
    //      - The same test methodology has been repeated with the folder not being excluded: this yielded ~220 gaps,
    //      which implies that excluding the folder in windows defender does not make a noticeable difference.
    //  - When enabling immediateFlush, the unexpected gaps are within the range [11, 13], but far more frequent. Hence,
    //  it is all but confirmed that the flushing of data to the file is the primary culprit.
    //  - Another aspect that is important to note is that the timestamp is generated at the moment of writing. Hence,
    //  it is theoretically possible that the program remains to run continuously. In this scenario, the flushing of
    //  entries causes the logger to halt, which in turn results in the log causes messages to pile up.
    //      - However, one would expect a peak of entries after such a pause, which does not seem to be the case.
    //  - Furthermore, it has been observed that all threads seem to halt consistently at the same moment, with several
    //  irregularities at a thread level occurring with negligible infrequency. Hence, it can be hypothesized that each
    //  thread is impacted to an equal degree, which in turn implies that the data remains congruent and representable.
    //  - Further investigation has shown that the ring buffer size has a noticeable impact, namely that the number of
    //  log files not displaying the large gap increases at a complementary rate when increasing the size of the buffer.
    //  Due to this, the suspicion is that the the issues described above are caused by the ring buffer filling up to
    //  full capacity, resulting in a full halt on adding new log messages.
    //      - However, removing the entry altogether results in the first ten logs proceeding smoothly, but following
    //      ones running at crippling speeds. Hence, the buffer may not be the root culprit. Hence, it is suspected that
    //      an additional throughput bottleneck exists elsewhere.
    //  - Further experimentation has shown that the compression of files is the culprit. The delay disappears when the
    //  compression of files is disabled. Occasional delays still exist, but the activity rate is generally above 90%.
    //  - An option has been added that decreases the compression level to one assuring better speeds. Note that the
    //  official documentation states that only the compression level of zip files can be controlled: this is incorrect,
    //  since it is supported for gzip too. The latter has been verified through practice and code inspection.
    // TODO:
    //  - Verify if all threads have the same time gaps.
    // TODO:
    //  - Verify that the order of messages is correct.
    //  - While testing, several inconsistencies were discovered that could imply that the original order of messages is
    //  not guaranteed. For one, timestamps were not always in increasing order. Hence, an unique id has been assigned
    //  to each of the log messages, which in turn shows order violations too.
    //  - As a test, the logging has been changed from asynchronous to synchronized. However, the same issue persists,
    //  which leads to the observation that it is not sufficient to make the unique id assignment thread safe, since a
    //  race condition could still exist between the assignment of the id and writing to the log.
    //  - The log order is not violated in an experiment where the logger call is converted to a synchronized method.
    //  However, the synchronize keyword does not guarantee fairness, and hence, the balance between the threads is not
    //  retained. Is there another way to test for incorrect behavior?

    // TODO:
    //  - It would be useful to have more information about the model added to the log. It would allow for messages to
    //  be made shorter by shortening the names of objects. This could in theory increase the throughput and density of
    //  the files. Moreover, the information itself could be used to create more detailed figures.
    //  - Moreover, the above could ensure that each event written to the log is of equal length, which in turn helps to
    //  ensure that each transition is affected equally by the logging.
}
