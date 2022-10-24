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

// SLCO model Tokens.
public class Tokens {
    // The objects in the model.
    private final SLCO_Class[] objects;

    // Additional supporting variables.
    // Define and initialize the logger to gather the appropriate performance data with.
    private static final Logger logger;
    static {
        Properties props = System.getProperties();
        props.setProperty("log4j2.asyncLoggerRingBufferSize", "4194304");

        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "Tokens";
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

    Tokens() {
        // Instantiate the objects.
        objects = new SLCO_Class[] {
            new P(
                new boolean[]{ false, false, false },
                0,
                0,
                0
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

    // Representation of the SLCO class P.
    private static class P implements SLCO_Class {
        // The state machine threads.
        private final Thread T_A;
        private final Thread T_B;
        private final Thread T_C;

        // Class variables.
        private final boolean[] tokens;
        private volatile int a;
        private volatile int b;
        private volatile int c;

        P(boolean[] tokens, int a, int b, int c) {
            // Create a lock manager.
            LockManager lockManager = new LockManager(6);

            // Instantiate the class variables.
            this.tokens = tokens;
            this.a = a;
            this.b = b;
            this.c = c;

            // Instantiate the state machine threads and pass on the class' lock manager.
            T_A = new P_AThread(lockManager);
            T_B = new P_BThread(lockManager);
            T_C = new P_CThread(lockManager);
        }

        // Define the states fot the state machine A.
        interface P_AThread_States {
            enum States {
                act, 
                update, 
                wait
            }
        }

        // Representation of the SLCO state machine A.
        class P_AThread extends Thread implements P_AThread_States {
            // Current state
            private P_AThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            P_AThread(LockManager lockManagerInstance) {
                currentState = P_AThread.States.act;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 1;
            }

            // SLCO expression wrapper | tokens[0].
            private boolean t_act_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(tokens[0]) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | act -> act | [tokens[0]; tokens[0] := false].
            private boolean execute_transition_act_0() {
                // SLCO composite | [tokens[0]; tokens[0] := false].
                // SLCO expression | tokens[0].
                if(!(t_act_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[0] := false.
                tokens[0] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_AThread.States.act;
                return true;
            }

            // SLCO expression wrapper | !tokens[0].
            private boolean t_act_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[0])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:1) | act -> update | !tokens[0] and x % 10 != 0.
            private boolean execute_transition_act_1() {
                // SLCO expression | !tokens[0] and x % 10 != 0.
                if(!(t_act_1_s_0_n_0() && Math.floorMod(x, 10) != 0)) {
                    return false;
                }

                currentState = P_AThread.States.update;
                return true;
            }

            // SLCO expression wrapper | !tokens[0].
            private boolean t_act_2_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[0])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:2) | act -> wait | [!tokens[0] and x % 10 = 0; tokens[1] := true].
            private boolean execute_transition_act_2() {
                // SLCO composite | [!tokens[0] and x % 10 = 0; tokens[1] := true].
                // SLCO expression | !tokens[0] and x % 10 = 0.
                if(!(t_act_2_s_0_n_0() && Math.floorMod(x, 10) == 0)) {
                    return false;
                }
                // SLCO assignment | tokens[1] := true.
                lock_ids[0] = target_locks[1] = 3 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                tokens[1] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_AThread.States.wait;
                return true;
            }

            // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (641 * x + 718) % 1009; a := a + 1].
            private boolean execute_transition_update_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [x := (641 * x + 718) % 1009; a := a + 1] -> [true; x := (641 * x + 718) % 1009; a := a + 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | x := (641 * x + 718) % 1009.
                x = Math.floorMod((641 * x + 718), 1009);
                // SLCO assignment | a := a + 1.
                lock_ids[0] = target_locks[0] = 0; // Acquire a
                lockManager.acquire_locks(lock_ids, 1);
                a = a + 1;
                lock_ids[0] = target_locks[0]; // Release a
                lockManager.release_locks(lock_ids, 1);

                currentState = P_AThread.States.act;
                return true;
            }

            // SLCO expression wrapper | tokens[0].
            private boolean t_wait_0_s_0_n_0() {
                lock_ids[0] = target_locks[1] = 3 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(tokens[0]) {
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | wait -> wait | [tokens[0]; tokens[0] := false].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [tokens[0]; tokens[0] := false].
                // SLCO expression | tokens[0].
                if(!(t_wait_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[0] := false.
                tokens[0] = false;
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_AThread.States.wait;
                return true;
            }

            // SLCO expression wrapper | !tokens[1].
            private boolean t_wait_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[1])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:1) | wait -> update | !tokens[1].
            private boolean execute_transition_wait_1() {
                // SLCO expression | !tokens[1].
                if(!(t_wait_1_s_0_n_0())) {
                    return false;
                }

                currentState = P_AThread.States.update;
                return true;
            }

            // Attempt to fire a transition starting in state act.
            private void exec_act() {
                logger.info("D00.O");
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | act -> act | [tokens[0]; tokens[0] := false].
                        logger.info("T00.O");
                        if(execute_transition_act_0()) {
                            logger.info("T00.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:1, id:1) | act -> update | !tokens[0] and x % 10 != 0.
                        logger.info("T01.O");
                        if(execute_transition_act_1()) {
                            logger.info("T01.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:1, id:2) | act -> wait | [!tokens[0] and x % 10 = 0; tokens[1] := true].
                        logger.info("T02.O");
                        if(execute_transition_act_2()) {
                            logger.info("T02.S");
                            logger.info("D00.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state update.
            private void exec_update() {
                logger.info("D01.O");
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (641 * x + 718) % 1009; a := a + 1].
                logger.info("T03.O");
                if(execute_transition_update_0()) {
                    logger.info("T03.S");
                    logger.info("D01.S");
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                logger.info("D02.O");
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | wait -> wait | [tokens[0]; tokens[0] := false].
                        logger.info("T04.O");
                        if(execute_transition_wait_0()) {
                            logger.info("T04.S");
                            logger.info("D02.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:1, id:1) | wait -> update | !tokens[1].
                        logger.info("T05.O");
                        if(execute_transition_wait_1()) {
                            logger.info("T05.S");
                            logger.info("D02.S");
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
                        case act -> exec_act();
                        case update -> exec_update();
                        case wait -> exec_wait();
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

        // Define the states fot the state machine B.
        interface P_BThread_States {
            enum States {
                act, 
                update, 
                wait
            }
        }

        // Representation of the SLCO state machine B.
        class P_BThread extends Thread implements P_BThread_States {
            // Current state
            private P_BThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            P_BThread(LockManager lockManagerInstance) {
                currentState = P_BThread.States.act;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 42;
            }

            // SLCO expression wrapper | tokens[1].
            private boolean t_act_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(tokens[1]) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | act -> act | [tokens[1]; tokens[1] := false].
            private boolean execute_transition_act_0() {
                // SLCO composite | [tokens[1]; tokens[1] := false].
                // SLCO expression | tokens[1].
                if(!(t_act_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[1] := false.
                tokens[1] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_BThread.States.act;
                return true;
            }

            // SLCO expression wrapper | !tokens[1].
            private boolean t_act_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[1])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:1) | act -> update | !tokens[1] and x % 10 != 0.
            private boolean execute_transition_act_1() {
                // SLCO expression | !tokens[1] and x % 10 != 0.
                if(!(t_act_1_s_0_n_0() && Math.floorMod(x, 10) != 0)) {
                    return false;
                }

                currentState = P_BThread.States.update;
                return true;
            }

            // SLCO expression wrapper | !tokens[1].
            private boolean t_act_2_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[1])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:2) | act -> wait | [!tokens[1] and x % 10 = 0; tokens[2] := true].
            private boolean execute_transition_act_2() {
                // SLCO composite | [!tokens[1] and x % 10 = 0; tokens[2] := true].
                // SLCO expression | !tokens[1] and x % 10 = 0.
                if(!(t_act_2_s_0_n_0() && Math.floorMod(x, 10) == 0)) {
                    return false;
                }
                // SLCO assignment | tokens[2] := true.
                lock_ids[0] = target_locks[1] = 3 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                tokens[2] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_BThread.States.wait;
                return true;
            }

            // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (193 * x + 953) % 1009; b := b + 1].
            private boolean execute_transition_update_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [x := (193 * x + 953) % 1009; b := b + 1] -> [true; x := (193 * x + 953) % 1009; b := b + 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | x := (193 * x + 953) % 1009.
                x = Math.floorMod((193 * x + 953), 1009);
                // SLCO assignment | b := b + 1.
                lock_ids[0] = target_locks[0] = 1; // Acquire b
                lockManager.acquire_locks(lock_ids, 1);
                b = b + 1;
                lock_ids[0] = target_locks[0]; // Release b
                lockManager.release_locks(lock_ids, 1);

                currentState = P_BThread.States.act;
                return true;
            }

            // SLCO expression wrapper | tokens[1].
            private boolean t_wait_0_s_0_n_0() {
                lock_ids[0] = target_locks[1] = 3 + 1; // Acquire tokens[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(tokens[1]) {
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | wait -> wait | [tokens[1]; tokens[1] := false].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [tokens[1]; tokens[1] := false].
                // SLCO expression | tokens[1].
                if(!(t_wait_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[1] := false.
                tokens[1] = false;
                lock_ids[0] = target_locks[1]; // Release tokens[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_BThread.States.wait;
                return true;
            }

            // SLCO expression wrapper | !tokens[2].
            private boolean t_wait_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[2])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:1) | wait -> update | !tokens[2].
            private boolean execute_transition_wait_1() {
                // SLCO expression | !tokens[2].
                if(!(t_wait_1_s_0_n_0())) {
                    return false;
                }

                currentState = P_BThread.States.update;
                return true;
            }

            // Attempt to fire a transition starting in state act.
            private void exec_act() {
                logger.info("D03.O");
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | act -> act | [tokens[1]; tokens[1] := false].
                        logger.info("T06.O");
                        if(execute_transition_act_0()) {
                            logger.info("T06.S");
                            logger.info("D03.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:1, id:1) | act -> update | !tokens[1] and x % 10 != 0.
                        logger.info("T07.O");
                        if(execute_transition_act_1()) {
                            logger.info("T07.S");
                            logger.info("D03.S");
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:1, id:2) | act -> wait | [!tokens[1] and x % 10 = 0; tokens[2] := true].
                        logger.info("T08.O");
                        if(execute_transition_act_2()) {
                            logger.info("T08.S");
                            logger.info("D03.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state update.
            private void exec_update() {
                logger.info("D04.O");
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (193 * x + 953) % 1009; b := b + 1].
                logger.info("T09.O");
                if(execute_transition_update_0()) {
                    logger.info("T09.S");
                    logger.info("D04.S");
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                logger.info("D05.O");
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | wait -> wait | [tokens[1]; tokens[1] := false].
                        logger.info("T10.O");
                        if(execute_transition_wait_0()) {
                            logger.info("T10.S");
                            logger.info("D05.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:1, id:1) | wait -> update | !tokens[2].
                        logger.info("T11.O");
                        if(execute_transition_wait_1()) {
                            logger.info("T11.S");
                            logger.info("D05.S");
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
                        case act -> exec_act();
                        case update -> exec_update();
                        case wait -> exec_wait();
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

        // Define the states fot the state machine C.
        interface P_CThread_States {
            enum States {
                act, 
                update, 
                wait
            }
        }

        // Representation of the SLCO state machine C.
        class P_CThread extends Thread implements P_CThread_States {
            // Current state
            private P_CThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int x;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            P_CThread(LockManager lockManagerInstance) {
                currentState = P_CThread.States.act;
                lockManager = lockManagerInstance;
                lock_ids = new int[2];
                target_locks = new int[2];
                random = new Random();

                // Variable instantiations.
                x = 308;
            }

            // SLCO expression wrapper | tokens[2].
            private boolean t_act_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(tokens[2]) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | act -> act | [tokens[2]; tokens[2] := false].
            private boolean execute_transition_act_0() {
                // SLCO composite | [tokens[2]; tokens[2] := false].
                // SLCO expression | tokens[2].
                if(!(t_act_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[2] := false.
                tokens[2] = false;
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_CThread.States.act;
                return true;
            }

            // SLCO expression wrapper | !tokens[2].
            private boolean t_act_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 2; // Acquire tokens[2]
                lock_ids[1] = target_locks[1] = 3 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 2);
                if(!(tokens[2])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lock_ids[1] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | x % 10 = 0.
            private boolean t_act_1_s_0_n_1() {
                if(Math.floorMod(x, 10) == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:1) | act -> wait | [!tokens[2] and x % 10 = 0; tokens[0] := true].
            private boolean execute_transition_act_1() {
                // SLCO composite | [!tokens[2] and x % 10 = 0; tokens[0] := true].
                // SLCO expression | !tokens[2] and x % 10 = 0.
                if(!(t_act_1_s_0_n_0() && t_act_1_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | tokens[0] := true.
                tokens[0] = true;
                lock_ids[0] = target_locks[1]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_CThread.States.wait;
                return true;
            }

            // SLCO expression wrapper | !tokens[2].
            private boolean t_act_2_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[2])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:2) | act -> update | !tokens[2] and x % 10 != 0.
            private boolean execute_transition_act_2() {
                // SLCO expression | !tokens[2] and x % 10 != 0.
                if(!(t_act_2_s_0_n_0() && Math.floorMod(x, 10) != 0)) {
                    return false;
                }

                currentState = P_CThread.States.update;
                return true;
            }

            // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (811 * x + 31) % 1009; c := c + 1].
            private boolean execute_transition_update_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [x := (811 * x + 31) % 1009; c := c + 1] -> [true; x := (811 * x + 31) % 1009; c := c + 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | x := (811 * x + 31) % 1009.
                x = Math.floorMod((811 * x + 31), 1009);
                // SLCO assignment | c := c + 1.
                lock_ids[0] = target_locks[0] = 2; // Acquire c
                lockManager.acquire_locks(lock_ids, 1);
                c = c + 1;
                lock_ids[0] = target_locks[0]; // Release c
                lockManager.release_locks(lock_ids, 1);

                currentState = P_CThread.States.act;
                return true;
            }

            // SLCO expression wrapper | tokens[2].
            private boolean t_wait_0_s_0_n_0() {
                lock_ids[0] = target_locks[1] = 3 + 2; // Acquire tokens[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(tokens[2]) {
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | wait -> wait | [tokens[2]; tokens[2] := false].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [tokens[2]; tokens[2] := false].
                // SLCO expression | tokens[2].
                if(!(t_wait_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | tokens[2] := false.
                tokens[2] = false;
                lock_ids[0] = target_locks[1]; // Release tokens[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = P_CThread.States.wait;
                return true;
            }

            // SLCO expression wrapper | !tokens[0].
            private boolean t_wait_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 0; // Acquire tokens[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(!(tokens[0])) {
                    lock_ids[0] = target_locks[0]; // Release tokens[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release tokens[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:1, id:1) | wait -> update | !tokens[0].
            private boolean execute_transition_wait_1() {
                // SLCO expression | !tokens[0].
                if(!(t_wait_1_s_0_n_0())) {
                    return false;
                }

                currentState = P_CThread.States.update;
                return true;
            }

            // Attempt to fire a transition starting in state act.
            private void exec_act() {
                logger.info("D06.O");
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | act -> act | [tokens[2]; tokens[2] := false].
                        logger.info("T12.O");
                        if(execute_transition_act_0()) {
                            logger.info("T12.S");
                            logger.info("D06.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:1, id:1) | act -> wait | [!tokens[2] and x % 10 = 0; tokens[0] := true].
                        logger.info("T13.O");
                        if(execute_transition_act_1()) {
                            logger.info("T13.S");
                            logger.info("D06.S");
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:1, id:2) | act -> update | !tokens[2] and x % 10 != 0.
                        logger.info("T14.O");
                        if(execute_transition_act_2()) {
                            logger.info("T14.S");
                            logger.info("D06.S");
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state update.
            private void exec_update() {
                logger.info("D07.O");
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (811 * x + 31) % 1009; c := c + 1].
                logger.info("T15.O");
                if(execute_transition_update_0()) {
                    logger.info("T15.S");
                    logger.info("D07.S");
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                logger.info("D08.O");
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | wait -> wait | [tokens[2]; tokens[2] := false].
                        logger.info("T16.O");
                        if(execute_transition_wait_0()) {
                            logger.info("T16.S");
                            logger.info("D08.S");
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:1, id:1) | wait -> update | !tokens[0].
                        logger.info("T17.O");
                        if(execute_transition_wait_1()) {
                            logger.info("T17.S");
                            logger.info("D08.S");
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
                        case act -> exec_act();
                        case update -> exec_update();
                        case wait -> exec_wait();
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
            T_A.start();
            T_B.start();
            T_C.start();
        }

        // Join all threads.
        public void joinThreads() {
            while (true) {
                try {
                    T_A.join();
                    T_B.join();
                    T_C.join();
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
        Tokens model = new Tokens();
        model.startThreads();
        model.joinThreads();

        // Include information about the model.
        logger.info("JSON {\"name\": \"Tokens\", \"settings\": \"test_models/Tokens.slco -use_random_pick -no_deterministic_structures -running_time=30 -package_name=testing.randompicknostructures -performance_measurements\", \"classes\": {\"P\": {\"name\": \"P\", \"state_machines\": {\"A\": {\"name\": \"A\", \"states\": [\"act\", \"update\", \"wait\"], \"decision_structures\": {\"act\": {\"source\": \"act\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | act -> act | [tokens[0]; tokens[0] := false]\", \"id\": \"T00\", \"source\": \"act\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | act -> update | !tokens[0] and x % 10 != 0\", \"id\": \"T01\", \"source\": \"act\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}, \"2\": {\"name\": \"(p:1, id:2) | act -> wait | [!tokens[0] and x % 10 = 0; tokens[1] := true]\", \"id\": \"T02\", \"source\": \"act\", \"target\": \"wait\", \"priority\": 1, \"is_excluded\": false}}}, \"update\": {\"source\": \"update\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | update -> act | true | [true; x := (641 * x + 718) % 1009; a := a + 1]\", \"id\": \"T03\", \"source\": \"update\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}}}, \"wait\": {\"source\": \"wait\", \"id\": \"D02\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> wait | [tokens[0]; tokens[0] := false]\", \"id\": \"T04\", \"source\": \"wait\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | wait -> update | !tokens[1]\", \"id\": \"T05\", \"source\": \"wait\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}}}, \"B\": {\"name\": \"B\", \"states\": [\"act\", \"update\", \"wait\"], \"decision_structures\": {\"act\": {\"source\": \"act\", \"id\": \"D03\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | act -> act | [tokens[1]; tokens[1] := false]\", \"id\": \"T06\", \"source\": \"act\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | act -> update | !tokens[1] and x % 10 != 0\", \"id\": \"T07\", \"source\": \"act\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}, \"2\": {\"name\": \"(p:1, id:2) | act -> wait | [!tokens[1] and x % 10 = 0; tokens[2] := true]\", \"id\": \"T08\", \"source\": \"act\", \"target\": \"wait\", \"priority\": 1, \"is_excluded\": false}}}, \"update\": {\"source\": \"update\", \"id\": \"D04\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | update -> act | true | [true; x := (193 * x + 953) % 1009; b := b + 1]\", \"id\": \"T09\", \"source\": \"update\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}}}, \"wait\": {\"source\": \"wait\", \"id\": \"D05\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> wait | [tokens[1]; tokens[1] := false]\", \"id\": \"T10\", \"source\": \"wait\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | wait -> update | !tokens[2]\", \"id\": \"T11\", \"source\": \"wait\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}}}, \"C\": {\"name\": \"C\", \"states\": [\"act\", \"update\", \"wait\"], \"decision_structures\": {\"act\": {\"source\": \"act\", \"id\": \"D06\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | act -> act | [tokens[2]; tokens[2] := false]\", \"id\": \"T12\", \"source\": \"act\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | act -> wait | [!tokens[2] and x % 10 = 0; tokens[0] := true]\", \"id\": \"T13\", \"source\": \"act\", \"target\": \"wait\", \"priority\": 1, \"is_excluded\": false}, \"2\": {\"name\": \"(p:1, id:2) | act -> update | !tokens[2] and x % 10 != 0\", \"id\": \"T14\", \"source\": \"act\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}, \"update\": {\"source\": \"update\", \"id\": \"D07\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | update -> act | true | [true; x := (811 * x + 31) % 1009; c := c + 1]\", \"id\": \"T15\", \"source\": \"update\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}}}, \"wait\": {\"source\": \"wait\", \"id\": \"D08\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> wait | [tokens[2]; tokens[2] := false]\", \"id\": \"T16\", \"source\": \"wait\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | wait -> update | !tokens[0]\", \"id\": \"T17\", \"source\": \"wait\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}}}}}}}");
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