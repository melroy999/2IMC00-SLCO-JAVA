package processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        // List the target models.
        String[] targetModels = {
//                "Elevator[CL=3,LBS=4194304,LFS=100MB,T=60s]"
                "SyntheticTestTokens[DSSI=0,T=60s]"
        };

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
