package processing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelReader {
    public static List<String> getRunNames(String name, String subfolder) {
        try (Stream<Path> paths = Files.list(Paths.get("logs", name, subfolder))) {
            return paths
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        File[] directories = new File("logs/").listFiles(File::isDirectory);
        assert directories != null;
        String[] targetModels = Arrays.stream(directories).map(File::getName).toArray(String[]::new);

        // List the target models.
//        targetModels = new String[] {
//                "Telephony[LA,NDS,T=30s,URP]",
//                "Telephony[NDS,NL,T=30s,URP]",
//                "Telephony[NDS,SLL,T=30s,URP]",
//                "Telephony[NDS,T=30s,URP]"
//        };

        // Process the given count and log-based results.
        for(String name : targetModels) {
            List<String> countingResults = getRunNames(name, "counting");
            if(countingResults != null) {
                for(String entry : countingResults) {
                    CountReader.processModel(Paths.get(name, "counting", entry).toString());
                }
            }

            List<String> loggingResults = getRunNames(name, "logging");
            if(loggingResults != null) {
                for(String entry : loggingResults) {
                    LogReader.processModel(Paths.get(name, "logging", entry).toString());
                }
            }
        }
    }
}
