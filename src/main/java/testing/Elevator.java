package testing;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        String log_settings = "[NL,T=10s]";
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
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();
            }

            // SLCO transition (p:0, id:0) | idle -> mov | v > 0.
            private boolean execute_transition_idle_0() {
                // SLCO expression | v > 0.
                if(!(v > 0)) {
                    return false;
                }

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO transition (p:0, id:0) | mov -> open | t = p.
            private boolean execute_transition_mov_0() {
                // SLCO expression | t = p.
                if(!(t == p)) {
                    return false;
                }

                currentState = GlobalClass_cabinThread.States.open;
                return true;
            }

            // SLCO transition (p:0, id:1) | mov -> mov | [t < p; p := p - 1].
            private boolean execute_transition_mov_1() {
                // SLCO composite | [t < p; p := p - 1].
                // SLCO expression | t < p.
                if(!(t < p)) {
                    return false;
                }
                // SLCO assignment | p := p - 1.
                p = p - 1;

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO transition (p:0, id:2) | mov -> mov | [t > p; p := p + 1].
            private boolean execute_transition_mov_2() {
                // SLCO composite | [t > p; p := p + 1].
                // SLCO expression | t > p.
                if(!(t > p)) {
                    return false;
                }
                // SLCO assignment | p := p + 1.
                p = p + 1;

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO transition (p:0, id:0) | open -> idle | true | [true; req[p] := 0; v := 0].
            private boolean execute_transition_open_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [req[p] := 0; v := 0] -> [true; req[p] := 0; v := 0].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | req[p] := 0.
                req[p] = (0) & 0xff;
                // SLCO assignment | v := 0.
                v = (0) & 0xff;

                currentState = GlobalClass_cabinThread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                logger.info("D.O GlobalClass cabin idle");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | idle -> mov | v > 0.
                logger.info("T.O GlobalClass cabin 0 idle mov");
                if(execute_transition_idle_0()) {
                    logger.info("T.CS GlobalClass cabin 0 idle mov");
                    return;
                }
                logger.info("T.CF GlobalClass cabin 0 idle mov");
                // [SEQ.END]
                logger.info("D.CF GlobalClass cabin idle");
            }

            // Attempt to fire a transition starting in state mov.
            private void exec_mov() {
                logger.info("D.O GlobalClass cabin mov");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | mov -> open | t = p.
                logger.info("T.O GlobalClass cabin 0 mov open");
                if(execute_transition_mov_0()) {
                    logger.info("T.CS GlobalClass cabin 0 mov open");
                    return;
                }
                logger.info("T.CF GlobalClass cabin 0 mov open");
                // SLCO transition (p:0, id:1) | mov -> mov | [t < p; p := p - 1].
                logger.info("T.O GlobalClass cabin 1 mov mov");
                if(execute_transition_mov_1()) {
                    logger.info("T.CS GlobalClass cabin 1 mov mov");
                    return;
                }
                logger.info("T.CF GlobalClass cabin 1 mov mov");
                // SLCO transition (p:0, id:2) | mov -> mov | [t > p; p := p + 1].
                logger.info("T.O GlobalClass cabin 2 mov mov");
                if(execute_transition_mov_2()) {
                    logger.info("T.CS GlobalClass cabin 2 mov mov");
                    return;
                }
                logger.info("T.CF GlobalClass cabin 2 mov mov");
                // [DET.END]
                // [SEQ.END]
                logger.info("D.CF GlobalClass cabin mov");
            }

            // Attempt to fire a transition starting in state open.
            private void exec_open() {
                logger.info("D.O GlobalClass cabin open");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | open -> idle | true | [true; req[p] := 0; v := 0].
                logger.info("T.O GlobalClass cabin 0 open idle");
                if(execute_transition_open_0()) {
                    logger.info("T.CS GlobalClass cabin 0 open idle");
                    return;
                }
                logger.info("T.CF GlobalClass cabin 0 open idle");
                // [SEQ.END]
                logger.info("D.CF GlobalClass cabin open");
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 10) {
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
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();
            }

            // SLCO transition (p:0, id:0) | read -> read | [req[0] = 0; req[0] := 1].
            private boolean execute_transition_read_0() {
                // SLCO composite | [req[0] = 0; req[0] := 1].
                // SLCO expression | req[0] = 0.
                if(!(req[0] == 0)) {
                    return false;
                }
                // SLCO assignment | req[0] := 1.
                req[0] = (1) & 0xff;

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // SLCO transition (p:0, id:1) | read -> read | [req[1] = 0; req[1] := 1].
            private boolean execute_transition_read_1() {
                // SLCO composite | [req[1] = 0; req[1] := 1].
                // SLCO expression | req[1] = 0.
                if(!(req[1] == 0)) {
                    return false;
                }
                // SLCO assignment | req[1] := 1.
                req[1] = (1) & 0xff;

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // SLCO transition (p:0, id:2) | read -> read | [req[2] = 0; req[2] := 1].
            private boolean execute_transition_read_2() {
                // SLCO composite | [req[2] = 0; req[2] := 1].
                // SLCO expression | req[2] = 0.
                if(!(req[2] == 0)) {
                    return false;
                }
                // SLCO assignment | req[2] := 1.
                req[2] = (1) & 0xff;

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // SLCO transition (p:0, id:3) | read -> read | [req[3] = 0; req[3] := 1].
            private boolean execute_transition_read_3() {
                // SLCO composite | [req[3] = 0; req[3] := 1].
                // SLCO expression | req[3] = 0.
                if(!(req[3] == 0)) {
                    return false;
                }
                // SLCO assignment | req[3] := 1.
                req[3] = (1) & 0xff;

                currentState = GlobalClass_environmentThread.States.read;
                return true;
            }

            // Attempt to fire a transition starting in state read.
            private void exec_read() {
                logger.info("D.O GlobalClass environment read");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | read -> read | [req[0] = 0; req[0] := 1].
                logger.info("T.O GlobalClass environment 0 read read");
                if(execute_transition_read_0()) {
                    logger.info("T.CS GlobalClass environment 0 read read");
                    return;
                }
                logger.info("T.CF GlobalClass environment 0 read read");
                // SLCO transition (p:0, id:1) | read -> read | [req[1] = 0; req[1] := 1].
                logger.info("T.O GlobalClass environment 1 read read");
                if(execute_transition_read_1()) {
                    logger.info("T.CS GlobalClass environment 1 read read");
                    return;
                }
                logger.info("T.CF GlobalClass environment 1 read read");
                // SLCO transition (p:0, id:2) | read -> read | [req[2] = 0; req[2] := 1].
                logger.info("T.O GlobalClass environment 2 read read");
                if(execute_transition_read_2()) {
                    logger.info("T.CS GlobalClass environment 2 read read");
                    return;
                }
                logger.info("T.CF GlobalClass environment 2 read read");
                // SLCO transition (p:0, id:3) | read -> read | [req[3] = 0; req[3] := 1].
                logger.info("T.O GlobalClass environment 3 read read");
                if(execute_transition_read_3()) {
                    logger.info("T.CS GlobalClass environment 3 read read");
                    return;
                }
                logger.info("T.CF GlobalClass environment 3 read read");
                // [SEQ.END]
                logger.info("D.CF GlobalClass environment read");
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 10) {
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
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                ldir = (char) 0;
            }

            // SLCO transition (p:0, id:0) | wait -> work | [v = 0; t := t + (2 * ldir) - 1].
            private boolean execute_transition_wait_0() {
                // SLCO composite | [v = 0; t := t + (2 * ldir) - 1].
                // SLCO expression | v = 0.
                if(!(v == 0)) {
                    return false;
                }
                // SLCO assignment | t := t + (2 * ldir) - 1.
                t = t + (2 * ldir) - 1;

                currentState = GlobalClass_controllerThread.States.work;
                return true;
            }

            // SLCO transition (p:0, id:0) | work -> wait | [t < 0 or t = 4; ldir := 1 - ldir].
            private boolean execute_transition_work_0() {
                // SLCO composite | [t < 0 or t = 4; ldir := 1 - ldir].
                // SLCO expression | t < 0 or t = 4.
                if(!(t < 0 || t == 4)) {
                    return false;
                }
                // SLCO assignment | ldir := 1 - ldir.
                ldir = (1 - ldir) & 0xff;

                currentState = GlobalClass_controllerThread.States.wait;
                return true;
            }

            // SLCO transition (p:0, id:1) | work -> done | t >= 0 and t < 4 and req[t] = 1.
            private boolean execute_transition_work_1() {
                // SLCO expression | t >= 0 and t < 4 and req[t] = 1.
                if(!(t >= 0 && t < 4 && req[t] == 1)) {
                    return false;
                }

                currentState = GlobalClass_controllerThread.States.done;
                return true;
            }

            // SLCO transition (p:0, id:2) | work -> work | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
            private boolean execute_transition_work_2() {
                // SLCO composite | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
                // SLCO expression | t >= 0 and t < 4 and req[t] = 0.
                if(!(t >= 0 && t < 4 && req[t] == 0)) {
                    return false;
                }
                // SLCO assignment | t := t + (2 * ldir) - 1.
                t = t + (2 * ldir) - 1;

                currentState = GlobalClass_controllerThread.States.work;
                return true;
            }

            // SLCO transition (p:0, id:0) | done -> wait | true | v := 1.
            private boolean execute_transition_done_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [v := 1] -> v := 1.
                v = (1) & 0xff;

                currentState = GlobalClass_controllerThread.States.wait;
                return true;
            }

            // Attempt to fire a transition starting in state wait.
            private void exec_wait() {
                logger.info("D.O GlobalClass controller wait");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | wait -> work | [v = 0; t := t + (2 * ldir) - 1].
                logger.info("T.O GlobalClass controller 0 wait work");
                if(execute_transition_wait_0()) {
                    logger.info("T.CS GlobalClass controller 0 wait work");
                    return;
                }
                logger.info("T.CF GlobalClass controller 0 wait work");
                // [SEQ.END]
                logger.info("D.CF GlobalClass controller wait");
            }

            // Attempt to fire a transition starting in state work.
            private void exec_work() {
                logger.info("D.O GlobalClass controller work");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | work -> wait | [t < 0 or t = 4; ldir := 1 - ldir].
                logger.info("T.O GlobalClass controller 0 work wait");
                if(execute_transition_work_0()) {
                    logger.info("T.CS GlobalClass controller 0 work wait");
                    return;
                }
                logger.info("T.CF GlobalClass controller 0 work wait");
                // SLCO transition (p:0, id:1) | work -> done | t >= 0 and t < 4 and req[t] = 1.
                logger.info("T.O GlobalClass controller 1 work done");
                if(execute_transition_work_1()) {
                    logger.info("T.CS GlobalClass controller 1 work done");
                    return;
                }
                logger.info("T.CF GlobalClass controller 1 work done");
                // SLCO transition (p:0, id:2) | work -> work | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
                logger.info("T.O GlobalClass controller 2 work work");
                if(execute_transition_work_2()) {
                    logger.info("T.CS GlobalClass controller 2 work work");
                    return;
                }
                logger.info("T.CF GlobalClass controller 2 work work");
                // [DET.END]
                // [SEQ.END]
                logger.info("D.CF GlobalClass controller work");
            }

            // Attempt to fire a transition starting in state done.
            private void exec_done() {
                logger.info("D.O GlobalClass controller done");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | done -> wait | true | v := 1.
                logger.info("T.O GlobalClass controller 0 done wait");
                if(execute_transition_done_0()) {
                    logger.info("T.CS GlobalClass controller 0 done wait");
                    return;
                }
                logger.info("T.CF GlobalClass controller 0 done wait");
                // [SEQ.END]
                logger.info("D.CF GlobalClass controller done");
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 10) {
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
    }
}