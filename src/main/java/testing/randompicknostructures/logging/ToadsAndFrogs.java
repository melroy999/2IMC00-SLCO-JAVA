package testing.randompicknostructures.logging;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import java.time.format.DateTimeFormatter;
import java.time.Instant;

// SLCO model ToadsAndFrogs.
public class ToadsAndFrogs {
    // The objects in the model.
    private final SLCO_Class[] objects;

    // Additional supporting variables.
    // Define and initialize the logger to gather the appropriate performance data with.
    private static final Logger logger;
    static {
        Properties props = System.getProperties();
        props.setProperty("log4j2.asyncLoggerRingBufferSize", "4194304");

        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "ToadsAndFrogs";
        String log_settings = "[NDS,T=30s,URP]";
        String log_file_size = "100MB";
        String compression_level = "3";
        String log_type = "logging";

        MainMapLookup.setMainArguments(
            "log_date", log_date,
            "log_settings", log_settings,
            "log_name", log_name,
            "log_file_size", log_file_size,
            "compression_level", compression_level,
            "log_type", log_type
        );
        logger = LogManager.getLogger();
    }

    // Interface for SLCO classes.
    interface SLCO_Class {
        void startThreads();
        void joinThreads();
    }

    ToadsAndFrogs() {
        // Instantiate the objects.
        objects = new SLCO_Class[] {
            new GlobalClass(
                4,
                0,
                8,
                new int[]{ 1, 1, 1, 1, 0, 2, 2, 2, 2 }
            )
        };
    }

    // Lock class to handle locks of global variables
    private static class LockManager {
        // The locks
        private final ReentrantLock[] locks;

        LockManager(int noVariables) {
            locks = new ReentrantLock[noVariables];
            for(int i = 0; i < locks.length; i++) {
                locks[i] = new ReentrantLock(true);
            }
        }

        // Lock method
        void acquire_locks(int[] lock_ids, int end) {
            Arrays.sort(lock_ids, 0, end);
            for (int i = 0; i < end; i++) {
                locks[lock_ids[i]].lock();
            }
        }

        // Unlock method
        void release_locks(int[] lock_ids, int end) {
            for (int i = 0; i < end; i++) {
                locks[lock_ids[i]].unlock();
            }
        }

