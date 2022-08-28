package testing.counting;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import java.time.format.DateTimeFormatter;
import java.time.Instant;

// SLCO model SyntheticTestTokens.
public class SyntheticTestTokens {
    // The objects in the model.
    private final SLCO_Class[] objects;

    // Additional supporting variables.
    // Define and initialize the logger to gather the appropriate performance data with.
    private static final Logger logger;
    static {
        Properties props = System.getProperties();
        props.setProperty("log4j2.asyncLoggerRingBufferSize", "4194304");

        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "SyntheticTestTokens";
        String log_settings = "[DSSI=0,T=60s]";
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

    SyntheticTestTokens() {
        // Instantiate the objects.
        objects = new SLCO_Class[] {
                new P(new boolean[]{ false, false, false, false, false })
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

    // Representation of the SLCO class P.
    private static class P implements SLCO_Class {
        // The state machine threads.
        private final Thread T_SM1;
        private final Thread T_SM2;
        private final Thread T_SM3;
        private final Thread T_SM4;
        private final Thread T_SM5;

        // Class variables.
        private final boolean[] tokens;

        P(boolean[] tokens) {
            // Create a lock manager.
            LockManager lockManager = new LockManager(5);

            // Instantiate the class variables.
            this.tokens = tokens;

            // Instantiate the state machine threads and pass on the class' lock manager.
            T_SM1 = new P_SM1Thread(lockManager);
            T_SM2 = new P_SM2Thread(lockManager);
            T_SM3 = new P_SM3Thread(lockManager);
            T_SM4 = new P_SM4Thread(lockManager);
            T_SM5 = new P_SM5Thread(lockManager);
        }

        // Define the states fot the state machine SM1.
        interface P_SM1Thread_States {
            enum States {
                SMC0,
                SMC1
            }
        }

        // Representation of the SLCO state machine SM1.
        class P_SM1Thread extends Thread implements P_SM1Thread_States {
            // Current state
            private P_SM1Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

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
            private long D01_O;
            private long D01_F;
            private long D01_S;
            private long T03_O;
            private long T03_F;
            private long T03_S;

            P_SM1Thread(LockManager lockManagerInstance) {
                currentState = P_SM1Thread.States.SMC0;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 0;
            }

            // SLCO expression wrapper | tokens[0].
            private boolean t_SMC0_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 1);
                return tokens[0];
            }

            // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[0]; tokens[0] := false].
            private boolean execute_transition_SMC0_0() {
                // SLCO composite | [tokens[0]; tokens[0] := false].
                // SLCO expression | tokens[0].
                if(!(t_SMC0_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[0] := false.
                tokens[0] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM1Thread.States.SMC0;
                return true;
            }

            // SLCO expression wrapper | x = 0.
            private boolean t_SMC0_1_s_0_n_0() {
                if(x == 0) {
                    lock_ids[0] = target_locks[0]; // Release tokens[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[0] and x = 0; tokens[1] := true].
            private boolean execute_transition_SMC0_1() {
                // SLCO composite | [!tokens[0] and x = 0; tokens[1] := true].
                // SLCO expression | !tokens[0] and x = 0.
                if(!(!(tokens[0]) && t_SMC0_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[1] := true.
                lock_ids[0] = target_locks[1] = 0 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                tokens[1] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM1Thread.States.SMC1;
                return true;
            }

            // SLCO expression wrapper | !tokens[0].
            private boolean t_SMC0_2_s_0_n_0() {
                if(!(tokens[0])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[0] and x != 0.
            private boolean execute_transition_SMC0_2() {
                // SLCO expression | !tokens[0] and x != 0.
                if(!(t_SMC0_2_s_0_n_0() && x != 0)) {
                    return false;
                }

                currentState = P_SM1Thread.States.SMC1;
                return true;
            }

            // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
            private boolean execute_transition_SMC1_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | x := (7 * x + 3) % 10.
                x = Math.floorMod((7 * x + 3), 10);

                currentState = P_SM1Thread.States.SMC0;
                return true;
            }

            // Attempt to fire a transition starting in state SMC0.
            private void exec_SMC0() {
                D00_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[0]; tokens[0] := false].
                T00_O++;
                if(execute_transition_SMC0_0()) {
                    T00_S++;
                    D00_S++;
                    return;
                }
                T00_F++;
                // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[0] and x = 0; tokens[1] := true].
                T01_O++;
                if(execute_transition_SMC0_1()) {
                    T01_S++;
                    D00_S++;
                    return;
                }
                T01_F++;
                // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[0] and x != 0.
                T02_O++;
                if(execute_transition_SMC0_2()) {
                    T02_S++;
                    D00_S++;
                    return;
                }
                T02_F++;
                // [DET.END]
                // [SEQ.END]
                D00_F++;
            }

            // Attempt to fire a transition starting in state SMC1.
            private void exec_SMC1() {
                D01_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
                T03_O++;
                if(execute_transition_SMC1_0()) {
                    T03_S++;
                    D01_S++;
                    return;
                }
                T03_F++;
                // [SEQ.END]
                D01_F++;
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case SMC0 -> exec_SMC0();
                        case SMC1 -> exec_SMC1();
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
                logger.info("D01.O " + D01_O);
                logger.info("D01.F " + D01_F);
                logger.info("D01.S " + D01_S);
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

        // Define the states fot the state machine SM2.
        interface P_SM2Thread_States {
            enum States {
                SMC0,
                SMC1
            }
        }

        // Representation of the SLCO state machine SM2.
        class P_SM2Thread extends Thread implements P_SM2Thread_States {
            // Current state
            private P_SM2Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            // Add variables needed for measurements.
            private long D02_O;
            private long D02_F;
            private long D02_S;
            private long T04_O;
            private long T04_F;
            private long T04_S;
            private long T05_O;
            private long T05_F;
            private long T05_S;
            private long T06_O;
            private long T06_F;
            private long T06_S;
            private long D03_O;
            private long D03_F;
            private long D03_S;
            private long T07_O;
            private long T07_F;
            private long T07_S;

            P_SM2Thread(LockManager lockManagerInstance) {
                currentState = P_SM2Thread.States.SMC0;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 0;
            }

            // SLCO expression wrapper | tokens[1].
            private boolean t_SMC0_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                return tokens[1];
            }

            // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[1]; tokens[1] := false].
            private boolean execute_transition_SMC0_0() {
                // SLCO composite | [tokens[1]; tokens[1] := false].
                // SLCO expression | tokens[1].
                if(!(t_SMC0_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[1] := false.
                tokens[1] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM2Thread.States.SMC0;
                return true;
            }

            // SLCO expression wrapper | x = 0.
            private boolean t_SMC0_1_s_0_n_0() {
                if(x == 0) {
                    lock_ids[0] = target_locks[0]; // Release tokens[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[1] and x = 0; tokens[2] := true].
            private boolean execute_transition_SMC0_1() {
                // SLCO composite | [!tokens[1] and x = 0; tokens[2] := true].
                // SLCO expression | !tokens[1] and x = 0.
                if(!(!(tokens[1]) && t_SMC0_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[2] := true.
                lock_ids[0] = target_locks[1] = 0 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                tokens[2] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM2Thread.States.SMC1;
                return true;
            }

            // SLCO expression wrapper | !tokens[1].
            private boolean t_SMC0_2_s_0_n_0() {
                if(!(tokens[1])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[1] and x != 0.
            private boolean execute_transition_SMC0_2() {
                // SLCO expression | !tokens[1] and x != 0.
                if(!(t_SMC0_2_s_0_n_0() && x != 0)) {
                    return false;
                }

                currentState = P_SM2Thread.States.SMC1;
                return true;
            }

            // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
            private boolean execute_transition_SMC1_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | x := (7 * x + 3) % 10.
                x = Math.floorMod((7 * x + 3), 10);

                currentState = P_SM2Thread.States.SMC0;
                return true;
            }

            // Attempt to fire a transition starting in state SMC0.
            private void exec_SMC0() {
                D02_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[1]; tokens[1] := false].
                T04_O++;
                if(execute_transition_SMC0_0()) {
                    T04_S++;
                    D02_S++;
                    return;
                }
                T04_F++;
                // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[1] and x = 0; tokens[2] := true].
                T05_O++;
                if(execute_transition_SMC0_1()) {
                    T05_S++;
                    D02_S++;
                    return;
                }
                T05_F++;
                // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[1] and x != 0.
                T06_O++;
                if(execute_transition_SMC0_2()) {
                    T06_S++;
                    D02_S++;
                    return;
                }
                T06_F++;
                // [DET.END]
                // [SEQ.END]
                D02_F++;
            }

            // Attempt to fire a transition starting in state SMC1.
            private void exec_SMC1() {
                D03_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
                T07_O++;
                if(execute_transition_SMC1_0()) {
                    T07_S++;
                    D03_S++;
                    return;
                }
                T07_F++;
                // [SEQ.END]
                D03_F++;
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case SMC0 -> exec_SMC0();
                        case SMC1 -> exec_SMC1();
                    }
                }

                // Report all counts.
                logger.info("D02.O " + D02_O);
                logger.info("D02.F " + D02_F);
                logger.info("D02.S " + D02_S);
                logger.info("T04.O " + T04_O);
                logger.info("T04.F " + T04_F);
                logger.info("T04.S " + T04_S);
                logger.info("T05.O " + T05_O);
                logger.info("T05.F " + T05_F);
                logger.info("T05.S " + T05_S);
                logger.info("T06.O " + T06_O);
                logger.info("T06.F " + T06_F);
                logger.info("T06.S " + T06_S);
                logger.info("D03.O " + D03_O);
                logger.info("D03.F " + D03_F);
                logger.info("D03.S " + D03_S);
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

        // Define the states fot the state machine SM3.
        interface P_SM3Thread_States {
            enum States {
                SMC0,
                SMC1
            }
        }

        // Representation of the SLCO state machine SM3.
        class P_SM3Thread extends Thread implements P_SM3Thread_States {
            // Current state
            private P_SM3Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            // Add variables needed for measurements.
            private long D04_O;
            private long D04_F;
            private long D04_S;
            private long T08_O;
            private long T08_F;
            private long T08_S;
            private long T09_O;
            private long T09_F;
            private long T09_S;
            private long T10_O;
            private long T10_F;
            private long T10_S;
            private long D05_O;
            private long D05_F;
            private long D05_S;
            private long T11_O;
            private long T11_F;
            private long T11_S;

            P_SM3Thread(LockManager lockManagerInstance) {
                currentState = P_SM3Thread.States.SMC0;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 0;
            }

            // SLCO expression wrapper | tokens[2].
            private boolean t_SMC0_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                return tokens[2];
            }

            // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[2]; tokens[2] := false].
            private boolean execute_transition_SMC0_0() {
                // SLCO composite | [tokens[2]; tokens[2] := false].
                // SLCO expression | tokens[2].
                if(!(t_SMC0_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[2] := false.
                tokens[2] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM3Thread.States.SMC0;
                return true;
            }

            // SLCO expression wrapper | x = 0.
            private boolean t_SMC0_1_s_0_n_0() {
                if(x == 0) {
                    lock_ids[0] = target_locks[0]; // Release tokens[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[2] and x = 0; tokens[3] := true].
            private boolean execute_transition_SMC0_1() {
                // SLCO composite | [!tokens[2] and x = 0; tokens[3] := true].
                // SLCO expression | !tokens[2] and x = 0.
                if(!(!(tokens[2]) && t_SMC0_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[3] := true.
                lock_ids[0] = target_locks[1] = 0 + 3; // Acquire tokens[3]
                lockManager.acquire_locks(lock_ids, 1);
                tokens[3] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM3Thread.States.SMC1;
                return true;
            }

            // SLCO expression wrapper | !tokens[2].
            private boolean t_SMC0_2_s_0_n_0() {
                if(!(tokens[2])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[2] and x != 0.
            private boolean execute_transition_SMC0_2() {
                // SLCO expression | !tokens[2] and x != 0.
                if(!(t_SMC0_2_s_0_n_0() && x != 0)) {
                    return false;
                }

                currentState = P_SM3Thread.States.SMC1;
                return true;
            }

            // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
            private boolean execute_transition_SMC1_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | x := (7 * x + 3) % 10.
                x = Math.floorMod((7 * x + 3), 10);

                currentState = P_SM3Thread.States.SMC0;
                return true;
            }

            // Attempt to fire a transition starting in state SMC0.
            private void exec_SMC0() {
                D04_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[2]; tokens[2] := false].
                T08_O++;
                if(execute_transition_SMC0_0()) {
                    T08_S++;
                    D04_S++;
                    return;
                }
                T08_F++;
                // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[2] and x = 0; tokens[3] := true].
                T09_O++;
                if(execute_transition_SMC0_1()) {
                    T09_S++;
                    D04_S++;
                    return;
                }
                T09_F++;
                // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[2] and x != 0.
                T10_O++;
                if(execute_transition_SMC0_2()) {
                    T10_S++;
                    D04_S++;
                    return;
                }
                T10_F++;
                // [DET.END]
                // [SEQ.END]
                D04_F++;
            }

            // Attempt to fire a transition starting in state SMC1.
            private void exec_SMC1() {
                D05_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
                T11_O++;
                if(execute_transition_SMC1_0()) {
                    T11_S++;
                    D05_S++;
                    return;
                }
                T11_F++;
                // [SEQ.END]
                D05_F++;
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case SMC0 -> exec_SMC0();
                        case SMC1 -> exec_SMC1();
                    }
                }

                // Report all counts.
                logger.info("D04.O " + D04_O);
                logger.info("D04.F " + D04_F);
                logger.info("D04.S " + D04_S);
                logger.info("T08.O " + T08_O);
                logger.info("T08.F " + T08_F);
                logger.info("T08.S " + T08_S);
                logger.info("T09.O " + T09_O);
                logger.info("T09.F " + T09_F);
                logger.info("T09.S " + T09_S);
                logger.info("T10.O " + T10_O);
                logger.info("T10.F " + T10_F);
                logger.info("T10.S " + T10_S);
                logger.info("D05.O " + D05_O);
                logger.info("D05.F " + D05_F);
                logger.info("D05.S " + D05_S);
                logger.info("T11.O " + T11_O);
                logger.info("T11.F " + T11_F);
                logger.info("T11.S " + T11_S);
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

        // Define the states fot the state machine SM4.
        interface P_SM4Thread_States {
            enum States {
                SMC0,
                SMC1
            }
        }

        // Representation of the SLCO state machine SM4.
        class P_SM4Thread extends Thread implements P_SM4Thread_States {
            // Current state
            private P_SM4Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            // Add variables needed for measurements.
            private long D06_O;
            private long D06_F;
            private long D06_S;
            private long T12_O;
            private long T12_F;
            private long T12_S;
            private long T13_O;
            private long T13_F;
            private long T13_S;
            private long T14_O;
            private long T14_F;
            private long T14_S;
            private long D07_O;
            private long D07_F;
            private long D07_S;
            private long T15_O;
            private long T15_F;
            private long T15_S;

            P_SM4Thread(LockManager lockManagerInstance) {
                currentState = P_SM4Thread.States.SMC0;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 0;
            }

            // SLCO expression wrapper | tokens[3].
            private boolean t_SMC0_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0 + 3; // Acquire tokens[3]
                lockManager.acquire_locks(lock_ids, 1);
                return tokens[3];
            }

            // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[3]; tokens[3] := false].
            private boolean execute_transition_SMC0_0() {
                // SLCO composite | [tokens[3]; tokens[3] := false].
                // SLCO expression | tokens[3].
                if(!(t_SMC0_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[3] := false.
                tokens[3] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM4Thread.States.SMC0;
                return true;
            }

            // SLCO expression wrapper | x = 0.
            private boolean t_SMC0_1_s_0_n_0() {
                if(x == 0) {
                    lock_ids[0] = target_locks[0]; // Release tokens[3]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[3] and x = 0; tokens[4] := true].
            private boolean execute_transition_SMC0_1() {
                // SLCO composite | [!tokens[3] and x = 0; tokens[4] := true].
                // SLCO expression | !tokens[3] and x = 0.
                if(!(!(tokens[3]) && t_SMC0_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[4] := true.
                lock_ids[0] = target_locks[1] = 0 + 4; // Acquire tokens[4]
                lockManager.acquire_locks(lock_ids, 1);
                tokens[4] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[4]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM4Thread.States.SMC1;
                return true;
            }

            // SLCO expression wrapper | !tokens[3].
            private boolean t_SMC0_2_s_0_n_0() {
                if(!(tokens[3])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[3]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[3]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[3] and x != 0.
            private boolean execute_transition_SMC0_2() {
                // SLCO expression | !tokens[3] and x != 0.
                if(!(t_SMC0_2_s_0_n_0() && x != 0)) {
                    return false;
                }

                currentState = P_SM4Thread.States.SMC1;
                return true;
            }

            // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
            private boolean execute_transition_SMC1_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | x := (7 * x + 3) % 10.
                x = Math.floorMod((7 * x + 3), 10);

                currentState = P_SM4Thread.States.SMC0;
                return true;
            }

            // Attempt to fire a transition starting in state SMC0.
            private void exec_SMC0() {
                D06_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[3]; tokens[3] := false].
                T12_O++;
                if(execute_transition_SMC0_0()) {
                    T12_S++;
                    D06_S++;
                    return;
                }
                T12_F++;
                // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[3] and x = 0; tokens[4] := true].
                T13_O++;
                if(execute_transition_SMC0_1()) {
                    T13_S++;
                    D06_S++;
                    return;
                }
                T13_F++;
                // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[3] and x != 0.
                T14_O++;
                if(execute_transition_SMC0_2()) {
                    T14_S++;
                    D06_S++;
                    return;
                }
                T14_F++;
                // [DET.END]
                // [SEQ.END]
                D06_F++;
            }

            // Attempt to fire a transition starting in state SMC1.
            private void exec_SMC1() {
                D07_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
                T15_O++;
                if(execute_transition_SMC1_0()) {
                    T15_S++;
                    D07_S++;
                    return;
                }
                T15_F++;
                // [SEQ.END]
                D07_F++;
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case SMC0 -> exec_SMC0();
                        case SMC1 -> exec_SMC1();
                    }
                }

                // Report all counts.
                logger.info("D06.O " + D06_O);
                logger.info("D06.F " + D06_F);
                logger.info("D06.S " + D06_S);
                logger.info("T12.O " + T12_O);
                logger.info("T12.F " + T12_F);
                logger.info("T12.S " + T12_S);
                logger.info("T13.O " + T13_O);
                logger.info("T13.F " + T13_F);
                logger.info("T13.S " + T13_S);
                logger.info("T14.O " + T14_O);
                logger.info("T14.F " + T14_F);
                logger.info("T14.S " + T14_S);
                logger.info("D07.O " + D07_O);
                logger.info("D07.F " + D07_F);
                logger.info("D07.S " + D07_S);
                logger.info("T15.O " + T15_O);
                logger.info("T15.F " + T15_F);
                logger.info("T15.S " + T15_S);
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

        // Define the states fot the state machine SM5.
        interface P_SM5Thread_States {
            enum States {
                SMC0,
                SMC1
            }
        }

        // Representation of the SLCO state machine SM5.
        class P_SM5Thread extends Thread implements P_SM5Thread_States {
            // Current state
            private P_SM5Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            // Add variables needed for measurements.
            private long D08_O;
            private long D08_F;
            private long D08_S;
            private long T16_O;
            private long T16_F;
            private long T16_S;
            private long T17_O;
            private long T17_F;
            private long T17_S;
            private long T18_O;
            private long T18_F;
            private long T18_S;
            private long D09_O;
            private long D09_F;
            private long D09_S;
            private long T19_O;
            private long T19_F;
            private long T19_S;

            P_SM5Thread(LockManager lockManagerInstance) {
                currentState = P_SM5Thread.States.SMC0;
                lockManager = lockManagerInstance;
                lock_ids = new int[2];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 0;
            }

            // SLCO expression wrapper | tokens[4].
            private boolean t_SMC0_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 0 + 4; // Acquire tokens[4]
                lock_ids[1] = target_locks[1] = 0 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 2);
                if(tokens[4]) {
                    lock_ids[0] = target_locks[1]; // Release tokens[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[4]; tokens[4] := false].
            private boolean execute_transition_SMC0_0() {
                // SLCO composite | [tokens[4]; tokens[4] := false].
                // SLCO expression | tokens[4].
                if(!(t_SMC0_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[4] := false.
                tokens[4] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[4]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM5Thread.States.SMC0;
                return true;
            }

            // SLCO expression wrapper | !tokens[4].
            private boolean t_SMC0_1_s_0_n_0() {
                if(!(tokens[4])) {
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | x = 0.
            private boolean t_SMC0_1_s_0_n_1() {
                if(x == 0) {
                    lock_ids[0] = target_locks[0]; // Release tokens[4]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[4] and x = 0; tokens[0] := true].
            private boolean execute_transition_SMC0_1() {
                // SLCO composite | [!tokens[4] and x = 0; tokens[0] := true].
                // SLCO expression | !tokens[4] and x = 0.
                if(!(t_SMC0_1_s_0_n_0() && t_SMC0_1_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | tokens[0] := true.
                tokens[0] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_SM5Thread.States.SMC1;
                return true;
            }

            // SLCO expression wrapper | !tokens[4].
            private boolean t_SMC0_2_s_0_n_0() {
                if(!(tokens[4])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[4]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[4]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[4] and x != 0.
            private boolean execute_transition_SMC0_2() {
                // SLCO expression | !tokens[4] and x != 0.
                if(!(t_SMC0_2_s_0_n_0() && x != 0)) {
                    return false;
                }

                currentState = P_SM5Thread.States.SMC1;
                return true;
            }

            // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
            private boolean execute_transition_SMC1_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | x := (7 * x + 3) % 10.
                x = Math.floorMod((7 * x + 3), 10);

                currentState = P_SM5Thread.States.SMC0;
                return true;
            }

            // Attempt to fire a transition starting in state SMC0.
            private void exec_SMC0() {
                D08_O++;
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | SMC0 -> SMC0 | [tokens[4]; tokens[4] := false].
                T16_O++;
                if(execute_transition_SMC0_0()) {
                    T16_S++;
                    D08_S++;
                    return;
                }
                T16_F++;
                // SLCO transition (p:0, id:1) | SMC0 -> SMC1 | [!tokens[4] and x = 0; tokens[0] := true].
                T17_O++;
                if(execute_transition_SMC0_1()) {
                    T17_S++;
                    D08_S++;
                    return;
                }
                T17_F++;
                // SLCO transition (p:0, id:2) | SMC0 -> SMC1 | !tokens[4] and x != 0.
                T18_O++;
                if(execute_transition_SMC0_2()) {
                    T18_S++;
                    D08_S++;
                    return;
                }
                T18_F++;
                // [DET.END]
                // [SEQ.END]
                D08_F++;
            }

            // Attempt to fire a transition starting in state SMC1.
            private void exec_SMC1() {
                D09_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10.
                T19_O++;
                if(execute_transition_SMC1_0()) {
                    T19_S++;
                    D09_S++;
                    return;
                }
                T19_F++;
                // [SEQ.END]
                D09_F++;
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case SMC0 -> exec_SMC0();
                        case SMC1 -> exec_SMC1();
                    }
                }

                // Report all counts.
                logger.info("D08.O " + D08_O);
                logger.info("D08.F " + D08_F);
                logger.info("D08.S " + D08_S);
                logger.info("T16.O " + T16_O);
                logger.info("T16.F " + T16_F);
                logger.info("T16.S " + T16_S);
                logger.info("T17.O " + T17_O);
                logger.info("T17.F " + T17_F);
                logger.info("T17.S " + T17_S);
                logger.info("T18.O " + T18_O);
                logger.info("T18.F " + T18_F);
                logger.info("T18.S " + T18_S);
                logger.info("D09.O " + D09_O);
                logger.info("D09.F " + D09_F);
                logger.info("D09.S " + D09_S);
                logger.info("T19.O " + T19_O);
                logger.info("T19.F " + T19_F);
                logger.info("T19.S " + T19_S);
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
            T_SM1.start();
            T_SM2.start();
            T_SM3.start();
            T_SM4.start();
            T_SM5.start();
        }

        // Join all threads.
        public void joinThreads() {
            while (true) {
                try {
                    T_SM1.join();
                    T_SM2.join();
                    T_SM3.join();
                    T_SM4.join();
                    T_SM5.join();
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
        SyntheticTestTokens model = new SyntheticTestTokens();
        model.startThreads();
        model.joinThreads();

        // Include information about the model.
        logger.info("JSON {\"name\": \"SyntheticTestTokens\", \"settings\": \"SyntheticTestTokens.slco -running_time=60 -package_name=testing -performance_measurements\", \"classes\": {\"P\": {\"name\": \"P\", \"state_machines\": {\"SM1\": {\"name\": \"SM1\", \"states\": [\"SMC0\", \"SMC1\"], \"decision_structures\": {\"SMC0\": {\"source\": \"SMC0\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC0 -> SMC0 | [tokens[0]; tokens[0] := false]\", \"id\": \"T00\", \"source\": \"SMC0\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | SMC0 -> SMC1 | [!tokens[0] and x = 0; tokens[1] := true]\", \"id\": \"T01\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | SMC0 -> SMC1 | !tokens[0] and x != 0\", \"id\": \"T02\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}}}, \"SMC1\": {\"source\": \"SMC1\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10\", \"id\": \"T03\", \"source\": \"SMC1\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}}}}}, \"SM2\": {\"name\": \"SM2\", \"states\": [\"SMC0\", \"SMC1\"], \"decision_structures\": {\"SMC0\": {\"source\": \"SMC0\", \"id\": \"D02\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC0 -> SMC0 | [tokens[1]; tokens[1] := false]\", \"id\": \"T04\", \"source\": \"SMC0\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | SMC0 -> SMC1 | [!tokens[1] and x = 0; tokens[2] := true]\", \"id\": \"T05\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | SMC0 -> SMC1 | !tokens[1] and x != 0\", \"id\": \"T06\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}}}, \"SMC1\": {\"source\": \"SMC1\", \"id\": \"D03\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10\", \"id\": \"T07\", \"source\": \"SMC1\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}}}}}, \"SM3\": {\"name\": \"SM3\", \"states\": [\"SMC0\", \"SMC1\"], \"decision_structures\": {\"SMC0\": {\"source\": \"SMC0\", \"id\": \"D04\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC0 -> SMC0 | [tokens[2]; tokens[2] := false]\", \"id\": \"T08\", \"source\": \"SMC0\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | SMC0 -> SMC1 | [!tokens[2] and x = 0; tokens[3] := true]\", \"id\": \"T09\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | SMC0 -> SMC1 | !tokens[2] and x != 0\", \"id\": \"T10\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}}}, \"SMC1\": {\"source\": \"SMC1\", \"id\": \"D05\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10\", \"id\": \"T11\", \"source\": \"SMC1\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}}}}}, \"SM4\": {\"name\": \"SM4\", \"states\": [\"SMC0\", \"SMC1\"], \"decision_structures\": {\"SMC0\": {\"source\": \"SMC0\", \"id\": \"D06\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC0 -> SMC0 | [tokens[3]; tokens[3] := false]\", \"id\": \"T12\", \"source\": \"SMC0\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | SMC0 -> SMC1 | [!tokens[3] and x = 0; tokens[4] := true]\", \"id\": \"T13\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | SMC0 -> SMC1 | !tokens[3] and x != 0\", \"id\": \"T14\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}}}, \"SMC1\": {\"source\": \"SMC1\", \"id\": \"D07\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10\", \"id\": \"T15\", \"source\": \"SMC1\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}}}}}, \"SM5\": {\"name\": \"SM5\", \"states\": [\"SMC0\", \"SMC1\"], \"decision_structures\": {\"SMC0\": {\"source\": \"SMC0\", \"id\": \"D08\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC0 -> SMC0 | [tokens[4]; tokens[4] := false]\", \"id\": \"T16\", \"source\": \"SMC0\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | SMC0 -> SMC1 | [!tokens[4] and x = 0; tokens[0] := true]\", \"id\": \"T17\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | SMC0 -> SMC1 | !tokens[4] and x != 0\", \"id\": \"T18\", \"source\": \"SMC0\", \"target\": \"SMC1\", \"priority\": 0, \"is_excluded\": false}}}, \"SMC1\": {\"source\": \"SMC1\", \"id\": \"D09\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | SMC1 -> SMC0 | true | x := (7 * x + 3) % 10\", \"id\": \"T19\", \"source\": \"SMC1\", \"target\": \"SMC0\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
    }
}