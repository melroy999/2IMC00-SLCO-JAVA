package testing.randompick.counting;

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
        Properties props = System.getProperties();
        props.setProperty("log4j2.asyncLoggerRingBufferSize", "4194304");

        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "Elevator";
        String log_settings = "[T=30s,URP]";
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

            // Add variables needed for measurements.
            private long D00_O;
            private long D00_F;
            private long D00_S;
            private long T00_O;
            private long T00_F;
            private long T00_S;
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
            private long D02_O;
            private long D02_F;
            private long D02_S;
            private long T04_O;
            private long T04_F;
            private long T04_S;

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
                lock_ids[0] = target_locks[0] = 0; // Acquire p
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 2; // Acquire t
                lockManager.acquire_locks(lock_ids, 1);
                if(t == p) {
                    lock_ids[0] = target_locks[0]; // Release p
                    lock_ids[1] = target_locks[1]; // Release t
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
                    lock_ids[0] = target_locks[1]; // Release t
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
                lock_ids[0] = target_locks[0]; // Release p
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_cabinThread.States.mov;
                return true;
            }

            // SLCO expression wrapper | t > p.
            private boolean t_mov_2_s_0_n_0() {
                if(t > p) {
                    lock_ids[0] = target_locks[1]; // Release t
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release p
                lock_ids[1] = target_locks[1]; // Release t
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
                lock_ids[0] = target_locks[0]; // Release p
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
                lock_ids[0] = target_locks[1] = 0; // Acquire p
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[0] = 1; // Acquire v
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[2] = 3 + p; // Acquire req[p]
                lockManager.acquire_locks(lock_ids, 1);
                req[p] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release p
                lock_ids[1] = target_locks[2]; // Release req[p]
                lockManager.release_locks(lock_ids, 2);
                // SLCO assignment | v := 0.
                v = (0) & 0xff;
                lock_ids[0] = target_locks[0]; // Release v
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_cabinThread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                D00_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | idle -> mov | v > 0.
                T00_O++;
                if(execute_transition_idle_0()) {
                    T00_S++;
                    D00_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state mov.
            private void exec_mov() {
                D01_O++;
                // [N_DET.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | mov -> open | t = p.
                T01_O++;
                if(execute_transition_mov_0()) {
                    T01_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | mov -> mov | [t < p; p := p - 1].
                T02_O++;
                if(execute_transition_mov_1()) {
                    T02_S++;
                    D01_S++;
                    return;
                }
                // SLCO transition (p:0, id:2) | mov -> mov | [t > p; p := p + 1].
                T03_O++;
                if(execute_transition_mov_2()) {
                    T03_S++;
                    D01_S++;
                    return;
                }
                // [DET.END]
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state open.
            private void exec_open() {
                D02_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | open -> idle | true | [true; req[p] := 0; v := 0].
                T04_O++;
                if(execute_transition_open_0()) {
                    T04_S++;
                    D02_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case idle -> exec_idle();
                        case mov -> exec_mov();
                        case open -> exec_open();
                    }
                }

                // Report all counts.
                logger.info("D00.O " + D00_O);
                logger.info("D00.F " + D00_F);
                logger.info("D00.S " + D00_S);
                logger.info("T00.O " + T00_O);
                logger.info("T00.F " + T00_F);
                logger.info("T00.S " + T00_S);
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
                logger.info("D02.O " + D02_O);
                logger.info("D02.F " + D02_F);
                logger.info("D02.S " + D02_S);
                logger.info("T04.O " + T04_O);
                logger.info("T04.F " + T04_F);
                logger.info("T04.S " + T04_S);
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

            // Add variables needed for measurements.
            private long D03_O;
            private long D03_F;
            private long D03_S;
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
                D03_O++;
                // [N_DET.START]
                switch(random.nextInt(4)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | read -> read | [req[0] = 0; req[0] := 1].
                        T05_O++;
                        if(execute_transition_read_0()) {
                            T05_S++;
                            D03_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | read -> read | [req[1] = 0; req[1] := 1].
                        T06_O++;
                        if(execute_transition_read_1()) {
                            T06_S++;
                            D03_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | read -> read | [req[2] = 0; req[2] := 1].
                        T07_O++;
                        if(execute_transition_read_2()) {
                            T07_S++;
                            D03_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | read -> read | [req[3] = 0; req[3] := 1].
                        T08_O++;
                        if(execute_transition_read_3()) {
                            T08_S++;
                            D03_S++;
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
                        case read -> exec_read();
                    }
                }

                // Report all counts.
                logger.info("D03.O " + D03_O);
                logger.info("D03.F " + D03_F);
                logger.info("D03.S " + D03_S);
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

            // Add variables needed for measurements.
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
            private long T12_O;
            private long T12_F;
            private long T12_S;
            private long D06_O;
            private long D06_F;
            private long D06_S;
            private long T13_O;
            private long T13_F;
            private long T13_S;

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
                lock_ids[1] = target_locks[2] = 3 + 1; // Acquire req[1]
                lock_ids[2] = target_locks[3] = 3 + 2; // Acquire req[2]
                lock_ids[3] = target_locks[4] = 3 + 3; // Acquire req[3]
                lockManager.acquire_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | req[t] = 1.
            private boolean t_work_1_s_0_n_1() {
                lock_ids[0] = target_locks[1] = 3 + 0; // Acquire req[0]
                lock_ids[1] = target_locks[2] = 3 + 1; // Acquire req[1]
                lock_ids[2] = target_locks[3] = 3 + 2; // Acquire req[2]
                lock_ids[3] = target_locks[4] = 3 + 3; // Acquire req[3]
                lock_ids[4] = target_locks[5] = 3 + t; // Acquire req[t]
                lockManager.acquire_locks(lock_ids, 5);
                if(req[t] == 1) {
                    lock_ids[0] = target_locks[0]; // Release t
                    lock_ids[1] = target_locks[1]; // Release req[0]
                    lock_ids[2] = target_locks[2]; // Release req[1]
                    lock_ids[3] = target_locks[3]; // Release req[2]
                    lock_ids[4] = target_locks[4]; // Release req[3]
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
                lock_ids[2] = target_locks[2]; // Release req[1]
                lock_ids[3] = target_locks[3]; // Release req[2]
                lock_ids[4] = target_locks[4]; // Release req[3]
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
                lock_ids[2] = target_locks[2]; // Release req[1]
                lock_ids[3] = target_locks[3]; // Release req[2]
                lock_ids[4] = target_locks[4]; // Release req[3]
                lockManager.release_locks(lock_ids, 5);
                return false;
            }

            // SLCO expression wrapper | req[t] = 0.
            private boolean t_work_2_s_0_n_2() {
                if(req[t] == 0) {
                    lock_ids[0] = target_locks[1]; // Release req[0]
                    lock_ids[1] = target_locks[2]; // Release req[1]
                    lock_ids[2] = target_locks[3]; // Release req[2]
                    lock_ids[3] = target_locks[4]; // Release req[3]
                    lockManager.release_locks(lock_ids, 4);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release t
                lock_ids[1] = target_locks[1]; // Release req[0]
                lock_ids[2] = target_locks[2]; // Release req[1]
                lock_ids[3] = target_locks[3]; // Release req[2]
                lock_ids[4] = target_locks[4]; // Release req[3]
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
                D04_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | wait -> work | [v = 0; t := t + (2 * ldir) - 1].
                T09_O++;
                if(execute_transition_wait_0()) {
                    T09_S++;
                    D04_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state work.
            private void exec_work() {
                D05_O++;
                // [N_DET.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | work -> wait | [t < 0 or t = 4; ldir := 1 - ldir].
                T10_O++;
                if(execute_transition_work_0()) {
                    T10_S++;
                    D05_S++;
                    return;
                }
                // SLCO transition (p:0, id:1) | work -> done | t >= 0 and t < 4 and req[t] = 1.
                T11_O++;
                if(execute_transition_work_1()) {
                    T11_S++;
                    D05_S++;
                    return;
                }
                // SLCO transition (p:0, id:2) | work -> work | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1].
                T12_O++;
                if(execute_transition_work_2()) {
                    T12_S++;
                    D05_S++;
                    return;
                }
                // [DET.END]
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state done.
            private void exec_done() {
                D06_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | done -> wait | true | v := 1.
                T13_O++;
                if(execute_transition_done_0()) {
                    T13_S++;
                    D06_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case wait -> exec_wait();
                        case work -> exec_work();
                        case done -> exec_done();
                    }
                }

                // Report all counts.
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
                logger.info("T12.O " + T12_O);
                logger.info("T12.F " + T12_F);
                logger.info("T12.S " + T12_S);
                logger.info("D06.O " + D06_O);
                logger.info("D06.F " + D06_F);
                logger.info("D06.S " + D06_S);
                logger.info("T13.O " + T13_O);
                logger.info("T13.F " + T13_F);
                logger.info("T13.S " + T13_S);
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

        // Include information about the model.
        logger.info("JSON {\"name\": \"Elevator\", \"settings\": \"test_models/Elevator.slco -use_random_pick -running_time=30 -package_name=testing.randompick -performance_measurements\", \"classes\": {\"GlobalClass\": {\"name\": \"GlobalClass\", \"state_machines\": {\"cabin\": {\"name\": \"cabin\", \"states\": [\"idle\", \"mov\", \"open\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D00\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> mov | v > 0\", \"id\": \"T00\", \"source\": \"idle\", \"target\": \"mov\", \"priority\": 0, \"is_excluded\": false}}}, \"mov\": {\"source\": \"mov\", \"id\": \"D01\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | mov -> open | t = p\", \"id\": \"T01\", \"source\": \"mov\", \"target\": \"open\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | mov -> mov | [t < p; p := p - 1]\", \"id\": \"T02\", \"source\": \"mov\", \"target\": \"mov\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | mov -> mov | [t > p; p := p + 1]\", \"id\": \"T03\", \"source\": \"mov\", \"target\": \"mov\", \"priority\": 0, \"is_excluded\": false}}}, \"open\": {\"source\": \"open\", \"id\": \"D02\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | open -> idle | true | [true; req[p] := 0; v := 0]\", \"id\": \"T04\", \"source\": \"open\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}}}, \"environment\": {\"name\": \"environment\", \"states\": [\"read\"], \"decision_structures\": {\"read\": {\"source\": \"read\", \"id\": \"D03\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | read -> read | [req[0] = 0; req[0] := 1]\", \"id\": \"T05\", \"source\": \"read\", \"target\": \"read\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | read -> read | [req[1] = 0; req[1] := 1]\", \"id\": \"T06\", \"source\": \"read\", \"target\": \"read\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | read -> read | [req[2] = 0; req[2] := 1]\", \"id\": \"T07\", \"source\": \"read\", \"target\": \"read\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | read -> read | [req[3] = 0; req[3] := 1]\", \"id\": \"T08\", \"source\": \"read\", \"target\": \"read\", \"priority\": 0, \"is_excluded\": false}}}}}, \"controller\": {\"name\": \"controller\", \"states\": [\"wait\", \"work\", \"done\"], \"decision_structures\": {\"wait\": {\"source\": \"wait\", \"id\": \"D04\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | wait -> work | [v = 0; t := t + (2 * ldir) - 1]\", \"id\": \"T09\", \"source\": \"wait\", \"target\": \"work\", \"priority\": 0, \"is_excluded\": false}}}, \"work\": {\"source\": \"work\", \"id\": \"D05\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | work -> wait | [t < 0 or t = 4; ldir := 1 - ldir]\", \"id\": \"T10\", \"source\": \"work\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | work -> done | t >= 0 and t < 4 and req[t] = 1\", \"id\": \"T11\", \"source\": \"work\", \"target\": \"done\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | work -> work | [t >= 0 and t < 4 and req[t] = 0; t := t + (2 * ldir) - 1]\", \"id\": \"T12\", \"source\": \"work\", \"target\": \"work\", \"priority\": 0, \"is_excluded\": false}}}, \"done\": {\"source\": \"done\", \"id\": \"D06\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | done -> wait | true | v := 1\", \"id\": \"T13\", \"source\": \"done\", \"target\": \"wait\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
    }
}