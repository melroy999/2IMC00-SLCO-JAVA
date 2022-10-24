package testing.statement.counting;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        String log_settings = "[SLL,T=30s]";
        String log_file_size = "100MB";
        String compression_level = "3";
        String log_type = "counting";

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

            // Add variables needed for measurements.
            private long D00_O;
            private long D00_F;
            private long D00_S;
            private long T00_O;
            private long T00_F;
            private long T00_S;
            private long T01_O;
            private long T01_F;
            private long T01_S;
            private long T02_O;
            private long T02_F;
            private long T02_S;
            private long T03_O;
            private long T03_F;
            private long T03_S;

            GlobalClass_toadThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_toadThread.States.q;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[1];
                random = new Random();
            }

            // SLCO expression wrapper | y > 0.
            private boolean t_q_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 11; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                return y > 0;
            }

            // SLCO transition (p:0, id:0) | q -> q | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0].
            private boolean execute_transition_q_0() {
                // SLCO composite | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0].
                // SLCO expression | y > 0 and tmin != y - 1 and a[y - 1] = 1.
                if(!(t_q_0_s_0_n_0() && tmin != y - 1 && a[y - 1] == 1)) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                // SLCO assignment | y := y - 1.
                y = y - 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // SLCO transition (p:0, id:1) | q -> q | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0].
            private boolean execute_transition_q_1() {
                // SLCO composite | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0].
                // SLCO expression | y > 0 and tmin = y - 1 and a[y - 1] = 1.
                if(!(y > 0 && tmin == y - 1 && a[y - 1] == 1)) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                // SLCO assignment | tmin := y.
                tmin = y;
                // SLCO assignment | y := y - 1.
                y = y - 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // SLCO transition (p:0, id:2) | q -> q | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0].
            private boolean execute_transition_q_2() {
                // SLCO composite | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0].
                // SLCO expression | y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2.
                if(!(y > 1 && tmin != y - 2 && a[y - 2] == 1 && a[y - 1] == 2)) {
                    return false;
                }
                // SLCO assignment | a[y] := 1.
                a[y] = 1;
                // SLCO assignment | y := y - 2.
                y = y - 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y > 1.
            private boolean t_q_3_s_0_n_0() {
                if(y > 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | tmin = y - 2.
            private boolean t_q_3_s_0_n_1() {
                if(tmin == y - 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 2] = 1.
            private boolean t_q_3_s_0_n_2() {
                if(a[y - 2] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_q_3_s_0_n_3() {
                if(a[y - 1] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
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
                // SLCO assignment | tmin := y.
                tmin = y;
                // SLCO assignment | y := y - 2.
                y = y - 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_toadThread.States.q;
                return true;
            }

            // Attempt to fire a transition starting in state q.
            private void exec_q() {
                D00_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | q -> q | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0].
                T00_O++;
                if(execute_transition_q_0()) {
                    T00_S++;
                    D00_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | q -> q | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0].
                T01_O++;
                if(execute_transition_q_1()) {
                    T01_S++;
                    D00_S++;
                    return;
                }
                // SLCO transition (p:0, id:2) | q -> q | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0].
                T02_O++;
                if(execute_transition_q_2()) {
                    T02_S++;
                    D00_S++;
                    return;
                }
                // SLCO transition (p:0, id:3) | q -> q | [y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; tmin := y; y := y - 2; a[y] := 0].
                T03_O++;
                if(execute_transition_q_3()) {
                    T03_S++;
                    D00_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case q -> exec_q();
                    }
                }

                // Report all counts.
                logger.info("D00.O " + D00_O);
                logger.info("D00.F " + D00_F);
                logger.info("D00.S " + D00_S);
                logger.info("T00.O " + T00_O);
                logger.info("T00.F " + T00_F);
                logger.info("T00.S " + T00_S);
                logger.info("T01.O " + T01_O);
                logger.info("T01.F " + T01_F);
                logger.info("T01.S " + T01_S);
                logger.info("T02.O " + T02_O);
                logger.info("T02.F " + T02_F);
                logger.info("T02.S " + T02_S);
                logger.info("T03.O " + T03_O);
                logger.info("T03.F " + T03_F);
                logger.info("T03.S " + T03_S);
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

            // Add variables needed for measurements.
            private long D01_O;
            private long D01_F;
            private long D01_S;
            private long T04_O;
            private long T04_F;
            private long T04_S;
            private long T05_O;
            private long T05_F;
            private long T05_S;
            private long T06_O;
            private long T06_F;
            private long T06_S;
            private long T07_O;
            private long T07_F;
            private long T07_S;

            GlobalClass_frogThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_frogThread.States.q;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[1];
                random = new Random();
            }

            // SLCO expression wrapper | y < 8.
            private boolean t_q_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 11; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                return y < 8;
            }

            // SLCO transition (p:0, id:0) | q -> q | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0].
            private boolean execute_transition_q_0() {
                // SLCO composite | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0].
                // SLCO expression | y < 8 and fmax != y + 1 and a[y + 1] = 2.
                if(!(t_q_0_s_0_n_0() && fmax != y + 1 && a[y + 1] == 2)) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                // SLCO assignment | y := y + 1.
                y = y + 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // SLCO transition (p:0, id:1) | q -> q | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0].
            private boolean execute_transition_q_1() {
                // SLCO composite | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0].
                // SLCO expression | y < 8 and fmax = y + 1 and a[y + 1] = 2.
                if(!(y < 8 && fmax == y + 1 && a[y + 1] == 2)) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                // SLCO assignment | fmax := y.
                fmax = y;
                // SLCO assignment | y := y + 1.
                y = y + 1;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // SLCO transition (p:0, id:2) | q -> q | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0].
            private boolean execute_transition_q_2() {
                // SLCO composite | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0].
                // SLCO expression | y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2.
                if(!(y < 7 && fmax != y + 2 && a[y + 1] == 1 && a[y + 2] == 2)) {
                    return false;
                }
                // SLCO assignment | a[y] := 2.
                a[y] = 2;
                // SLCO assignment | y := y + 2.
                y = y + 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // SLCO expression wrapper | y < 7.
            private boolean t_q_3_s_0_n_0() {
                if(y < 7) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | fmax = y + 2.
            private boolean t_q_3_s_0_n_1() {
                if(fmax == y + 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_q_3_s_0_n_2() {
                if(a[y + 1] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 2.
            private boolean t_q_3_s_0_n_3() {
                if(a[y + 2] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
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
                // SLCO assignment | fmax := y.
                fmax = y;
                // SLCO assignment | y := y + 2.
                y = y + 2;
                // SLCO assignment | a[y] := 0.
                a[y] = 0;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_frogThread.States.q;
                return true;
            }

            // Attempt to fire a transition starting in state q.
            private void exec_q() {
                D01_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | q -> q | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0].
                T04_O++;
                if(execute_transition_q_0()) {
                    T04_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | q -> q | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0].
                T05_O++;
                if(execute_transition_q_1()) {
                    T05_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:2) | q -> q | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0].
                T06_O++;
                if(execute_transition_q_2()) {
                    T06_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:3) | q -> q | [y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; fmax := y; y := y + 2; a[y] := 0].
                T07_O++;
                if(execute_transition_q_3()) {
                    T07_S++;
                    D01_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case q -> exec_q();
                    }
                }

                // Report all counts.
                logger.info("D01.O " + D01_O);
                logger.info("D01.F " + D01_F);
                logger.info("D01.S " + D01_S);
                logger.info("T04.O " + T04_O);
                logger.info("T04.F " + T04_F);
                logger.info("T04.S " + T04_S);
                logger.info("T05.O " + T05_O);
                logger.info("T05.F " + T05_F);
                logger.info("T05.S " + T05_S);
                logger.info("T06.O " + T06_O);
                logger.info("T06.F " + T06_F);
                logger.info("T06.S " + T06_S);
                logger.info("T07.O " + T07_O);
                logger.info("T07.F " + T07_F);
                logger.info("T07.S " + T07_S);
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

            // Add variables needed for measurements.
            private long D02_O;
            private long D02_F;
            private long D02_S;
            private long T08_O;
            private long T08_F;
            private long T08_S;
            private long T09_O;
            private long T09_F;
            private long T09_S;
            private long T10_O;
            private long T10_F;
            private long T10_S;
            private long T11_O;
            private long T11_F;
            private long T11_S;
            private long T12_O;
            private long T12_F;
            private long T12_S;
            private long D03_O;
            private long D03_F;
            private long D03_S;
            private long T13_O;
            private long T13_F;
            private long T13_S;
            private long T14_O;
            private long T14_F;
            private long T14_S;
            private long D04_O;
            private long D04_F;
            private long D04_S;
            private long T15_O;
            private long T15_F;
            private long T15_S;
            private long D05_O;
            private long D05_F;
            private long D05_S;
            private long T16_O;
            private long T16_F;
            private long T16_S;
            private long D06_O;
            private long D06_F;
            private long D06_S;
            private long T17_O;
            private long T17_F;
            private long T17_S;

            GlobalClass_controlThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_controlThread.States.running;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[1];
                random = new Random();
            }

            // SLCO expression wrapper | y = 0.
            private boolean t_running_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 11; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                return y == 0;
            }

            // SLCO expression wrapper | a[y + 2] = 1.
            private boolean t_running_0_s_0_n_1() {
                if(a[y + 2] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | running -> done | y = 0 and a[y + 1] = 1 and a[y + 2] = 1.
            private boolean execute_transition_running_0() {
                // SLCO expression | y = 0 and a[y + 1] = 1 and a[y + 2] = 1.
                if(!(t_running_0_s_0_n_0() && a[y + 1] == 1 && t_running_0_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | a[y + 2] = 1.
            private boolean t_running_1_s_0_n_0() {
                if(a[y + 2] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | running -> done | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
            private boolean execute_transition_running_1() {
                // SLCO expression | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                if(!(y == 1 && a[y - 1] == 2 && a[y + 1] == 1 && t_running_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_running_2_s_0_n_0() {
                if(a[y + 1] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:2) | running -> done | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1.
            private boolean execute_transition_running_2() {
                // SLCO expression | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1.
                if(!(y == 7 && a[y - 2] == 2 && a[y - 1] == 2 && t_running_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_running_3_s_0_n_0() {
                if(a[y - 1] == 2) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:3) | running -> done | y = 8 and a[y - 2] = 2 and a[y - 1] = 2.
            private boolean execute_transition_running_3() {
                // SLCO expression | y = 8 and a[y - 2] = 2 and a[y - 1] = 2.
                if(!(y == 8 && a[y - 2] == 2 && t_running_3_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_controlThread.States.done;
                return true;
            }

            // SLCO expression wrapper | y > 1.
            private boolean t_running_4_s_0_n_0() {
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
                if(a[y - 2] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y - 1] = 2.
            private boolean t_running_4_s_0_n_3() {
                if(a[y - 1] == 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y + 1] = 1.
            private boolean t_running_4_s_0_n_4() {
                if(a[y + 1] == 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | a[y + 2] = 1.
            private boolean t_running_4_s_0_n_5() {
                if(a[y + 2] == 1) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
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
                lock_ids[0] = target_locks[0] = 11; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                return tmin > y;
            }

            // SLCO expression wrapper | fmax < y.
            private boolean t_done_0_s_0_n_1() {
                if(fmax < y) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
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
                if(tmin > y) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | fmax < y.
            private boolean t_done_1_s_0_n_1() {
                if(fmax < y) {
                    lock_ids[0] = target_locks[0]; // Release y
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);
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
                lock_ids[0] = target_locks[0] = 11; // Acquire y
                lockManager.acquire_locks(lock_ids, 1);
                y = 4;
                // SLCO assignment | tmin := 0.
                tmin = 0;
                // SLCO assignment | fmax := 8.
                fmax = 8;
                // SLCO assignment | a[4] := 0.
                a[4] = 0;
                // SLCO assignment | a[0] := 1.
                a[0] = 1;
                // SLCO assignment | a[1] := 1.
                a[1] = 1;
                // SLCO assignment | a[2] := 1.
                a[2] = 1;
                // SLCO assignment | a[3] := 1.
                a[3] = 1;
                // SLCO assignment | a[5] := 2.
                a[5] = 2;
                // SLCO assignment | a[6] := 2.
                a[6] = 2;
                // SLCO assignment | a[7] := 2.
                a[7] = 2;
                // SLCO assignment | a[8] := 2.
                a[8] = 2;
                lock_ids[0] = target_locks[0]; // Release y
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_controlThread.States.running;
                return true;
            }

            // Attempt to fire a transition starting in state running.
            private void exec_running() {
                D02_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | running -> done | y = 0 and a[y + 1] = 1 and a[y + 2] = 1.
                T08_O++;
                if(execute_transition_running_0()) {
                    T08_S++;
                    D02_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | running -> done | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                T09_O++;
                if(execute_transition_running_1()) {
                    T09_S++;
                    D02_S++;
                    return;
                }
                // SLCO transition (p:0, id:2) | running -> done | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1.
                T10_O++;
                if(execute_transition_running_2()) {
                    T10_S++;
                    D02_S++;
                    return;
                }
                // SLCO transition (p:0, id:3) | running -> done | y = 8 and a[y - 2] = 2 and a[y - 1] = 2.
                T11_O++;
                if(execute_transition_running_3()) {
                    T11_S++;
                    D02_S++;
                    return;
                }
                // SLCO transition (p:0, id:4) | running -> done | y > 1 and y < 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1.
                T12_O++;
                if(execute_transition_running_4()) {
                    T12_S++;
                    D02_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state done.
            private void exec_done() {
                D03_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | done -> success | tmin > y and fmax < y.
                T13_O++;
                if(execute_transition_done_0()) {
                    T13_S++;
                    D03_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | done -> failure | !(tmin > y and fmax < y).
                T14_O++;
                if(execute_transition_done_1()) {
                    T14_S++;
                    D03_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state success.
            private void exec_success() {
                D04_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | success -> reset | true.
                T15_O++;
                if(execute_transition_success_0()) {
                    T15_S++;
                    D04_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state failure.
            private void exec_failure() {
                D05_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | failure -> reset | true.
                T16_O++;
                if(execute_transition_failure_0()) {
                    T16_S++;
                    D05_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state reset.
            private void exec_reset() {
                D06_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | reset -> running | true | [true; y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2].
                T17_O++;
                if(execute_transition_reset_0()) {
                    T17_S++;
                    D06_S++;
                    return;
                }
                // [SEQ.END]
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

                // Report all counts.
                logger.info("D02.O " + D02_O);
                logger.info("D02.F " + D02_F);
                logger.info("D02.S " + D02_S);
                logger.info("T08.O " + T08_O);
                logger.info("T08.F " + T08_F);
                logger.info("T08.S " + T08_S);
                logger.info("T09.O " + T09_O);
                logger.info("T09.F " + T09_F);
                logger.info("T09.S " + T09_S);
                logger.info("T10.O " + T10_O);
                logger.info("T10.F " + T10_F);
                logger.info("T10.S " + T10_S);
                logger.info("T11.O " + T11_O);
                logger.info("T11.F " + T11_F);
                logger.info("T11.S " + T11_S);
                logger.info("T12.O " + T12_O);
                logger.info("T12.F " + T12_F);
                logger.info("T12.S " + T12_S);
                logger.info("D03.O " + D03_O);
                logger.info("D03.F " + D03_F);
                logger.info("D03.S " + D03_S);
                logger.info("T13.O " + T13_O);
                logger.info("T13.F " + T13_F);
                logger.info("T13.S " + T13_S);
                logger.info("T14.O " + T14_O);
                logger.info("T14.F " + T14_F);
                logger.info("T14.S " + T14_S);
                logger.info("D04.O " + D04_O);
                logger.info("D04.F " + D04_F);
                logger.info("D04.S " + D04_S);
                logger.info("T15.O " + T15_O);
                logger.info("T15.F " + T15_F);
                logger.info("T15.S " + T15_S);
                logger.info("D05.O " + D05_O);
                logger.info("D05.F " + D05_F);
                logger.info("D05.S " + D05_S);
                logger.info("T16.O " + T16_O);
                logger.info("T16.F " + T16_F);
                logger.info("T16.S " + T16_S);
                logger.info("D06.O " + D06_O);
                logger.info("D06.F " + D06_F);
                logger.info("D06.S " + D06_S);
                logger.info("T17.O " + T17_O);
                logger.info("T17.F " + T17_F);
                logger.info("T17.S " + T17_S);
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
        logger.info("JSON {\"name\": \"ToadsAndFrogs\", \"settings\": \"test_models/Toads.slco -statement_level_locking -running_time=30 -package_name=testing.statement -performance_measurements -vercors_verification\", \"classes\": {\"GlobalClass\": {\"name\": \"GlobalClass\", \"state_machines\": {\"toad\": {\"name\": \"toad\", \"states\": [\"q\"], \"decision_structures\": {\"q\": {\"source\": \"q\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | q -> q | [y > 0 and tmin != y - 1 and a[y - 1] = 1; a[y] := 1; y := y - 1; a[y] := 0]\", \"id\": \"T00\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | q -> q | [y > 0 and tmin = y - 1 and a[y - 1] = 1; a[y] := 1; tmin := y; y := y - 1; a[y] := 0]\", \"id\": \"T01\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | q -> q | [y > 1 and tmin != y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; y := y - 2; a[y] := 0]\", \"id\": \"T02\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | q -> q | [y > 1 and tmin = y - 2 and a[y - 2] = 1 and a[y - 1] = 2; a[y] := 1; tmin := y; y := y - 2; a[y] := 0]\", \"id\": \"T03\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}}}}}, \"frog\": {\"name\": \"frog\", \"states\": [\"q\"], \"decision_structures\": {\"q\": {\"source\": \"q\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | q -> q | [y < 8 and fmax != y + 1 and a[y + 1] = 2; a[y] := 2; y := y + 1; a[y] := 0]\", \"id\": \"T04\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | q -> q | [y < 8 and fmax = y + 1 and a[y + 1] = 2; a[y] := 2; fmax := y; y := y + 1; a[y] := 0]\", \"id\": \"T05\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | q -> q | [y < 7 and fmax != y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; y := y + 2; a[y] := 0]\", \"id\": \"T06\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | q -> q | [y < 7 and fmax = y + 2 and a[y + 1] = 1 and a[y + 2] = 2; a[y] := 2; fmax := y; y := y + 2; a[y] := 0]\", \"id\": \"T07\", \"source\": \"q\", \"target\": \"q\", \"priority\": 0, \"is_excluded\": false}}}}}, \"control\": {\"name\": \"control\", \"states\": [\"running\", \"done\", \"success\", \"failure\", \"reset\"], \"decision_structures\": {\"running\": {\"source\": \"running\", \"id\": \"D02\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | running -> done | y = 0 and a[y + 1] = 1 and a[y + 2] = 1\", \"id\": \"T08\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | running -> done | y = 1 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1\", \"id\": \"T09\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | running -> done | y = 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1\", \"id\": \"T10\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | running -> done | y = 8 and a[y - 2] = 2 and a[y - 1] = 2\", \"id\": \"T11\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | running -> done | y > 1 and y < 7 and a[y - 2] = 2 and a[y - 1] = 2 and a[y + 1] = 1 and a[y + 2] = 1\", \"id\": \"T12\", \"source\": \"running\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}}}, \"done\": {\"source\": \"done\", \"id\": \"D03\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | done -> success | tmin > y and fmax < y\", \"id\": \"T13\", \"source\": \"done\", \"target\": \"success\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | done -> failure | !(tmin > y and fmax < y)\", \"id\": \"T14\", \"source\": \"done\", \"target\": \"failure\", \"priority\": 0, \"is_excluded\": false}}}, \"success\": {\"source\": \"success\", \"id\": \"D04\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | success -> reset | true\", \"id\": \"T15\", \"source\": \"success\", \"target\": \"reset\", \"priority\": 0, \"is_excluded\": false}}}, \"failure\": {\"source\": \"failure\", \"id\": \"D05\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | failure -> reset | true\", \"id\": \"T16\", \"source\": \"failure\", \"target\": \"reset\", \"priority\": 0, \"is_excluded\": false}}}, \"reset\": {\"source\": \"reset\", \"id\": \"D06\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | reset -> running | true | [true; y := 4; tmin := 0; fmax := 8; a[4] := 0; a[0] := 1; a[1] := 1; a[2] := 1; a[3] := 1; a[5] := 2; a[6] := 2; a[7] := 2; a[8] := 2]\", \"id\": \"T17\", \"source\": \"reset\", \"target\": \"running\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
    }
}