        // Unlock method during exceptions
        void exception_unlock() {
            System.err.println("Exception encountered. Releasing all locks currently owned by " + Thread.currentThread().getName() + ".");
            for(ReentrantLock lock: locks) {
                while(lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    // Representation of the SLCO class GlobalClass.
    private static class GlobalClass implements SLCO_Class {
        // The state machine threads.
        private final Thread T_toad;
        private final Thread T_frog;
        private final Thread T_control;

        // Class variables.
        private volatile int y;
        private volatile int tmin;
        private volatile int fmax;
        private final int[] a;

        GlobalClass(int y, int tmin, int fmax, int[] a) {
            // Create a lock manager.
            LockManager lockManager = new LockManager(12);

            // Instantiate the class variables.
            this.y = y;
            this.tmin = tmin;
            this.fmax = fmax;
            this.a = a;

            // Instantiate the state machine threads and pass on the class' lock manager.
            T_toad = new GlobalClass_toadThread(lockManager);
            T_frog = new GlobalClass_frogThread(lockManager);
            T_control = new GlobalClass_controlThread(lockManager);
        }

        // Define the states fot the state machine toad.
        interface GlobalClass_toadThread_States {
            enum States {
                q
            }
        }

        // Representation of the SLCO state machine toad.
        class GlobalClass_toadThread extends Thread implements GlobalClass_toadThread_States {
            // Current state
            private GlobalClass_toadThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_toadThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_toadThread.States.q;
                lockManager = lockManagerInstance;
                lock_ids = new int[5];
                target_locks = new int[5];
                random = new Random();
            }

            // SLCO expression wrapper | y > 0.
            private boolean t_q_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y > 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | tmin != y - 1.
            private boolean t_q_0_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                if(tmin != y - 1) {
                    lock_ids[0] = target_locks[1]; // Release tmin
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 1.
            private boolean t_q_0_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y - 1; // Acquire a[y - 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lockManager.acquire_locks(lock_ids, 2);
                if(a[y - 1] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y - 1]
                lock_ids[2] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:0) | q -> q | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0].
            private boolean execute_transition_q_0() {
                // SLCO composite | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0].
                // SLCO expression | y > 0 and tmin != y - 1 and a[y - 1] = 1.
                if(!(t_q_0_s_0_n_0() && t_q_0_s_0_n_1() && t_q_0_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y - 1.
                y = y - 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y > 0.
            private boolean t_q_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y > 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | tmin = y - 1.
            private boolean t_q_1_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                if(tmin == y - 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 1.
            private boolean t_q_1_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y - 1; // Acquire a[y - 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lockManager.acquire_locks(lock_ids, 2);
                if(a[y - 1] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lock_ids[2] = target_locks[2]; // Release a[y - 1]
                lock_ids[3] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:1) | q -> q | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0].
            private boolean execute_transition_q_1() {
                // SLCO composite | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0].
                // SLCO expression | y > 0 and tmin = y - 1 and a[y - 1] = 1.
                if(!(t_q_1_s_0_n_0() && t_q_1_s_0_n_1() && t_q_1_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | tmin := y.
                tmin = y;
                lock_ids[0] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y - 1.
                y = y - 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y > 1.
            private boolean t_q_2_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y > 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | tmin != y - 2.
            private boolean t_q_2_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                if(tmin != y - 2) {
                    lock_ids[0] = target_locks[1]; // Release tmin
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y - 2] = 1.
            private boolean t_q_2_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y - 1; // Acquire a[y - 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lock_ids[2] = target_locks[4] = 3 + y - 2; // Acquire a[y - 2]
                lockManager.acquire_locks(lock_ids, 3);
                if(a[y - 2] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y - 1]
                lock_ids[2] = target_locks[3]; // Release a[y]
                lock_ids[3] = target_locks[4]; // Release a[y - 2]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_q_2_s_0_n_3() {
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[2]; // Release a[y - 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y - 1]
                lock_ids[2] = target_locks[3]; // Release a[y]
                lock_ids[3] = target_locks[4]; // Release a[y - 2]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:2) | q -> q | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0].
            private boolean execute_transition_q_2() {
                // SLCO composite | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0].
                // SLCO expression | y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2.
                if(!(t_q_2_s_0_n_0() && t_q_2_s_0_n_1() && t_q_2_s_0_n_2() && t_q_2_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y - 2.
                y = y - 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[4]; // Release a[y - 2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y > 1.
            private boolean t_q_3_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y > 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | tmin = y - 2.
            private boolean t_q_3_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                if(tmin == y - 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y - 2] = 1.
            private boolean t_q_3_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y - 1; // Acquire a[y - 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lock_ids[2] = target_locks[4] = 3 + y - 2; // Acquire a[y - 2]
                lockManager.acquire_locks(lock_ids, 3);
                if(a[y - 2] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lock_ids[2] = target_locks[2]; // Release a[y - 1]
                lock_ids[3] = target_locks[3]; // Release a[y]
                lock_ids[4] = target_locks[4]; // Release a[y - 2]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_q_3_s_0_n_3() {
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[2]; // Release a[y - 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lock_ids[2] = target_locks[2]; // Release a[y - 1]
                lock_ids[3] = target_locks[3]; // Release a[y]
                lock_ids[4] = target_locks[4]; // Release a[y - 2]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO transition (p:0, id:3) | q -> q | [y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; tmin := y; y := y - 2; a[y] := 0].
            private boolean execute_transition_q_3() {
                // SLCO composite | [y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; tmin := y; y := y - 2; a[y] := 0].
                // SLCO expression | y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2.
                if(!(t_q_3_s_0_n_0() && t_q_3_s_0_n_1() && t_q_3_s_0_n_2() && t_q_3_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | tmin := y.
                tmin = y;
                lock_ids[0] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y - 2.
                y = y - 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[4]; // Release a[y - 2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // Attempt to fire a transition starting in state q.
            private void exec_q() {
                logger.info("D00.O");
                // [N_DET.START]
                switch(random.nextInt(4)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | q -> q | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0].
                        logger.info("T00.O");
                        if(execute_transition_q_0()) {
                            logger.info("T00.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | q -> q | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0].
                        logger.info("T01.O");
                        if(execute_transition_q_1()) {
                            logger.info("T01.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | q -> q | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0].
                        logger.info("T02.O");
                        if(execute_transition_q_2()) {
                            logger.info("T02.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | q -> q | [y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; tmin := y; y := y - 2; a[y] := 0].
                        logger.info("T03.O");
                        if(execute_transition_q_3()) {
                            logger.info("T03.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case q -> exec_q();
                    }
                }
            }

            // The thread's run method.
            public void run() {
                try {
                    exec();
                } catch(Exception e) {
                    lockManager.exception_unlock();
                    throw e;
                }
            }
        }

        // Define the states fot the state machine frog.
        interface GlobalClass_frogThread_States {
            enum States {
                q
            }
        }

        // Representation of the SLCO state machine frog.
        class GlobalClass_frogThread extends Thread implements GlobalClass_frogThread_States {
            // Current state
            private GlobalClass_frogThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_frogThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_frogThread.States.q;
                lockManager = lockManagerInstance;
                lock_ids = new int[5];
                target_locks = new int[5];
                random = new Random();
            }

            // SLCO expression wrapper | y < 8.
            private boolean t_q_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y < 8) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | fmax != y + 1.
            private boolean t_q_0_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                if(fmax != y + 1) {
                    lock_ids[0] = target_locks[1]; // Release fmax
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 2.
            private boolean t_q_0_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lockManager.acquire_locks(lock_ids, 2);
                if(a[y + 1] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 1]
                lock_ids[2] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:0) | q -> q | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0].
            private boolean execute_transition_q_0() {
                // SLCO composite | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0].
                // SLCO expression | y < 8 and fmax != y + 1 and a[y + 1] = 2.
                if(!(t_q_0_s_0_n_0() && t_q_0_s_0_n_1() && t_q_0_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y + 1.
                y = y + 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y < 8.
            private boolean t_q_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y < 8) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | fmax = y + 1.
            private boolean t_q_1_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                if(fmax == y + 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 2.
            private boolean t_q_1_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lockManager.acquire_locks(lock_ids, 2);
                if(a[y + 1] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lock_ids[2] = target_locks[2]; // Release a[y + 1]
                lock_ids[3] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:1) | q -> q | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0].
            private boolean execute_transition_q_1() {
                // SLCO composite | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0].
                // SLCO expression | y < 8 and fmax = y + 1 and a[y + 1] = 2.
                if(!(t_q_1_s_0_n_0() && t_q_1_s_0_n_1() && t_q_1_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | fmax := y.
                fmax = y;
                lock_ids[0] = target_locks[1]; // Release fmax
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y + 1.
                y = y + 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y < 7.
            private boolean t_q_2_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y < 7) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | fmax != y + 2.
            private boolean t_q_2_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                if(fmax != y + 2) {
                    lock_ids[0] = target_locks[1]; // Release fmax
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_q_2_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lock_ids[2] = target_locks[4] = 3 + y + 2; // Acquire a[y + 2]
                lockManager.acquire_locks(lock_ids, 3);
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[2]; // Release a[y + 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 1]
                lock_ids[2] = target_locks[3]; // Release a[y]
                lock_ids[3] = target_locks[4]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 2.
            private boolean t_q_2_s_0_n_3() {
                if(a[y + 2] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[3]; // Release a[y]
                lock_ids[2] = target_locks[4]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:2) | q -> q | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0].
            private boolean execute_transition_q_2() {
                // SLCO composite | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0].
                // SLCO expression | y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2.
                if(!(t_q_2_s_0_n_0() && t_q_2_s_0_n_1() && t_q_2_s_0_n_2() && t_q_2_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y + 2.
                y = y + 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[4]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y < 7.
            private boolean t_q_3_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y < 7) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | fmax = y + 2.
            private boolean t_q_3_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                if(fmax == y + 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_q_3_s_0_n_2() {
                lock_ids[0] = target_locks[2] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[3] = 3 + y; // Acquire a[y]
                lock_ids[2] = target_locks[4] = 3 + y + 2; // Acquire a[y + 2]
                lockManager.acquire_locks(lock_ids, 3);
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[2]; // Release a[y + 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lock_ids[2] = target_locks[2]; // Release a[y + 1]
                lock_ids[3] = target_locks[3]; // Release a[y]
                lock_ids[4] = target_locks[4]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 2.
            private boolean t_q_3_s_0_n_3() {
                if(a[y + 2] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release fmax
                lock_ids[2] = target_locks[3]; // Release a[y]
                lock_ids[3] = target_locks[4]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:3) | q -> q | [y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; fmax := y; y := y + 2; a[y] := 0].
            private boolean execute_transition_q_3() {
                // SLCO composite | [y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; fmax := y; y := y + 2; a[y] := 0].
                // SLCO expression | y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2.
                if(!(t_q_3_s_0_n_0() && t_q_3_s_0_n_1() && t_q_3_s_0_n_2() && t_q_3_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                lock_ids[0] = target_locks[3]; // Release a[y]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | fmax := y.
                fmax = y;
                lock_ids[0] = target_locks[1]; // Release fmax
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | y := y + 2.
                y = y + 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[4]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // Attempt to fire a transition starting in state q.
            private void exec_q() {
                logger.info("D01.O");
                // [N_DET.START]
                switch(random.nextInt(4)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | q -> q | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0].
                        logger.info("T04.O");
                        if(execute_transition_q_0()) {
                            logger.info("T04.S");
                            logger.info("D01.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | q -> q | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0].
                        logger.info("T05.O");
                        if(execute_transition_q_1()) {
                            logger.info("T05.S");
                            logger.info("D01.S");
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | q -> q | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0].
                        logger.info("T06.O");
                        if(execute_transition_q_2()) {
                            logger.info("T06.S");
                            logger.info("D01.S");
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | q -> q | [y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; fmax := y; y := y + 2; a[y] := 0].
                        logger.info("T07.O");
                        if(execute_transition_q_3()) {
                            logger.info("T07.S");
                            logger.info("D01.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case q -> exec_q();
                    }
                }
            }

            // The thread's run method.
            public void run() {
                try {
                    exec();
                } catch(Exception e) {
                    lockManager.exception_unlock();
                    throw e;
                }
            }
        }

        // Define the states fot the state machine control.
        interface GlobalClass_controlThread_States {
            enum States {
                running, 
                done, 
                success, 
                failure, 
                reset
            }
        }

        // Representation of the SLCO state machine control.
        class GlobalClass_controlThread extends Thread implements GlobalClass_controlThread_States {
            // Current state
            private GlobalClass_controlThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_controlThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_controlThread.States.running;
                lockManager = lockManagerInstance;
                lock_ids = new int[5];
                target_locks = new int[12];
                random = new Random();
            }

            // SLCO expression wrapper | y = 0.
            private boolean t_running_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_running_0_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[2] = 3 + y + 2; // Acquire a[y + 2]
                lockManager.acquire_locks(lock_ids, 2);
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[1]; // Release a[y + 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[2]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 1.
            private boolean t_running_0_s_0_n_2() {
                if(a[y + 2] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[2]; // Release a[y + 2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:0) | running -> done | y = 0 and a[y + 1] = 1 and a[y + 2] = 1.
            private boolean execute_transition_running_0() {
                // SLCO expression | y = 0 and a[y + 1] = 1 and a[y + 2] = 1.
                if(!(t_running_0_s_0_n_0() && t_running_0_s_0_n_1() && t_running_0_s_0_n_2())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | y = 1.
            private boolean t_running_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_running_1_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[2] = 3 + y + 2; // Acquire a[y + 2]
                lock_ids[2] = target_locks[4] = 3 + y - 1; // Acquire a[y - 1]
                lockManager.acquire_locks(lock_ids, 3);
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[4]; // Release a[y - 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[2]; // Release a[y + 2]
                lock_ids[3] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_running_1_s_0_n_2() {
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[1]; // Release a[y + 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[2]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 1.
            private boolean t_running_1_s_0_n_3() {
                if(a[y + 2] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[2]; // Release a[y + 2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | running -> done | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
            private boolean execute_transition_running_1() {
                // SLCO expression | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                if(!(t_running_1_s_0_n_0() && t_running_1_s_0_n_1() && t_running_1_s_0_n_2() && t_running_1_s_0_n_3())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | y = 7.
            private boolean t_running_2_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y == 7) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 2] = 2.
            private boolean t_running_2_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[3] = 3 + y - 2; // Acquire a[y - 2]
                lock_ids[2] = target_locks[4] = 3 + y - 1; // Acquire a[y - 1]
                lockManager.acquire_locks(lock_ids, 3);
                if(a[y - 2] == 2) {
                    lock_ids[0] = target_locks[3]; // Release a[y - 2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[3]; // Release a[y - 2]
                lock_ids[3] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_running_2_s_0_n_2() {
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[4]; // Release a[y - 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_running_2_s_0_n_3() {
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[1]; // Release a[y + 1]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | running -> done | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1.
            private boolean execute_transition_running_2() {
                // SLCO expression | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1.
                if(!(t_running_2_s_0_n_0() && t_running_2_s_0_n_1() && t_running_2_s_0_n_2() && t_running_2_s_0_n_3())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | y = 8.
            private boolean t_running_3_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y == 8) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 2] = 2.
            private boolean t_running_3_s_0_n_1() {
                lock_ids[0] = target_locks[3] = 3 + y - 2; // Acquire a[y - 2]
                lock_ids[1] = target_locks[4] = 3 + y - 1; // Acquire a[y - 1]
                lockManager.acquire_locks(lock_ids, 2);
                if(a[y - 2] == 2) {
                    lock_ids[0] = target_locks[3]; // Release a[y - 2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[3]; // Release a[y - 2]
                lock_ids[2] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_running_3_s_0_n_2() {
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[4]; // Release a[y - 1]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:3) | running -> done | y = 8 and a[y - 2] = 2 and a[y - 1] = 2.
            private boolean execute_transition_running_3() {
                // SLCO expression | y = 8 and a[y - 2] = 2 and a[y - 1] = 2.
                if(!(t_running_3_s_0_n_0() && t_running_3_s_0_n_1() && t_running_3_s_0_n_2())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | y > 1.
            private boolean t_running_4_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                if(y > 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | y < 7.
            private boolean t_running_4_s_0_n_1() {
                if(y < 7) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 2] = 2.
            private boolean t_running_4_s_0_n_2() {
                lock_ids[0] = target_locks[1] = 3 + y + 1; // Acquire a[y + 1]
                lock_ids[1] = target_locks[2] = 3 + y + 2; // Acquire a[y + 2]
                lock_ids[2] = target_locks[3] = 3 + y - 2; // Acquire a[y - 2]
                lock_ids[3] = target_locks[4] = 3 + y - 1; // Acquire a[y - 1]
                lockManager.acquire_locks(lock_ids, 4);
                if(a[y - 2] == 2) {
                    lock_ids[0] = target_locks[3]; // Release a[y - 2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[2]; // Release a[y + 2]
                lock_ids[3] = target_locks[3]; // Release a[y - 2]
                lock_ids[4] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_running_4_s_0_n_3() {
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[4]; // Release a[y - 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[2]; // Release a[y + 2]
                lock_ids[3] = target_locks[4]; // Release a[y - 1]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_running_4_s_0_n_4() {
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[1]; // Release a[y + 1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release a[y + 1]
                lock_ids[2] = target_locks[2]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 1.
            private boolean t_running_4_s_0_n_5() {
                if(a[y + 2] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[2]; // Release a[y + 2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release a[y + 2]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:4) | running -> done | y > 1 and y < 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
            private boolean execute_transition_running_4() {
                // SLCO expression | y > 1 and y < 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                if(!(t_running_4_s_0_n_0() && t_running_4_s_0_n_1() && t_running_4_s_0_n_2() && t_running_4_s_0_n_3() && t_running_4_s_0_n_4() && t_running_4_s_0_n_5())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | tmin > y.
            private boolean t_done_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                if(tmin > y) {
                    lock_ids[0] = target_locks[1]; // Release tmin
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | fmax < y.
            private boolean t_done_0_s_0_n_1() {
                lock_ids[0] = target_locks[2] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                if(fmax < y) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[2]; // Release fmax
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release fmax
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:0) | done -> success | tmin > y and fmax < y.
            private boolean execute_transition_done_0() {
                // SLCO expression | tmin > y and fmax < y.
                if(!(t_done_0_s_0_n_0() && t_done_0_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.success;
                return true;
            }

            // SLCO expression wrapper | tmin > y.
            private boolean t_done_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                if(tmin > y) {
                    lock_ids[0] = target_locks[1]; // Release tmin
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | fmax < y.
            private boolean t_done_1_s_0_n_1() {
                lock_ids[0] = target_locks[2] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                if(fmax < y) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lock_ids[1] = target_locks[2]; // Release fmax
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lock_ids[1] = target_locks[2]; // Release fmax
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | done -> failure | !(tmin > y and fmax < y).
            private boolean execute_transition_done_1() {
                // SLCO expression | !(tmin > y and fmax < y).
                if(!(!((t_done_1_s_0_n_0() && t_done_1_s_0_n_1())))) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.failure;
                return true;
            }

            // SLCO transition (p:0, id:0) | success -> reset | true.
            private boolean execute_transition_success_0() {
                // (Superfluous) SLCO expression | true.

                currentState = GlobalClass_controlThread.States.reset;
                return true;
            }

            // SLCO transition (p:0, id:0) | failure -> reset | true.
            private boolean execute_transition_failure_0() {
                // (Superfluous) SLCO expression | true.

                currentState = GlobalClass_controlThread.States.reset;
                return true;
            }

            // SLCO transition (p:0, id:0) | reset -> running | true | [true; y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2].
            private boolean execute_transition_reset_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2] -> [true; y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | y := 4.
                lock_ids[0] = target_locks[0] = 0; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                y = 4;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | tmin := 0.
                lock_ids[0] = target_locks[1] = 1; // Acquire tmin
                lockManager.acquire_locks(lock_ids, 1);
                tmin = 0;
                lock_ids[0] = target_locks[1]; // Release tmin
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | fmax := 8.
                lock_ids[0] = target_locks[2] = 2; // Acquire fmax
                lockManager.acquire_locks(lock_ids, 1);
                fmax = 8;
                lock_ids[0] = target_locks[2]; // Release fmax
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[4] := 0.
                lock_ids[0] = target_locks[3] = 3 + 1; // Acquire a[1]
                lock_ids[1] = target_locks[4] = 3 + 3; // Acquire a[3]
                lock_ids[2] = target_locks[5] = 3 + 0; // Acquire a[0]
                lock_ids[3] = target_locks[6] = 3 + 2; // Acquire a[2]
                lock_ids[4] = target_locks[7] = 3 + 4; // Acquire a[4]
                lockManager.acquire_locks(lock_ids, 5);
                a[4] = 0;
                lock_ids[0] = target_locks[7]; // Release a[4]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[0] := 1.
                a[0] = 1;
                lock_ids[0] = target_locks[5]; // Release a[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[1] := 1.
                a[1] = 1;
                lock_ids[0] = target_locks[3]; // Release a[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[2] := 1.
                a[2] = 1;
                lock_ids[0] = target_locks[6]; // Release a[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[3] := 1.
                a[3] = 1;
                lock_ids[0] = target_locks[4]; // Release a[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[5] := 2.
                lock_ids[0] = target_locks[8] = 3 + 5; // Acquire a[5]
                lockManager.acquire_locks(lock_ids, 1);
                a[5] = 2;
                lock_ids[0] = target_locks[8]; // Release a[5]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[6] := 2.
                lock_ids[0] = target_locks[9] = 3 + 6; // Acquire a[6]
                lockManager.acquire_locks(lock_ids, 1);
                a[6] = 2;
                lock_ids[0] = target_locks[9]; // Release a[6]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[7] := 2.
                lock_ids[0] = target_locks[10] = 3 + 7; // Acquire a[7]
                lockManager.acquire_locks(lock_ids, 1);
                a[7] = 2;
                lock_ids[0] = target_locks[10]; // Release a[7]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | a[8] := 2.
                lock_ids[0] = target_locks[11] = 3 + 8; // Acquire a[8]
                lockManager.acquire_locks(lock_ids, 1);
                a[8] = 2;
                lock_ids[0] = target_locks[11]; // Release a[8]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_controlThread.States.running;
                return true;
            }

            // Attempt to fire a transition starting in state running.
            private void exec_running() {
                logger.info("D02.O");
                // [N_DET.START]
                switch(random.nextInt(5)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | running -> done | y = 0 and a[y + 1] = 1 and a[y + 2] = 1.
                        logger.info("T08.O");
                        if(execute_transition_running_0()) {
                            logger.info("T08.S");
                            logger.info("D02.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | running -> done | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                        logger.info("T09.O");
                        if(execute_transition_running_1()) {
                            logger.info("T09.S");
                            logger.info("D02.S");
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | running -> done | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1.
                        logger.info("T10.O");
                        if(execute_transition_running_2()) {
                            logger.info("T10.S");
                            logger.info("D02.S");
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | running -> done | y = 8 and a[y - 2] = 2 and a[y - 1] = 2.
                        logger.info("T11.O");
                        if(execute_transition_running_3()) {
                            logger.info("T11.S");
                            logger.info("D02.S");
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | running -> done | y > 1 and y < 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                        logger.info("T12.O");
                        if(execute_transition_running_4()) {
                            logger.info("T12.S");
                            logger.info("D02.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state done.
            private void exec_done() {
                logger.info("D03.O");
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | done -> success | tmin > y and fmax < y.
                        logger.info("T13.O");
                        if(execute_transition_done_0()) {
                            logger.info("T13.S");
                            logger.info("D03.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | done -> failure | !(tmin > y and fmax < y).
                        logger.info("T14.O");
                        if(execute_transition_done_1()) {
                            logger.info("T14.S");
                            logger.info("D03.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state success.
            private void exec_success() {
                logger.info("D04.O");
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | success -> reset | true.
                logger.info("T15.O");
                if(execute_transition_success_0()) {
                    logger.info("T15.S");
                    logger.info("D04.S");
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state failure.
            private void exec_failure() {
                logger.info("D05.O");
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | failure -> reset | true.
                logger.info("T16.O");
                if(execute_transition_failure_0()) {
                    logger.info("T16.S");
                    logger.info("D05.S");
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state reset.
            private void exec_reset() {
                logger.info("D06.O");
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | reset -> running | true | [true; y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2].
                logger.info("T17.O");
                if(execute_transition_reset_0()) {
                    logger.info("T17.S");
                    logger.info("D06.S");
                    return;
                }
                // [N_DET.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case running -> exec_running();
                        case done -> exec_done();
                        case success -> exec_success();
                        case failure -> exec_failure();
                        case reset -> exec_reset();
                    }
                }
            }

            // The thread's run method.
            public void run() {
                try {
                    exec();
                } catch(Exception e) {
                    lockManager.exception_unlock();
                    throw e;
                }
            }
        }

        // Start all threads.
        public void startThreads() {
            T_toad.start();
            T_frog.start();
            T_control.start();
        }

        // Join all threads.
        public void joinThreads() {
            while (true) {
                try {
                    T_toad.join();
                    T_frog.join();
                    T_control.join();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Start all threads.
    private void startThreads() {
        for(SLCO_Class object : objects) {
            object.startThreads();
        }
    }

    // Join all threads.
    private void joinThreads() {
        for(SLCO_Class object : objects) {
            object.joinThreads();
        }
    }

    // Run the application.
    public static void main(String[] args) {
        // Initialize the model and execute.
        ToadsAndFrogs model = new ToadsAndFrogs();
        model.startThreads();
        model.joinThreads();

        // Include information about the model.
        logger.info("JSON {\"name\": \"ToadsAndFrogs\", \"settings\": \"test_models/Toads.slco -use_random_pick -no_deterministic_structures -running_time=30 -package_name=testing.randompicknostructures -performance_measurements\", \"classes\": {\"GlobalClass\": {\"name\": \"GlobalClass\", \"state_machines\": {\"toad\": {\"name\": \"toad\", \"states\": [\"q\"], \"decision_structures\": {\"q\": {\"source\": \"q\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | q -> q | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0]\", \"id\": \"T00\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | q -> q | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0]\", \"id\": \"T01\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | q -> q | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0]\", \"id\": \"T02\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | q -> q | [y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; tmin := y; y := y - 2; a[y] := 0]\", \"id\": \"T03\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}}}}}, \"frog\": {\"name\": \"frog\", \"states\": [\"q\"], \"decision_structures\": {\"q\": {\"source\": \"q\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | q -> q | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0]\", \"id\": \"T04\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | q -> q | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0]\", \"id\": \"T05\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | q -> q | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0]\", \"id\": \"T06\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | q -> q | [y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; fmax := y; y := y + 2; a[y] := 0]\", \"id\": \"T07\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}}}}}, \"control\": {\"name\": \"control\", \"states\": [\"running\", \"done\", \"success\", \"failure\", \"reset\"], \"decision_structures\": {\"running\": {\"source\": \"running\", \"id\": \"D02\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | running -> done | y = 0 and a[y + 1] = 1 and a[y + 2] = 1\", \"id\": \"T08\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | running -> done | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1\", \"id\": \"T09\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | running -> done | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1\", \"id\": \"T10\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | running -> done | y = 8 and a[y - 2] = 2 and a[y - 1] = 2\", \"id\": \"T11\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | running -> done | y > 1 and y < 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1\", \"id\": \"T12\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}}}, \"done\": {\"source\": \"done\", \"id\": \"D03\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | done -> success | tmin > y and fmax < y\", \"id\": \"T13\", \"source\": \"done\", \"target\": \"success\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | done -> failure | !(tmin > y and fmax < y)\", \"id\": \"T14\", \"source\": \"done\", \"target\": \"failure\", \"priority\": 0, \"is_excluded\": false}}}, \"success\": {\"source\": \"success\", \"id\": \"D04\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | success -> reset | true\", \"id\": \"T15\", \"source\": \"success\", \"target\": \"reset\", \"priority\": 0, \"is_excluded\": false}}}, \"failure\": {\"source\": \"failure\", \"id\": \"D05\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | failure -> reset | true\", \"id\": \"T16\", \"source\": \"failure\", \"target\": \"reset\", \"priority\": 0, \"is_excluded\": false}}}, \"reset\": {\"source\": \"reset\", \"id\": \"D06\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | reset -> running | true | [true; y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2]\", \"id\": \"T17\", \"source\": \"reset\", \"target\": \"running\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
        // Give the logger time to finish asynchronous tasks.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Force a rollover to take place.
        LoggerContext context = LoggerContext.getContext(false);
        Appender appender = context.getConfiguration().getAppender("RollingRandomAccessFile");
        if (appender instanceof RollingRandomAccessFileAppender) {
            ((RollingRandomAccessFileAppender) appender).getManager().rollover();
        }
        // Give the logger time to finish asynchronous tasks.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}