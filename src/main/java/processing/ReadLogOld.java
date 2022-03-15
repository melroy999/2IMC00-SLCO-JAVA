package processing;

import processing.processors.IProcessor;
import processing.processors.LogOrderCheck;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class ReadLogOld {
    // The processors.
    private final IProcessor[] processors;

    // Control the warmup grace period.
    private static final long WARMUP_GRACE_PERIOD_DURATION = 100;
    private Instant startTime;
    private boolean hasWarmedUp = false;

    public ReadLogOld(IProcessor[] processors) {
        this.processors = processors;
    }

    /**
     * Java needs time to optimize code during runtime--hence, include a warm-up check.
     *
     * @param header The header containing the timestamp.
     * @return true if the grace period is over, false otherwise.
     */
    public boolean hasWarmedUp(String header) {
        // Skip calculations if already warmed up.
        if(hasWarmedUp) {
            return true;
        }

        // Parse the header and get the current timestamp.
        String[] headerComponents = header.substring(1).replaceAll("]", "").split("\\[");
        Instant instant = Instant.ofEpochMilli(Long.parseLong(headerComponents[1]));
        if (startTime == null) {
            startTime = instant;
        }

        // Check how much time has passed and set the appropriate flags.
        if (Duration.between(startTime, instant).toMillis() > WARMUP_GRACE_PERIOD_DURATION) {
            System.out.printf(
                    "Warmup grace period of %s milliseconds is over, starting processing.%n",
                    WARMUP_GRACE_PERIOD_DURATION
            );
            hasWarmedUp = true;
            return true;
        }
        return false;
    }

    /**
     * Process the given line by passing on the relevant data to each of the processors.
     *
     * @param line The line to be processed.
     */
    public void processLine(String line) {
        String[] components = line.split(" ", 2);
        if(hasWarmedUp(components[0])) {
            String message = components[1];
            for(IProcessor processor : processors) {
                processor.process(message);
            }
        }
    }

    /**
     * Process the file at the given path by reading it line by line.
     *
     * @param path The path to the log file.
     */
    public void processFile(String path) {
        try (
                FileInputStream inputStream = new FileInputStream(path);
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)
        ) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                processLine(line);
            }
            // note that Scanner suppresses exceptions
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReadLogOld operation = new ReadLogOld(
                new IProcessor[]{
                        new LogOrderCheck()
                }
        );
        operation.processFile("logs/data.log");
        System.out.println("Verification succeeded without incident.");
    }
}
