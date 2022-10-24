package testing.nolocks.counting;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        String log_settings = "[NL,T=30s]";
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
            private long D02_O;
            private long D02_F;
            private long D02_S;
            private long T04_O;
            private long T04_F;
            private long T04_S;
            private long T05_O;
            private long T05_F;
            private long T05_S;

            P_AThread(LockManager lockManagerInstance) {
                currentState = P_AThread.States.act;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                x = 1;
            }

            // SLCO transition (p:0, id:0) | act -> act | [tokens[0]; tokens[0] := false].
            private boolean execute_transition_act_0() {
                // SLCO composite | [tokens[0]; tokens[0] := false].
                // SLCO expression | tokens[0].
                if(!(tokens[0])) {
                    return false;
                }
                // SLCO assignment | tokens[0] := false.
                tokens[0] = false;

                currentState = P_AThread.States.act;
                return true;
            }

            // SLCO transition (p:1, id:1) | act -> update | !tokens[0] and x % 10 != 0.
            private boolean execute_transition_act_1() {
                // SLCO expression | !tokens[0] and x % 10 != 0.
                if(!(!(tokens[0]) && Math.floorMod(x, 10) != 0)) {
                    return false;
                }

                currentState = P_AThread.States.update;
                return true;
            }

            // SLCO transition (p:1, id:2) | act -> wait | [!tokens[0] and x % 10 = 0; tokens[1] := true].
            private boolean execute_transition_act_2() {
                // SLCO composite | [!tokens[0] and x % 10 = 0; tokens[1] := true].
                // SLCO expression | !tokens[0] and x % 10 = 0.
                if(!(!(tokens[0]) && Math.floorMod(x, 10) == 0)) {
                    return false;
                }
                // SLCO assignment | tokens[1] := true.
                tokens[1] = true;

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
                a = a + 1;

                currentState = P_AThread.States.act;
                return true;
            }

            // SLCO transition (p:0, id:0) | wait -> wait | [tokens[0]; tokens[0] := false].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [tokens[0]; tokens[0] := false].
                // SLCO expression | tokens[0].
                if(!(tokens[0])) {
                    return false;
                }
                // SLCO assignment | tokens[0] := false.
                tokens[0] = false;

                currentState = P_AThread.States.wait;
                return true;
            }

            // SLCO transition (p:1, id:1) | wait -> update | !tokens[1].
            private boolean execute_transition_wait_1() {
                // SLCO expression | !tokens[1].
                if(!(!(tokens[1]))) {
                    return false;
                }

                currentState = P_AThread.States.update;
                return true;
            }

            // Attempt to fire a transition starting in state act.
            private void exec_act() {
                D00_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | act -> act | [tokens[0]; tokens[0] := false].
                T00_O++;
                if(execute_transition_act_0()) {
                    T00_S++;
                    D00_S++;
                    return;
                }
                // [DET.START]
                // SLCO transition (p:1, id:1) | act -> update | !tokens[0] and x % 10 != 0.
                T01_O++;
                if(execute_transition_act_1()) {
                    T01_S++;
                    D00_S++;
                    return;
                }
                // SLCO transition (p:1, id:2) | act -> wait | [!tokens[0] and x % 10 = 0; tokens[1] := true].
                T02_O++;
                if(execute_transition_act_2()) {
                    T02_S++;
                    D00_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state update.
            private void exec_update() {
                D01_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (641 * x + 718) % 1009; a := a + 1].
                T03_O++;
                if(execute_transition_update_0()) {
                    T03_S++;
                    D01_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                D02_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | wait -> wait | [tokens[0]; tokens[0] := false].
                T04_O++;
                if(execute_transition_wait_0()) {
                    T04_S++;
                    D02_S++;
                    return;
                }
                // SLCO transition (p:1, id:1) | wait -> update | !tokens[1].
                T05_O++;
                if(execute_transition_wait_1()) {
                    T05_S++;
                    D02_S++;
                    return;
                }
                // [SEQ.END]
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
                logger.info("D02.O " + D02_O);
                logger.info("D02.F " + D02_F);
                logger.info("D02.S " + D02_S);
                logger.info("T04.O " + T04_O);
                logger.info("T04.F " + T04_F);
                logger.info("T04.S " + T04_S);
                logger.info("T05.O " + T05_O);
                logger.info("T05.F " + T05_F);
                logger.info("T05.S " + T05_S);
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

            // Add variables needed for measurements.
            private long D03_O;
            private long D03_F;
            private long D03_S;
            private long T06_O;
            private long T06_F;
            private long T06_S;
            private long T07_O;
            private long T07_F;
            private long T07_S;
            private long T08_O;
            private long T08_F;
            private long T08_S;
            private long D04_O;
            private long D04_F;
            private long D04_S;
            private long T09_O;
            private long T09_F;
            private long T09_S;
            private long D05_O;
            private long D05_F;
            private long D05_S;
            private long T10_O;
            private long T10_F;
            private long T10_S;
            private long T11_O;
            private long T11_F;
            private long T11_S;

            P_BThread(LockManager lockManagerInstance) {
                currentState = P_BThread.States.act;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                x = 42;
            }

            // SLCO transition (p:0, id:0) | act -> act | [tokens[1]; tokens[1] := false].
            private boolean execute_transition_act_0() {
                // SLCO composite | [tokens[1]; tokens[1] := false].
                // SLCO expression | tokens[1].
                if(!(tokens[1])) {
                    return false;
                }
                // SLCO assignment | tokens[1] := false.
                tokens[1] = false;

                currentState = P_BThread.States.act;
                return true;
            }

            // SLCO transition (p:1, id:1) | act -> update | !tokens[1] and x % 10 != 0.
            private boolean execute_transition_act_1() {
                // SLCO expression | !tokens[1] and x % 10 != 0.
                if(!(!(tokens[1]) && Math.floorMod(x, 10) != 0)) {
                    return false;
                }

                currentState = P_BThread.States.update;
                return true;
            }

            // SLCO transition (p:1, id:2) | act -> wait | [!tokens[1] and x % 10 = 0; tokens[2] := true].
            private boolean execute_transition_act_2() {
                // SLCO composite | [!tokens[1] and x % 10 = 0; tokens[2] := true].
                // SLCO expression | !tokens[1] and x % 10 = 0.
                if(!(!(tokens[1]) && Math.floorMod(x, 10) == 0)) {
                    return false;
                }
                // SLCO assignment | tokens[2] := true.
                tokens[2] = true;

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
                b = b + 1;

                currentState = P_BThread.States.act;
                return true;
            }

            // SLCO transition (p:0, id:0) | wait -> wait | [tokens[1]; tokens[1] := false].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [tokens[1]; tokens[1] := false].
                // SLCO expression | tokens[1].
                if(!(tokens[1])) {
                    return false;
                }
                // SLCO assignment | tokens[1] := false.
                tokens[1] = false;

                currentState = P_BThread.States.wait;
                return true;
            }

            // SLCO transition (p:1, id:1) | wait -> update | !tokens[2].
            private boolean execute_transition_wait_1() {
                // SLCO expression | !tokens[2].
                if(!(!(tokens[2]))) {
                    return false;
                }

                currentState = P_BThread.States.update;
                return true;
            }

            // Attempt to fire a transition starting in state act.
            private void exec_act() {
                D03_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | act -> act | [tokens[1]; tokens[1] := false].
                T06_O++;
                if(execute_transition_act_0()) {
                    T06_S++;
                    D03_S++;
                    return;
                }
                // [DET.START]
                // SLCO transition (p:1, id:1) | act -> update | !tokens[1] and x % 10 != 0.
                T07_O++;
                if(execute_transition_act_1()) {
                    T07_S++;
                    D03_S++;
                    return;
                }
                // SLCO transition (p:1, id:2) | act -> wait | [!tokens[1] and x % 10 = 0; tokens[2] := true].
                T08_O++;
                if(execute_transition_act_2()) {
                    T08_S++;
                    D03_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state update.
            private void exec_update() {
                D04_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (193 * x + 953) % 1009; b := b + 1].
                T09_O++;
                if(execute_transition_update_0()) {
                    T09_S++;
                    D04_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                D05_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | wait -> wait | [tokens[1]; tokens[1] := false].
                T10_O++;
                if(execute_transition_wait_0()) {
                    T10_S++;
                    D05_S++;
                    return;
                }
                // SLCO transition (p:1, id:1) | wait -> update | !tokens[2].
                T11_O++;
                if(execute_transition_wait_1()) {
                    T11_S++;
                    D05_S++;
                    return;
                }
                // [SEQ.END]
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

                // Report all counts.
                logger.info("D03.O " + D03_O);
                logger.info("D03.F " + D03_F);
                logger.info("D03.S " + D03_S);
                logger.info("T06.O " + T06_O);
                logger.info("T06.F " + T06_F);
                logger.info("T06.S " + T06_S);
                logger.info("T07.O " + T07_O);
                logger.info("T07.F " + T07_F);
                logger.info("T07.S " + T07_S);
                logger.info("T08.O " + T08_O);
                logger.info("T08.F " + T08_F);
                logger.info("T08.S " + T08_S);
                logger.info("D04.O " + D04_O);
                logger.info("D04.F " + D04_F);
                logger.info("D04.S " + D04_S);
                logger.info("T09.O " + T09_O);
                logger.info("T09.F " + T09_F);
                logger.info("T09.S " + T09_S);
                logger.info("D05.O " + D05_O);
                logger.info("D05.F " + D05_F);
                logger.info("D05.S " + D05_S);
                logger.info("T10.O " + T10_O);
                logger.info("T10.F " + T10_F);
                logger.info("T10.S " + T10_S);
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
            private long D08_O;
            private long D08_F;
            private long D08_S;
            private long T16_O;
            private long T16_F;
            private long T16_S;
            private long T17_O;
            private long T17_F;
            private long T17_S;

            P_CThread(LockManager lockManagerInstance) {
                currentState = P_CThread.States.act;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                x = 308;
            }

            // SLCO transition (p:0, id:0) | act -> act | [tokens[2]; tokens[2] := false].
            private boolean execute_transition_act_0() {
                // SLCO composite | [tokens[2]; tokens[2] := false].
                // SLCO expression | tokens[2].
                if(!(tokens[2])) {
                    return false;
                }
                // SLCO assignment | tokens[2] := false.
                tokens[2] = false;

                currentState = P_CThread.States.act;
                return true;
            }

            // SLCO transition (p:1, id:1) | act -> wait | [!tokens[2] and x % 10 = 0; tokens[0] := true].
            private boolean execute_transition_act_1() {
                // SLCO composite | [!tokens[2] and x % 10 = 0; tokens[0] := true].
                // SLCO expression | !tokens[2] and x % 10 = 0.
                if(!(!(tokens[2]) && Math.floorMod(x, 10) == 0)) {
                    return false;
                }
                // SLCO assignment | tokens[0] := true.
                tokens[0] = true;

                currentState = P_CThread.States.wait;
                return true;
            }

            // SLCO transition (p:1, id:2) | act -> update | !tokens[2] and x % 10 != 0.
            private boolean execute_transition_act_2() {
                // SLCO expression | !tokens[2] and x % 10 != 0.
                if(!(!(tokens[2]) && Math.floorMod(x, 10) != 0)) {
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
                c = c + 1;

                currentState = P_CThread.States.act;
                return true;
            }

            // SLCO transition (p:0, id:0) | wait -> wait | [tokens[2]; tokens[2] := false].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [tokens[2]; tokens[2] := false].
                // SLCO expression | tokens[2].
                if(!(tokens[2])) {
                    return false;
                }
                // SLCO assignment | tokens[2] := false.
                tokens[2] = false;

                currentState = P_CThread.States.wait;
                return true;
            }

            // SLCO transition (p:1, id:1) | wait -> update | !tokens[0].
            private boolean execute_transition_wait_1() {
                // SLCO expression | !tokens[0].
                if(!(!(tokens[0]))) {
                    return false;
                }

                currentState = P_CThread.States.update;
                return true;
            }

            // Attempt to fire a transition starting in state act.
            private void exec_act() {
                D06_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | act -> act | [tokens[2]; tokens[2] := false].
                T12_O++;
                if(execute_transition_act_0()) {
                    T12_S++;
                    D06_S++;
                    return;
                }
                // [DET.START]
                // SLCO transition (p:1, id:1) | act -> wait | [!tokens[2] and x % 10 = 0; tokens[0] := true].
                T13_O++;
                if(execute_transition_act_1()) {
                    T13_S++;
                    D06_S++;
                    return;
                }
                // SLCO transition (p:1, id:2) | act -> update | !tokens[2] and x % 10 != 0.
                T14_O++;
                if(execute_transition_act_2()) {
                    T14_S++;
                    D06_S++;
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state update.
            private void exec_update() {
                D07_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | update -> act | true | [true; x := (811 * x + 31) % 1009; c := c + 1].
                T15_O++;
                if(execute_transition_update_0()) {
                    T15_S++;
                    D07_S++;
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                D08_O++;
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | wait -> wait | [tokens[2]; tokens[2] := false].
                T16_O++;
                if(execute_transition_wait_0()) {
                    T16_S++;
                    D08_S++;
                    return;
                }
                // SLCO transition (p:1, id:1) | wait -> update | !tokens[0].
                T17_O++;
                if(execute_transition_wait_1()) {
                    T17_S++;
                    D08_S++;
                    return;
                }
                // [SEQ.END]
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
                logger.info("D08.O " + D08_O);
                logger.info("D08.F " + D08_F);
                logger.info("D08.S " + D08_S);
                logger.info("T16.O " + T16_O);
                logger.info("T16.F " + T16_F);
                logger.info("T16.S " + T16_S);
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
        logger.info("JSON {\"name\": \"Tokens\", \"settings\": \"test_models/Tokens.slco -no_locks -running_time=30 -package_name=testing.nolocks -performance_measurements\", \"classes\": {\"P\": {\"name\": \"P\", \"state_machines\": {\"A\": {\"name\": \"A\", \"states\": [\"act\", \"update\", \"wait\"], \"decision_structures\": {\"act\": {\"source\": \"act\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | act -> act | [tokens[0]; tokens[0] := false]\", \"id\": \"T00\", \"source\": \"act\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | act -> update | !tokens[0] and x % 10 != 0\", \"id\": \"T01\", \"source\": \"act\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}, \"2\": {\"name\": \"(p:1, id:2) | act -> wait | [!tokens[0] and x % 10 = 0; tokens[1] := true]\", \"id\": \"T02\", \"source\": \"act\", \"target\": \"wait\", \"priority\": 1, \"is_excluded\": false}}}, \"update\": {\"source\": \"update\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | update -> act | true | [true; x := (641 * x + 718) % 1009; a := a + 1]\", \"id\": \"T03\", \"source\": \"update\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}}}, \"wait\": {\"source\": \"wait\", \"id\": \"D02\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> wait | [tokens[0]; tokens[0] := false]\", \"id\": \"T04\", \"source\": \"wait\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | wait -> update | !tokens[1]\", \"id\": \"T05\", \"source\": \"wait\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}}}, \"B\": {\"name\": \"B\", \"states\": [\"act\", \"update\", \"wait\"], \"decision_structures\": {\"act\": {\"source\": \"act\", \"id\": \"D03\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | act -> act | [tokens[1]; tokens[1] := false]\", \"id\": \"T06\", \"source\": \"act\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | act -> update | !tokens[1] and x % 10 != 0\", \"id\": \"T07\", \"source\": \"act\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}, \"2\": {\"name\": \"(p:1, id:2) | act -> wait | [!tokens[1] and x % 10 = 0; tokens[2] := true]\", \"id\": \"T08\", \"source\": \"act\", \"target\": \"wait\", \"priority\": 1, \"is_excluded\": false}}}, \"update\": {\"source\": \"update\", \"id\": \"D04\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | update -> act | true | [true; x := (193 * x + 953) % 1009; b := b + 1]\", \"id\": \"T09\", \"source\": \"update\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}}}, \"wait\": {\"source\": \"wait\", \"id\": \"D05\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> wait | [tokens[1]; tokens[1] := false]\", \"id\": \"T10\", \"source\": \"wait\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | wait -> update | !tokens[2]\", \"id\": \"T11\", \"source\": \"wait\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}}}, \"C\": {\"name\": \"C\", \"states\": [\"act\", \"update\", \"wait\"], \"decision_structures\": {\"act\": {\"source\": \"act\", \"id\": \"D06\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | act -> act | [tokens[2]; tokens[2] := false]\", \"id\": \"T12\", \"source\": \"act\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | act -> wait | [!tokens[2] and x % 10 = 0; tokens[0] := true]\", \"id\": \"T13\", \"source\": \"act\", \"target\": \"wait\", \"priority\": 1, \"is_excluded\": false}, \"2\": {\"name\": \"(p:1, id:2) | act -> update | !tokens[2] and x % 10 != 0\", \"id\": \"T14\", \"source\": \"act\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}, \"update\": {\"source\": \"update\", \"id\": \"D07\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | update -> act | true | [true; x := (811 * x + 31) % 1009; c := c + 1]\", \"id\": \"T15\", \"source\": \"update\", \"target\": \"act\", \"priority\": 0, \"is_excluded\": false}}}, \"wait\": {\"source\": \"wait\", \"id\": \"D08\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> wait | [tokens[2]; tokens[2] := false]\", \"id\": \"T16\", \"source\": \"wait\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:1, id:1) | wait -> update | !tokens[0]\", \"id\": \"T17\", \"source\": \"wait\", \"target\": \"update\", \"priority\": 1, \"is_excluded\": false}}}}}}}}}");
    }
}