package processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CountReader {
    // The path to the log file.
    private final String targetFolder;

    // The model information encoded as a JSON string.
    private String modelInformation;

    // Gather the found counts.
    private final Map<String, Long> counts = new HashMap<>();

    public CountReader(String targetFolder) {
        this.targetFolder = targetFolder;
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
        String thread = components[0].substring(index + 1);

        // Check if the message contains special information.
        if(components[1].startsWith("JSON")) {
            modelInformation = components[1].substring(5);
        } else {
            // Gather the recorded count.
            String[] data = components[1].split(" ");
            String key = data[0];
            long count = Long.parseLong(data[1]);
            counts.put(thread + "." + key, count);
        }
    }

    /**
     * Process the plain and uncompressed file at the target path by reading it line by line.
     */
    public void processPlainFile() throws IOException {
        File file = Paths.get("logs", targetFolder, "data.log").toFile();
        System.out.printf("[CountReader] Checking log file: '%s'%n", file.getPath());
        try (
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }
    }

    /**
     * Report all the gathered results.
     */
    public void reportResults() {
        System.out.println("[CountReader] Reporting the results gathered by the processors.");
        File file = Paths.get("results", targetFolder).toFile();
        if(file.mkdirs()) {
            System.out.println("[CountReader] Results folder already exists. Results will be overwritten.");
        }

        // Report the results.
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        JsonObject root = new JsonObject();
        JsonObject model = gson.fromJson(modelInformation, JsonObject.class);
        root.add("model", model);
        root.add("event_count", gson.toJsonTree(counts));

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
            processPlainFile();
            reportResults();
            System.out.printf(
                    "[CountReader] Processing and reporting of count log file in folder '%s' completed in %s ms.%n",
                    targetFolder,
                    Duration.between(instant, Instant.now()).toMillis()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String[] targetFolders = {
                "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s,URP]/counting/2022-04-03T17.38.58.402268500Z",
                "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s,URP]/counting/2022-04-03T17.56.01.688720800Z",
                "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s,URP]/counting/2022-04-03T17.57.09.166267600Z",
        };

        for(String targetFolder : targetFolders) {
            CountReader operation = new CountReader(targetFolder);
            operation.execute();
        }
    }
}
