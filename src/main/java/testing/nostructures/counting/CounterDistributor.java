package testing.nostructures.counting;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import java.time.format.DateTimeFormatter;
import java.time.Instant;

// SLCO model CounterDistributor.
public class CounterDistributor {
    // The objects in the model.
    private final SLCO_Class[] objects;

    // Additional supporting variables.
    // Define and initialize the logger to gather the appropriate performance data with.
    private static final Logger logger;
    static {
        Properties props = System.getProperties();
        props.setProperty("log4j2.asyncLoggerRingBufferSize", "4194304");

        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "CounterDistributor";
        String log_settings = "[NDS,T=30s]";
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

    CounterDistributor() {
        // Instantiate the objects.
        objects = new SLCO_Class[] {
            new CounterDistributorExact(0)
        };
    }

    // Lock class to handle locks of global variables
    private static class LockManager {
        // The locks
        private final ReentrantLock[] locks;

        LockManager(int noVariables) {
            locks = new ReentrantLock[noVariables];
            for(int i = 0; i < locks.length; i++) {
                locks[i] = new ReentrantLock();
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

    // Representation of the SLCO class CounterDistributorExact.
    private static class CounterDistributorExact implements SLCO_Class {
        // The state machine threads.
        private final Thread T_Counter;
        private final Thread T_Distributor;

        // Class variables.
        private volatile int x;

        CounterDistributorExact(int x) {
            // Create a lock manager.
            LockManager lockManager = new LockManager(1);

            // Instantiate the class variables.
            this.x = x;

            // Instantiate the state machine threads and pass on the class' lock manager.
            T_Counter = new CounterDistributorExact_CounterThread(lockManager);
            T_Distributor = new CounterDistributorExact_DistributorThread(lockManager);
        }

        // Define the states fot the state machine Counter.
        interface CounterDistributorExact_CounterThread_States {
            enum States {
                C
            }
        }

        // Representation of the SLCO state machine Counter.
        class CounterDistributorExact_CounterThread extends Thread implements CounterDistributorExact_CounterThread_States {
            // Current state
            private CounterDistributorExact_CounterThread.States currentState;

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

            CounterDistributorExact_CounterThread(LockManager lockManagerInstance) {
                currentState = CounterDistributorExact_CounterThread.States.C;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[1];
                random = new Random();
            }

            // SLCO transition (p:0, id:0) | C -> C | true | x := (x + 1) % 10.
            private boolean execute_transition_C_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | x := (x + 1) % 10.
                lock_ids[0] = target_locks[0] = 0; // Acquire x
                lockManager.acquire_locks(lock_ids, 1);
                x = Math.floorMod((x + 1), 10);
                lock_ids[0] = target_locks[0]; // Release x
                lockManager.release_locks(lock_ids, 1);

                currentState = CounterDistributorExact_CounterThread.States.C;
                return true;
            }

            // Attempt to fire a transition starting in state C.
            private void exec_C() {
                D00_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | C -> C | true | x := (x + 1) % 10.
                T00_O++;
                if(execute_transition_C_0()) {
                    T00_S++;
                    D00_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case C -> exec_C();
                    }
                }

                // Report all counts.
                logger.info("D00.O " + D00_O);
                logger.info("D00.F " + D00_F);
                logger.info("D00.S " + D00_S);
                logger.info("T00.O " + T00_O);
                logger.info("T00.F " + T00_F);
                logger.info("T00.S " + T00_S);
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

        // Define the states fot the state machine Distributor.
        interface CounterDistributorExact_DistributorThread_States {
            enum States {
                P
            }
        }

        // Representation of the SLCO state machine Distributor.
        class CounterDistributorExact_DistributorThread extends Thread implements CounterDistributorExact_DistributorThread_States {
            // Current state
            private CounterDistributorExact_DistributorThread.States currentState;

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
            private long T01_O;
            private long T01_F;
            private long T01_S;
            private long T02_O;
            private long T02_F;
            private long T02_S;
            private long T03_O;
            private long T03_F;
            private long T03_S;
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
            private long T08_O;
            private long T08_F;
            private long T08_S;
            private long T09_O;
            private long T09_F;
            private long T09_S;
            private long T10_O;
            private long T10_F;
            private long T10_S;

            CounterDistributorExact_DistributorThread(LockManager lockManagerInstance) {
                currentState = CounterDistributorExact_DistributorThread.States.P;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[1];
                random = new Random();
            }

            // SLCO expression wrapper | x = 0.
            private boolean t_P_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0; // Acquire x
                lockManager.acquire_locks(lock_ids, 1);
                if(x == 0) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | P -> P | x = 0.
            private boolean execute_transition_P_0() {
                // SLCO expression | x = 0.
                if(!(t_P_0_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 1.
            private boolean t_P_1_s_0_n_0() {
                if(x == 1) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | P -> P | x = 1.
            private boolean execute_transition_P_1() {
                // SLCO expression | x = 1.
                if(!(t_P_1_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 2.
            private boolean t_P_2_s_0_n_0() {
                if(x == 2) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:2) | P -> P | x = 2.
            private boolean execute_transition_P_2() {
                // SLCO expression | x = 2.
                if(!(t_P_2_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 3.
            private boolean t_P_3_s_0_n_0() {
                if(x == 3) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:3) | P -> P | x = 3.
            private boolean execute_transition_P_3() {
                // SLCO expression | x = 3.
                if(!(t_P_3_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 4.
            private boolean t_P_4_s_0_n_0() {
                if(x == 4) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:4) | P -> P | x = 4.
            private boolean execute_transition_P_4() {
                // SLCO expression | x = 4.
                if(!(t_P_4_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 5.
            private boolean t_P_5_s_0_n_0() {
                if(x == 5) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:5) | P -> P | x = 5.
            private boolean execute_transition_P_5() {
                // SLCO expression | x = 5.
                if(!(t_P_5_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 6.
            private boolean t_P_6_s_0_n_0() {
                if(x == 6) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:6) | P -> P | x = 6.
            private boolean execute_transition_P_6() {
                // SLCO expression | x = 6.
                if(!(t_P_6_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 7.
            private boolean t_P_7_s_0_n_0() {
                if(x == 7) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:7) | P -> P | x = 7.
            private boolean execute_transition_P_7() {
                // SLCO expression | x = 7.
                if(!(t_P_7_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 8.
            private boolean t_P_8_s_0_n_0() {
                if(x == 8) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:8) | P -> P | x = 8.
            private boolean execute_transition_P_8() {
                // SLCO expression | x = 8.
                if(!(t_P_8_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // SLCO expression wrapper | x = 9.
            private boolean t_P_9_s_0_n_0() {
                if(x == 9) {
                    lock_ids[0] = target_locks[0]; // Release x
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release x
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:9) | P -> P | x = 9.
            private boolean execute_transition_P_9() {
                // SLCO expression | x = 9.
                if(!(t_P_9_s_0_n_0())) {
                    return false;
                }

                currentState = CounterDistributorExact_DistributorThread.States.P;
                return true;
            }

            // Attempt to fire a transition starting in state P.
            private void exec_P() {
                D01_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | P -> P | x = 0.
                T01_O++;
                if(execute_transition_P_0()) {
                    T01_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | P -> P | x = 1.
                T02_O++;
                if(execute_transition_P_1()) {
                    T02_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:2) | P -> P | x = 2.
                T03_O++;
                if(execute_transition_P_2()) {
                    T03_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:3) | P -> P | x = 3.
                T04_O++;
                if(execute_transition_P_3()) {
                    T04_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:4) | P -> P | x = 4.
                T05_O++;
                if(execute_transition_P_4()) {
                    T05_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:5) | P -> P | x = 5.
                T06_O++;
                if(execute_transition_P_5()) {
                    T06_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:6) | P -> P | x = 6.
                T07_O++;
                if(execute_transition_P_6()) {
                    T07_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:7) | P -> P | x = 7.
                T08_O++;
                if(execute_transition_P_7()) {
                    T08_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:8) | P -> P | x = 8.
                T09_O++;
                if(execute_transition_P_8()) {
                    T09_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:9) | P -> P | x = 9.
                T10_O++;
                if(execute_transition_P_9()) {
                    T10_S++;
                    D01_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case P -> exec_P();
                    }
                }

                // Report all counts.
                logger.info("D01.O " + D01_O);
                logger.info("D01.F " + D01_F);
                logger.info("D01.S " + D01_S);
                logger.info("T01.O " + T01_O);
                logger.info("T01.F " + T01_F);
                logger.info("T01.S " + T01_S);
                logger.info("T02.O " + T02_O);
                logger.info("T02.F " + T02_F);
                logger.info("T02.S " + T02_S);
                logger.info("T03.O " + T03_O);
                logger.info("T03.F " + T03_F);
                logger.info("T03.S " + T03_S);
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
                logger.info("T08.O " + T08_O);
                logger.info("T08.F " + T08_F);
                logger.info("T08.S " + T08_S);
                logger.info("T09.O " + T09_O);
                logger.info("T09.F " + T09_F);
                logger.info("T09.S " + T09_S);
                logger.info("T10.O " + T10_O);
                logger.info("T10.F " + T10_F);
                logger.info("T10.S " + T10_S);
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
            T_Counter.start();
            T_Distributor.start();
        }

        // Join all threads.
        public void joinThreads() {
            while (true) {
                try {
                    T_Counter.join();
                    T_Distributor.join();
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
        CounterDistributor model = new CounterDistributor();
        model.startThreads();
        model.joinThreads();

        // Include information about the model.
        logger.info("JSON {\"name\": \"CounterDistributor\", \"settings\": \"test_models/CounterDistributor.slco -no_deterministic_structures -running_time=30 -package_name=testing.nostructures -performance_measurements\", \"classes\": {\"CounterDistributorExact\": {\"name\": \"CounterDistributorExact\", \"state_machines\": {\"Counter\": {\"name\": \"Counter\", \"states\": [\"C\"], \"decision_structures\": {\"C\": {\"source\": \"C\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | C -> C | true | x := (x + 1) % 10\", \"id\": \"T00\", \"source\": \"C\", \"target\": \"C\", \"priority\": 0, \"is_excluded\": false}}}}}, \"Distributor\": {\"name\": \"Distributor\", \"states\": [\"P\"], \"decision_structures\": {\"P\": {\"source\": \"P\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | P -> P | x = 0\", \"id\": \"T01\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | P -> P | x = 1\", \"id\": \"T02\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | P -> P | x = 2\", \"id\": \"T03\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | P -> P | x = 3\", \"id\": \"T04\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | P -> P | x = 4\", \"id\": \"T05\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | P -> P | x = 5\", \"id\": \"T06\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"6\": {\"name\": \"(p:0, id:6) | P -> P | x = 6\", \"id\": \"T07\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"7\": {\"name\": \"(p:0, id:7) | P -> P | x = 7\", \"id\": \"T08\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"8\": {\"name\": \"(p:0, id:8) | P -> P | x = 8\", \"id\": \"T09\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}, \"9\": {\"name\": \"(p:0, id:9) | P -> P | x = 9\", \"id\": \"T10\", \"source\": \"P\", \"target\": \"P\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
    }
}