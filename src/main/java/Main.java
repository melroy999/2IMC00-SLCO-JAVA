import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.MainMapLookup;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger;
    static {
        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "B";
        MainMapLookup.setMainArguments("log_date", log_date, "log_name", log_name);
        logger = LogManager.getLogger();
    }
    private static volatile int current_thread = 0;
    private static final int NUMBER_OF_THREADS = 8;

    public static class Action implements Runnable {
        private final int thread_number;

        public Action(int thread_number) {
            this.thread_number = thread_number;
        }

        @Override
        public void run() {
            int i = 0;
            while(i < 1000000) {
                if(current_thread == thread_number) {
                    logger.info(thread_number);
                    synchronized (new Object()) {
                        current_thread = (current_thread + 1) % NUMBER_OF_THREADS;
                    }
                    i++;
                }
            }
        }
    }

    public static void runThreads() throws InterruptedException {
        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for(int i = 0; i < NUMBER_OF_THREADS; i++) {
            executor.submit(new Action(i));
        }
        executor.shutdown();
        //noinspection ResultOfMethodCallIgnored
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        long timeElapsed = Duration.between(start, Instant.now()).toMillis();
        System.out.println(timeElapsed);
    }

    /**
     * Initialize the logger to gather the appropriate performance data with.
     */
    public static void main(String[] args) throws InterruptedException {
        // Run the program.
        runThreads();
    }
}
