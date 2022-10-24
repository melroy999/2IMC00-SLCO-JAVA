package testing.basic.logging;

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

// SLCO model Telephony.
public class Telephony {
    // The objects in the model.
    private final SLCO_Class[] objects;

    // Additional supporting variables.
    // Define and initialize the logger to gather the appropriate performance data with.
    private static final Logger logger;
    static {
        Properties props = System.getProperties();
        props.setProperty("log4j2.asyncLoggerRingBufferSize", "4194304");

        String log_date = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":", ".");
        String log_name = "Telephony";
        String log_settings = "[T=30s]";
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

    Telephony() {
        // Instantiate the objects.
        objects = new SLCO_Class[] {
            new GlobalClass(
                new int[]{ 255, 255, 255, 255 },
                new int[]{ 255, 255, 255, 255 },
                new int[]{ 1, 2, 3, 255 },
                new int[]{ 255, 255, 255, 255 }
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
        private final Thread T_User_0;
        private final Thread T_User_1;
        private final Thread T_User_2;
        private final Thread T_User_3;

        // Class variables.
        private final int[] chan;
        private final int[] partner;
        private final int[] callforwardbusy;
        private final int[] record;

        GlobalClass(int[] chan, int[] partner, int[] callforwardbusy, int[] record) {
            // Create a lock manager.
            LockManager lockManager = new LockManager(16);

            // Instantiate the class variables.
            this.chan = chan;
            this.partner = partner;
            this.callforwardbusy = callforwardbusy;
            this.record = record;

            // Instantiate the state machine threads and pass on the class' lock manager.
            T_User_0 = new GlobalClass_User_0Thread(lockManager);
            T_User_1 = new GlobalClass_User_1Thread(lockManager);
            T_User_2 = new GlobalClass_User_2Thread(lockManager);
            T_User_3 = new GlobalClass_User_3Thread(lockManager);
        }

        // Define the states fot the state machine User_0.
        interface GlobalClass_User_0Thread_States {
            enum States {
                idle, 
                dialing, 
                calling, 
                busy, 
                qi, 
                talert, 
                unobtainable, 
                oalert, 
                errorstate, 
                oconnected, 
                dveoringout, 
                tpickup, 
                tconnected, 
                ringback
            }
        }

        // Representation of the SLCO state machine User_0.
        class GlobalClass_User_0Thread extends Thread implements GlobalClass_User_0Thread_States {
            // Current state
            private GlobalClass_User_0Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int dev;
            private int mbit;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_User_0Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_0Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[9];
                target_locks = new int[14];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO expression wrapper | chan[0] = 255.
            private boolean t_idle_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(chan[0] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[0] = 255; dev := 0; chan[0] := ((0) + (0) * 20)] -> [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)].
                // SLCO expression | chan[0] = 255.
                if(!(t_idle_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[0] := (0 + 0 * 20).
                chan[0] = ((0 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.dialing;
                return true;
            }

            // SLCO expression wrapper | chan[0] != 255.
            private boolean t_idle_1_s_0_n_0() {
                if(chan[0] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[0] != 255; partner[0] := (chan[0] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[0] != 255; partner[0] := ((chan[0]) % 20)] -> [chan[0] != 255; partner[0] := (chan[0] % 20)].
                // SLCO expression | chan[0] != 255.
                if(!(t_idle_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[0] := (chan[0] % 20).
                partner[0] = ((Math.floorMod(chan[0], 20))) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_0Thread.States.qi;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[0]] % 20) = 0.
            private boolean t_qi_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[0]; // Acquire chan[partner[0]]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[partner[0]], 20)) == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[0]] % 20) = 0.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[0]]) % 20) = 0 -> (chan[partner[0]] % 20) = 0.
                if(!(t_qi_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.talert;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[0]] % 20) != 0.
            private boolean t_qi_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[0]], 20)) != 0) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[0]] % 20) != 0; partner[0] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[0]]) % 20) != 0; partner[0] := 255] -> [(chan[partner[0]] % 20) != 0; partner[0] := 255].
                // SLCO expression | (chan[partner[0]] % 20) != 0.
                if(!(t_qi_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[0] := 255].
            private boolean execute_transition_dialing_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[0] := 255] -> [true; dev := 1; chan[0] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[0] := 255.
                lock_ids[0] = target_locks[0] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[0] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 0] -> partner[0] := 0.
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                partner[0] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[0] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 1] -> partner[0] := 1.
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                partner[0] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[0] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 2] -> partner[0] := 2.
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                partner[0] = (2) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[0] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 3] -> partner[0] := 3.
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                partner[0] = (3) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[0] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 4] -> partner[0] := 4.
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                partner[0] = (4) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[0] = 0.
            private boolean t_calling_0_s_0_n_0() {
                lock_ids[0] = target_locks[5] = 0 + 0; // Acquire callforwardbusy[0]
                lock_ids[1] = target_locks[6] = 0 + 1; // Acquire callforwardbusy[1]
                lock_ids[2] = target_locks[7] = 0 + 2; // Acquire callforwardbusy[2]
                lock_ids[3] = target_locks[8] = 0 + 3; // Acquire callforwardbusy[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[1] = 4 + 0; // Acquire record[0]
                lock_ids[1] = target_locks[2] = 4 + 1; // Acquire record[1]
                lock_ids[2] = target_locks[3] = 4 + 2; // Acquire record[2]
                lock_ids[3] = target_locks[4] = 4 + 3; // Acquire record[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(partner[0] == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[0] = 0.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[0] = 0.
                if(!(t_calling_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[0] = 4.
            private boolean t_calling_1_s_0_n_0() {
                if(partner[0] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[0] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[0] = 4.
                if(!(t_calling_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.unobtainable;
                return true;
            }

            // SLCO expression wrapper | partner[0] = 4.
            private boolean t_calling_2_s_0_n_0() {
                if(partner[0] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[0] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[0] = 4.
                if(!(t_calling_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.ringback;
                return true;
            }

            // SLCO expression wrapper | partner[0] != 0 and partner[0] != 4.
            private boolean t_calling_3_s_0_n_0() {
                if(partner[0] != 0 && partner[0] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[9] = 12 + 0; // Acquire chan[0]
                lock_ids[1] = target_locks[10] = 12 + 1; // Acquire chan[1]
                lock_ids[2] = target_locks[11] = 12 + 2; // Acquire chan[2]
                lock_ids[3] = target_locks[12] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[0]] != 255.
            private boolean t_calling_3_s_0_n_1() {
                lock_ids[0] = target_locks[9] = 12 + 0; // Acquire chan[0]
                lock_ids[1] = target_locks[10] = 12 + 1; // Acquire chan[1]
                lock_ids[2] = target_locks[11] = 12 + 2; // Acquire chan[2]
                lock_ids[3] = target_locks[12] = 12 + 3; // Acquire chan[3]
                lock_ids[4] = target_locks[13] = 12 + partner[0]; // Acquire chan[partner[0]]
                lockManager.acquire_locks(lock_ids, 5);
                if(chan[partner[0]] != 255) {
                    lock_ids[0] = target_locks[13]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[13]; // Release chan[partner[0]]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[0]] = 255.
            private boolean t_calling_3_s_0_n_2() {
                if(callforwardbusy[partner[0]] == 255) {
                    lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                    lock_ids[4] = target_locks[9]; // Release chan[0]
                    lock_ids[5] = target_locks[10]; // Release chan[1]
                    lock_ids[6] = target_locks[11]; // Release chan[2]
                    lock_ids[7] = target_locks[12]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 8);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0].
                // SLCO expression | partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255.
                if(!(t_calling_3_s_0_n_0() && t_calling_3_s_0_n_1() && t_calling_3_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[0]] := 0.
                record[partner[0]] = (0) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_0Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[0] != 0.
            private boolean t_calling_4_s_0_n_0() {
                if(partner[0] != 0) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | partner[0] != 4.
            private boolean t_calling_4_s_0_n_1() {
                if(partner[0] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[0]] != 255.
            private boolean t_calling_4_s_0_n_2() {
                if(chan[partner[0]] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[0]] != 255.
            private boolean t_calling_4_s_0_n_3() {
                if(callforwardbusy[partner[0]] != 255) {
                    lock_ids[0] = target_locks[9]; // Release chan[0]
                    lock_ids[1] = target_locks[10]; // Release chan[1]
                    lock_ids[2] = target_locks[11]; // Release chan[2]
                    lock_ids[3] = target_locks[12]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 4);
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]].
                // SLCO expression | partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255.
                if(!(t_calling_4_s_0_n_0() && t_calling_4_s_0_n_1() && t_calling_4_s_0_n_2() && t_calling_4_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | record[partner[0]] := 0.
                record[partner[0]] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | partner[0] := callforwardbusy[partner[0]].
                partner[0] = (callforwardbusy[partner[0]]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[2] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[3] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[4] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[0] != 0.
            private boolean t_calling_5_s_0_n_0() {
                if(partner[0] != 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[0]
                lock_ids[6] = target_locks[10]; // Release chan[1]
                lock_ids[7] = target_locks[11]; // Release chan[2]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | partner[0] != 4.
            private boolean t_calling_5_s_0_n_1() {
                if(partner[0] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[0]
                lock_ids[6] = target_locks[10]; // Release chan[1]
                lock_ids[7] = target_locks[11]; // Release chan[2]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | chan[partner[0]] = 255.
            private boolean t_calling_5_s_0_n_2() {
                if(chan[partner[0]] == 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[0]
                lock_ids[6] = target_locks[10]; // Release chan[1]
                lock_ids[7] = target_locks[11]; // Release chan[2]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := ((0) + (0) * 20); chan[0] := ((partner[0]) + (0) * 20)] -> [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)].
                // SLCO expression | partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255.
                if(!(t_calling_5_s_0_n_0() && t_calling_5_s_0_n_1() && t_calling_5_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[0]] := 0.
                record[partner[0]] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | chan[partner[0]] := (0 + 0 * 20).
                chan[partner[0]] = ((0 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[10]; // Release chan[1]
                lock_ids[1] = target_locks[11]; // Release chan[2]
                lock_ids[2] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 3);
                // SLCO assignment | chan[0] := (partner[0] + 0 * 20).
                chan[0] = ((partner[0] + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[9]; // Release chan[0]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_0Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[0] := 255; partner[0] := 255; dev := 1] -> [true; chan[0] := 255; partner[0] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[0] := 255.
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[0] % 20) != partner[0].
            private boolean t_oalert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[0], 20)) != partner[0]) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[0]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[0] % 20) != partner[0].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[0]) % 20) != partner[0] -> (chan[0] % 20) != partner[0].
                if(!(t_oalert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[0] / 20) = 1.
            private boolean t_oalert_1_s_0_n_0() {
                if((chan[0] / 20) == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[0]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[0]) % 20) = partner[0] and ((chan[0]) / 20) = 1 -> (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1.
                if(!((Math.floorMod(chan[0], 20)) == partner[0] && t_oalert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.oconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[0] % 20) = partner[0].
            private boolean t_oalert_2_s_0_n_0() {
                if((Math.floorMod(chan[0], 20)) == partner[0]) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | (chan[0] / 20) = 0.
            private boolean t_oalert_2_s_0_n_1() {
                if((chan[0] / 20) == 0) {
                    lock_ids[0] = target_locks[1]; // Release chan[0]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[0]) % 20) = partner[0] and ((chan[0]) / 20) = 0 -> (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0.
                if(!(t_oalert_2_s_0_n_0() && t_oalert_2_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.dveoringout;
                return true;
            }

            // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[0] := 255; chan[partner[0]] := 255].
            private boolean execute_transition_oconnected_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[0] := 255; chan[partner[0]] := 255] -> [true; dev := 1; chan[0] := 255; chan[partner[0]] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[0] := 255.
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lock_ids[1] = target_locks[2] = 12 + partner[0]; // Acquire chan[partner[0]]
                lockManager.acquire_locks(lock_ids, 2);
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[partner[0]] := 255.
                chan[partner[0]] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[2]; // Release chan[partner[0]]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[0] := 255; partner[0] := ((partner[0] % 20) + 0 * 20)].
            private boolean execute_transition_dveoringout_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[0] := 255; partner[0] := ((((partner[0]) % 20)) + (0) * 20)] -> [true; dev := 1; chan[0] := 255; partner[0] := ((partner[0] % 20) + 0 * 20)].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[0] := 255.
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[0] := ((partner[0] % 20) + 0 * 20).
                partner[0] = (((Math.floorMod(partner[0], 20)) + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[0] := 255; partner[0] := 255; dev := 1] -> [true; chan[0] := 255; partner[0] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[0] := 255.
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
            private boolean execute_transition_ringback_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[0] := 255; partner[0] := 255; dev := 1] -> [true; chan[0] := 255; partner[0] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[0] := 255.
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[2] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | record[0] != 255.
            private boolean t_ringback_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 4 + 0; // Acquire record[0]
                lockManager.acquire_locks(lock_ids, 1);
                if(record[0] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release record[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[0] != 255; partner[0] := record[0]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[0] != 255; partner[0] := record[0]].
                // SLCO expression | record[0] != 255.
                if(!(t_ringback_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[0] := record[0].
                lock_ids[0] = target_locks[1] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                partner[0] = (record[0]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release record[0]
                lock_ids[1] = target_locks[1]; // Release partner[0]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | chan[0] = 255.
            private boolean t_talert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[0]; // Acquire chan[partner[0]]
                lock_ids[1] = target_locks[2] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 2);
                if(chan[0] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                    lock_ids[2] = target_locks[2]; // Release chan[0]
                    lockManager.release_locks(lock_ids, 3);
                    return true;
                }
                lock_ids[0] = target_locks[2]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[0] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[0] = 255.
                if(!(dev != 1 || t_talert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[0]] % 20) = 0.
            private boolean t_talert_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[0]], 20)) == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[0]] % 20) = 0.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[0]]) % 20) = 0 -> (chan[partner[0]] % 20) = 0.
                if(!(t_talert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.tpickup;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[0]] % 20) != 0.
            private boolean t_talert_2_s_0_n_0() {
                if((Math.floorMod(chan[partner[0]], 20)) != 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[0]] % 20) != 0.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[0]]) % 20) != 0 -> (chan[partner[0]] % 20) != 0.
                if(!(t_talert_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[0]] % 20) = 0.
            private boolean t_tpickup_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[0]; // Acquire chan[partner[0]]
                lock_ids[1] = target_locks[2] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 2);
                return (Math.floorMod(chan[partner[0]], 20)) == 0;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[0]]) % 20) = 0 and ((chan[partner[0]]) / 20) = 0; dev := 0; chan[partner[0]] := ((0) + (1) * 20); chan[0] := ((partner[0]) + (1) * 20)] -> [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)].
                // SLCO expression | (chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0.
                if(!(t_tpickup_0_s_0_n_0() && (chan[partner[0]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[0]] := (0 + 1 * 20).
                chan[partner[0]] = ((0 + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[partner[0]]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[0] := (partner[0] + 1 * 20).
                chan[0] = ((partner[0] + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[2]; // Release chan[0]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_0Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | chan[partner[0]] = 255.
            private boolean t_tpickup_1_s_0_n_0() {
                if(chan[partner[0]] == 255) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO expression wrapper | (chan[partner[0]] % 20) != 0.
            private boolean t_tpickup_1_s_0_n_1() {
                if((Math.floorMod(chan[partner[0]], 20)) != 0) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[0]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[partner[0]]
                lock_ids[2] = target_locks[2]; // Release chan[0]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[0]] = 255 or ((chan[partner[0]]) % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255] -> [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255].
                // SLCO expression | chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0.
                if(!(t_tpickup_1_s_0_n_0() || t_tpickup_1_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[0] := 255.
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[0] / 20) = 1.
            private boolean t_tconnected_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 0; // Acquire partner[0]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 0; // Acquire chan[0]
                lockManager.acquire_locks(lock_ids, 1);
                return (chan[0] / 20) == 1;
            }

            // SLCO expression wrapper | dev = 0.
            private boolean t_tconnected_0_s_0_n_1() {
                if(dev == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[0]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[0]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[0] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[0] / 20) = 1 and dev = 0.
                if(!(t_tconnected_0_s_0_n_0() && t_tconnected_0_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | dev = 1.
            private boolean t_tconnected_1_s_0_n_0() {
                if(dev == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[0]
                    lock_ids[1] = target_locks[1]; // Release chan[0]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[0]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[0] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[0] / 20) = 1 and dev = 1.
                if(!((chan[0] / 20) == 1 && t_tconnected_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_0Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[0] / 20) = 0.
            private boolean t_tconnected_2_s_0_n_0() {
                if((chan[0] / 20) == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lock_ids[1] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[0]) / 20) = 0; partner[0] := 255; chan[0] := 255] -> [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255].
                // SLCO expression | (chan[0] / 20) = 0.
                if(!(t_tconnected_2_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[0]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[0] := 255.
                chan[0] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[0]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                logger.info("D000.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | idle -> dialing | [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)].
                logger.info("T000.O");
                if(execute_transition_idle_0()) {
                    logger.info("T000.S");
                    logger.info("D000.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | idle -> qi | [chan[0] != 255; partner[0] := (chan[0] % 20)].
                logger.info("T001.O");
                if(execute_transition_idle_1()) {
                    logger.info("T001.S");
                    logger.info("D000.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                logger.info("D001.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[0] := 255].
                logger.info("T002.O");
                if(execute_transition_dialing_0()) {
                    logger.info("T002.S");
                    logger.info("D001.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[0] := 0.
                logger.info("T003.O");
                if(execute_transition_dialing_1()) {
                    logger.info("T003.S");
                    logger.info("D001.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[0] := 1.
                logger.info("T004.O");
                if(execute_transition_dialing_2()) {
                    logger.info("T004.S");
                    logger.info("D001.S");
                    return;
                }
                // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[0] := 2.
                logger.info("T005.O");
                if(execute_transition_dialing_3()) {
                    logger.info("T005.S");
                    logger.info("D001.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[0] := 3.
                logger.info("T006.O");
                if(execute_transition_dialing_4()) {
                    logger.info("T006.S");
                    logger.info("D001.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[0] := 4.
                logger.info("T007.O");
                if(execute_transition_dialing_5()) {
                    logger.info("T007.S");
                    logger.info("D001.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                logger.info("D002.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | calling -> busy | partner[0] = 0.
                logger.info("T008.O");
                if(execute_transition_calling_0()) {
                    logger.info("T008.S");
                    logger.info("D002.S");
                    return;
                }
                // SLCO expression | partner[0] = 4.
                if(partner[0] == 4) {
                    // [SEQ.START]
                    // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[0] = 4.
                    logger.info("T009.O");
                    if(execute_transition_calling_1()) {
                        logger.info("T009.S");
                        logger.info("D002.S");
                        return;
                    }
                    // SLCO transition (p:0, id:2) | calling -> ringback | partner[0] = 4.
                    logger.info("T010.O");
                    if(execute_transition_calling_2()) {
                        logger.info("T010.S");
                        logger.info("D002.S");
                        return;
                    }
                    // [SEQ.END]
                }
                // SLCO transition (p:0, id:3) | calling -> busy | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0].
                logger.info("T011.O");
                if(execute_transition_calling_3()) {
                    logger.info("T011.S");
                    logger.info("D002.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | calling -> calling | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]].
                logger.info("T012.O");
                if(execute_transition_calling_4()) {
                    logger.info("T012.S");
                    logger.info("D002.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | calling -> oalert | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)].
                logger.info("T013.O");
                if(execute_transition_calling_5()) {
                    logger.info("T013.S");
                    logger.info("D002.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                logger.info("D003.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
                logger.info("T014.O");
                if(execute_transition_busy_0()) {
                    logger.info("T014.S");
                    logger.info("D003.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                logger.info("D004.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[0]] % 20) = 0.
                logger.info("T015.O");
                if(execute_transition_qi_0()) {
                    logger.info("T015.S");
                    logger.info("D004.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[0]] % 20) != 0; partner[0] := 255].
                logger.info("T016.O");
                if(execute_transition_qi_1()) {
                    logger.info("T016.S");
                    logger.info("D004.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                logger.info("D005.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[0] = 255.
                logger.info("T017.O");
                if(execute_transition_talert_0()) {
                    logger.info("T017.S");
                    logger.info("D005.S");
                    return;
                }
                // [DET.START]
                // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[0]] % 20) = 0.
                logger.info("T018.O");
                if(execute_transition_talert_1()) {
                    logger.info("T018.S");
                    logger.info("D005.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[0]] % 20) != 0.
                logger.info("T019.O");
                if(execute_transition_talert_2()) {
                    logger.info("T019.S");
                    logger.info("D005.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                logger.info("D006.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
                logger.info("T020.O");
                if(execute_transition_unobtainable_0()) {
                    logger.info("T020.S");
                    logger.info("D006.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                logger.info("D007.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[0] % 20) != partner[0].
                logger.info("T021.O");
                if(execute_transition_oalert_0()) {
                    logger.info("T021.S");
                    logger.info("D007.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1.
                logger.info("T022.O");
                if(execute_transition_oalert_1()) {
                    logger.info("T022.S");
                    logger.info("D007.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0.
                logger.info("T023.O");
                if(execute_transition_oalert_2()) {
                    logger.info("T023.S");
                    logger.info("D007.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                logger.info("D008.O");
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                logger.info("D009.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[0] := 255; chan[partner[0]] := 255].
                logger.info("T024.O");
                if(execute_transition_oconnected_0()) {
                    logger.info("T024.S");
                    logger.info("D009.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                logger.info("D010.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[0] := 255; partner[0] := ((partner[0] % 20) + 0 * 20)].
                logger.info("T025.O");
                if(execute_transition_dveoringout_0()) {
                    logger.info("T025.S");
                    logger.info("D010.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                logger.info("D011.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)].
                logger.info("T026.O");
                if(execute_transition_tpickup_0()) {
                    logger.info("T026.S");
                    logger.info("D011.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255].
                logger.info("T027.O");
                if(execute_transition_tpickup_1()) {
                    logger.info("T027.S");
                    logger.info("D011.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                logger.info("D012.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 0; dev := 1].
                logger.info("T028.O");
                if(execute_transition_tconnected_0()) {
                    logger.info("T028.S");
                    logger.info("D012.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 1; dev := 0].
                logger.info("T029.O");
                if(execute_transition_tconnected_1()) {
                    logger.info("T029.S");
                    logger.info("D012.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255].
                logger.info("T030.O");
                if(execute_transition_tconnected_2()) {
                    logger.info("T030.S");
                    logger.info("D012.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                logger.info("D013.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
                logger.info("T031.O");
                if(execute_transition_ringback_0()) {
                    logger.info("T031.S");
                    logger.info("D013.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | ringback -> calling | [record[0] != 255; partner[0] := record[0]].
                logger.info("T032.O");
                if(execute_transition_ringback_1()) {
                    logger.info("T032.S");
                    logger.info("D013.S");
                    return;
                }
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case idle -> exec_idle();
                        case dialing -> exec_dialing();
                        case calling -> exec_calling();
                        case busy -> exec_busy();
                        case qi -> exec_qi();
                        case talert -> exec_talert();
                        case unobtainable -> exec_unobtainable();
                        case oalert -> exec_oalert();
                        case errorstate -> exec_errorstate();
                        case oconnected -> exec_oconnected();
                        case dveoringout -> exec_dveoringout();
                        case tpickup -> exec_tpickup();
                        case tconnected -> exec_tconnected();
                        case ringback -> exec_ringback();
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

        // Define the states fot the state machine User_1.
        interface GlobalClass_User_1Thread_States {
            enum States {
                idle, 
                dialing, 
                calling, 
                busy, 
                qi, 
                talert, 
                unobtainable, 
                oalert, 
                errorstate, 
                oconnected, 
                dveoringout, 
                tpickup, 
                tconnected, 
                ringback
            }
        }

        // Representation of the SLCO state machine User_1.
        class GlobalClass_User_1Thread extends Thread implements GlobalClass_User_1Thread_States {
            // Current state
            private GlobalClass_User_1Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int dev;
            private int mbit;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_User_1Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_1Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[9];
                target_locks = new int[14];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO expression wrapper | chan[1] = 255.
            private boolean t_idle_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(chan[1] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[1] = 255; dev := 0; chan[1] := ((1) + (0) * 20)] -> [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)].
                // SLCO expression | chan[1] = 255.
                if(!(t_idle_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[1] := (1 + 0 * 20).
                chan[1] = ((1 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.dialing;
                return true;
            }

            // SLCO expression wrapper | chan[1] != 255.
            private boolean t_idle_1_s_0_n_0() {
                if(chan[1] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[1] != 255; partner[1] := (chan[1] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[1] != 255; partner[1] := ((chan[1]) % 20)] -> [chan[1] != 255; partner[1] := (chan[1] % 20)].
                // SLCO expression | chan[1] != 255.
                if(!(t_idle_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[1] := (chan[1] % 20).
                partner[1] = ((Math.floorMod(chan[1], 20))) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_1Thread.States.qi;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[1]] % 20) = 1.
            private boolean t_qi_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[1]; // Acquire chan[partner[1]]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[partner[1]], 20)) == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[1]] % 20) = 1.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[1]]) % 20) = 1 -> (chan[partner[1]] % 20) = 1.
                if(!(t_qi_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.talert;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[1]] % 20) != 1.
            private boolean t_qi_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[1]], 20)) != 1) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[1]] % 20) != 1; partner[1] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[1]]) % 20) != 1; partner[1] := 255] -> [(chan[partner[1]] % 20) != 1; partner[1] := 255].
                // SLCO expression | (chan[partner[1]] % 20) != 1.
                if(!(t_qi_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[1] := 255].
            private boolean execute_transition_dialing_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[1] := 255] -> [true; dev := 1; chan[1] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[1] := 255.
                lock_ids[0] = target_locks[0] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[1] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 0] -> partner[1] := 0.
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                partner[1] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[1] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 1] -> partner[1] := 1.
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                partner[1] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[1] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 2] -> partner[1] := 2.
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                partner[1] = (2) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[1] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 3] -> partner[1] := 3.
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                partner[1] = (3) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[1] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 4] -> partner[1] := 4.
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                partner[1] = (4) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[1] = 1.
            private boolean t_calling_0_s_0_n_0() {
                lock_ids[0] = target_locks[5] = 0 + 0; // Acquire callforwardbusy[0]
                lock_ids[1] = target_locks[6] = 0 + 1; // Acquire callforwardbusy[1]
                lock_ids[2] = target_locks[7] = 0 + 2; // Acquire callforwardbusy[2]
                lock_ids[3] = target_locks[8] = 0 + 3; // Acquire callforwardbusy[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[1] = 4 + 0; // Acquire record[0]
                lock_ids[1] = target_locks[2] = 4 + 1; // Acquire record[1]
                lock_ids[2] = target_locks[3] = 4 + 2; // Acquire record[2]
                lock_ids[3] = target_locks[4] = 4 + 3; // Acquire record[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(partner[1] == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[1] = 1.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[1] = 1.
                if(!(t_calling_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[1] = 4.
            private boolean t_calling_1_s_0_n_0() {
                if(partner[1] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[1] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[1] = 4.
                if(!(t_calling_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.unobtainable;
                return true;
            }

            // SLCO expression wrapper | partner[1] = 4.
            private boolean t_calling_2_s_0_n_0() {
                if(partner[1] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[1] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[1] = 4.
                if(!(t_calling_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.ringback;
                return true;
            }

            // SLCO expression wrapper | partner[1] != 1 and partner[1] != 4.
            private boolean t_calling_3_s_0_n_0() {
                if(partner[1] != 1 && partner[1] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[9] = 12 + 1; // Acquire chan[1]
                lock_ids[1] = target_locks[10] = 12 + 0; // Acquire chan[0]
                lock_ids[2] = target_locks[11] = 12 + 2; // Acquire chan[2]
                lock_ids[3] = target_locks[12] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[1]] != 255.
            private boolean t_calling_3_s_0_n_1() {
                lock_ids[0] = target_locks[9] = 12 + 1; // Acquire chan[1]
                lock_ids[1] = target_locks[10] = 12 + 0; // Acquire chan[0]
                lock_ids[2] = target_locks[11] = 12 + 2; // Acquire chan[2]
                lock_ids[3] = target_locks[12] = 12 + 3; // Acquire chan[3]
                lock_ids[4] = target_locks[13] = 12 + partner[1]; // Acquire chan[partner[1]]
                lockManager.acquire_locks(lock_ids, 5);
                if(chan[partner[1]] != 255) {
                    lock_ids[0] = target_locks[13]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[13]; // Release chan[partner[1]]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[1]] = 255.
            private boolean t_calling_3_s_0_n_2() {
                if(callforwardbusy[partner[1]] == 255) {
                    lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                    lock_ids[4] = target_locks[9]; // Release chan[1]
                    lock_ids[5] = target_locks[10]; // Release chan[0]
                    lock_ids[6] = target_locks[11]; // Release chan[2]
                    lock_ids[7] = target_locks[12]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 8);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1].
                // SLCO expression | partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255.
                if(!(t_calling_3_s_0_n_0() && t_calling_3_s_0_n_1() && t_calling_3_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[1]] := 1.
                record[partner[1]] = (1) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_1Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[1] != 1.
            private boolean t_calling_4_s_0_n_0() {
                if(partner[1] != 1) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | partner[1] != 4.
            private boolean t_calling_4_s_0_n_1() {
                if(partner[1] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[1]] != 255.
            private boolean t_calling_4_s_0_n_2() {
                if(chan[partner[1]] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[1]] != 255.
            private boolean t_calling_4_s_0_n_3() {
                if(callforwardbusy[partner[1]] != 255) {
                    lock_ids[0] = target_locks[9]; // Release chan[1]
                    lock_ids[1] = target_locks[10]; // Release chan[0]
                    lock_ids[2] = target_locks[11]; // Release chan[2]
                    lock_ids[3] = target_locks[12]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 4);
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]].
                // SLCO expression | partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255.
                if(!(t_calling_4_s_0_n_0() && t_calling_4_s_0_n_1() && t_calling_4_s_0_n_2() && t_calling_4_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | record[partner[1]] := 1.
                record[partner[1]] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | partner[1] := callforwardbusy[partner[1]].
                partner[1] = (callforwardbusy[partner[1]]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[2] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[3] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[4] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[1] != 1.
            private boolean t_calling_5_s_0_n_0() {
                if(partner[1] != 1) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[1]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[2]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | partner[1] != 4.
            private boolean t_calling_5_s_0_n_1() {
                if(partner[1] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[1]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[2]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | chan[partner[1]] = 255.
            private boolean t_calling_5_s_0_n_2() {
                if(chan[partner[1]] == 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[1]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[2]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := ((1) + (0) * 20); chan[1] := ((partner[1]) + (0) * 20)] -> [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)].
                // SLCO expression | partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255.
                if(!(t_calling_5_s_0_n_0() && t_calling_5_s_0_n_1() && t_calling_5_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[1]] := 1.
                record[partner[1]] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | chan[partner[1]] := (1 + 0 * 20).
                chan[partner[1]] = ((1 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[10]; // Release chan[0]
                lock_ids[1] = target_locks[11]; // Release chan[2]
                lock_ids[2] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 3);
                // SLCO assignment | chan[1] := (partner[1] + 0 * 20).
                chan[1] = ((partner[1] + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[9]; // Release chan[1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_1Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[1] := 255; partner[1] := 255; dev := 1] -> [true; chan[1] := 255; partner[1] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[1] := 255.
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[1] % 20) != partner[1].
            private boolean t_oalert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[1], 20)) != partner[1]) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[1]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[1] % 20) != partner[1].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[1]) % 20) != partner[1] -> (chan[1] % 20) != partner[1].
                if(!(t_oalert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[1] / 20) = 1.
            private boolean t_oalert_1_s_0_n_0() {
                if((chan[1] / 20) == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[1]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[1]) % 20) = partner[1] and ((chan[1]) / 20) = 1 -> (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1.
                if(!((Math.floorMod(chan[1], 20)) == partner[1] && t_oalert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.oconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[1] % 20) = partner[1].
            private boolean t_oalert_2_s_0_n_0() {
                if((Math.floorMod(chan[1], 20)) == partner[1]) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | (chan[1] / 20) = 0.
            private boolean t_oalert_2_s_0_n_1() {
                if((chan[1] / 20) == 0) {
                    lock_ids[0] = target_locks[1]; // Release chan[1]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[1]) % 20) = partner[1] and ((chan[1]) / 20) = 0 -> (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0.
                if(!(t_oalert_2_s_0_n_0() && t_oalert_2_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.dveoringout;
                return true;
            }

            // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[1] := 255; chan[partner[1]] := 255].
            private boolean execute_transition_oconnected_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[1] := 255; chan[partner[1]] := 255] -> [true; dev := 1; chan[1] := 255; chan[partner[1]] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[1] := 255.
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lock_ids[1] = target_locks[2] = 12 + partner[1]; // Acquire chan[partner[1]]
                lockManager.acquire_locks(lock_ids, 2);
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[partner[1]] := 255.
                chan[partner[1]] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[2]; // Release chan[partner[1]]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[1] := 255; partner[1] := ((partner[1] % 20) + 0 * 20)].
            private boolean execute_transition_dveoringout_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[1] := 255; partner[1] := ((((partner[1]) % 20)) + (0) * 20)] -> [true; dev := 1; chan[1] := 255; partner[1] := ((partner[1] % 20) + 0 * 20)].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[1] := 255.
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[1] := ((partner[1] % 20) + 0 * 20).
                partner[1] = (((Math.floorMod(partner[1], 20)) + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[1] := 255; partner[1] := 255; dev := 1] -> [true; chan[1] := 255; partner[1] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[1] := 255.
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
            private boolean execute_transition_ringback_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[1] := 255; partner[1] := 255; dev := 1] -> [true; chan[1] := 255; partner[1] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[1] := 255.
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[2] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | record[1] != 255.
            private boolean t_ringback_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 4 + 1; // Acquire record[1]
                lockManager.acquire_locks(lock_ids, 1);
                if(record[1] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release record[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[1] != 255; partner[1] := record[1]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[1] != 255; partner[1] := record[1]].
                // SLCO expression | record[1] != 255.
                if(!(t_ringback_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[1] := record[1].
                lock_ids[0] = target_locks[1] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                partner[1] = (record[1]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release record[1]
                lock_ids[1] = target_locks[1]; // Release partner[1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | chan[1] = 255.
            private boolean t_talert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[1]; // Acquire chan[partner[1]]
                lock_ids[1] = target_locks[2] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 2);
                if(chan[1] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                    lock_ids[2] = target_locks[2]; // Release chan[1]
                    lockManager.release_locks(lock_ids, 3);
                    return true;
                }
                lock_ids[0] = target_locks[2]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[1] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[1] = 255.
                if(!(dev != 1 || t_talert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[1]] % 20) = 1.
            private boolean t_talert_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[1]], 20)) == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[1]] % 20) = 1.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[1]]) % 20) = 1 -> (chan[partner[1]] % 20) = 1.
                if(!(t_talert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.tpickup;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[1]] % 20) != 1.
            private boolean t_talert_2_s_0_n_0() {
                if((Math.floorMod(chan[partner[1]], 20)) != 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[1]] % 20) != 1.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[1]]) % 20) != 1 -> (chan[partner[1]] % 20) != 1.
                if(!(t_talert_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[1]] % 20) = 1.
            private boolean t_tpickup_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[1]; // Acquire chan[partner[1]]
                lock_ids[1] = target_locks[2] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 2);
                return (Math.floorMod(chan[partner[1]], 20)) == 1;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[1]]) % 20) = 1 and ((chan[partner[1]]) / 20) = 0; dev := 0; chan[partner[1]] := ((1) + (1) * 20); chan[1] := ((partner[1]) + (1) * 20)] -> [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)].
                // SLCO expression | (chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0.
                if(!(t_tpickup_0_s_0_n_0() && (chan[partner[1]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[1]] := (1 + 1 * 20).
                chan[partner[1]] = ((1 + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[partner[1]]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[1] := (partner[1] + 1 * 20).
                chan[1] = ((partner[1] + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[2]; // Release chan[1]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_1Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | chan[partner[1]] = 255.
            private boolean t_tpickup_1_s_0_n_0() {
                if(chan[partner[1]] == 255) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO expression wrapper | (chan[partner[1]] % 20) != 1.
            private boolean t_tpickup_1_s_0_n_1() {
                if((Math.floorMod(chan[partner[1]], 20)) != 1) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[1]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[partner[1]]
                lock_ids[2] = target_locks[2]; // Release chan[1]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[1]] = 255 or ((chan[partner[1]]) % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255] -> [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255].
                // SLCO expression | chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1.
                if(!(t_tpickup_1_s_0_n_0() || t_tpickup_1_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[1] := 255.
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[1] / 20) = 1.
            private boolean t_tconnected_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 1; // Acquire partner[1]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 1; // Acquire chan[1]
                lockManager.acquire_locks(lock_ids, 1);
                return (chan[1] / 20) == 1;
            }

            // SLCO expression wrapper | dev = 0.
            private boolean t_tconnected_0_s_0_n_1() {
                if(dev == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[1]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[1]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[1] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[1] / 20) = 1 and dev = 0.
                if(!(t_tconnected_0_s_0_n_0() && t_tconnected_0_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | dev = 1.
            private boolean t_tconnected_1_s_0_n_0() {
                if(dev == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[1]
                    lock_ids[1] = target_locks[1]; // Release chan[1]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[1]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[1] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[1] / 20) = 1 and dev = 1.
                if(!((chan[1] / 20) == 1 && t_tconnected_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_1Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[1] / 20) = 0.
            private boolean t_tconnected_2_s_0_n_0() {
                if((chan[1] / 20) == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lock_ids[1] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[1]) / 20) = 0; partner[1] := 255; chan[1] := 255] -> [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255].
                // SLCO expression | (chan[1] / 20) = 0.
                if(!(t_tconnected_2_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[1]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[1] := 255.
                chan[1] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[1]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                logger.info("D014.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | idle -> dialing | [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)].
                logger.info("T033.O");
                if(execute_transition_idle_0()) {
                    logger.info("T033.S");
                    logger.info("D014.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | idle -> qi | [chan[1] != 255; partner[1] := (chan[1] % 20)].
                logger.info("T034.O");
                if(execute_transition_idle_1()) {
                    logger.info("T034.S");
                    logger.info("D014.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                logger.info("D015.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[1] := 255].
                logger.info("T035.O");
                if(execute_transition_dialing_0()) {
                    logger.info("T035.S");
                    logger.info("D015.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[1] := 0.
                logger.info("T036.O");
                if(execute_transition_dialing_1()) {
                    logger.info("T036.S");
                    logger.info("D015.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[1] := 1.
                logger.info("T037.O");
                if(execute_transition_dialing_2()) {
                    logger.info("T037.S");
                    logger.info("D015.S");
                    return;
                }
                // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[1] := 2.
                logger.info("T038.O");
                if(execute_transition_dialing_3()) {
                    logger.info("T038.S");
                    logger.info("D015.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[1] := 3.
                logger.info("T039.O");
                if(execute_transition_dialing_4()) {
                    logger.info("T039.S");
                    logger.info("D015.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[1] := 4.
                logger.info("T040.O");
                if(execute_transition_dialing_5()) {
                    logger.info("T040.S");
                    logger.info("D015.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                logger.info("D016.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | calling -> busy | partner[1] = 1.
                logger.info("T041.O");
                if(execute_transition_calling_0()) {
                    logger.info("T041.S");
                    logger.info("D016.S");
                    return;
                }
                // SLCO expression | partner[1] = 4.
                if(partner[1] == 4) {
                    // [SEQ.START]
                    // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[1] = 4.
                    logger.info("T042.O");
                    if(execute_transition_calling_1()) {
                        logger.info("T042.S");
                        logger.info("D016.S");
                        return;
                    }
                    // SLCO transition (p:0, id:2) | calling -> ringback | partner[1] = 4.
                    logger.info("T043.O");
                    if(execute_transition_calling_2()) {
                        logger.info("T043.S");
                        logger.info("D016.S");
                        return;
                    }
                    // [SEQ.END]
                }
                // SLCO transition (p:0, id:3) | calling -> busy | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1].
                logger.info("T044.O");
                if(execute_transition_calling_3()) {
                    logger.info("T044.S");
                    logger.info("D016.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | calling -> calling | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]].
                logger.info("T045.O");
                if(execute_transition_calling_4()) {
                    logger.info("T045.S");
                    logger.info("D016.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | calling -> oalert | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)].
                logger.info("T046.O");
                if(execute_transition_calling_5()) {
                    logger.info("T046.S");
                    logger.info("D016.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                logger.info("D017.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
                logger.info("T047.O");
                if(execute_transition_busy_0()) {
                    logger.info("T047.S");
                    logger.info("D017.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                logger.info("D018.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[1]] % 20) = 1.
                logger.info("T048.O");
                if(execute_transition_qi_0()) {
                    logger.info("T048.S");
                    logger.info("D018.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[1]] % 20) != 1; partner[1] := 255].
                logger.info("T049.O");
                if(execute_transition_qi_1()) {
                    logger.info("T049.S");
                    logger.info("D018.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                logger.info("D019.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[1] = 255.
                logger.info("T050.O");
                if(execute_transition_talert_0()) {
                    logger.info("T050.S");
                    logger.info("D019.S");
                    return;
                }
                // [DET.START]
                // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[1]] % 20) = 1.
                logger.info("T051.O");
                if(execute_transition_talert_1()) {
                    logger.info("T051.S");
                    logger.info("D019.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[1]] % 20) != 1.
                logger.info("T052.O");
                if(execute_transition_talert_2()) {
                    logger.info("T052.S");
                    logger.info("D019.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                logger.info("D020.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
                logger.info("T053.O");
                if(execute_transition_unobtainable_0()) {
                    logger.info("T053.S");
                    logger.info("D020.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                logger.info("D021.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[1] % 20) != partner[1].
                logger.info("T054.O");
                if(execute_transition_oalert_0()) {
                    logger.info("T054.S");
                    logger.info("D021.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1.
                logger.info("T055.O");
                if(execute_transition_oalert_1()) {
                    logger.info("T055.S");
                    logger.info("D021.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0.
                logger.info("T056.O");
                if(execute_transition_oalert_2()) {
                    logger.info("T056.S");
                    logger.info("D021.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                logger.info("D022.O");
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                logger.info("D023.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[1] := 255; chan[partner[1]] := 255].
                logger.info("T057.O");
                if(execute_transition_oconnected_0()) {
                    logger.info("T057.S");
                    logger.info("D023.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                logger.info("D024.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[1] := 255; partner[1] := ((partner[1] % 20) + 0 * 20)].
                logger.info("T058.O");
                if(execute_transition_dveoringout_0()) {
                    logger.info("T058.S");
                    logger.info("D024.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                logger.info("D025.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)].
                logger.info("T059.O");
                if(execute_transition_tpickup_0()) {
                    logger.info("T059.S");
                    logger.info("D025.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255].
                logger.info("T060.O");
                if(execute_transition_tpickup_1()) {
                    logger.info("T060.S");
                    logger.info("D025.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                logger.info("D026.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 0; dev := 1].
                logger.info("T061.O");
                if(execute_transition_tconnected_0()) {
                    logger.info("T061.S");
                    logger.info("D026.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 1; dev := 0].
                logger.info("T062.O");
                if(execute_transition_tconnected_1()) {
                    logger.info("T062.S");
                    logger.info("D026.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255].
                logger.info("T063.O");
                if(execute_transition_tconnected_2()) {
                    logger.info("T063.S");
                    logger.info("D026.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                logger.info("D027.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
                logger.info("T064.O");
                if(execute_transition_ringback_0()) {
                    logger.info("T064.S");
                    logger.info("D027.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | ringback -> calling | [record[1] != 255; partner[1] := record[1]].
                logger.info("T065.O");
                if(execute_transition_ringback_1()) {
                    logger.info("T065.S");
                    logger.info("D027.S");
                    return;
                }
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case idle -> exec_idle();
                        case dialing -> exec_dialing();
                        case calling -> exec_calling();
                        case busy -> exec_busy();
                        case qi -> exec_qi();
                        case talert -> exec_talert();
                        case unobtainable -> exec_unobtainable();
                        case oalert -> exec_oalert();
                        case errorstate -> exec_errorstate();
                        case oconnected -> exec_oconnected();
                        case dveoringout -> exec_dveoringout();
                        case tpickup -> exec_tpickup();
                        case tconnected -> exec_tconnected();
                        case ringback -> exec_ringback();
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

        // Define the states fot the state machine User_2.
        interface GlobalClass_User_2Thread_States {
            enum States {
                idle, 
                dialing, 
                calling, 
                busy, 
                qi, 
                talert, 
                unobtainable, 
                oalert, 
                errorstate, 
                oconnected, 
                dveoringout, 
                tpickup, 
                tconnected, 
                ringback
            }
        }

        // Representation of the SLCO state machine User_2.
        class GlobalClass_User_2Thread extends Thread implements GlobalClass_User_2Thread_States {
            // Current state
            private GlobalClass_User_2Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int dev;
            private int mbit;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_User_2Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_2Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[9];
                target_locks = new int[14];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO expression wrapper | chan[2] = 255.
            private boolean t_idle_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(chan[2] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[2] = 255; dev := 0; chan[2] := ((2) + (0) * 20)] -> [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)].
                // SLCO expression | chan[2] = 255.
                if(!(t_idle_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[2] := (2 + 0 * 20).
                chan[2] = ((2 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.dialing;
                return true;
            }

            // SLCO expression wrapper | chan[2] != 255.
            private boolean t_idle_1_s_0_n_0() {
                if(chan[2] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[2] != 255; partner[2] := (chan[2] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[2] != 255; partner[2] := ((chan[2]) % 20)] -> [chan[2] != 255; partner[2] := (chan[2] % 20)].
                // SLCO expression | chan[2] != 255.
                if(!(t_idle_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[2] := (chan[2] % 20).
                partner[2] = ((Math.floorMod(chan[2], 20))) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_2Thread.States.qi;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[2]] % 20) = 2.
            private boolean t_qi_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[2]; // Acquire chan[partner[2]]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[partner[2]], 20)) == 2) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[2]] % 20) = 2.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[2]]) % 20) = 2 -> (chan[partner[2]] % 20) = 2.
                if(!(t_qi_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.talert;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[2]] % 20) != 2.
            private boolean t_qi_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[2]], 20)) != 2) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[2]] % 20) != 2; partner[2] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[2]]) % 20) != 2; partner[2] := 255] -> [(chan[partner[2]] % 20) != 2; partner[2] := 255].
                // SLCO expression | (chan[partner[2]] % 20) != 2.
                if(!(t_qi_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[2] := 255].
            private boolean execute_transition_dialing_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[2] := 255] -> [true; dev := 1; chan[2] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[2] := 255.
                lock_ids[0] = target_locks[0] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[2] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 0] -> partner[2] := 0.
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                partner[2] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[2] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 1] -> partner[2] := 1.
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                partner[2] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[2] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 2] -> partner[2] := 2.
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                partner[2] = (2) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[2] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 3] -> partner[2] := 3.
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                partner[2] = (3) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[2] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 4] -> partner[2] := 4.
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                partner[2] = (4) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[2] = 2.
            private boolean t_calling_0_s_0_n_0() {
                lock_ids[0] = target_locks[5] = 0 + 0; // Acquire callforwardbusy[0]
                lock_ids[1] = target_locks[6] = 0 + 1; // Acquire callforwardbusy[1]
                lock_ids[2] = target_locks[7] = 0 + 2; // Acquire callforwardbusy[2]
                lock_ids[3] = target_locks[8] = 0 + 3; // Acquire callforwardbusy[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[1] = 4 + 0; // Acquire record[0]
                lock_ids[1] = target_locks[2] = 4 + 1; // Acquire record[1]
                lock_ids[2] = target_locks[3] = 4 + 2; // Acquire record[2]
                lock_ids[3] = target_locks[4] = 4 + 3; // Acquire record[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(partner[2] == 2) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[2] = 2.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[2] = 2.
                if(!(t_calling_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[2] = 4.
            private boolean t_calling_1_s_0_n_0() {
                if(partner[2] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[2] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[2] = 4.
                if(!(t_calling_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.unobtainable;
                return true;
            }

            // SLCO expression wrapper | partner[2] = 4.
            private boolean t_calling_2_s_0_n_0() {
                if(partner[2] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[2] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[2] = 4.
                if(!(t_calling_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.ringback;
                return true;
            }

            // SLCO expression wrapper | partner[2] != 2 and partner[2] != 4.
            private boolean t_calling_3_s_0_n_0() {
                if(partner[2] != 2 && partner[2] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[9] = 12 + 2; // Acquire chan[2]
                lock_ids[1] = target_locks[10] = 12 + 0; // Acquire chan[0]
                lock_ids[2] = target_locks[11] = 12 + 1; // Acquire chan[1]
                lock_ids[3] = target_locks[12] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[2]] != 255.
            private boolean t_calling_3_s_0_n_1() {
                lock_ids[0] = target_locks[9] = 12 + 2; // Acquire chan[2]
                lock_ids[1] = target_locks[10] = 12 + 0; // Acquire chan[0]
                lock_ids[2] = target_locks[11] = 12 + 1; // Acquire chan[1]
                lock_ids[3] = target_locks[12] = 12 + 3; // Acquire chan[3]
                lock_ids[4] = target_locks[13] = 12 + partner[2]; // Acquire chan[partner[2]]
                lockManager.acquire_locks(lock_ids, 5);
                if(chan[partner[2]] != 255) {
                    lock_ids[0] = target_locks[13]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[13]; // Release chan[partner[2]]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[2]] = 255.
            private boolean t_calling_3_s_0_n_2() {
                if(callforwardbusy[partner[2]] == 255) {
                    lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                    lock_ids[4] = target_locks[9]; // Release chan[2]
                    lock_ids[5] = target_locks[10]; // Release chan[0]
                    lock_ids[6] = target_locks[11]; // Release chan[1]
                    lock_ids[7] = target_locks[12]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 8);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2].
                // SLCO expression | partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255.
                if(!(t_calling_3_s_0_n_0() && t_calling_3_s_0_n_1() && t_calling_3_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[2]] := 2.
                record[partner[2]] = (2) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_2Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[2] != 2.
            private boolean t_calling_4_s_0_n_0() {
                if(partner[2] != 2) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | partner[2] != 4.
            private boolean t_calling_4_s_0_n_1() {
                if(partner[2] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[2]] != 255.
            private boolean t_calling_4_s_0_n_2() {
                if(chan[partner[2]] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[2]] != 255.
            private boolean t_calling_4_s_0_n_3() {
                if(callforwardbusy[partner[2]] != 255) {
                    lock_ids[0] = target_locks[9]; // Release chan[2]
                    lock_ids[1] = target_locks[10]; // Release chan[0]
                    lock_ids[2] = target_locks[11]; // Release chan[1]
                    lock_ids[3] = target_locks[12]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 4);
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]].
                // SLCO expression | partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255.
                if(!(t_calling_4_s_0_n_0() && t_calling_4_s_0_n_1() && t_calling_4_s_0_n_2() && t_calling_4_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | record[partner[2]] := 2.
                record[partner[2]] = (2) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | partner[2] := callforwardbusy[partner[2]].
                partner[2] = (callforwardbusy[partner[2]]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[2] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[3] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[4] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[2] != 2.
            private boolean t_calling_5_s_0_n_0() {
                if(partner[2] != 2) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[2]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[1]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | partner[2] != 4.
            private boolean t_calling_5_s_0_n_1() {
                if(partner[2] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[2]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[1]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | chan[partner[2]] = 255.
            private boolean t_calling_5_s_0_n_2() {
                if(chan[partner[2]] == 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[2]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[1]
                lock_ids[8] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := ((2) + (0) * 20); chan[2] := ((partner[2]) + (0) * 20)] -> [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)].
                // SLCO expression | partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255.
                if(!(t_calling_5_s_0_n_0() && t_calling_5_s_0_n_1() && t_calling_5_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[2]] := 2.
                record[partner[2]] = (2) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | chan[partner[2]] := (2 + 0 * 20).
                chan[partner[2]] = ((2 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[10]; // Release chan[0]
                lock_ids[1] = target_locks[11]; // Release chan[1]
                lock_ids[2] = target_locks[12]; // Release chan[3]
                lockManager.release_locks(lock_ids, 3);
                // SLCO assignment | chan[2] := (partner[2] + 0 * 20).
                chan[2] = ((partner[2] + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[9]; // Release chan[2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_2Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[2] := 255; partner[2] := 255; dev := 1] -> [true; chan[2] := 255; partner[2] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[2] := 255.
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[2] % 20) != partner[2].
            private boolean t_oalert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[2], 20)) != partner[2]) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[2] % 20) != partner[2].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[2]) % 20) != partner[2] -> (chan[2] % 20) != partner[2].
                if(!(t_oalert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[2] / 20) = 1.
            private boolean t_oalert_1_s_0_n_0() {
                if((chan[2] / 20) == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[2]) % 20) = partner[2] and ((chan[2]) / 20) = 1 -> (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1.
                if(!((Math.floorMod(chan[2], 20)) == partner[2] && t_oalert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.oconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[2] % 20) = partner[2].
            private boolean t_oalert_2_s_0_n_0() {
                if((Math.floorMod(chan[2], 20)) == partner[2]) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | (chan[2] / 20) = 0.
            private boolean t_oalert_2_s_0_n_1() {
                if((chan[2] / 20) == 0) {
                    lock_ids[0] = target_locks[1]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[2]) % 20) = partner[2] and ((chan[2]) / 20) = 0 -> (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0.
                if(!(t_oalert_2_s_0_n_0() && t_oalert_2_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.dveoringout;
                return true;
            }

            // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[2] := 255; chan[partner[2]] := 255].
            private boolean execute_transition_oconnected_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[2] := 255; chan[partner[2]] := 255] -> [true; dev := 1; chan[2] := 255; chan[partner[2]] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[2] := 255.
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lock_ids[1] = target_locks[2] = 12 + partner[2]; // Acquire chan[partner[2]]
                lockManager.acquire_locks(lock_ids, 2);
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[partner[2]] := 255.
                chan[partner[2]] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[2]; // Release chan[partner[2]]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[2] := 255; partner[2] := ((partner[2] % 20) + 0 * 20)].
            private boolean execute_transition_dveoringout_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[2] := 255; partner[2] := ((((partner[2]) % 20)) + (0) * 20)] -> [true; dev := 1; chan[2] := 255; partner[2] := ((partner[2] % 20) + 0 * 20)].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[2] := 255.
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[2] := ((partner[2] % 20) + 0 * 20).
                partner[2] = (((Math.floorMod(partner[2], 20)) + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[2] := 255; partner[2] := 255; dev := 1] -> [true; chan[2] := 255; partner[2] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[2] := 255.
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
            private boolean execute_transition_ringback_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[2] := 255; partner[2] := 255; dev := 1] -> [true; chan[2] := 255; partner[2] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[2] := 255.
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[2] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | record[2] != 255.
            private boolean t_ringback_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 4 + 2; // Acquire record[2]
                lockManager.acquire_locks(lock_ids, 1);
                if(record[2] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release record[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[2] != 255; partner[2] := record[2]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[2] != 255; partner[2] := record[2]].
                // SLCO expression | record[2] != 255.
                if(!(t_ringback_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[2] := record[2].
                lock_ids[0] = target_locks[1] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                partner[2] = (record[2]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release record[2]
                lock_ids[1] = target_locks[1]; // Release partner[2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | chan[2] = 255.
            private boolean t_talert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[2]; // Acquire chan[partner[2]]
                lock_ids[1] = target_locks[2] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 2);
                if(chan[2] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                    lock_ids[2] = target_locks[2]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 3);
                    return true;
                }
                lock_ids[0] = target_locks[2]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[2] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[2] = 255.
                if(!(dev != 1 || t_talert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[2]] % 20) = 2.
            private boolean t_talert_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[2]], 20)) == 2) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[2]] % 20) = 2.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[2]]) % 20) = 2 -> (chan[partner[2]] % 20) = 2.
                if(!(t_talert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.tpickup;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[2]] % 20) != 2.
            private boolean t_talert_2_s_0_n_0() {
                if((Math.floorMod(chan[partner[2]], 20)) != 2) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[2]] % 20) != 2.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[2]]) % 20) != 2 -> (chan[partner[2]] % 20) != 2.
                if(!(t_talert_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[2]] % 20) = 2.
            private boolean t_tpickup_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[2]; // Acquire chan[partner[2]]
                lock_ids[1] = target_locks[2] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 2);
                return (Math.floorMod(chan[partner[2]], 20)) == 2;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[2]]) % 20) = 2 and ((chan[partner[2]]) / 20) = 0; dev := 0; chan[partner[2]] := ((2) + (1) * 20); chan[2] := ((partner[2]) + (1) * 20)] -> [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)].
                // SLCO expression | (chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0.
                if(!(t_tpickup_0_s_0_n_0() && (chan[partner[2]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[2]] := (2 + 1 * 20).
                chan[partner[2]] = ((2 + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[partner[2]]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[2] := (partner[2] + 1 * 20).
                chan[2] = ((partner[2] + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[2]; // Release chan[2]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_2Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | chan[partner[2]] = 255.
            private boolean t_tpickup_1_s_0_n_0() {
                if(chan[partner[2]] == 255) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO expression wrapper | (chan[partner[2]] % 20) != 2.
            private boolean t_tpickup_1_s_0_n_1() {
                if((Math.floorMod(chan[partner[2]], 20)) != 2) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[2]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[partner[2]]
                lock_ids[2] = target_locks[2]; // Release chan[2]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[2]] = 255 or ((chan[partner[2]]) % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255] -> [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255].
                // SLCO expression | chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2.
                if(!(t_tpickup_1_s_0_n_0() || t_tpickup_1_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[2] := 255.
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[2] / 20) = 1.
            private boolean t_tconnected_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 2; // Acquire partner[2]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 1);
                return (chan[2] / 20) == 1;
            }

            // SLCO expression wrapper | dev = 0.
            private boolean t_tconnected_0_s_0_n_1() {
                if(dev == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[2]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[2] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[2] / 20) = 1 and dev = 0.
                if(!(t_tconnected_0_s_0_n_0() && t_tconnected_0_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | dev = 1.
            private boolean t_tconnected_1_s_0_n_0() {
                if(dev == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[2]
                    lock_ids[1] = target_locks[1]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[2]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[2] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[2] / 20) = 1 and dev = 1.
                if(!((chan[2] / 20) == 1 && t_tconnected_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_2Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[2] / 20) = 0.
            private boolean t_tconnected_2_s_0_n_0() {
                if((chan[2] / 20) == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lock_ids[1] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[2]) / 20) = 0; partner[2] := 255; chan[2] := 255] -> [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255].
                // SLCO expression | (chan[2] / 20) = 0.
                if(!(t_tconnected_2_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[2]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[2] := 255.
                chan[2] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[2]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                logger.info("D028.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | idle -> dialing | [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)].
                logger.info("T066.O");
                if(execute_transition_idle_0()) {
                    logger.info("T066.S");
                    logger.info("D028.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | idle -> qi | [chan[2] != 255; partner[2] := (chan[2] % 20)].
                logger.info("T067.O");
                if(execute_transition_idle_1()) {
                    logger.info("T067.S");
                    logger.info("D028.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                logger.info("D029.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[2] := 255].
                logger.info("T068.O");
                if(execute_transition_dialing_0()) {
                    logger.info("T068.S");
                    logger.info("D029.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[2] := 0.
                logger.info("T069.O");
                if(execute_transition_dialing_1()) {
                    logger.info("T069.S");
                    logger.info("D029.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[2] := 1.
                logger.info("T070.O");
                if(execute_transition_dialing_2()) {
                    logger.info("T070.S");
                    logger.info("D029.S");
                    return;
                }
                // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[2] := 2.
                logger.info("T071.O");
                if(execute_transition_dialing_3()) {
                    logger.info("T071.S");
                    logger.info("D029.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[2] := 3.
                logger.info("T072.O");
                if(execute_transition_dialing_4()) {
                    logger.info("T072.S");
                    logger.info("D029.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[2] := 4.
                logger.info("T073.O");
                if(execute_transition_dialing_5()) {
                    logger.info("T073.S");
                    logger.info("D029.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                logger.info("D030.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | calling -> busy | partner[2] = 2.
                logger.info("T074.O");
                if(execute_transition_calling_0()) {
                    logger.info("T074.S");
                    logger.info("D030.S");
                    return;
                }
                // SLCO expression | partner[2] = 4.
                if(partner[2] == 4) {
                    // [SEQ.START]
                    // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[2] = 4.
                    logger.info("T075.O");
                    if(execute_transition_calling_1()) {
                        logger.info("T075.S");
                        logger.info("D030.S");
                        return;
                    }
                    // SLCO transition (p:0, id:2) | calling -> ringback | partner[2] = 4.
                    logger.info("T076.O");
                    if(execute_transition_calling_2()) {
                        logger.info("T076.S");
                        logger.info("D030.S");
                        return;
                    }
                    // [SEQ.END]
                }
                // SLCO transition (p:0, id:3) | calling -> busy | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2].
                logger.info("T077.O");
                if(execute_transition_calling_3()) {
                    logger.info("T077.S");
                    logger.info("D030.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | calling -> calling | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]].
                logger.info("T078.O");
                if(execute_transition_calling_4()) {
                    logger.info("T078.S");
                    logger.info("D030.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | calling -> oalert | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)].
                logger.info("T079.O");
                if(execute_transition_calling_5()) {
                    logger.info("T079.S");
                    logger.info("D030.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                logger.info("D031.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
                logger.info("T080.O");
                if(execute_transition_busy_0()) {
                    logger.info("T080.S");
                    logger.info("D031.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                logger.info("D032.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[2]] % 20) = 2.
                logger.info("T081.O");
                if(execute_transition_qi_0()) {
                    logger.info("T081.S");
                    logger.info("D032.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[2]] % 20) != 2; partner[2] := 255].
                logger.info("T082.O");
                if(execute_transition_qi_1()) {
                    logger.info("T082.S");
                    logger.info("D032.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                logger.info("D033.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[2] = 255.
                logger.info("T083.O");
                if(execute_transition_talert_0()) {
                    logger.info("T083.S");
                    logger.info("D033.S");
                    return;
                }
                // [DET.START]
                // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[2]] % 20) = 2.
                logger.info("T084.O");
                if(execute_transition_talert_1()) {
                    logger.info("T084.S");
                    logger.info("D033.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[2]] % 20) != 2.
                logger.info("T085.O");
                if(execute_transition_talert_2()) {
                    logger.info("T085.S");
                    logger.info("D033.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                logger.info("D034.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
                logger.info("T086.O");
                if(execute_transition_unobtainable_0()) {
                    logger.info("T086.S");
                    logger.info("D034.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                logger.info("D035.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[2] % 20) != partner[2].
                logger.info("T087.O");
                if(execute_transition_oalert_0()) {
                    logger.info("T087.S");
                    logger.info("D035.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1.
                logger.info("T088.O");
                if(execute_transition_oalert_1()) {
                    logger.info("T088.S");
                    logger.info("D035.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0.
                logger.info("T089.O");
                if(execute_transition_oalert_2()) {
                    logger.info("T089.S");
                    logger.info("D035.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                logger.info("D036.O");
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                logger.info("D037.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[2] := 255; chan[partner[2]] := 255].
                logger.info("T090.O");
                if(execute_transition_oconnected_0()) {
                    logger.info("T090.S");
                    logger.info("D037.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                logger.info("D038.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[2] := 255; partner[2] := ((partner[2] % 20) + 0 * 20)].
                logger.info("T091.O");
                if(execute_transition_dveoringout_0()) {
                    logger.info("T091.S");
                    logger.info("D038.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                logger.info("D039.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)].
                logger.info("T092.O");
                if(execute_transition_tpickup_0()) {
                    logger.info("T092.S");
                    logger.info("D039.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255].
                logger.info("T093.O");
                if(execute_transition_tpickup_1()) {
                    logger.info("T093.S");
                    logger.info("D039.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                logger.info("D040.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 0; dev := 1].
                logger.info("T094.O");
                if(execute_transition_tconnected_0()) {
                    logger.info("T094.S");
                    logger.info("D040.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 1; dev := 0].
                logger.info("T095.O");
                if(execute_transition_tconnected_1()) {
                    logger.info("T095.S");
                    logger.info("D040.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255].
                logger.info("T096.O");
                if(execute_transition_tconnected_2()) {
                    logger.info("T096.S");
                    logger.info("D040.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                logger.info("D041.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
                logger.info("T097.O");
                if(execute_transition_ringback_0()) {
                    logger.info("T097.S");
                    logger.info("D041.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | ringback -> calling | [record[2] != 255; partner[2] := record[2]].
                logger.info("T098.O");
                if(execute_transition_ringback_1()) {
                    logger.info("T098.S");
                    logger.info("D041.S");
                    return;
                }
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case idle -> exec_idle();
                        case dialing -> exec_dialing();
                        case calling -> exec_calling();
                        case busy -> exec_busy();
                        case qi -> exec_qi();
                        case talert -> exec_talert();
                        case unobtainable -> exec_unobtainable();
                        case oalert -> exec_oalert();
                        case errorstate -> exec_errorstate();
                        case oconnected -> exec_oconnected();
                        case dveoringout -> exec_dveoringout();
                        case tpickup -> exec_tpickup();
                        case tconnected -> exec_tconnected();
                        case ringback -> exec_ringback();
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

        // Define the states fot the state machine User_3.
        interface GlobalClass_User_3Thread_States {
            enum States {
                idle, 
                dialing, 
                calling, 
                busy, 
                qi, 
                talert, 
                unobtainable, 
                oalert, 
                errorstate, 
                oconnected, 
                dveoringout, 
                tpickup, 
                tconnected, 
                ringback
            }
        }

        // Representation of the SLCO state machine User_3.
        class GlobalClass_User_3Thread extends Thread implements GlobalClass_User_3Thread_States {
            // Current state
            private GlobalClass_User_3Thread.States currentState;

            // Random number generator to handle non-determinism.
            private final Random random;

            // Thread local variables.
            private int dev;
            private int mbit;

            // The lock manager of the parent class.
            private final LockManager lockManager;

            // A list of lock ids and target locks that can be reused.
            private final int[] lock_ids;
            private final int[] target_locks;

            GlobalClass_User_3Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_3Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[9];
                target_locks = new int[14];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO expression wrapper | chan[3] = 255.
            private boolean t_idle_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                if(chan[3] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[3] = 255; dev := 0; chan[3] := ((3) + (0) * 20)] -> [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)].
                // SLCO expression | chan[3] = 255.
                if(!(t_idle_0_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[3] := (3 + 0 * 20).
                chan[3] = ((3 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.dialing;
                return true;
            }

            // SLCO expression wrapper | chan[3] != 255.
            private boolean t_idle_1_s_0_n_0() {
                if(chan[3] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[3] != 255; partner[3] := (chan[3] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[3] != 255; partner[3] := ((chan[3]) % 20)] -> [chan[3] != 255; partner[3] := (chan[3] % 20)].
                // SLCO expression | chan[3] != 255.
                if(!(t_idle_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[3] := (chan[3] % 20).
                partner[3] = ((Math.floorMod(chan[3], 20))) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_3Thread.States.qi;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[3]] % 20) = 3.
            private boolean t_qi_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[3]; // Acquire chan[partner[3]]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[partner[3]], 20)) == 3) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[3]] % 20) = 3.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[3]]) % 20) = 3 -> (chan[partner[3]] % 20) = 3.
                if(!(t_qi_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.talert;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[3]] % 20) != 3.
            private boolean t_qi_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[3]], 20)) != 3) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[3]] % 20) != 3; partner[3] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[3]]) % 20) != 3; partner[3] := 255] -> [(chan[partner[3]] % 20) != 3; partner[3] := 255].
                // SLCO expression | (chan[partner[3]] % 20) != 3.
                if(!(t_qi_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[3] := 255].
            private boolean execute_transition_dialing_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[3] := 255] -> [true; dev := 1; chan[3] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[3] := 255.
                lock_ids[0] = target_locks[0] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[3] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 0] -> partner[3] := 0.
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                partner[3] = (0) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[3] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 1] -> partner[3] := 1.
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                partner[3] = (1) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[3] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 2] -> partner[3] := 2.
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                partner[3] = (2) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[3] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 3] -> partner[3] := 3.
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                partner[3] = (3) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[3] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 4] -> partner[3] := 4.
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                partner[3] = (4) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[3] = 3.
            private boolean t_calling_0_s_0_n_0() {
                lock_ids[0] = target_locks[5] = 0 + 0; // Acquire callforwardbusy[0]
                lock_ids[1] = target_locks[6] = 0 + 1; // Acquire callforwardbusy[1]
                lock_ids[2] = target_locks[7] = 0 + 2; // Acquire callforwardbusy[2]
                lock_ids[3] = target_locks[8] = 0 + 3; // Acquire callforwardbusy[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[1] = 4 + 0; // Acquire record[0]
                lock_ids[1] = target_locks[2] = 4 + 1; // Acquire record[1]
                lock_ids[2] = target_locks[3] = 4 + 2; // Acquire record[2]
                lock_ids[3] = target_locks[4] = 4 + 3; // Acquire record[3]
                lockManager.acquire_locks(lock_ids, 4);
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                if(partner[3] == 3) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[3] = 3.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[3] = 3.
                if(!(t_calling_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[3] = 4.
            private boolean t_calling_1_s_0_n_0() {
                if(partner[3] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[3] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[3] = 4.
                if(!(t_calling_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.unobtainable;
                return true;
            }

            // SLCO expression wrapper | partner[3] = 4.
            private boolean t_calling_2_s_0_n_0() {
                if(partner[3] == 4) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release record[0]
                    lock_ids[2] = target_locks[2]; // Release record[1]
                    lock_ids[3] = target_locks[3]; // Release record[2]
                    lock_ids[4] = target_locks[4]; // Release record[3]
                    lock_ids[5] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[6] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[7] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[8] = target_locks[8]; // Release callforwardbusy[3]
                    lockManager.release_locks(lock_ids, 9);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[3] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[3] = 4.
                if(!(t_calling_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.ringback;
                return true;
            }

            // SLCO expression wrapper | partner[3] != 3 and partner[3] != 4.
            private boolean t_calling_3_s_0_n_0() {
                if(partner[3] != 3 && partner[3] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[9] = 12 + 3; // Acquire chan[3]
                lock_ids[1] = target_locks[10] = 12 + 0; // Acquire chan[0]
                lock_ids[2] = target_locks[11] = 12 + 1; // Acquire chan[1]
                lock_ids[3] = target_locks[12] = 12 + 2; // Acquire chan[2]
                lockManager.acquire_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[3]] != 255.
            private boolean t_calling_3_s_0_n_1() {
                lock_ids[0] = target_locks[9] = 12 + 3; // Acquire chan[3]
                lock_ids[1] = target_locks[10] = 12 + 0; // Acquire chan[0]
                lock_ids[2] = target_locks[11] = 12 + 1; // Acquire chan[1]
                lock_ids[3] = target_locks[12] = 12 + 2; // Acquire chan[2]
                lock_ids[4] = target_locks[13] = 12 + partner[3]; // Acquire chan[partner[3]]
                lockManager.acquire_locks(lock_ids, 5);
                if(chan[partner[3]] != 255) {
                    lock_ids[0] = target_locks[13]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[13]; // Release chan[partner[3]]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[3]] = 255.
            private boolean t_calling_3_s_0_n_2() {
                if(callforwardbusy[partner[3]] == 255) {
                    lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                    lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                    lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                    lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                    lock_ids[4] = target_locks[9]; // Release chan[3]
                    lock_ids[5] = target_locks[10]; // Release chan[0]
                    lock_ids[6] = target_locks[11]; // Release chan[1]
                    lock_ids[7] = target_locks[12]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 8);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3].
                // SLCO expression | partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255.
                if(!(t_calling_3_s_0_n_0() && t_calling_3_s_0_n_1() && t_calling_3_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[3]] := 3.
                record[partner[3]] = (3) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_3Thread.States.busy;
                return true;
            }

            // SLCO expression wrapper | partner[3] != 3.
            private boolean t_calling_4_s_0_n_0() {
                if(partner[3] != 3) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | partner[3] != 4.
            private boolean t_calling_4_s_0_n_1() {
                if(partner[3] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | chan[partner[3]] != 255.
            private boolean t_calling_4_s_0_n_2() {
                if(chan[partner[3]] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO expression wrapper | callforwardbusy[partner[3]] != 255.
            private boolean t_calling_4_s_0_n_3() {
                if(callforwardbusy[partner[3]] != 255) {
                    lock_ids[0] = target_locks[9]; // Release chan[3]
                    lock_ids[1] = target_locks[10]; // Release chan[0]
                    lock_ids[2] = target_locks[11]; // Release chan[1]
                    lock_ids[3] = target_locks[12]; // Release chan[2]
                    lockManager.release_locks(lock_ids, 4);
                    return true;
                }
                lock_ids[0] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[1] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[2] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[3] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 4);
                return false;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]].
                // SLCO expression | partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255.
                if(!(t_calling_4_s_0_n_0() && t_calling_4_s_0_n_1() && t_calling_4_s_0_n_2() && t_calling_4_s_0_n_3())) {
                    return false;
                }
                // SLCO assignment | record[partner[3]] := 3.
                record[partner[3]] = (3) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | partner[3] := callforwardbusy[partner[3]].
                partner[3] = (callforwardbusy[partner[3]]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[5]; // Release callforwardbusy[0]
                lock_ids[2] = target_locks[6]; // Release callforwardbusy[1]
                lock_ids[3] = target_locks[7]; // Release callforwardbusy[2]
                lock_ids[4] = target_locks[8]; // Release callforwardbusy[3]
                lockManager.release_locks(lock_ids, 5);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | partner[3] != 3.
            private boolean t_calling_5_s_0_n_0() {
                if(partner[3] != 3) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[3]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[1]
                lock_ids[8] = target_locks[12]; // Release chan[2]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | partner[3] != 4.
            private boolean t_calling_5_s_0_n_1() {
                if(partner[3] != 4) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[3]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[1]
                lock_ids[8] = target_locks[12]; // Release chan[2]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO expression wrapper | chan[partner[3]] = 255.
            private boolean t_calling_5_s_0_n_2() {
                if(chan[partner[3]] == 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release record[0]
                lock_ids[2] = target_locks[2]; // Release record[1]
                lock_ids[3] = target_locks[3]; // Release record[2]
                lock_ids[4] = target_locks[4]; // Release record[3]
                lock_ids[5] = target_locks[9]; // Release chan[3]
                lock_ids[6] = target_locks[10]; // Release chan[0]
                lock_ids[7] = target_locks[11]; // Release chan[1]
                lock_ids[8] = target_locks[12]; // Release chan[2]
                lockManager.release_locks(lock_ids, 9);
                return false;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := ((3) + (0) * 20); chan[3] := ((partner[3]) + (0) * 20)] -> [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)].
                // SLCO expression | partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255.
                if(!(t_calling_5_s_0_n_0() && t_calling_5_s_0_n_1() && t_calling_5_s_0_n_2())) {
                    return false;
                }
                // SLCO assignment | record[partner[3]] := 3.
                record[partner[3]] = (3) & 0xff;
                lock_ids[0] = target_locks[1]; // Release record[0]
                lock_ids[1] = target_locks[2]; // Release record[1]
                lock_ids[2] = target_locks[3]; // Release record[2]
                lock_ids[3] = target_locks[4]; // Release record[3]
                lockManager.release_locks(lock_ids, 4);
                // SLCO assignment | chan[partner[3]] := (3 + 0 * 20).
                chan[partner[3]] = ((3 + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[10]; // Release chan[0]
                lock_ids[1] = target_locks[11]; // Release chan[1]
                lock_ids[2] = target_locks[12]; // Release chan[2]
                lockManager.release_locks(lock_ids, 3);
                // SLCO assignment | chan[3] := (partner[3] + 0 * 20).
                chan[3] = ((partner[3] + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[9]; // Release chan[3]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_3Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[3] := 255; partner[3] := 255; dev := 1] -> [true; chan[3] := 255; partner[3] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[3] := 255.
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[3] % 20) != partner[3].
            private boolean t_oalert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                if((Math.floorMod(chan[3], 20)) != partner[3]) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[3] % 20) != partner[3].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[3]) % 20) != partner[3] -> (chan[3] % 20) != partner[3].
                if(!(t_oalert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[3] / 20) = 1.
            private boolean t_oalert_1_s_0_n_0() {
                if((chan[3] / 20) == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[3]) % 20) = partner[3] and ((chan[3]) / 20) = 1 -> (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1.
                if(!((Math.floorMod(chan[3], 20)) == partner[3] && t_oalert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.oconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[3] % 20) = partner[3].
            private boolean t_oalert_2_s_0_n_0() {
                if((Math.floorMod(chan[3], 20)) == partner[3]) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO expression wrapper | (chan[3] / 20) = 0.
            private boolean t_oalert_2_s_0_n_1() {
                if((chan[3] / 20) == 0) {
                    lock_ids[0] = target_locks[1]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[3]) % 20) = partner[3] and ((chan[3]) / 20) = 0 -> (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0.
                if(!(t_oalert_2_s_0_n_0() && t_oalert_2_s_0_n_1())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.dveoringout;
                return true;
            }

            // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[3] := 255; chan[partner[3]] := 255].
            private boolean execute_transition_oconnected_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[3] := 255; chan[partner[3]] := 255] -> [true; dev := 1; chan[3] := 255; chan[partner[3]] := 255].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[3] := 255.
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lock_ids[1] = target_locks[2] = 12 + partner[3]; // Acquire chan[partner[3]]
                lockManager.acquire_locks(lock_ids, 2);
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[partner[3]] := 255.
                chan[partner[3]] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[2]; // Release chan[partner[3]]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[3] := 255; partner[3] := ((partner[3] % 20) + 0 * 20)].
            private boolean execute_transition_dveoringout_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [dev := 1; chan[3] := 255; partner[3] := ((((partner[3]) % 20)) + (0) * 20)] -> [true; dev := 1; chan[3] := 255; partner[3] := ((partner[3] % 20) + 0 * 20)].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | chan[3] := 255.
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[3] := ((partner[3] % 20) + 0 * 20).
                partner[3] = (((Math.floorMod(partner[3], 20)) + 0 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[3] := 255; partner[3] := 255; dev := 1] -> [true; chan[3] := 255; partner[3] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[3] := 255.
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
            private boolean execute_transition_ringback_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[3] := 255; partner[3] := 255; dev := 1] -> [true; chan[3] := 255; partner[3] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[3] := 255.
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[2] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | record[3] != 255.
            private boolean t_ringback_1_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 4 + 3; // Acquire record[3]
                lockManager.acquire_locks(lock_ids, 1);
                if(record[3] != 255) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release record[3]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[3] != 255; partner[3] := record[3]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[3] != 255; partner[3] := record[3]].
                // SLCO expression | record[3] != 255.
                if(!(t_ringback_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[3] := record[3].
                lock_ids[0] = target_locks[1] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                partner[3] = (record[3]) & 0xff;
                lock_ids[0] = target_locks[0]; // Release record[3]
                lock_ids[1] = target_locks[1]; // Release partner[3]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO expression wrapper | chan[3] = 255.
            private boolean t_talert_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[3]; // Acquire chan[partner[3]]
                lock_ids[1] = target_locks[2] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 2);
                if(chan[3] == 255) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                    lock_ids[2] = target_locks[2]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 3);
                    return true;
                }
                lock_ids[0] = target_locks[2]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);
                return false;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[3] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[3] = 255.
                if(!(dev != 1 || t_talert_0_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.errorstate;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[3]] % 20) = 3.
            private boolean t_talert_1_s_0_n_0() {
                if((Math.floorMod(chan[partner[3]], 20)) == 3) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[3]] % 20) = 3.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[3]]) % 20) = 3 -> (chan[partner[3]] % 20) = 3.
                if(!(t_talert_1_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.tpickup;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[3]] % 20) != 3.
            private boolean t_talert_2_s_0_n_0() {
                if((Math.floorMod(chan[partner[3]], 20)) != 3) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[3]] % 20) != 3.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[3]]) % 20) != 3 -> (chan[partner[3]] % 20) != 3.
                if(!(t_talert_2_s_0_n_0())) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[partner[3]] % 20) = 3.
            private boolean t_tpickup_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + partner[3]; // Acquire chan[partner[3]]
                lock_ids[1] = target_locks[2] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 2);
                return (Math.floorMod(chan[partner[3]], 20)) == 3;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[3]]) % 20) = 3 and ((chan[partner[3]]) / 20) = 0; dev := 0; chan[partner[3]] := ((3) + (1) * 20); chan[3] := ((partner[3]) + (1) * 20)] -> [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)].
                // SLCO expression | (chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0.
                if(!(t_tpickup_0_s_0_n_0() && (chan[partner[3]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[3]] := (3 + 1 * 20).
                chan[partner[3]] = ((3 + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[partner[3]]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[3] := (partner[3] + 1 * 20).
                chan[3] = ((partner[3] + 1 * 20)) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[2]; // Release chan[3]
                lockManager.release_locks(lock_ids, 2);

                currentState = GlobalClass_User_3Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | chan[partner[3]] = 255.
            private boolean t_tpickup_1_s_0_n_0() {
                if(chan[partner[3]] == 255) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                return false;
            }

            // SLCO expression wrapper | (chan[partner[3]] % 20) != 3.
            private boolean t_tpickup_1_s_0_n_1() {
                if((Math.floorMod(chan[partner[3]], 20)) != 3) {
                    lock_ids[0] = target_locks[1]; // Release chan[partner[3]]
                    lockManager.release_locks(lock_ids, 1);
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[partner[3]]
                lock_ids[2] = target_locks[2]; // Release chan[3]
                lockManager.release_locks(lock_ids, 3);
                return false;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[3]] = 255 or ((chan[partner[3]]) % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255] -> [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255].
                // SLCO expression | chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3.
                if(!(t_tpickup_1_s_0_n_0() || t_tpickup_1_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[3] := 255.
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[2]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO expression wrapper | (chan[3] / 20) = 1.
            private boolean t_tconnected_0_s_0_n_0() {
                lock_ids[0] = target_locks[0] = 8 + 3; // Acquire partner[3]
                lockManager.acquire_locks(lock_ids, 1);
                lock_ids[0] = target_locks[1] = 12 + 3; // Acquire chan[3]
                lockManager.acquire_locks(lock_ids, 1);
                return (chan[3] / 20) == 1;
            }

            // SLCO expression wrapper | dev = 0.
            private boolean t_tconnected_0_s_0_n_1() {
                if(dev == 0) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[3]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[3] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[3] / 20) = 1 and dev = 0.
                if(!(t_tconnected_0_s_0_n_0() && t_tconnected_0_s_0_n_1())) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | dev = 1.
            private boolean t_tconnected_1_s_0_n_0() {
                if(dev == 1) {
                    lock_ids[0] = target_locks[0]; // Release partner[3]
                    lock_ids[1] = target_locks[1]; // Release chan[3]
                    lockManager.release_locks(lock_ids, 2);
                    return true;
                }
                return false;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[3]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[3] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[3] / 20) = 1 and dev = 1.
                if(!((chan[3] / 20) == 1 && t_tconnected_1_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_3Thread.States.tconnected;
                return true;
            }

            // SLCO expression wrapper | (chan[3] / 20) = 0.
            private boolean t_tconnected_2_s_0_n_0() {
                if((chan[3] / 20) == 0) {
                    return true;
                }
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lock_ids[1] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 2);
                return false;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[3]) / 20) = 0; partner[3] := 255; chan[3] := 255] -> [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255].
                // SLCO expression | (chan[3] / 20) = 0.
                if(!(t_tconnected_2_s_0_n_0())) {
                    return false;
                }
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                lock_ids[0] = target_locks[0]; // Release partner[3]
                lockManager.release_locks(lock_ids, 1);
                // SLCO assignment | chan[3] := 255.
                chan[3] = (255) & 0xff;
                lock_ids[0] = target_locks[1]; // Release chan[3]
                lockManager.release_locks(lock_ids, 1);

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                logger.info("D042.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | idle -> dialing | [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)].
                logger.info("T099.O");
                if(execute_transition_idle_0()) {
                    logger.info("T099.S");
                    logger.info("D042.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | idle -> qi | [chan[3] != 255; partner[3] := (chan[3] % 20)].
                logger.info("T100.O");
                if(execute_transition_idle_1()) {
                    logger.info("T100.S");
                    logger.info("D042.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                logger.info("D043.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[3] := 255].
                logger.info("T101.O");
                if(execute_transition_dialing_0()) {
                    logger.info("T101.S");
                    logger.info("D043.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[3] := 0.
                logger.info("T102.O");
                if(execute_transition_dialing_1()) {
                    logger.info("T102.S");
                    logger.info("D043.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[3] := 1.
                logger.info("T103.O");
                if(execute_transition_dialing_2()) {
                    logger.info("T103.S");
                    logger.info("D043.S");
                    return;
                }
                // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[3] := 2.
                logger.info("T104.O");
                if(execute_transition_dialing_3()) {
                    logger.info("T104.S");
                    logger.info("D043.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[3] := 3.
                logger.info("T105.O");
                if(execute_transition_dialing_4()) {
                    logger.info("T105.S");
                    logger.info("D043.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[3] := 4.
                logger.info("T106.O");
                if(execute_transition_dialing_5()) {
                    logger.info("T106.S");
                    logger.info("D043.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                logger.info("D044.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | calling -> busy | partner[3] = 3.
                logger.info("T107.O");
                if(execute_transition_calling_0()) {
                    logger.info("T107.S");
                    logger.info("D044.S");
                    return;
                }
                // SLCO expression | partner[3] = 4.
                if(partner[3] == 4) {
                    // [SEQ.START]
                    // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[3] = 4.
                    logger.info("T108.O");
                    if(execute_transition_calling_1()) {
                        logger.info("T108.S");
                        logger.info("D044.S");
                        return;
                    }
                    // SLCO transition (p:0, id:2) | calling -> ringback | partner[3] = 4.
                    logger.info("T109.O");
                    if(execute_transition_calling_2()) {
                        logger.info("T109.S");
                        logger.info("D044.S");
                        return;
                    }
                    // [SEQ.END]
                }
                // SLCO transition (p:0, id:3) | calling -> busy | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3].
                logger.info("T110.O");
                if(execute_transition_calling_3()) {
                    logger.info("T110.S");
                    logger.info("D044.S");
                    return;
                }
                // SLCO transition (p:0, id:4) | calling -> calling | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]].
                logger.info("T111.O");
                if(execute_transition_calling_4()) {
                    logger.info("T111.S");
                    logger.info("D044.S");
                    return;
                }
                // SLCO transition (p:0, id:5) | calling -> oalert | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)].
                logger.info("T112.O");
                if(execute_transition_calling_5()) {
                    logger.info("T112.S");
                    logger.info("D044.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                logger.info("D045.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
                logger.info("T113.O");
                if(execute_transition_busy_0()) {
                    logger.info("T113.S");
                    logger.info("D045.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                logger.info("D046.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[3]] % 20) = 3.
                logger.info("T114.O");
                if(execute_transition_qi_0()) {
                    logger.info("T114.S");
                    logger.info("D046.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[3]] % 20) != 3; partner[3] := 255].
                logger.info("T115.O");
                if(execute_transition_qi_1()) {
                    logger.info("T115.S");
                    logger.info("D046.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                logger.info("D047.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[3] = 255.
                logger.info("T116.O");
                if(execute_transition_talert_0()) {
                    logger.info("T116.S");
                    logger.info("D047.S");
                    return;
                }
                // [DET.START]
                // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[3]] % 20) = 3.
                logger.info("T117.O");
                if(execute_transition_talert_1()) {
                    logger.info("T117.S");
                    logger.info("D047.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[3]] % 20) != 3.
                logger.info("T118.O");
                if(execute_transition_talert_2()) {
                    logger.info("T118.S");
                    logger.info("D047.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                logger.info("D048.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
                logger.info("T119.O");
                if(execute_transition_unobtainable_0()) {
                    logger.info("T119.S");
                    logger.info("D048.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                logger.info("D049.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[3] % 20) != partner[3].
                logger.info("T120.O");
                if(execute_transition_oalert_0()) {
                    logger.info("T120.S");
                    logger.info("D049.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1.
                logger.info("T121.O");
                if(execute_transition_oalert_1()) {
                    logger.info("T121.S");
                    logger.info("D049.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0.
                logger.info("T122.O");
                if(execute_transition_oalert_2()) {
                    logger.info("T122.S");
                    logger.info("D049.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                logger.info("D050.O");
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                logger.info("D051.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[3] := 255; chan[partner[3]] := 255].
                logger.info("T123.O");
                if(execute_transition_oconnected_0()) {
                    logger.info("T123.S");
                    logger.info("D051.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                logger.info("D052.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[3] := 255; partner[3] := ((partner[3] % 20) + 0 * 20)].
                logger.info("T124.O");
                if(execute_transition_dveoringout_0()) {
                    logger.info("T124.S");
                    logger.info("D052.S");
                    return;
                }
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                logger.info("D053.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)].
                logger.info("T125.O");
                if(execute_transition_tpickup_0()) {
                    logger.info("T125.S");
                    logger.info("D053.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255].
                logger.info("T126.O");
                if(execute_transition_tpickup_1()) {
                    logger.info("T126.S");
                    logger.info("D053.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                logger.info("D054.O");
                // [SEQ.START]
                // [DET.START]
                // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 0; dev := 1].
                logger.info("T127.O");
                if(execute_transition_tconnected_0()) {
                    logger.info("T127.S");
                    logger.info("D054.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 1; dev := 0].
                logger.info("T128.O");
                if(execute_transition_tconnected_1()) {
                    logger.info("T128.S");
                    logger.info("D054.S");
                    return;
                }
                // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255].
                logger.info("T129.O");
                if(execute_transition_tconnected_2()) {
                    logger.info("T129.S");
                    logger.info("D054.S");
                    return;
                }
                // [DET.END]
                // [SEQ.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                logger.info("D055.O");
                // [SEQ.START]
                // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
                logger.info("T130.O");
                if(execute_transition_ringback_0()) {
                    logger.info("T130.S");
                    logger.info("D055.S");
                    return;
                }
                // SLCO transition (p:0, id:1) | ringback -> calling | [record[3] != 255; partner[3] := record[3]].
                logger.info("T131.O");
                if(execute_transition_ringback_1()) {
                    logger.info("T131.S");
                    logger.info("D055.S");
                    return;
                }
                // [SEQ.END]
            }

            // Main state machine loop.
            private void exec() {
                Instant time_start = Instant.now();
                while(Duration.between(time_start, Instant.now()).toSeconds() < 30) {
                    switch(currentState) {
                        case idle -> exec_idle();
                        case dialing -> exec_dialing();
                        case calling -> exec_calling();
                        case busy -> exec_busy();
                        case qi -> exec_qi();
                        case talert -> exec_talert();
                        case unobtainable -> exec_unobtainable();
                        case oalert -> exec_oalert();
                        case errorstate -> exec_errorstate();
                        case oconnected -> exec_oconnected();
                        case dveoringout -> exec_dveoringout();
                        case tpickup -> exec_tpickup();
                        case tconnected -> exec_tconnected();
                        case ringback -> exec_ringback();
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
            T_User_0.start();
            T_User_1.start();
            T_User_2.start();
            T_User_3.start();
        }

        // Join all threads.
        public void joinThreads() {
            while (true) {
                try {
                    T_User_0.join();
                    T_User_1.join();
                    T_User_2.join();
                    T_User_3.join();
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
        Telephony model = new Telephony();
        model.startThreads();
        model.joinThreads();

        // Include information about the model.
        logger.info("JSON {\"name\": \"Telephony\", \"settings\": \"test_models/Telephony.slco -running_time=30 -package_name=testing.default -performance_measurements -vercors_verification\", \"classes\": {\"GlobalClass\": {\"name\": \"GlobalClass\", \"state_machines\": {\"User_0\": {\"name\": \"User_0\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D000\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)]\", \"id\": \"T000\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[0] != 255; partner[0] := (chan[0] % 20)]\", \"id\": \"T001\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D001\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[0] := 255]\", \"id\": \"T002\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[0] := 0\", \"id\": \"T003\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[0] := 1\", \"id\": \"T004\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[0] := 2\", \"id\": \"T005\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[0] := 3\", \"id\": \"T006\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[0] := 4\", \"id\": \"T007\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D002\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[0] = 0\", \"id\": \"T008\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[0] = 4\", \"id\": \"T009\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[0] = 4\", \"id\": \"T010\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0]\", \"id\": \"T011\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]]\", \"id\": \"T012\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)]\", \"id\": \"T013\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D003\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1]\", \"id\": \"T014\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D004\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[0]] % 20) = 0\", \"id\": \"T015\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[0]] % 20) != 0; partner[0] := 255]\", \"id\": \"T016\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D005\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[0] = 255\", \"id\": \"T017\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[0]] % 20) = 0\", \"id\": \"T018\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[0]] % 20) != 0\", \"id\": \"T019\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D006\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1]\", \"id\": \"T020\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D007\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[0] % 20) != partner[0]\", \"id\": \"T021\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1\", \"id\": \"T022\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0\", \"id\": \"T023\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D008\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D009\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[0] := 255; chan[partner[0]] := 255]\", \"id\": \"T024\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D010\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[0] := 255; partner[0] := ((partner[0] % 20) + 0 * 20)]\", \"id\": \"T025\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D011\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)]\", \"id\": \"T026\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255]\", \"id\": \"T027\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D012\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T028\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T029\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255]\", \"id\": \"T030\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D013\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1]\", \"id\": \"T031\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[0] != 255; partner[0] := record[0]]\", \"id\": \"T032\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}, \"User_1\": {\"name\": \"User_1\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D014\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)]\", \"id\": \"T033\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[1] != 255; partner[1] := (chan[1] % 20)]\", \"id\": \"T034\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D015\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[1] := 255]\", \"id\": \"T035\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[1] := 0\", \"id\": \"T036\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[1] := 1\", \"id\": \"T037\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[1] := 2\", \"id\": \"T038\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[1] := 3\", \"id\": \"T039\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[1] := 4\", \"id\": \"T040\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D016\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[1] = 1\", \"id\": \"T041\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[1] = 4\", \"id\": \"T042\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[1] = 4\", \"id\": \"T043\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1]\", \"id\": \"T044\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]]\", \"id\": \"T045\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)]\", \"id\": \"T046\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D017\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1]\", \"id\": \"T047\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D018\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[1]] % 20) = 1\", \"id\": \"T048\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[1]] % 20) != 1; partner[1] := 255]\", \"id\": \"T049\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D019\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[1] = 255\", \"id\": \"T050\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[1]] % 20) = 1\", \"id\": \"T051\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[1]] % 20) != 1\", \"id\": \"T052\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D020\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1]\", \"id\": \"T053\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D021\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[1] % 20) != partner[1]\", \"id\": \"T054\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1\", \"id\": \"T055\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0\", \"id\": \"T056\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D022\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D023\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[1] := 255; chan[partner[1]] := 255]\", \"id\": \"T057\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D024\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[1] := 255; partner[1] := ((partner[1] % 20) + 0 * 20)]\", \"id\": \"T058\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D025\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)]\", \"id\": \"T059\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255]\", \"id\": \"T060\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D026\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T061\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T062\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255]\", \"id\": \"T063\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D027\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1]\", \"id\": \"T064\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[1] != 255; partner[1] := record[1]]\", \"id\": \"T065\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}, \"User_2\": {\"name\": \"User_2\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D028\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)]\", \"id\": \"T066\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[2] != 255; partner[2] := (chan[2] % 20)]\", \"id\": \"T067\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D029\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[2] := 255]\", \"id\": \"T068\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[2] := 0\", \"id\": \"T069\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[2] := 1\", \"id\": \"T070\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[2] := 2\", \"id\": \"T071\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[2] := 3\", \"id\": \"T072\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[2] := 4\", \"id\": \"T073\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D030\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[2] = 2\", \"id\": \"T074\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[2] = 4\", \"id\": \"T075\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[2] = 4\", \"id\": \"T076\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2]\", \"id\": \"T077\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]]\", \"id\": \"T078\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)]\", \"id\": \"T079\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D031\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1]\", \"id\": \"T080\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D032\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[2]] % 20) = 2\", \"id\": \"T081\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[2]] % 20) != 2; partner[2] := 255]\", \"id\": \"T082\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D033\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[2] = 255\", \"id\": \"T083\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[2]] % 20) = 2\", \"id\": \"T084\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[2]] % 20) != 2\", \"id\": \"T085\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D034\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1]\", \"id\": \"T086\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D035\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[2] % 20) != partner[2]\", \"id\": \"T087\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1\", \"id\": \"T088\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0\", \"id\": \"T089\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D036\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D037\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[2] := 255; chan[partner[2]] := 255]\", \"id\": \"T090\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D038\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[2] := 255; partner[2] := ((partner[2] % 20) + 0 * 20)]\", \"id\": \"T091\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D039\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)]\", \"id\": \"T092\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255]\", \"id\": \"T093\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D040\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T094\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T095\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255]\", \"id\": \"T096\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D041\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1]\", \"id\": \"T097\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[2] != 255; partner[2] := record[2]]\", \"id\": \"T098\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}, \"User_3\": {\"name\": \"User_3\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D042\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)]\", \"id\": \"T099\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[3] != 255; partner[3] := (chan[3] % 20)]\", \"id\": \"T100\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D043\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[3] := 255]\", \"id\": \"T101\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[3] := 0\", \"id\": \"T102\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[3] := 1\", \"id\": \"T103\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[3] := 2\", \"id\": \"T104\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[3] := 3\", \"id\": \"T105\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[3] := 4\", \"id\": \"T106\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D044\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[3] = 3\", \"id\": \"T107\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[3] = 4\", \"id\": \"T108\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[3] = 4\", \"id\": \"T109\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3]\", \"id\": \"T110\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]]\", \"id\": \"T111\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)]\", \"id\": \"T112\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D045\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1]\", \"id\": \"T113\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D046\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[3]] % 20) = 3\", \"id\": \"T114\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[3]] % 20) != 3; partner[3] := 255]\", \"id\": \"T115\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D047\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[3] = 255\", \"id\": \"T116\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[3]] % 20) = 3\", \"id\": \"T117\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[3]] % 20) != 3\", \"id\": \"T118\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D048\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1]\", \"id\": \"T119\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D049\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[3] % 20) != partner[3]\", \"id\": \"T120\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1\", \"id\": \"T121\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0\", \"id\": \"T122\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D050\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D051\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[3] := 255; chan[partner[3]] := 255]\", \"id\": \"T123\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D052\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[3] := 255; partner[3] := ((partner[3] % 20) + 0 * 20)]\", \"id\": \"T124\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D053\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)]\", \"id\": \"T125\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255]\", \"id\": \"T126\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D054\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T127\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T128\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255]\", \"id\": \"T129\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D055\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1]\", \"id\": \"T130\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[3] != 255; partner[3] := record[3]]\", \"id\": \"T131\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
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