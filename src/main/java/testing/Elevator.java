package testing;

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

// SLCO model Elevator.
public class Elevator {
    // The objects in the model.
    private final SLCO_Class[] objects;

    // Additional supporting variables.
    // Define and initialize the logger to gather the appropriate performance data with.
    private static final Logger logger;
    static {
        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "Elevator";
        String log_settings = "[T=60s]";
        MainMapLookup.setMainArguments("log_date", log_date, "log_settings", log_settings, "log_name", log_name);
        logger = LogManager.getLogger();
    }

    // Interface for SLCO classes.
    interface SLCO_Class {
        void startThreads();
        void joinThreads();
    }

    Elevator() {
        // Instantiate the objects.
        objects = new SLCO_Class[] {
                new GlobalClass(
                        new int[]{ 0, 0, 0, 0 },
                        0,
                        0,
                        (char) 0
                )
        };
    }

    private static long counter = 0;

    public static synchronized long getMessageId() {
        return counter++;
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

    // Representation of the SLCO class GlobalClass.
    private static class GlobalClass implements SLCO_Class {
        // The state machine threads.
        private final Thread T_cabin;
        private final Thread T_environment;
        private final Thread T_controller;

        // Class variables.
        private final int[] req;
        private volatile int t;
        private volatile int p;
        private volatile int v;

        GlobalClass(int[] req, int t, int p, int v) {
            // Create a lock manager.
            LockManager lockManager = new LockManager(7);

            // Instantiate the class variables.
            this.req = req;
            this.t = t;
            this.p = p;
            this.v = v;

            // Instantiate the state machine threads and pass on the class' lock manager.
            T_cabin = new GlobalClass_cabinThread(lockManager);
            T_environment = new GlobalClass_environmentThread(lockManager);
            T_controller = new GlobalClass_controllerThread(lockManager);
        }

        // Define the states fot the state machine cabin.
        interface GlobalClass_cabinThread_States {
            enum States {
                idle,
                mov,
                open
            }
        }

        // Representation of the SLCO state machine cabin.
        class GlobalClass_cabinThread extends Thread implements GlobalClass_cabinThread_States {
            // Current state
            private GlobalClass_cabinThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_cabinThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_cabinThread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[2];
                target_locks = new int[3];
                random = new Random();
            }

            // SLCO expression wrapper | v > 0.
            private boolean t_idle_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 1; // Acquire v
                lockManager.acquire_locks(lock_ids, 1);
                if(v > 0) {
                    lock_ids[0] = target_locks[0]; // Release v
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release v
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | idle -> mov | v > 0.
            private boolean execute_transition_idle_0() {
                // SLCO expression | v > 0.
                if(!(t_idle_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO expression wrapper | t = p.
            private boolean t_mov_0_s_0_n_0() {
                lock_ids[0] = target_locks[1] = 0; // Acquire p
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[0] = 2; // Acquire t
                lockManager.acquire_locks(lock_ids, 1);
                if(t == p) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lock_ids[1] = target_locks[1]; // Release p
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | mov -> open | t = p.
            private boolean execute_transition_mov_0() {
                // SLCO expression | t = p.
                if(!(t_mov_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_cabinThread.States.open;
                return true;
            }

            // SLCO expression wrapper | t < p.
            private boolean t_mov_1_s_0_n_0() {
                if(t < p) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | mov -> mov | [t < p; p := p - 1].
            private boolean execute_transition_mov_1() {
                // SLCO composite | [t < p; p := p - 1].
                // SLCO expression | t < p.
                if(!(t_mov_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | p := p - 1.
                p = p - 1;
                lock_ids[0] = target_locks[1]; // Release p
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO expression wrapper | t > p.
            private boolean t_mov_2_s_0_n_0() {
                if(t > p) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release t
                lock_ids[1] = target_locks[1]; // Release p
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | mov -> mov | [t > p; p := p + 1].
            private boolean execute_transition_mov_2() {
                // SLCO composite | [t > p; p := p + 1].
                // SLCO expression | t > p.
                if(!(t_mov_2_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | p := p + 1.
                p = p + 1;
                lock_ids[0] = target_locks[1]; // Release p
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO transition (p:0, id:0) | open -> idle | true | [true; req[p] := 0; v := 0].
            private boolean execute_transition_open_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [req[p] := 0; v := 0] -> [true; req[p] := 0; v := 0].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | req[p] := 0.
                lock_ids[0] = target_locks[2] = 0; // Acquire p
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 1; // Acquire v
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[0] = 3 + p; // Acquire req[p]
                lockManager.acquire_locks(lock_ids, 1);
                req[p] = (0) & 0xff;
                lock_ids[0] = target_locks[0]; // Release req[p]
                lock_ids[1] = target_locks[2]; // Release p
                lockManager.release_locks(lock_ids, 2);
                // SLCO assignment | v := 0.
                v = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release v
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_cabinThread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                logger.info("GlobalClass.cabin.idle.O" + "." + getMessageId());
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | idle -> mov | v > 0.
                logger.info("GlobalClass.cabin.idle.mov.0.O" + "." + getMessageId());
                if(execute_transition_idle_0()) {
                    logger.info("GlobalClass.cabin.idle.mov.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.cabin.idle.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.cabin.idle.mov.0.CF" + "." + getMessageId());
                // [SEQ.END]
                logger.info("GlobalClass.cabin.idle.CF" + "." + getMessageId());
            }

            // Attempt to fire a transition starting in state mov.
            private void exec_mov() {
                logger.info("GlobalClass.cabin.mov.O" + "." + getMessageId());
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | mov -> open | t = p.
                logger.info("GlobalClass.cabin.mov.open.0.O" + "." + getMessageId());
                if(execute_transition_mov_0()) {
                    logger.info("GlobalClass.cabin.mov.open.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.cabin.mov.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.cabin.mov.open.0.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:1) | mov -> mov | [t < p; p := p - 1].
                logger.info("GlobalClass.cabin.mov.mov.1.O" + "." + getMessageId());
                if(execute_transition_mov_1()) {
                    logger.info("GlobalClass.cabin.mov.mov.1.CS" + "." + getMessageId());
                    logger.info("GlobalClass.cabin.mov.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.cabin.mov.mov.1.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:2) | mov -> mov | [t > p; p := p + 1].
                logger.info("GlobalClass.cabin.mov.mov.2.O" + "." + getMessageId());
                if(execute_transition_mov_2()) {
                    logger.info("GlobalClass.cabin.mov.mov.2.CS" + "." + getMessageId());
                    logger.info("GlobalClass.cabin.mov.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.cabin.mov.mov.2.CF" + "." + getMessageId());
                // [DET.END]
                // [SEQ.END]
                logger.info("GlobalClass.cabin.mov.CF" + "." + getMessageId());
            }

            // Attempt to fire a transition starting in state open.
            private void exec_open() {
                logger.info("GlobalClass.cabin.open.O" + "." + getMessageId());
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | open -> idle | true | [true; req[p] := 0; v := 0].
                logger.info("GlobalClass.cabin.open.idle.0.O" + "." + getMessageId());
                if(execute_transition_open_0()) {
                    logger.info("GlobalClass.cabin.open.idle.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.cabin.open.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.cabin.open.idle.0.CF" + "." + getMessageId());
                // [SEQ.END]
                logger.info("GlobalClass.cabin.open.CF" + "." + getMessageId());
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case idle -> exec_idle();
                        case mov -> exec_mov();
                        case open -> exec_open();
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

        // Define the states fot the state machine environment.
        interface GlobalClass_environmentThread_States {
            enum States {
                read
            }
        }

        // Representation of the SLCO state machine environment.
        class GlobalClass_environmentThread extends Thread implements GlobalClass_environmentThread_States {
            // Current state
            private GlobalClass_environmentThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_environmentThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_environmentThread.States.read;
                lockManager = lockManagerInstance;
                lock_ids = new int[1];
                target_locks = new int[4];
                random = new Random();
            }

            // SLCO expression wrapper | req[0] = 0.
            private boolean t_read_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 3 + 0; // Acquire req[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(req[0] == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release req[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | read -> read | [req[0] = 0; req[0] := 1].
            private boolean execute_transition_read_0() {
                // SLCO composite | [req[0] = 0; req[0] := 1].
                // SLCO expression | req[0] = 0.
                if(!(t_read_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | req[0] := 1.
                req[0] = (1) & 0xff;
                lock_ids[0] = target_locks[0]; // Release req[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // SLCO expression wrapper | req[1] = 0.
            private boolean t_read_1_s_0_n_0() {
                lock_ids[0] = target_locks[1] = 3 + 1; // Acquire req[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(req[1] == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release req[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | read -> read | [req[1] = 0; req[1] := 1].
            private boolean execute_transition_read_1() {
                // SLCO composite | [req[1] = 0; req[1] := 1].
                // SLCO expression | req[1] = 0.
                if(!(t_read_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | req[1] := 1.
                req[1] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release req[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // SLCO expression wrapper | req[2] = 0.
            private boolean t_read_2_s_0_n_0() {
                lock_ids[0] = target_locks[2] = 3 + 2; // Acquire req[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(req[2] == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[2]; // Release req[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | read -> read | [req[2] = 0; req[2] := 1].
            private boolean execute_transition_read_2() {
                // SLCO composite | [req[2] = 0; req[2] := 1].
                // SLCO expression | req[2] = 0.
                if(!(t_read_2_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | req[2] := 1.
                req[2] = (1) & 0xff;
                lock_ids[0] = target_locks[2]; // Release req[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // SLCO expression wrapper | req[3] = 0.
            private boolean t_read_3_s_0_n_0() {
                lock_ids[0] = target_locks[3] = 3 + 3; // Acquire req[3]
                lockManager.acquire_locks(lock_ids, 1);
                if(req[3] == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[3]; // Release req[3]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:3) | read -> read | [req[3] = 0; req[3] := 1].
            private boolean execute_transition_read_3() {
                // SLCO composite | [req[3] = 0; req[3] := 1].
                // SLCO expression | req[3] = 0.
                if(!(t_read_3_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | req[3] := 1.
                req[3] = (1) & 0xff;
                lock_ids[0] = target_locks[3]; // Release req[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // Attempt to fire a transition starting in state read.
            private void exec_read() {
                logger.info("GlobalClass.environment.read.O" + "." + getMessageId());
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | read -> read | [req[0] = 0; req[0] := 1].
                logger.info("GlobalClass.environment.read.read.0.O" + "." + getMessageId());
                if(execute_transition_read_0()) {
                    logger.info("GlobalClass.environment.read.read.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.environment.read.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.environment.read.read.0.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:1) | read -> read | [req[1] = 0; req[1] := 1].
                logger.info("GlobalClass.environment.read.read.1.O" + "." + getMessageId());
                if(execute_transition_read_1()) {
                    logger.info("GlobalClass.environment.read.read.1.CS" + "." + getMessageId());
                    logger.info("GlobalClass.environment.read.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.environment.read.read.1.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:2) | read -> read | [req[2] = 0; req[2] := 1].
                logger.info("GlobalClass.environment.read.read.2.O" + "." + getMessageId());
                if(execute_transition_read_2()) {
                    logger.info("GlobalClass.environment.read.read.2.CS" + "." + getMessageId());
                    logger.info("GlobalClass.environment.read.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.environment.read.read.2.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:3) | read -> read | [req[3] = 0; req[3] := 1].
                logger.info("GlobalClass.environment.read.read.3.O" + "." + getMessageId());
                if(execute_transition_read_3()) {
                    logger.info("GlobalClass.environment.read.read.3.CS" + "." + getMessageId());
                    logger.info("GlobalClass.environment.read.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.environment.read.read.3.CF" + "." + getMessageId());
                // [SEQ.END]
                logger.info("GlobalClass.environment.read.CF" + "." + getMessageId());
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case read -> exec_read();
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

        // Define the states fot the state machine controller.
        interface GlobalClass_controllerThread_States {
            enum States {
                wait,
                work,
                done
            }
        }

        // Representation of the SLCO state machine controller.
        class GlobalClass_controllerThread extends Thread implements GlobalClass_controllerThread_States {
            // Current state
            private GlobalClass_controllerThread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int ldir;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_controllerThread(LockManager lockManagerInstance) {
                currentState = GlobalClass_controllerThread.States.wait;
                lockManager = lockManagerInstance;
                lock_ids = new int[6];
                target_locks = new int[6];
                random = new Random();

                // Variable instantiations.
                ldir = (char) 0;
            }

            // SLCO expression wrapper | v = 0.
            private boolean t_wait_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 1; // Acquire v
                lockManager.acquire_locks(lock_ids, 1);
                if(v == 0) {
                    lock_ids[0] = target_locks[0]; // Release v
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release v
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | wait -> work | [v = 0; t := t + (2 * ldir) - 1].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [v = 0; t := t + (2 * ldir) - 1].
                // SLCO expression | v = 0.
                if(!(t_wait_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | t := t + (2 * ldir) - 1.
                lock_ids[0] = target_locks[1] = 2; // Acquire t
                lockManager.acquire_locks(lock_ids, 1);
                t = t + (2 * ldir) - 1;
                lock_ids[0] = target_locks[1]; // Release t
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_controllerThread.States.work;
                return true;
            }

            // SLCO expression wrapper | t < 0.
            private boolean t_work_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 2; // Acquire t
                lockManager.acquire_locks(lock_ids, 1);
                if(t < 0) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO expression wrapper | t = 4.
            private boolean t_work_0_s_0_n_1() {
                if(t == 4) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | work -> wait | [t < 0 or t = 4; ldir := 1 - ldir].
            private boolean execute_transition_work_0() {
                // SLCO composite | [t < 0 or t = 4; ldir := 1 - ldir].
                // SLCO expression | t < 0 or t = 4.
                if(!(t_work_0_s_0_n_0() || t_work_0_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | ldir := 1 - ldir.
                ldir = (1 - ldir) & 0xff;

                currentState = GlobalClass_controllerThread.States.wait;
                return true;
            }

            // SLCO expression wrapper | t >= 0 and t < 4.
            private boolean t_work_1_s_0_n_0() {
                if(t >= 0 && t < 4) {
                    return true;
                }
                lock_ids[0] = target_locks[1] = 3 + 0; // Acquire req[0]
                lock_ids[1] = target_locks[2] = 3 + 3; // Acquire req[3]
                lock_ids[2] = target_locks[3] = 3 + 2; // Acquire req[2]
                lock_ids[3] = target_locks[4] = 3 + 1; // Acquire req[1]
                lockManager.acquire_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | req[t] = 1.
            private boolean t_work_1_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 3 + 0; // Acquire req[0]
                lock_ids[1] = target_locks[2] = 3 + 3; // Acquire req[3]
                lock_ids[2] = target_locks[3] = 3 + 2; // Acquire req[2]
                lock_ids[3] = target_locks[4] = 3 + 1; // Acquire req[1]
                lock_ids[4] = target_locks[5] = 3 + t; // Acquire req[t]
                lockManager.acquire_locks(lock_ids, 5);
                if(req[t] == 1) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lock_ids[1] = target_locks[1]; // Release req[0]
                    lock_ids[2] = target_locks[2]; // Release req[3]
                    lock_ids[3] = target_locks[3]; // Release req[2]
                    lock_ids[4] = target_locks[4]; // Release req[1]
                    lock_ids[5] = target_locks[5]; // Release req[t]
                    lockManager.release_locks(lock_ids, 6);
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release req[t]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | work -> done | t >= 0 and t < 4 and req[t] = 1.
            private boolean execute_transition_work_1() {
                // SLCO expression | t >= 0 and t < 4 and req[t] = 1.
                if(!(t_work_1_s_0_n_0() && t_work_1_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_controllerThread.States.done;
                return true;
            }

            // SLCO expression wrapper | t >= 0.
            private boolean t_work_2_s_0_n_0() {
                if(t >= 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release t
                lock_ids[1] = target_locks[1]; // Release req[0]
                lock_ids[2] = target_locks[2]; // Release req[3]
                lock_ids[3] = target_locks[3]; // Release req[2]
                lock_ids[4] = target_locks[4]; // Release req[1]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO expression wrapper | t < 4.
            private boolean t_work_2_s_0_n_1() {
                if(t < 4) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release t
                lock_ids[1] = target_locks[1]; // Release req[0]
                lock_ids[2] = target_locks[2]; // Release req[3]
                lock_ids[3] = target_locks[3]; // Release req[2]
                lock_ids[4] = target_locks[4]; // Release req[1]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO expression wrapper | req[t] = 0.
            private boolean t_work_2_s_0_n_2() {
                if(req[t] == 0) {
                    lock_ids[0] = target_locks[1]; // Release req[0]
                    lock_ids[1] = target_locks[2]; // Release req[3]
                    lock_ids[2] = target_locks[3]; // Release req[2]
                    lock_ids[3] = target_locks[4]; // Release req[1]
                    lockManager.release_locks(lock_ids, 4);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release t
                lock_ids[1] = target_locks[1]; // Release req[0]
                lock_ids[2] = target_locks[2]; // Release req[3]
                lock_ids[3] = target_locks[3]; // Release req[2]
                lock_ids[4] = target_locks[4]; // Release req[1]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO transition (p:0, id:2) | work -> work | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
            private boolean execute_transition_work_2() {
                // SLCO composite | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
                // SLCO expression | t >= 0 and t < 4 and req[t] = 0.
                if(!(t_work_2_s_0_n_0() && t_work_2_s_0_n_1() && t_work_2_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | t := t + (2 * ldir) - 1.
                t = t + (2 * ldir) - 1;
                lock_ids[0] = target_locks[0]; // Release t
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_controllerThread.States.work;
                return true;
            }

            // SLCO transition (p:0, id:0) | done -> wait | true | v := 1.
            private boolean execute_transition_done_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [v := 1] -> v := 1.
                lock_ids[0] = target_locks[0] = 1; // Acquire v
                lockManager.acquire_locks(lock_ids, 1);
                v = (1) & 0xff;
                lock_ids[0] = target_locks[0]; // Release v
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_controllerThread.States.wait;
                return true;
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                logger.info("GlobalClass.controller.wait.O" + "." + getMessageId());
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | wait -> work | [v = 0; t := t + (2 * ldir) - 1].
                logger.info("GlobalClass.controller.wait.work.0.O" + "." + getMessageId());
                if(execute_transition_wait_0()) {
                    logger.info("GlobalClass.controller.wait.work.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.controller.wait.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.controller.wait.work.0.CF" + "." + getMessageId());
                // [SEQ.END]
                logger.info("GlobalClass.controller.wait.CF" + "." + getMessageId());
            }

            // Attempt to fire a transition starting in state work.
            private void exec_work() {
                logger.info("GlobalClass.controller.work.O" + "." + getMessageId());
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | work -> wait | [t < 0 or t = 4; ldir := 1 - ldir].
                logger.info("GlobalClass.controller.work.wait.0.O" + "." + getMessageId());
                if(execute_transition_work_0()) {
                    logger.info("GlobalClass.controller.work.wait.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.controller.work.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.controller.work.wait.0.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:1) | work -> done | t >= 0 and t < 4 and req[t] = 1.
                logger.info("GlobalClass.controller.work.done.1.O" + "." + getMessageId());
                if(execute_transition_work_1()) {
                    logger.info("GlobalClass.controller.work.done.1.CS" + "." + getMessageId());
                    logger.info("GlobalClass.controller.work.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.controller.work.done.1.CF" + "." + getMessageId());
                // SLCO transition (p:0, id:2) | work -> work | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
                logger.info("GlobalClass.controller.work.work.2.O" + "." + getMessageId());
                if(execute_transition_work_2()) {
                    logger.info("GlobalClass.controller.work.work.2.CS" + "." + getMessageId());
                    logger.info("GlobalClass.controller.work.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.controller.work.work.2.CF" + "." + getMessageId());
                // [DET.END]
                // [SEQ.END]
                logger.info("GlobalClass.controller.work.CF" + "." + getMessageId());
            }

            // Attempt to fire a transition starting in state done.
            private void exec_done() {
                logger.info("GlobalClass.controller.done.O" + "." + getMessageId());
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | done -> wait | true | v := 1.
                logger.info("GlobalClass.controller.done.wait.0.O" + "." + getMessageId());
                if(execute_transition_done_0()) {
                    logger.info("GlobalClass.controller.done.wait.0.CS" + "." + getMessageId());
                    logger.info("GlobalClass.controller.done.CS" + "." + getMessageId());
                    return;
                }
                logger.info("GlobalClass.controller.done.wait.0.CF" + "." + getMessageId());
                // [SEQ.END]
                logger.info("GlobalClass.controller.done.CF" + "." + getMessageId());
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 60) {
                    switch(currentState) {
                        case wait -> exec_wait();
                        case work -> exec_work();
                        case done -> exec_done();
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
            T_cabin.start();
            T_environment.start();
            T_controller.start();
        }

        // Join all threads.
        public void joinThreads() {
            while (true) {
                try {
                    T_cabin.join();
                    T_environment.join();
                    T_controller.join();
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
        Elevator model = new Elevator();
        model.startThreads();
        model.joinThreads();

        // Give the logger time to finish asynchronous tasks.
        try {
            Thread.sleep(2000);
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
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}