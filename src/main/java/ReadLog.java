import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ReadLog {
    private static final String fileName = "logs/data.log";
    private static int expectedValue = 0;
    private static final int NUMBER_OF_THREADS = 8;

    public static void processLine(String line) {
        if(line.startsWith("[")) {
            String target = line.split(" ")[1];
            int value = Integer.parseInt(target);
            if(value != expectedValue) {
                throw new Error("Order violation");
            }
            expectedValue = (expectedValue + 1) % NUMBER_OF_THREADS;
        }
    }

    public static void processFile(String path) {
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
        processFile(fileName);
        System.out.println("Verification succeeded without incident.");
    }
}
