package testing.randompicknostructuresnolocks.counting;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        String log_settings = "[NDS,NL,T=30s,URP]";
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

            // Add variables needed for measurements.
            private long D000_O;
            private long D000_F;
            private long D000_S;
            private long T000_O;
            private long T000_F;
            private long T000_S;
            private long T001_O;
            private long T001_F;
            private long T001_S;
            private long D001_O;
            private long D001_F;
            private long D001_S;
            private long T002_O;
            private long T002_F;
            private long T002_S;
            private long T003_O;
            private long T003_F;
            private long T003_S;
            private long T004_O;
            private long T004_F;
            private long T004_S;
            private long T005_O;
            private long T005_F;
            private long T005_S;
            private long T006_O;
            private long T006_F;
            private long T006_S;
            private long T007_O;
            private long T007_F;
            private long T007_S;
            private long D002_O;
            private long D002_F;
            private long D002_S;
            private long T008_O;
            private long T008_F;
            private long T008_S;
            private long T009_O;
            private long T009_F;
            private long T009_S;
            private long T010_O;
            private long T010_F;
            private long T010_S;
            private long T011_O;
            private long T011_F;
            private long T011_S;
            private long T012_O;
            private long T012_F;
            private long T012_S;
            private long T013_O;
            private long T013_F;
            private long T013_S;
            private long D003_O;
            private long D003_F;
            private long D003_S;
            private long T014_O;
            private long T014_F;
            private long T014_S;
            private long D004_O;
            private long D004_F;
            private long D004_S;
            private long T015_O;
            private long T015_F;
            private long T015_S;
            private long T016_O;
            private long T016_F;
            private long T016_S;
            private long D005_O;
            private long D005_F;
            private long D005_S;
            private long T017_O;
            private long T017_F;
            private long T017_S;
            private long T018_O;
            private long T018_F;
            private long T018_S;
            private long T019_O;
            private long T019_F;
            private long T019_S;
            private long D006_O;
            private long D006_F;
            private long D006_S;
            private long T020_O;
            private long T020_F;
            private long T020_S;
            private long D007_O;
            private long D007_F;
            private long D007_S;
            private long T021_O;
            private long T021_F;
            private long T021_S;
            private long T022_O;
            private long T022_F;
            private long T022_S;
            private long T023_O;
            private long T023_F;
            private long T023_S;
            private long D008_O;
            private long D008_F;
            private long D008_S;
            private long D009_O;
            private long D009_F;
            private long D009_S;
            private long T024_O;
            private long T024_F;
            private long T024_S;
            private long D010_O;
            private long D010_F;
            private long D010_S;
            private long T025_O;
            private long T025_F;
            private long T025_S;
            private long D011_O;
            private long D011_F;
            private long D011_S;
            private long T026_O;
            private long T026_F;
            private long T026_S;
            private long T027_O;
            private long T027_F;
            private long T027_S;
            private long D012_O;
            private long D012_F;
            private long D012_S;
            private long T028_O;
            private long T028_F;
            private long T028_S;
            private long T029_O;
            private long T029_F;
            private long T029_S;
            private long T030_O;
            private long T030_F;
            private long T030_S;
            private long D013_O;
            private long D013_F;
            private long D013_S;
            private long T031_O;
            private long T031_F;
            private long T031_S;
            private long T032_O;
            private long T032_F;
            private long T032_S;

            GlobalClass_User_0Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_0Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[0] = 255; dev := 0; chan[0] := ((0) + (0) * 20)] -> [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)].
                // SLCO expression | chan[0] = 255.
                if(!(chan[0] == 255)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[0] := (0 + 0 * 20).
                chan[0] = ((0 + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_0Thread.States.dialing;
                return true;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[0] != 255; partner[0] := (chan[0] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[0] != 255; partner[0] := ((chan[0]) % 20)] -> [chan[0] != 255; partner[0] := (chan[0] % 20)].
                // SLCO expression | chan[0] != 255.
                if(!(chan[0] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[0] := (chan[0] % 20).
                partner[0] = ((Math.floorMod(chan[0], 20))) & 0xff;

                currentState = GlobalClass_User_0Thread.States.qi;
                return true;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[0]] % 20) = 0.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[0]]) % 20) = 0 -> (chan[partner[0]] % 20) = 0.
                if(!((Math.floorMod(chan[partner[0]], 20)) == 0)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.talert;
                return true;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[0]] % 20) != 0; partner[0] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[0]]) % 20) != 0; partner[0] := 255] -> [(chan[partner[0]] % 20) != 0; partner[0] := 255].
                // SLCO expression | (chan[partner[0]] % 20) != 0.
                if(!((Math.floorMod(chan[partner[0]], 20)) != 0)) {
                    return false;
                }
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;

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
                chan[0] = (255) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[0] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 0] -> partner[0] := 0.
                partner[0] = (0) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[0] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 1] -> partner[0] := 1.
                partner[0] = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[0] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 2] -> partner[0] := 2.
                partner[0] = (2) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[0] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 3] -> partner[0] := 3.
                partner[0] = (3) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[0] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[0] := 4] -> partner[0] := 4.
                partner[0] = (4) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[0] = 0.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[0] = 0.
                if(!(partner[0] == 0)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[0] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[0] = 4.
                if(!(partner[0] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.unobtainable;
                return true;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[0] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[0] = 4.
                if(!(partner[0] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.ringback;
                return true;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0].
                // SLCO expression | partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255.
                if(!(partner[0] != 0 && partner[0] != 4 && chan[partner[0]] != 255 && callforwardbusy[partner[0]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[0]] := 0.
                record[partner[0]] = (0) & 0xff;

                currentState = GlobalClass_User_0Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]].
                // SLCO expression | partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255.
                if(!(partner[0] != 0 && partner[0] != 4 && chan[partner[0]] != 255 && callforwardbusy[partner[0]] != 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[0]] := 0.
                record[partner[0]] = (0) & 0xff;
                // SLCO assignment | partner[0] := callforwardbusy[partner[0]].
                partner[0] = (callforwardbusy[partner[0]]) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := ((0) + (0) * 20); chan[0] := ((partner[0]) + (0) * 20)] -> [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)].
                // SLCO expression | partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255.
                if(!(partner[0] != 0 && partner[0] != 4 && chan[partner[0]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[0]] := 0.
                record[partner[0]] = (0) & 0xff;
                // SLCO assignment | chan[partner[0]] := (0 + 0 * 20).
                chan[partner[0]] = ((0 + 0 * 20)) & 0xff;
                // SLCO assignment | chan[0] := (partner[0] + 0 * 20).
                chan[0] = ((partner[0] + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_0Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[0] := 255; partner[0] := 255; dev := 1] -> [true; chan[0] := 255; partner[0] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[0] := 255.
                chan[0] = (255) & 0xff;
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[0] % 20) != partner[0].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[0]) % 20) != partner[0] -> (chan[0] % 20) != partner[0].
                if(!((Math.floorMod(chan[0], 20)) != partner[0])) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[0]) % 20) = partner[0] and ((chan[0]) / 20) = 1 -> (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1.
                if(!((Math.floorMod(chan[0], 20)) == partner[0] && (chan[0] / 20) == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.oconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[0]) % 20) = partner[0] and ((chan[0]) / 20) = 0 -> (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0.
                if(!((Math.floorMod(chan[0], 20)) == partner[0] && (chan[0] / 20) == 0)) {
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
                chan[0] = (255) & 0xff;
                // SLCO assignment | chan[partner[0]] := 255.
                chan[partner[0]] = (255) & 0xff;

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
                chan[0] = (255) & 0xff;
                // SLCO assignment | partner[0] := ((partner[0] % 20) + 0 * 20).
                partner[0] = (((Math.floorMod(partner[0], 20)) + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[0] := 255; partner[0] := 255; dev := 1] -> [true; chan[0] := 255; partner[0] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[0] := 255.
                chan[0] = (255) & 0xff;
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
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
                chan[0] = (255) & 0xff;
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[0] != 255; partner[0] := record[0]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[0] != 255; partner[0] := record[0]].
                // SLCO expression | record[0] != 255.
                if(!(record[0] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[0] := record[0].
                partner[0] = (record[0]) & 0xff;

                currentState = GlobalClass_User_0Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[0] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[0] = 255.
                if(!(dev != 1 || chan[0] == 255)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[0]] % 20) = 0.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[0]]) % 20) = 0 -> (chan[partner[0]] % 20) = 0.
                if(!((Math.floorMod(chan[partner[0]], 20)) == 0)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.tpickup;
                return true;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[0]] % 20) != 0.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[0]]) % 20) != 0 -> (chan[partner[0]] % 20) != 0.
                if(!((Math.floorMod(chan[partner[0]], 20)) != 0)) {
                    return false;
                }

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[0]]) % 20) = 0 and ((chan[partner[0]]) / 20) = 0; dev := 0; chan[partner[0]] := ((0) + (1) * 20); chan[0] := ((partner[0]) + (1) * 20)] -> [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)].
                // SLCO expression | (chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0.
                if(!((Math.floorMod(chan[partner[0]], 20)) == 0 && (chan[partner[0]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[0]] := (0 + 1 * 20).
                chan[partner[0]] = ((0 + 1 * 20)) & 0xff;
                // SLCO assignment | chan[0] := (partner[0] + 1 * 20).
                chan[0] = ((partner[0] + 1 * 20)) & 0xff;

                currentState = GlobalClass_User_0Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[0]] = 255 or ((chan[partner[0]]) % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255] -> [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255].
                // SLCO expression | chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0.
                if(!(chan[partner[0]] == 255 || (Math.floorMod(chan[partner[0]], 20)) != 0)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                // SLCO assignment | chan[0] := 255.
                chan[0] = (255) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[0]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[0] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[0] / 20) = 1 and dev = 0.
                if(!((chan[0] / 20) == 1 && dev == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_0Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[0]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[0] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[0] / 20) = 1 and dev = 1.
                if(!((chan[0] / 20) == 1 && dev == 1)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_0Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[0]) / 20) = 0; partner[0] := 255; chan[0] := 255] -> [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255].
                // SLCO expression | (chan[0] / 20) = 0.
                if(!((chan[0] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | partner[0] := 255.
                partner[0] = (255) & 0xff;
                // SLCO assignment | chan[0] := 255.
                chan[0] = (255) & 0xff;

                currentState = GlobalClass_User_0Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                D000_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | idle -> dialing | [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)].
                        T000_O++;
                        if(execute_transition_idle_0()) {
                            T000_S++;
                            D000_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | idle -> qi | [chan[0] != 255; partner[0] := (chan[0] % 20)].
                        T001_O++;
                        if(execute_transition_idle_1()) {
                            T001_S++;
                            D000_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                D001_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[0] := 255].
                        T002_O++;
                        if(execute_transition_dialing_0()) {
                            T002_S++;
                            D001_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[0] := 0.
                        T003_O++;
                        if(execute_transition_dialing_1()) {
                            T003_S++;
                            D001_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[0] := 1.
                        T004_O++;
                        if(execute_transition_dialing_2()) {
                            T004_S++;
                            D001_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[0] := 2.
                        T005_O++;
                        if(execute_transition_dialing_3()) {
                            T005_S++;
                            D001_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[0] := 3.
                        T006_O++;
                        if(execute_transition_dialing_4()) {
                            T006_S++;
                            D001_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[0] := 4.
                        T007_O++;
                        if(execute_transition_dialing_5()) {
                            T007_S++;
                            D001_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                D002_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | calling -> busy | partner[0] = 0.
                        T008_O++;
                        if(execute_transition_calling_0()) {
                            T008_S++;
                            D002_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[0] = 4.
                        T009_O++;
                        if(execute_transition_calling_1()) {
                            T009_S++;
                            D002_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | calling -> ringback | partner[0] = 4.
                        T010_O++;
                        if(execute_transition_calling_2()) {
                            T010_S++;
                            D002_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | calling -> busy | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0].
                        T011_O++;
                        if(execute_transition_calling_3()) {
                            T011_S++;
                            D002_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | calling -> calling | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]].
                        T012_O++;
                        if(execute_transition_calling_4()) {
                            T012_S++;
                            D002_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | calling -> oalert | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)].
                        T013_O++;
                        if(execute_transition_calling_5()) {
                            T013_S++;
                            D002_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                D003_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
                T014_O++;
                if(execute_transition_busy_0()) {
                    T014_S++;
                    D003_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                D004_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[0]] % 20) = 0.
                        T015_O++;
                        if(execute_transition_qi_0()) {
                            T015_S++;
                            D004_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[0]] % 20) != 0; partner[0] := 255].
                        T016_O++;
                        if(execute_transition_qi_1()) {
                            T016_S++;
                            D004_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                D005_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[0] = 255.
                        T017_O++;
                        if(execute_transition_talert_0()) {
                            T017_S++;
                            D005_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[0]] % 20) = 0.
                        T018_O++;
                        if(execute_transition_talert_1()) {
                            T018_S++;
                            D005_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[0]] % 20) != 0.
                        T019_O++;
                        if(execute_transition_talert_2()) {
                            T019_S++;
                            D005_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                D006_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
                T020_O++;
                if(execute_transition_unobtainable_0()) {
                    T020_S++;
                    D006_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                D007_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[0] % 20) != partner[0].
                        T021_O++;
                        if(execute_transition_oalert_0()) {
                            T021_S++;
                            D007_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1.
                        T022_O++;
                        if(execute_transition_oalert_1()) {
                            T022_S++;
                            D007_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0.
                        T023_O++;
                        if(execute_transition_oalert_2()) {
                            T023_S++;
                            D007_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                D008_O++;
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                D009_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[0] := 255; chan[partner[0]] := 255].
                T024_O++;
                if(execute_transition_oconnected_0()) {
                    T024_S++;
                    D009_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                D010_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[0] := 255; partner[0] := ((partner[0] % 20) + 0 * 20)].
                T025_O++;
                if(execute_transition_dveoringout_0()) {
                    T025_S++;
                    D010_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                D011_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)].
                        T026_O++;
                        if(execute_transition_tpickup_0()) {
                            T026_S++;
                            D011_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255].
                        T027_O++;
                        if(execute_transition_tpickup_1()) {
                            T027_S++;
                            D011_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                D012_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 0; dev := 1].
                        T028_O++;
                        if(execute_transition_tconnected_0()) {
                            T028_S++;
                            D012_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 1; dev := 0].
                        T029_O++;
                        if(execute_transition_tconnected_1()) {
                            T029_S++;
                            D012_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255].
                        T030_O++;
                        if(execute_transition_tconnected_2()) {
                            T030_S++;
                            D012_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                D013_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1].
                        T031_O++;
                        if(execute_transition_ringback_0()) {
                            T031_S++;
                            D013_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | ringback -> calling | [record[0] != 255; partner[0] := record[0]].
                        T032_O++;
                        if(execute_transition_ringback_1()) {
                            T032_S++;
                            D013_S++;
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

                // Report all counts.
                logger.info("D000.O " + D000_O);
                logger.info("D000.F " + D000_F);
                logger.info("D000.S " + D000_S);
                logger.info("T000.O " + T000_O);
                logger.info("T000.F " + T000_F);
                logger.info("T000.S " + T000_S);
                logger.info("T001.O " + T001_O);
                logger.info("T001.F " + T001_F);
                logger.info("T001.S " + T001_S);
                logger.info("D001.O " + D001_O);
                logger.info("D001.F " + D001_F);
                logger.info("D001.S " + D001_S);
                logger.info("T002.O " + T002_O);
                logger.info("T002.F " + T002_F);
                logger.info("T002.S " + T002_S);
                logger.info("T003.O " + T003_O);
                logger.info("T003.F " + T003_F);
                logger.info("T003.S " + T003_S);
                logger.info("T004.O " + T004_O);
                logger.info("T004.F " + T004_F);
                logger.info("T004.S " + T004_S);
                logger.info("T005.O " + T005_O);
                logger.info("T005.F " + T005_F);
                logger.info("T005.S " + T005_S);
                logger.info("T006.O " + T006_O);
                logger.info("T006.F " + T006_F);
                logger.info("T006.S " + T006_S);
                logger.info("T007.O " + T007_O);
                logger.info("T007.F " + T007_F);
                logger.info("T007.S " + T007_S);
                logger.info("D002.O " + D002_O);
                logger.info("D002.F " + D002_F);
                logger.info("D002.S " + D002_S);
                logger.info("T008.O " + T008_O);
                logger.info("T008.F " + T008_F);
                logger.info("T008.S " + T008_S);
                logger.info("T009.O " + T009_O);
                logger.info("T009.F " + T009_F);
                logger.info("T009.S " + T009_S);
                logger.info("T010.O " + T010_O);
                logger.info("T010.F " + T010_F);
                logger.info("T010.S " + T010_S);
                logger.info("T011.O " + T011_O);
                logger.info("T011.F " + T011_F);
                logger.info("T011.S " + T011_S);
                logger.info("T012.O " + T012_O);
                logger.info("T012.F " + T012_F);
                logger.info("T012.S " + T012_S);
                logger.info("T013.O " + T013_O);
                logger.info("T013.F " + T013_F);
                logger.info("T013.S " + T013_S);
                logger.info("D003.O " + D003_O);
                logger.info("D003.F " + D003_F);
                logger.info("D003.S " + D003_S);
                logger.info("T014.O " + T014_O);
                logger.info("T014.F " + T014_F);
                logger.info("T014.S " + T014_S);
                logger.info("D004.O " + D004_O);
                logger.info("D004.F " + D004_F);
                logger.info("D004.S " + D004_S);
                logger.info("T015.O " + T015_O);
                logger.info("T015.F " + T015_F);
                logger.info("T015.S " + T015_S);
                logger.info("T016.O " + T016_O);
                logger.info("T016.F " + T016_F);
                logger.info("T016.S " + T016_S);
                logger.info("D005.O " + D005_O);
                logger.info("D005.F " + D005_F);
                logger.info("D005.S " + D005_S);
                logger.info("T017.O " + T017_O);
                logger.info("T017.F " + T017_F);
                logger.info("T017.S " + T017_S);
                logger.info("T018.O " + T018_O);
                logger.info("T018.F " + T018_F);
                logger.info("T018.S " + T018_S);
                logger.info("T019.O " + T019_O);
                logger.info("T019.F " + T019_F);
                logger.info("T019.S " + T019_S);
                logger.info("D006.O " + D006_O);
                logger.info("D006.F " + D006_F);
                logger.info("D006.S " + D006_S);
                logger.info("T020.O " + T020_O);
                logger.info("T020.F " + T020_F);
                logger.info("T020.S " + T020_S);
                logger.info("D007.O " + D007_O);
                logger.info("D007.F " + D007_F);
                logger.info("D007.S " + D007_S);
                logger.info("T021.O " + T021_O);
                logger.info("T021.F " + T021_F);
                logger.info("T021.S " + T021_S);
                logger.info("T022.O " + T022_O);
                logger.info("T022.F " + T022_F);
                logger.info("T022.S " + T022_S);
                logger.info("T023.O " + T023_O);
                logger.info("T023.F " + T023_F);
                logger.info("T023.S " + T023_S);
                logger.info("D008.O " + D008_O);
                logger.info("D008.F " + D008_F);
                logger.info("D008.S " + D008_S);
                logger.info("D009.O " + D009_O);
                logger.info("D009.F " + D009_F);
                logger.info("D009.S " + D009_S);
                logger.info("T024.O " + T024_O);
                logger.info("T024.F " + T024_F);
                logger.info("T024.S " + T024_S);
                logger.info("D010.O " + D010_O);
                logger.info("D010.F " + D010_F);
                logger.info("D010.S " + D010_S);
                logger.info("T025.O " + T025_O);
                logger.info("T025.F " + T025_F);
                logger.info("T025.S " + T025_S);
                logger.info("D011.O " + D011_O);
                logger.info("D011.F " + D011_F);
                logger.info("D011.S " + D011_S);
                logger.info("T026.O " + T026_O);
                logger.info("T026.F " + T026_F);
                logger.info("T026.S " + T026_S);
                logger.info("T027.O " + T027_O);
                logger.info("T027.F " + T027_F);
                logger.info("T027.S " + T027_S);
                logger.info("D012.O " + D012_O);
                logger.info("D012.F " + D012_F);
                logger.info("D012.S " + D012_S);
                logger.info("T028.O " + T028_O);
                logger.info("T028.F " + T028_F);
                logger.info("T028.S " + T028_S);
                logger.info("T029.O " + T029_O);
                logger.info("T029.F " + T029_F);
                logger.info("T029.S " + T029_S);
                logger.info("T030.O " + T030_O);
                logger.info("T030.F " + T030_F);
                logger.info("T030.S " + T030_S);
                logger.info("D013.O " + D013_O);
                logger.info("D013.F " + D013_F);
                logger.info("D013.S " + D013_S);
                logger.info("T031.O " + T031_O);
                logger.info("T031.F " + T031_F);
                logger.info("T031.S " + T031_S);
                logger.info("T032.O " + T032_O);
                logger.info("T032.F " + T032_F);
                logger.info("T032.S " + T032_S);
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

            // Add variables needed for measurements.
            private long D014_O;
            private long D014_F;
            private long D014_S;
            private long T033_O;
            private long T033_F;
            private long T033_S;
            private long T034_O;
            private long T034_F;
            private long T034_S;
            private long D015_O;
            private long D015_F;
            private long D015_S;
            private long T035_O;
            private long T035_F;
            private long T035_S;
            private long T036_O;
            private long T036_F;
            private long T036_S;
            private long T037_O;
            private long T037_F;
            private long T037_S;
            private long T038_O;
            private long T038_F;
            private long T038_S;
            private long T039_O;
            private long T039_F;
            private long T039_S;
            private long T040_O;
            private long T040_F;
            private long T040_S;
            private long D016_O;
            private long D016_F;
            private long D016_S;
            private long T041_O;
            private long T041_F;
            private long T041_S;
            private long T042_O;
            private long T042_F;
            private long T042_S;
            private long T043_O;
            private long T043_F;
            private long T043_S;
            private long T044_O;
            private long T044_F;
            private long T044_S;
            private long T045_O;
            private long T045_F;
            private long T045_S;
            private long T046_O;
            private long T046_F;
            private long T046_S;
            private long D017_O;
            private long D017_F;
            private long D017_S;
            private long T047_O;
            private long T047_F;
            private long T047_S;
            private long D018_O;
            private long D018_F;
            private long D018_S;
            private long T048_O;
            private long T048_F;
            private long T048_S;
            private long T049_O;
            private long T049_F;
            private long T049_S;
            private long D019_O;
            private long D019_F;
            private long D019_S;
            private long T050_O;
            private long T050_F;
            private long T050_S;
            private long T051_O;
            private long T051_F;
            private long T051_S;
            private long T052_O;
            private long T052_F;
            private long T052_S;
            private long D020_O;
            private long D020_F;
            private long D020_S;
            private long T053_O;
            private long T053_F;
            private long T053_S;
            private long D021_O;
            private long D021_F;
            private long D021_S;
            private long T054_O;
            private long T054_F;
            private long T054_S;
            private long T055_O;
            private long T055_F;
            private long T055_S;
            private long T056_O;
            private long T056_F;
            private long T056_S;
            private long D022_O;
            private long D022_F;
            private long D022_S;
            private long D023_O;
            private long D023_F;
            private long D023_S;
            private long T057_O;
            private long T057_F;
            private long T057_S;
            private long D024_O;
            private long D024_F;
            private long D024_S;
            private long T058_O;
            private long T058_F;
            private long T058_S;
            private long D025_O;
            private long D025_F;
            private long D025_S;
            private long T059_O;
            private long T059_F;
            private long T059_S;
            private long T060_O;
            private long T060_F;
            private long T060_S;
            private long D026_O;
            private long D026_F;
            private long D026_S;
            private long T061_O;
            private long T061_F;
            private long T061_S;
            private long T062_O;
            private long T062_F;
            private long T062_S;
            private long T063_O;
            private long T063_F;
            private long T063_S;
            private long D027_O;
            private long D027_F;
            private long D027_S;
            private long T064_O;
            private long T064_F;
            private long T064_S;
            private long T065_O;
            private long T065_F;
            private long T065_S;

            GlobalClass_User_1Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_1Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[1] = 255; dev := 0; chan[1] := ((1) + (0) * 20)] -> [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)].
                // SLCO expression | chan[1] = 255.
                if(!(chan[1] == 255)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[1] := (1 + 0 * 20).
                chan[1] = ((1 + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_1Thread.States.dialing;
                return true;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[1] != 255; partner[1] := (chan[1] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[1] != 255; partner[1] := ((chan[1]) % 20)] -> [chan[1] != 255; partner[1] := (chan[1] % 20)].
                // SLCO expression | chan[1] != 255.
                if(!(chan[1] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[1] := (chan[1] % 20).
                partner[1] = ((Math.floorMod(chan[1], 20))) & 0xff;

                currentState = GlobalClass_User_1Thread.States.qi;
                return true;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[1]] % 20) = 1.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[1]]) % 20) = 1 -> (chan[partner[1]] % 20) = 1.
                if(!((Math.floorMod(chan[partner[1]], 20)) == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.talert;
                return true;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[1]] % 20) != 1; partner[1] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[1]]) % 20) != 1; partner[1] := 255] -> [(chan[partner[1]] % 20) != 1; partner[1] := 255].
                // SLCO expression | (chan[partner[1]] % 20) != 1.
                if(!((Math.floorMod(chan[partner[1]], 20)) != 1)) {
                    return false;
                }
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;

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
                chan[1] = (255) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[1] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 0] -> partner[1] := 0.
                partner[1] = (0) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[1] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 1] -> partner[1] := 1.
                partner[1] = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[1] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 2] -> partner[1] := 2.
                partner[1] = (2) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[1] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 3] -> partner[1] := 3.
                partner[1] = (3) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[1] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[1] := 4] -> partner[1] := 4.
                partner[1] = (4) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[1] = 1.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[1] = 1.
                if(!(partner[1] == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[1] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[1] = 4.
                if(!(partner[1] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.unobtainable;
                return true;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[1] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[1] = 4.
                if(!(partner[1] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.ringback;
                return true;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1].
                // SLCO expression | partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255.
                if(!(partner[1] != 1 && partner[1] != 4 && chan[partner[1]] != 255 && callforwardbusy[partner[1]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[1]] := 1.
                record[partner[1]] = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]].
                // SLCO expression | partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255.
                if(!(partner[1] != 1 && partner[1] != 4 && chan[partner[1]] != 255 && callforwardbusy[partner[1]] != 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[1]] := 1.
                record[partner[1]] = (1) & 0xff;
                // SLCO assignment | partner[1] := callforwardbusy[partner[1]].
                partner[1] = (callforwardbusy[partner[1]]) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := ((1) + (0) * 20); chan[1] := ((partner[1]) + (0) * 20)] -> [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)].
                // SLCO expression | partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255.
                if(!(partner[1] != 1 && partner[1] != 4 && chan[partner[1]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[1]] := 1.
                record[partner[1]] = (1) & 0xff;
                // SLCO assignment | chan[partner[1]] := (1 + 0 * 20).
                chan[partner[1]] = ((1 + 0 * 20)) & 0xff;
                // SLCO assignment | chan[1] := (partner[1] + 0 * 20).
                chan[1] = ((partner[1] + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_1Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[1] := 255; partner[1] := 255; dev := 1] -> [true; chan[1] := 255; partner[1] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[1] := 255.
                chan[1] = (255) & 0xff;
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[1] % 20) != partner[1].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[1]) % 20) != partner[1] -> (chan[1] % 20) != partner[1].
                if(!((Math.floorMod(chan[1], 20)) != partner[1])) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[1]) % 20) = partner[1] and ((chan[1]) / 20) = 1 -> (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1.
                if(!((Math.floorMod(chan[1], 20)) == partner[1] && (chan[1] / 20) == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.oconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[1]) % 20) = partner[1] and ((chan[1]) / 20) = 0 -> (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0.
                if(!((Math.floorMod(chan[1], 20)) == partner[1] && (chan[1] / 20) == 0)) {
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
                chan[1] = (255) & 0xff;
                // SLCO assignment | chan[partner[1]] := 255.
                chan[partner[1]] = (255) & 0xff;

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
                chan[1] = (255) & 0xff;
                // SLCO assignment | partner[1] := ((partner[1] % 20) + 0 * 20).
                partner[1] = (((Math.floorMod(partner[1], 20)) + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[1] := 255; partner[1] := 255; dev := 1] -> [true; chan[1] := 255; partner[1] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[1] := 255.
                chan[1] = (255) & 0xff;
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
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
                chan[1] = (255) & 0xff;
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[1] != 255; partner[1] := record[1]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[1] != 255; partner[1] := record[1]].
                // SLCO expression | record[1] != 255.
                if(!(record[1] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[1] := record[1].
                partner[1] = (record[1]) & 0xff;

                currentState = GlobalClass_User_1Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[1] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[1] = 255.
                if(!(dev != 1 || chan[1] == 255)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[1]] % 20) = 1.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[1]]) % 20) = 1 -> (chan[partner[1]] % 20) = 1.
                if(!((Math.floorMod(chan[partner[1]], 20)) == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.tpickup;
                return true;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[1]] % 20) != 1.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[1]]) % 20) != 1 -> (chan[partner[1]] % 20) != 1.
                if(!((Math.floorMod(chan[partner[1]], 20)) != 1)) {
                    return false;
                }

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[1]]) % 20) = 1 and ((chan[partner[1]]) / 20) = 0; dev := 0; chan[partner[1]] := ((1) + (1) * 20); chan[1] := ((partner[1]) + (1) * 20)] -> [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)].
                // SLCO expression | (chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0.
                if(!((Math.floorMod(chan[partner[1]], 20)) == 1 && (chan[partner[1]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[1]] := (1 + 1 * 20).
                chan[partner[1]] = ((1 + 1 * 20)) & 0xff;
                // SLCO assignment | chan[1] := (partner[1] + 1 * 20).
                chan[1] = ((partner[1] + 1 * 20)) & 0xff;

                currentState = GlobalClass_User_1Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[1]] = 255 or ((chan[partner[1]]) % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255] -> [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255].
                // SLCO expression | chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1.
                if(!(chan[partner[1]] == 255 || (Math.floorMod(chan[partner[1]], 20)) != 1)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                // SLCO assignment | chan[1] := 255.
                chan[1] = (255) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[1]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[1] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[1] / 20) = 1 and dev = 0.
                if(!((chan[1] / 20) == 1 && dev == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_1Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[1]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[1] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[1] / 20) = 1 and dev = 1.
                if(!((chan[1] / 20) == 1 && dev == 1)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_1Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[1]) / 20) = 0; partner[1] := 255; chan[1] := 255] -> [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255].
                // SLCO expression | (chan[1] / 20) = 0.
                if(!((chan[1] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | partner[1] := 255.
                partner[1] = (255) & 0xff;
                // SLCO assignment | chan[1] := 255.
                chan[1] = (255) & 0xff;

                currentState = GlobalClass_User_1Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                D014_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | idle -> dialing | [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)].
                        T033_O++;
                        if(execute_transition_idle_0()) {
                            T033_S++;
                            D014_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | idle -> qi | [chan[1] != 255; partner[1] := (chan[1] % 20)].
                        T034_O++;
                        if(execute_transition_idle_1()) {
                            T034_S++;
                            D014_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                D015_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[1] := 255].
                        T035_O++;
                        if(execute_transition_dialing_0()) {
                            T035_S++;
                            D015_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[1] := 0.
                        T036_O++;
                        if(execute_transition_dialing_1()) {
                            T036_S++;
                            D015_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[1] := 1.
                        T037_O++;
                        if(execute_transition_dialing_2()) {
                            T037_S++;
                            D015_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[1] := 2.
                        T038_O++;
                        if(execute_transition_dialing_3()) {
                            T038_S++;
                            D015_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[1] := 3.
                        T039_O++;
                        if(execute_transition_dialing_4()) {
                            T039_S++;
                            D015_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[1] := 4.
                        T040_O++;
                        if(execute_transition_dialing_5()) {
                            T040_S++;
                            D015_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                D016_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | calling -> busy | partner[1] = 1.
                        T041_O++;
                        if(execute_transition_calling_0()) {
                            T041_S++;
                            D016_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[1] = 4.
                        T042_O++;
                        if(execute_transition_calling_1()) {
                            T042_S++;
                            D016_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | calling -> ringback | partner[1] = 4.
                        T043_O++;
                        if(execute_transition_calling_2()) {
                            T043_S++;
                            D016_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | calling -> busy | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1].
                        T044_O++;
                        if(execute_transition_calling_3()) {
                            T044_S++;
                            D016_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | calling -> calling | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]].
                        T045_O++;
                        if(execute_transition_calling_4()) {
                            T045_S++;
                            D016_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | calling -> oalert | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)].
                        T046_O++;
                        if(execute_transition_calling_5()) {
                            T046_S++;
                            D016_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                D017_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
                T047_O++;
                if(execute_transition_busy_0()) {
                    T047_S++;
                    D017_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                D018_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[1]] % 20) = 1.
                        T048_O++;
                        if(execute_transition_qi_0()) {
                            T048_S++;
                            D018_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[1]] % 20) != 1; partner[1] := 255].
                        T049_O++;
                        if(execute_transition_qi_1()) {
                            T049_S++;
                            D018_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                D019_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[1] = 255.
                        T050_O++;
                        if(execute_transition_talert_0()) {
                            T050_S++;
                            D019_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[1]] % 20) = 1.
                        T051_O++;
                        if(execute_transition_talert_1()) {
                            T051_S++;
                            D019_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[1]] % 20) != 1.
                        T052_O++;
                        if(execute_transition_talert_2()) {
                            T052_S++;
                            D019_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                D020_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
                T053_O++;
                if(execute_transition_unobtainable_0()) {
                    T053_S++;
                    D020_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                D021_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[1] % 20) != partner[1].
                        T054_O++;
                        if(execute_transition_oalert_0()) {
                            T054_S++;
                            D021_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1.
                        T055_O++;
                        if(execute_transition_oalert_1()) {
                            T055_S++;
                            D021_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0.
                        T056_O++;
                        if(execute_transition_oalert_2()) {
                            T056_S++;
                            D021_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                D022_O++;
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                D023_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[1] := 255; chan[partner[1]] := 255].
                T057_O++;
                if(execute_transition_oconnected_0()) {
                    T057_S++;
                    D023_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                D024_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[1] := 255; partner[1] := ((partner[1] % 20) + 0 * 20)].
                T058_O++;
                if(execute_transition_dveoringout_0()) {
                    T058_S++;
                    D024_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                D025_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)].
                        T059_O++;
                        if(execute_transition_tpickup_0()) {
                            T059_S++;
                            D025_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255].
                        T060_O++;
                        if(execute_transition_tpickup_1()) {
                            T060_S++;
                            D025_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                D026_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 0; dev := 1].
                        T061_O++;
                        if(execute_transition_tconnected_0()) {
                            T061_S++;
                            D026_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 1; dev := 0].
                        T062_O++;
                        if(execute_transition_tconnected_1()) {
                            T062_S++;
                            D026_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255].
                        T063_O++;
                        if(execute_transition_tconnected_2()) {
                            T063_S++;
                            D026_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                D027_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1].
                        T064_O++;
                        if(execute_transition_ringback_0()) {
                            T064_S++;
                            D027_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | ringback -> calling | [record[1] != 255; partner[1] := record[1]].
                        T065_O++;
                        if(execute_transition_ringback_1()) {
                            T065_S++;
                            D027_S++;
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

                // Report all counts.
                logger.info("D014.O " + D014_O);
                logger.info("D014.F " + D014_F);
                logger.info("D014.S " + D014_S);
                logger.info("T033.O " + T033_O);
                logger.info("T033.F " + T033_F);
                logger.info("T033.S " + T033_S);
                logger.info("T034.O " + T034_O);
                logger.info("T034.F " + T034_F);
                logger.info("T034.S " + T034_S);
                logger.info("D015.O " + D015_O);
                logger.info("D015.F " + D015_F);
                logger.info("D015.S " + D015_S);
                logger.info("T035.O " + T035_O);
                logger.info("T035.F " + T035_F);
                logger.info("T035.S " + T035_S);
                logger.info("T036.O " + T036_O);
                logger.info("T036.F " + T036_F);
                logger.info("T036.S " + T036_S);
                logger.info("T037.O " + T037_O);
                logger.info("T037.F " + T037_F);
                logger.info("T037.S " + T037_S);
                logger.info("T038.O " + T038_O);
                logger.info("T038.F " + T038_F);
                logger.info("T038.S " + T038_S);
                logger.info("T039.O " + T039_O);
                logger.info("T039.F " + T039_F);
                logger.info("T039.S " + T039_S);
                logger.info("T040.O " + T040_O);
                logger.info("T040.F " + T040_F);
                logger.info("T040.S " + T040_S);
                logger.info("D016.O " + D016_O);
                logger.info("D016.F " + D016_F);
                logger.info("D016.S " + D016_S);
                logger.info("T041.O " + T041_O);
                logger.info("T041.F " + T041_F);
                logger.info("T041.S " + T041_S);
                logger.info("T042.O " + T042_O);
                logger.info("T042.F " + T042_F);
                logger.info("T042.S " + T042_S);
                logger.info("T043.O " + T043_O);
                logger.info("T043.F " + T043_F);
                logger.info("T043.S " + T043_S);
                logger.info("T044.O " + T044_O);
                logger.info("T044.F " + T044_F);
                logger.info("T044.S " + T044_S);
                logger.info("T045.O " + T045_O);
                logger.info("T045.F " + T045_F);
                logger.info("T045.S " + T045_S);
                logger.info("T046.O " + T046_O);
                logger.info("T046.F " + T046_F);
                logger.info("T046.S " + T046_S);
                logger.info("D017.O " + D017_O);
                logger.info("D017.F " + D017_F);
                logger.info("D017.S " + D017_S);
                logger.info("T047.O " + T047_O);
                logger.info("T047.F " + T047_F);
                logger.info("T047.S " + T047_S);
                logger.info("D018.O " + D018_O);
                logger.info("D018.F " + D018_F);
                logger.info("D018.S " + D018_S);
                logger.info("T048.O " + T048_O);
                logger.info("T048.F " + T048_F);
                logger.info("T048.S " + T048_S);
                logger.info("T049.O " + T049_O);
                logger.info("T049.F " + T049_F);
                logger.info("T049.S " + T049_S);
                logger.info("D019.O " + D019_O);
                logger.info("D019.F " + D019_F);
                logger.info("D019.S " + D019_S);
                logger.info("T050.O " + T050_O);
                logger.info("T050.F " + T050_F);
                logger.info("T050.S " + T050_S);
                logger.info("T051.O " + T051_O);
                logger.info("T051.F " + T051_F);
                logger.info("T051.S " + T051_S);
                logger.info("T052.O " + T052_O);
                logger.info("T052.F " + T052_F);
                logger.info("T052.S " + T052_S);
                logger.info("D020.O " + D020_O);
                logger.info("D020.F " + D020_F);
                logger.info("D020.S " + D020_S);
                logger.info("T053.O " + T053_O);
                logger.info("T053.F " + T053_F);
                logger.info("T053.S " + T053_S);
                logger.info("D021.O " + D021_O);
                logger.info("D021.F " + D021_F);
                logger.info("D021.S " + D021_S);
                logger.info("T054.O " + T054_O);
                logger.info("T054.F " + T054_F);
                logger.info("T054.S " + T054_S);
                logger.info("T055.O " + T055_O);
                logger.info("T055.F " + T055_F);
                logger.info("T055.S " + T055_S);
                logger.info("T056.O " + T056_O);
                logger.info("T056.F " + T056_F);
                logger.info("T056.S " + T056_S);
                logger.info("D022.O " + D022_O);
                logger.info("D022.F " + D022_F);
                logger.info("D022.S " + D022_S);
                logger.info("D023.O " + D023_O);
                logger.info("D023.F " + D023_F);
                logger.info("D023.S " + D023_S);
                logger.info("T057.O " + T057_O);
                logger.info("T057.F " + T057_F);
                logger.info("T057.S " + T057_S);
                logger.info("D024.O " + D024_O);
                logger.info("D024.F " + D024_F);
                logger.info("D024.S " + D024_S);
                logger.info("T058.O " + T058_O);
                logger.info("T058.F " + T058_F);
                logger.info("T058.S " + T058_S);
                logger.info("D025.O " + D025_O);
                logger.info("D025.F " + D025_F);
                logger.info("D025.S " + D025_S);
                logger.info("T059.O " + T059_O);
                logger.info("T059.F " + T059_F);
                logger.info("T059.S " + T059_S);
                logger.info("T060.O " + T060_O);
                logger.info("T060.F " + T060_F);
                logger.info("T060.S " + T060_S);
                logger.info("D026.O " + D026_O);
                logger.info("D026.F " + D026_F);
                logger.info("D026.S " + D026_S);
                logger.info("T061.O " + T061_O);
                logger.info("T061.F " + T061_F);
                logger.info("T061.S " + T061_S);
                logger.info("T062.O " + T062_O);
                logger.info("T062.F " + T062_F);
                logger.info("T062.S " + T062_S);
                logger.info("T063.O " + T063_O);
                logger.info("T063.F " + T063_F);
                logger.info("T063.S " + T063_S);
                logger.info("D027.O " + D027_O);
                logger.info("D027.F " + D027_F);
                logger.info("D027.S " + D027_S);
                logger.info("T064.O " + T064_O);
                logger.info("T064.F " + T064_F);
                logger.info("T064.S " + T064_S);
                logger.info("T065.O " + T065_O);
                logger.info("T065.F " + T065_F);
                logger.info("T065.S " + T065_S);
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

            // Add variables needed for measurements.
            private long D028_O;
            private long D028_F;
            private long D028_S;
            private long T066_O;
            private long T066_F;
            private long T066_S;
            private long T067_O;
            private long T067_F;
            private long T067_S;
            private long D029_O;
            private long D029_F;
            private long D029_S;
            private long T068_O;
            private long T068_F;
            private long T068_S;
            private long T069_O;
            private long T069_F;
            private long T069_S;
            private long T070_O;
            private long T070_F;
            private long T070_S;
            private long T071_O;
            private long T071_F;
            private long T071_S;
            private long T072_O;
            private long T072_F;
            private long T072_S;
            private long T073_O;
            private long T073_F;
            private long T073_S;
            private long D030_O;
            private long D030_F;
            private long D030_S;
            private long T074_O;
            private long T074_F;
            private long T074_S;
            private long T075_O;
            private long T075_F;
            private long T075_S;
            private long T076_O;
            private long T076_F;
            private long T076_S;
            private long T077_O;
            private long T077_F;
            private long T077_S;
            private long T078_O;
            private long T078_F;
            private long T078_S;
            private long T079_O;
            private long T079_F;
            private long T079_S;
            private long D031_O;
            private long D031_F;
            private long D031_S;
            private long T080_O;
            private long T080_F;
            private long T080_S;
            private long D032_O;
            private long D032_F;
            private long D032_S;
            private long T081_O;
            private long T081_F;
            private long T081_S;
            private long T082_O;
            private long T082_F;
            private long T082_S;
            private long D033_O;
            private long D033_F;
            private long D033_S;
            private long T083_O;
            private long T083_F;
            private long T083_S;
            private long T084_O;
            private long T084_F;
            private long T084_S;
            private long T085_O;
            private long T085_F;
            private long T085_S;
            private long D034_O;
            private long D034_F;
            private long D034_S;
            private long T086_O;
            private long T086_F;
            private long T086_S;
            private long D035_O;
            private long D035_F;
            private long D035_S;
            private long T087_O;
            private long T087_F;
            private long T087_S;
            private long T088_O;
            private long T088_F;
            private long T088_S;
            private long T089_O;
            private long T089_F;
            private long T089_S;
            private long D036_O;
            private long D036_F;
            private long D036_S;
            private long D037_O;
            private long D037_F;
            private long D037_S;
            private long T090_O;
            private long T090_F;
            private long T090_S;
            private long D038_O;
            private long D038_F;
            private long D038_S;
            private long T091_O;
            private long T091_F;
            private long T091_S;
            private long D039_O;
            private long D039_F;
            private long D039_S;
            private long T092_O;
            private long T092_F;
            private long T092_S;
            private long T093_O;
            private long T093_F;
            private long T093_S;
            private long D040_O;
            private long D040_F;
            private long D040_S;
            private long T094_O;
            private long T094_F;
            private long T094_S;
            private long T095_O;
            private long T095_F;
            private long T095_S;
            private long T096_O;
            private long T096_F;
            private long T096_S;
            private long D041_O;
            private long D041_F;
            private long D041_S;
            private long T097_O;
            private long T097_F;
            private long T097_S;
            private long T098_O;
            private long T098_F;
            private long T098_S;

            GlobalClass_User_2Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_2Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[2] = 255; dev := 0; chan[2] := ((2) + (0) * 20)] -> [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)].
                // SLCO expression | chan[2] = 255.
                if(!(chan[2] == 255)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[2] := (2 + 0 * 20).
                chan[2] = ((2 + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_2Thread.States.dialing;
                return true;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[2] != 255; partner[2] := (chan[2] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[2] != 255; partner[2] := ((chan[2]) % 20)] -> [chan[2] != 255; partner[2] := (chan[2] % 20)].
                // SLCO expression | chan[2] != 255.
                if(!(chan[2] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[2] := (chan[2] % 20).
                partner[2] = ((Math.floorMod(chan[2], 20))) & 0xff;

                currentState = GlobalClass_User_2Thread.States.qi;
                return true;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[2]] % 20) = 2.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[2]]) % 20) = 2 -> (chan[partner[2]] % 20) = 2.
                if(!((Math.floorMod(chan[partner[2]], 20)) == 2)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.talert;
                return true;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[2]] % 20) != 2; partner[2] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[2]]) % 20) != 2; partner[2] := 255] -> [(chan[partner[2]] % 20) != 2; partner[2] := 255].
                // SLCO expression | (chan[partner[2]] % 20) != 2.
                if(!((Math.floorMod(chan[partner[2]], 20)) != 2)) {
                    return false;
                }
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;

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
                chan[2] = (255) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[2] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 0] -> partner[2] := 0.
                partner[2] = (0) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[2] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 1] -> partner[2] := 1.
                partner[2] = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[2] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 2] -> partner[2] := 2.
                partner[2] = (2) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[2] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 3] -> partner[2] := 3.
                partner[2] = (3) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[2] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[2] := 4] -> partner[2] := 4.
                partner[2] = (4) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[2] = 2.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[2] = 2.
                if(!(partner[2] == 2)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[2] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[2] = 4.
                if(!(partner[2] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.unobtainable;
                return true;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[2] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[2] = 4.
                if(!(partner[2] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.ringback;
                return true;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2].
                // SLCO expression | partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255.
                if(!(partner[2] != 2 && partner[2] != 4 && chan[partner[2]] != 255 && callforwardbusy[partner[2]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[2]] := 2.
                record[partner[2]] = (2) & 0xff;

                currentState = GlobalClass_User_2Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]].
                // SLCO expression | partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255.
                if(!(partner[2] != 2 && partner[2] != 4 && chan[partner[2]] != 255 && callforwardbusy[partner[2]] != 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[2]] := 2.
                record[partner[2]] = (2) & 0xff;
                // SLCO assignment | partner[2] := callforwardbusy[partner[2]].
                partner[2] = (callforwardbusy[partner[2]]) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := ((2) + (0) * 20); chan[2] := ((partner[2]) + (0) * 20)] -> [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)].
                // SLCO expression | partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255.
                if(!(partner[2] != 2 && partner[2] != 4 && chan[partner[2]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[2]] := 2.
                record[partner[2]] = (2) & 0xff;
                // SLCO assignment | chan[partner[2]] := (2 + 0 * 20).
                chan[partner[2]] = ((2 + 0 * 20)) & 0xff;
                // SLCO assignment | chan[2] := (partner[2] + 0 * 20).
                chan[2] = ((partner[2] + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_2Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[2] := 255; partner[2] := 255; dev := 1] -> [true; chan[2] := 255; partner[2] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[2] := 255.
                chan[2] = (255) & 0xff;
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[2] % 20) != partner[2].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[2]) % 20) != partner[2] -> (chan[2] % 20) != partner[2].
                if(!((Math.floorMod(chan[2], 20)) != partner[2])) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[2]) % 20) = partner[2] and ((chan[2]) / 20) = 1 -> (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1.
                if(!((Math.floorMod(chan[2], 20)) == partner[2] && (chan[2] / 20) == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.oconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[2]) % 20) = partner[2] and ((chan[2]) / 20) = 0 -> (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0.
                if(!((Math.floorMod(chan[2], 20)) == partner[2] && (chan[2] / 20) == 0)) {
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
                chan[2] = (255) & 0xff;
                // SLCO assignment | chan[partner[2]] := 255.
                chan[partner[2]] = (255) & 0xff;

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
                chan[2] = (255) & 0xff;
                // SLCO assignment | partner[2] := ((partner[2] % 20) + 0 * 20).
                partner[2] = (((Math.floorMod(partner[2], 20)) + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[2] := 255; partner[2] := 255; dev := 1] -> [true; chan[2] := 255; partner[2] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[2] := 255.
                chan[2] = (255) & 0xff;
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
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
                chan[2] = (255) & 0xff;
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[2] != 255; partner[2] := record[2]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[2] != 255; partner[2] := record[2]].
                // SLCO expression | record[2] != 255.
                if(!(record[2] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[2] := record[2].
                partner[2] = (record[2]) & 0xff;

                currentState = GlobalClass_User_2Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[2] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[2] = 255.
                if(!(dev != 1 || chan[2] == 255)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[2]] % 20) = 2.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[2]]) % 20) = 2 -> (chan[partner[2]] % 20) = 2.
                if(!((Math.floorMod(chan[partner[2]], 20)) == 2)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.tpickup;
                return true;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[2]] % 20) != 2.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[2]]) % 20) != 2 -> (chan[partner[2]] % 20) != 2.
                if(!((Math.floorMod(chan[partner[2]], 20)) != 2)) {
                    return false;
                }

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[2]]) % 20) = 2 and ((chan[partner[2]]) / 20) = 0; dev := 0; chan[partner[2]] := ((2) + (1) * 20); chan[2] := ((partner[2]) + (1) * 20)] -> [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)].
                // SLCO expression | (chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0.
                if(!((Math.floorMod(chan[partner[2]], 20)) == 2 && (chan[partner[2]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[2]] := (2 + 1 * 20).
                chan[partner[2]] = ((2 + 1 * 20)) & 0xff;
                // SLCO assignment | chan[2] := (partner[2] + 1 * 20).
                chan[2] = ((partner[2] + 1 * 20)) & 0xff;

                currentState = GlobalClass_User_2Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[2]] = 255 or ((chan[partner[2]]) % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255] -> [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255].
                // SLCO expression | chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2.
                if(!(chan[partner[2]] == 255 || (Math.floorMod(chan[partner[2]], 20)) != 2)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                // SLCO assignment | chan[2] := 255.
                chan[2] = (255) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[2]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[2] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[2] / 20) = 1 and dev = 0.
                if(!((chan[2] / 20) == 1 && dev == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_2Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[2]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[2] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[2] / 20) = 1 and dev = 1.
                if(!((chan[2] / 20) == 1 && dev == 1)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_2Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[2]) / 20) = 0; partner[2] := 255; chan[2] := 255] -> [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255].
                // SLCO expression | (chan[2] / 20) = 0.
                if(!((chan[2] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | partner[2] := 255.
                partner[2] = (255) & 0xff;
                // SLCO assignment | chan[2] := 255.
                chan[2] = (255) & 0xff;

                currentState = GlobalClass_User_2Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                D028_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | idle -> dialing | [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)].
                        T066_O++;
                        if(execute_transition_idle_0()) {
                            T066_S++;
                            D028_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | idle -> qi | [chan[2] != 255; partner[2] := (chan[2] % 20)].
                        T067_O++;
                        if(execute_transition_idle_1()) {
                            T067_S++;
                            D028_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                D029_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[2] := 255].
                        T068_O++;
                        if(execute_transition_dialing_0()) {
                            T068_S++;
                            D029_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[2] := 0.
                        T069_O++;
                        if(execute_transition_dialing_1()) {
                            T069_S++;
                            D029_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[2] := 1.
                        T070_O++;
                        if(execute_transition_dialing_2()) {
                            T070_S++;
                            D029_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[2] := 2.
                        T071_O++;
                        if(execute_transition_dialing_3()) {
                            T071_S++;
                            D029_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[2] := 3.
                        T072_O++;
                        if(execute_transition_dialing_4()) {
                            T072_S++;
                            D029_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[2] := 4.
                        T073_O++;
                        if(execute_transition_dialing_5()) {
                            T073_S++;
                            D029_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                D030_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | calling -> busy | partner[2] = 2.
                        T074_O++;
                        if(execute_transition_calling_0()) {
                            T074_S++;
                            D030_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[2] = 4.
                        T075_O++;
                        if(execute_transition_calling_1()) {
                            T075_S++;
                            D030_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | calling -> ringback | partner[2] = 4.
                        T076_O++;
                        if(execute_transition_calling_2()) {
                            T076_S++;
                            D030_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | calling -> busy | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2].
                        T077_O++;
                        if(execute_transition_calling_3()) {
                            T077_S++;
                            D030_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | calling -> calling | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]].
                        T078_O++;
                        if(execute_transition_calling_4()) {
                            T078_S++;
                            D030_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | calling -> oalert | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)].
                        T079_O++;
                        if(execute_transition_calling_5()) {
                            T079_S++;
                            D030_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                D031_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
                T080_O++;
                if(execute_transition_busy_0()) {
                    T080_S++;
                    D031_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                D032_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[2]] % 20) = 2.
                        T081_O++;
                        if(execute_transition_qi_0()) {
                            T081_S++;
                            D032_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[2]] % 20) != 2; partner[2] := 255].
                        T082_O++;
                        if(execute_transition_qi_1()) {
                            T082_S++;
                            D032_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                D033_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[2] = 255.
                        T083_O++;
                        if(execute_transition_talert_0()) {
                            T083_S++;
                            D033_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[2]] % 20) = 2.
                        T084_O++;
                        if(execute_transition_talert_1()) {
                            T084_S++;
                            D033_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[2]] % 20) != 2.
                        T085_O++;
                        if(execute_transition_talert_2()) {
                            T085_S++;
                            D033_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                D034_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
                T086_O++;
                if(execute_transition_unobtainable_0()) {
                    T086_S++;
                    D034_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                D035_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[2] % 20) != partner[2].
                        T087_O++;
                        if(execute_transition_oalert_0()) {
                            T087_S++;
                            D035_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1.
                        T088_O++;
                        if(execute_transition_oalert_1()) {
                            T088_S++;
                            D035_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0.
                        T089_O++;
                        if(execute_transition_oalert_2()) {
                            T089_S++;
                            D035_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                D036_O++;
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                D037_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[2] := 255; chan[partner[2]] := 255].
                T090_O++;
                if(execute_transition_oconnected_0()) {
                    T090_S++;
                    D037_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                D038_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[2] := 255; partner[2] := ((partner[2] % 20) + 0 * 20)].
                T091_O++;
                if(execute_transition_dveoringout_0()) {
                    T091_S++;
                    D038_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                D039_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)].
                        T092_O++;
                        if(execute_transition_tpickup_0()) {
                            T092_S++;
                            D039_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255].
                        T093_O++;
                        if(execute_transition_tpickup_1()) {
                            T093_S++;
                            D039_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                D040_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 0; dev := 1].
                        T094_O++;
                        if(execute_transition_tconnected_0()) {
                            T094_S++;
                            D040_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 1; dev := 0].
                        T095_O++;
                        if(execute_transition_tconnected_1()) {
                            T095_S++;
                            D040_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255].
                        T096_O++;
                        if(execute_transition_tconnected_2()) {
                            T096_S++;
                            D040_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                D041_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1].
                        T097_O++;
                        if(execute_transition_ringback_0()) {
                            T097_S++;
                            D041_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | ringback -> calling | [record[2] != 255; partner[2] := record[2]].
                        T098_O++;
                        if(execute_transition_ringback_1()) {
                            T098_S++;
                            D041_S++;
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

                // Report all counts.
                logger.info("D028.O " + D028_O);
                logger.info("D028.F " + D028_F);
                logger.info("D028.S " + D028_S);
                logger.info("T066.O " + T066_O);
                logger.info("T066.F " + T066_F);
                logger.info("T066.S " + T066_S);
                logger.info("T067.O " + T067_O);
                logger.info("T067.F " + T067_F);
                logger.info("T067.S " + T067_S);
                logger.info("D029.O " + D029_O);
                logger.info("D029.F " + D029_F);
                logger.info("D029.S " + D029_S);
                logger.info("T068.O " + T068_O);
                logger.info("T068.F " + T068_F);
                logger.info("T068.S " + T068_S);
                logger.info("T069.O " + T069_O);
                logger.info("T069.F " + T069_F);
                logger.info("T069.S " + T069_S);
                logger.info("T070.O " + T070_O);
                logger.info("T070.F " + T070_F);
                logger.info("T070.S " + T070_S);
                logger.info("T071.O " + T071_O);
                logger.info("T071.F " + T071_F);
                logger.info("T071.S " + T071_S);
                logger.info("T072.O " + T072_O);
                logger.info("T072.F " + T072_F);
                logger.info("T072.S " + T072_S);
                logger.info("T073.O " + T073_O);
                logger.info("T073.F " + T073_F);
                logger.info("T073.S " + T073_S);
                logger.info("D030.O " + D030_O);
                logger.info("D030.F " + D030_F);
                logger.info("D030.S " + D030_S);
                logger.info("T074.O " + T074_O);
                logger.info("T074.F " + T074_F);
                logger.info("T074.S " + T074_S);
                logger.info("T075.O " + T075_O);
                logger.info("T075.F " + T075_F);
                logger.info("T075.S " + T075_S);
                logger.info("T076.O " + T076_O);
                logger.info("T076.F " + T076_F);
                logger.info("T076.S " + T076_S);
                logger.info("T077.O " + T077_O);
                logger.info("T077.F " + T077_F);
                logger.info("T077.S " + T077_S);
                logger.info("T078.O " + T078_O);
                logger.info("T078.F " + T078_F);
                logger.info("T078.S " + T078_S);
                logger.info("T079.O " + T079_O);
                logger.info("T079.F " + T079_F);
                logger.info("T079.S " + T079_S);
                logger.info("D031.O " + D031_O);
                logger.info("D031.F " + D031_F);
                logger.info("D031.S " + D031_S);
                logger.info("T080.O " + T080_O);
                logger.info("T080.F " + T080_F);
                logger.info("T080.S " + T080_S);
                logger.info("D032.O " + D032_O);
                logger.info("D032.F " + D032_F);
                logger.info("D032.S " + D032_S);
                logger.info("T081.O " + T081_O);
                logger.info("T081.F " + T081_F);
                logger.info("T081.S " + T081_S);
                logger.info("T082.O " + T082_O);
                logger.info("T082.F " + T082_F);
                logger.info("T082.S " + T082_S);
                logger.info("D033.O " + D033_O);
                logger.info("D033.F " + D033_F);
                logger.info("D033.S " + D033_S);
                logger.info("T083.O " + T083_O);
                logger.info("T083.F " + T083_F);
                logger.info("T083.S " + T083_S);
                logger.info("T084.O " + T084_O);
                logger.info("T084.F " + T084_F);
                logger.info("T084.S " + T084_S);
                logger.info("T085.O " + T085_O);
                logger.info("T085.F " + T085_F);
                logger.info("T085.S " + T085_S);
                logger.info("D034.O " + D034_O);
                logger.info("D034.F " + D034_F);
                logger.info("D034.S " + D034_S);
                logger.info("T086.O " + T086_O);
                logger.info("T086.F " + T086_F);
                logger.info("T086.S " + T086_S);
                logger.info("D035.O " + D035_O);
                logger.info("D035.F " + D035_F);
                logger.info("D035.S " + D035_S);
                logger.info("T087.O " + T087_O);
                logger.info("T087.F " + T087_F);
                logger.info("T087.S " + T087_S);
                logger.info("T088.O " + T088_O);
                logger.info("T088.F " + T088_F);
                logger.info("T088.S " + T088_S);
                logger.info("T089.O " + T089_O);
                logger.info("T089.F " + T089_F);
                logger.info("T089.S " + T089_S);
                logger.info("D036.O " + D036_O);
                logger.info("D036.F " + D036_F);
                logger.info("D036.S " + D036_S);
                logger.info("D037.O " + D037_O);
                logger.info("D037.F " + D037_F);
                logger.info("D037.S " + D037_S);
                logger.info("T090.O " + T090_O);
                logger.info("T090.F " + T090_F);
                logger.info("T090.S " + T090_S);
                logger.info("D038.O " + D038_O);
                logger.info("D038.F " + D038_F);
                logger.info("D038.S " + D038_S);
                logger.info("T091.O " + T091_O);
                logger.info("T091.F " + T091_F);
                logger.info("T091.S " + T091_S);
                logger.info("D039.O " + D039_O);
                logger.info("D039.F " + D039_F);
                logger.info("D039.S " + D039_S);
                logger.info("T092.O " + T092_O);
                logger.info("T092.F " + T092_F);
                logger.info("T092.S " + T092_S);
                logger.info("T093.O " + T093_O);
                logger.info("T093.F " + T093_F);
                logger.info("T093.S " + T093_S);
                logger.info("D040.O " + D040_O);
                logger.info("D040.F " + D040_F);
                logger.info("D040.S " + D040_S);
                logger.info("T094.O " + T094_O);
                logger.info("T094.F " + T094_F);
                logger.info("T094.S " + T094_S);
                logger.info("T095.O " + T095_O);
                logger.info("T095.F " + T095_F);
                logger.info("T095.S " + T095_S);
                logger.info("T096.O " + T096_O);
                logger.info("T096.F " + T096_F);
                logger.info("T096.S " + T096_S);
                logger.info("D041.O " + D041_O);
                logger.info("D041.F " + D041_F);
                logger.info("D041.S " + D041_S);
                logger.info("T097.O " + T097_O);
                logger.info("T097.F " + T097_F);
                logger.info("T097.S " + T097_S);
                logger.info("T098.O " + T098_O);
                logger.info("T098.F " + T098_F);
                logger.info("T098.S " + T098_S);
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

            // Add variables needed for measurements.
            private long D042_O;
            private long D042_F;
            private long D042_S;
            private long T099_O;
            private long T099_F;
            private long T099_S;
            private long T100_O;
            private long T100_F;
            private long T100_S;
            private long D043_O;
            private long D043_F;
            private long D043_S;
            private long T101_O;
            private long T101_F;
            private long T101_S;
            private long T102_O;
            private long T102_F;
            private long T102_S;
            private long T103_O;
            private long T103_F;
            private long T103_S;
            private long T104_O;
            private long T104_F;
            private long T104_S;
            private long T105_O;
            private long T105_F;
            private long T105_S;
            private long T106_O;
            private long T106_F;
            private long T106_S;
            private long D044_O;
            private long D044_F;
            private long D044_S;
            private long T107_O;
            private long T107_F;
            private long T107_S;
            private long T108_O;
            private long T108_F;
            private long T108_S;
            private long T109_O;
            private long T109_F;
            private long T109_S;
            private long T110_O;
            private long T110_F;
            private long T110_S;
            private long T111_O;
            private long T111_F;
            private long T111_S;
            private long T112_O;
            private long T112_F;
            private long T112_S;
            private long D045_O;
            private long D045_F;
            private long D045_S;
            private long T113_O;
            private long T113_F;
            private long T113_S;
            private long D046_O;
            private long D046_F;
            private long D046_S;
            private long T114_O;
            private long T114_F;
            private long T114_S;
            private long T115_O;
            private long T115_F;
            private long T115_S;
            private long D047_O;
            private long D047_F;
            private long D047_S;
            private long T116_O;
            private long T116_F;
            private long T116_S;
            private long T117_O;
            private long T117_F;
            private long T117_S;
            private long T118_O;
            private long T118_F;
            private long T118_S;
            private long D048_O;
            private long D048_F;
            private long D048_S;
            private long T119_O;
            private long T119_F;
            private long T119_S;
            private long D049_O;
            private long D049_F;
            private long D049_S;
            private long T120_O;
            private long T120_F;
            private long T120_S;
            private long T121_O;
            private long T121_F;
            private long T121_S;
            private long T122_O;
            private long T122_F;
            private long T122_S;
            private long D050_O;
            private long D050_F;
            private long D050_S;
            private long D051_O;
            private long D051_F;
            private long D051_S;
            private long T123_O;
            private long T123_F;
            private long T123_S;
            private long D052_O;
            private long D052_F;
            private long D052_S;
            private long T124_O;
            private long T124_F;
            private long T124_S;
            private long D053_O;
            private long D053_F;
            private long D053_S;
            private long T125_O;
            private long T125_F;
            private long T125_S;
            private long T126_O;
            private long T126_F;
            private long T126_S;
            private long D054_O;
            private long D054_F;
            private long D054_S;
            private long T127_O;
            private long T127_F;
            private long T127_S;
            private long T128_O;
            private long T128_F;
            private long T128_S;
            private long T129_O;
            private long T129_F;
            private long T129_S;
            private long D055_O;
            private long D055_F;
            private long D055_S;
            private long T130_O;
            private long T130_F;
            private long T130_S;
            private long T131_O;
            private long T131_F;
            private long T131_S;

            GlobalClass_User_3Thread(LockManager lockManagerInstance) {
                currentState = GlobalClass_User_3Thread.States.idle;
                lockManager = lockManagerInstance;
                lock_ids = new int[0];
                target_locks = new int[0];
                random = new Random();

                // Variable instantiations.
                dev = (char) 1;
                mbit = (char) 0;
            }

            // SLCO transition (p:0, id:0) | idle -> dialing | [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)].
            private boolean execute_transition_idle_0() {
                // SLCO composite | [chan[3] = 255; dev := 0; chan[3] := ((3) + (0) * 20)] -> [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)].
                // SLCO expression | chan[3] = 255.
                if(!(chan[3] == 255)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[3] := (3 + 0 * 20).
                chan[3] = ((3 + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_3Thread.States.dialing;
                return true;
            }

            // SLCO transition (p:0, id:1) | idle -> qi | [chan[3] != 255; partner[3] := (chan[3] % 20)].
            private boolean execute_transition_idle_1() {
                // SLCO composite | [chan[3] != 255; partner[3] := ((chan[3]) % 20)] -> [chan[3] != 255; partner[3] := (chan[3] % 20)].
                // SLCO expression | chan[3] != 255.
                if(!(chan[3] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[3] := (chan[3] % 20).
                partner[3] = ((Math.floorMod(chan[3], 20))) & 0xff;

                currentState = GlobalClass_User_3Thread.States.qi;
                return true;
            }

            // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[3]] % 20) = 3.
            private boolean execute_transition_qi_0() {
                // SLCO expression | ((chan[partner[3]]) % 20) = 3 -> (chan[partner[3]] % 20) = 3.
                if(!((Math.floorMod(chan[partner[3]], 20)) == 3)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.talert;
                return true;
            }

            // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[3]] % 20) != 3; partner[3] := 255].
            private boolean execute_transition_qi_1() {
                // SLCO composite | [((chan[partner[3]]) % 20) != 3; partner[3] := 255] -> [(chan[partner[3]] % 20) != 3; partner[3] := 255].
                // SLCO expression | (chan[partner[3]] % 20) != 3.
                if(!((Math.floorMod(chan[partner[3]], 20)) != 3)) {
                    return false;
                }
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;

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
                chan[3] = (255) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[3] := 0.
            private boolean execute_transition_dialing_1() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 0] -> partner[3] := 0.
                partner[3] = (0) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[3] := 1.
            private boolean execute_transition_dialing_2() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 1] -> partner[3] := 1.
                partner[3] = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[3] := 2.
            private boolean execute_transition_dialing_3() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 2] -> partner[3] := 2.
                partner[3] = (2) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[3] := 3.
            private boolean execute_transition_dialing_4() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 3] -> partner[3] := 3.
                partner[3] = (3) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[3] := 4.
            private boolean execute_transition_dialing_5() {
                // (Superfluous) SLCO expression | true.

                // SLCO assignment | [partner[3] := 4] -> partner[3] := 4.
                partner[3] = (4) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | calling -> busy | partner[3] = 3.
            private boolean execute_transition_calling_0() {
                // SLCO expression | partner[3] = 3.
                if(!(partner[3] == 3)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[3] = 4.
            private boolean execute_transition_calling_1() {
                // SLCO expression | partner[3] = 4.
                if(!(partner[3] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.unobtainable;
                return true;
            }

            // SLCO transition (p:0, id:2) | calling -> ringback | partner[3] = 4.
            private boolean execute_transition_calling_2() {
                // SLCO expression | partner[3] = 4.
                if(!(partner[3] == 4)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.ringback;
                return true;
            }

            // SLCO transition (p:0, id:3) | calling -> busy | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3].
            private boolean execute_transition_calling_3() {
                // SLCO composite | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3].
                // SLCO expression | partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255.
                if(!(partner[3] != 3 && partner[3] != 4 && chan[partner[3]] != 255 && callforwardbusy[partner[3]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[3]] := 3.
                record[partner[3]] = (3) & 0xff;

                currentState = GlobalClass_User_3Thread.States.busy;
                return true;
            }

            // SLCO transition (p:0, id:4) | calling -> calling | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]].
            private boolean execute_transition_calling_4() {
                // SLCO composite | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]].
                // SLCO expression | partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255.
                if(!(partner[3] != 3 && partner[3] != 4 && chan[partner[3]] != 255 && callforwardbusy[partner[3]] != 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[3]] := 3.
                record[partner[3]] = (3) & 0xff;
                // SLCO assignment | partner[3] := callforwardbusy[partner[3]].
                partner[3] = (callforwardbusy[partner[3]]) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:5) | calling -> oalert | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)].
            private boolean execute_transition_calling_5() {
                // SLCO composite | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := ((3) + (0) * 20); chan[3] := ((partner[3]) + (0) * 20)] -> [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)].
                // SLCO expression | partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255.
                if(!(partner[3] != 3 && partner[3] != 4 && chan[partner[3]] == 255)) {
                    return false;
                }
                // SLCO assignment | record[partner[3]] := 3.
                record[partner[3]] = (3) & 0xff;
                // SLCO assignment | chan[partner[3]] := (3 + 0 * 20).
                chan[partner[3]] = ((3 + 0 * 20)) & 0xff;
                // SLCO assignment | chan[3] := (partner[3] + 0 * 20).
                chan[3] = ((partner[3] + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_3Thread.States.oalert;
                return true;
            }

            // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
            private boolean execute_transition_busy_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[3] := 255; partner[3] := 255; dev := 1] -> [true; chan[3] := 255; partner[3] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[3] := 255.
                chan[3] = (255) & 0xff;
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[3] % 20) != partner[3].
            private boolean execute_transition_oalert_0() {
                // SLCO expression | ((chan[3]) % 20) != partner[3] -> (chan[3] % 20) != partner[3].
                if(!((Math.floorMod(chan[3], 20)) != partner[3])) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1.
            private boolean execute_transition_oalert_1() {
                // SLCO expression | ((chan[3]) % 20) = partner[3] and ((chan[3]) / 20) = 1 -> (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1.
                if(!((Math.floorMod(chan[3], 20)) == partner[3] && (chan[3] / 20) == 1)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.oconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0.
            private boolean execute_transition_oalert_2() {
                // SLCO expression | ((chan[3]) % 20) = partner[3] and ((chan[3]) / 20) = 0 -> (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0.
                if(!((Math.floorMod(chan[3], 20)) == partner[3] && (chan[3] / 20) == 0)) {
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
                chan[3] = (255) & 0xff;
                // SLCO assignment | chan[partner[3]] := 255.
                chan[partner[3]] = (255) & 0xff;

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
                chan[3] = (255) & 0xff;
                // SLCO assignment | partner[3] := ((partner[3] % 20) + 0 * 20).
                partner[3] = (((Math.floorMod(partner[3], 20)) + 0 * 20)) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
            private boolean execute_transition_unobtainable_0() {
                // (Superfluous) SLCO expression | true.

                // SLCO composite | [chan[3] := 255; partner[3] := 255; dev := 1] -> [true; chan[3] := 255; partner[3] := 255; dev := 1].
                // (Superfluous) SLCO expression | true.
                // SLCO assignment | chan[3] := 255.
                chan[3] = (255) & 0xff;
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
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
                chan[3] = (255) & 0xff;
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:1) | ringback -> calling | [record[3] != 255; partner[3] := record[3]].
            private boolean execute_transition_ringback_1() {
                // SLCO composite | [record[3] != 255; partner[3] := record[3]].
                // SLCO expression | record[3] != 255.
                if(!(record[3] != 255)) {
                    return false;
                }
                // SLCO assignment | partner[3] := record[3].
                partner[3] = (record[3]) & 0xff;

                currentState = GlobalClass_User_3Thread.States.calling;
                return true;
            }

            // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[3] = 255.
            private boolean execute_transition_talert_0() {
                // SLCO expression | dev != 1 or chan[3] = 255.
                if(!(dev != 1 || chan[3] == 255)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.errorstate;
                return true;
            }

            // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[3]] % 20) = 3.
            private boolean execute_transition_talert_1() {
                // SLCO expression | ((chan[partner[3]]) % 20) = 3 -> (chan[partner[3]] % 20) = 3.
                if(!((Math.floorMod(chan[partner[3]], 20)) == 3)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.tpickup;
                return true;
            }

            // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[3]] % 20) != 3.
            private boolean execute_transition_talert_2() {
                // SLCO expression | ((chan[partner[3]]) % 20) != 3 -> (chan[partner[3]] % 20) != 3.
                if(!((Math.floorMod(chan[partner[3]], 20)) != 3)) {
                    return false;
                }

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)].
            private boolean execute_transition_tpickup_0() {
                // SLCO composite | [((chan[partner[3]]) % 20) = 3 and ((chan[partner[3]]) / 20) = 0; dev := 0; chan[partner[3]] := ((3) + (1) * 20); chan[3] := ((partner[3]) + (1) * 20)] -> [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)].
                // SLCO expression | (chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0.
                if(!((Math.floorMod(chan[partner[3]], 20)) == 3 && (chan[partner[3]] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;
                // SLCO assignment | chan[partner[3]] := (3 + 1 * 20).
                chan[partner[3]] = ((3 + 1 * 20)) & 0xff;
                // SLCO assignment | chan[3] := (partner[3] + 1 * 20).
                chan[3] = ((partner[3] + 1 * 20)) & 0xff;

                currentState = GlobalClass_User_3Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255].
            private boolean execute_transition_tpickup_1() {
                // SLCO composite | [chan[partner[3]] = 255 or ((chan[partner[3]]) % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255] -> [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255].
                // SLCO expression | chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3.
                if(!(chan[partner[3]] == 255 || (Math.floorMod(chan[partner[3]], 20)) != 3)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                // SLCO assignment | chan[3] := 255.
                chan[3] = (255) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 0; dev := 1].
            private boolean execute_transition_tconnected_0() {
                // SLCO composite | [((chan[3]) / 20) = 1 and dev = 0; dev := 1] -> [(chan[3] / 20) = 1 and dev = 0; dev := 1].
                // SLCO expression | (chan[3] / 20) = 1 and dev = 0.
                if(!((chan[3] / 20) == 1 && dev == 0)) {
                    return false;
                }
                // SLCO assignment | dev := 1.
                dev = (1) & 0xff;

                currentState = GlobalClass_User_3Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 1; dev := 0].
            private boolean execute_transition_tconnected_1() {
                // SLCO composite | [((chan[3]) / 20) = 1 and dev = 1; dev := 0] -> [(chan[3] / 20) = 1 and dev = 1; dev := 0].
                // SLCO expression | (chan[3] / 20) = 1 and dev = 1.
                if(!((chan[3] / 20) == 1 && dev == 1)) {
                    return false;
                }
                // SLCO assignment | dev := 0.
                dev = (0) & 0xff;

                currentState = GlobalClass_User_3Thread.States.tconnected;
                return true;
            }

            // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255].
            private boolean execute_transition_tconnected_2() {
                // SLCO composite | [((chan[3]) / 20) = 0; partner[3] := 255; chan[3] := 255] -> [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255].
                // SLCO expression | (chan[3] / 20) = 0.
                if(!((chan[3] / 20) == 0)) {
                    return false;
                }
                // SLCO assignment | partner[3] := 255.
                partner[3] = (255) & 0xff;
                // SLCO assignment | chan[3] := 255.
                chan[3] = (255) & 0xff;

                currentState = GlobalClass_User_3Thread.States.idle;
                return true;
            }

            // Attempt to fire a transition starting in state idle.
            private void exec_idle() {
                D042_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | idle -> dialing | [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)].
                        T099_O++;
                        if(execute_transition_idle_0()) {
                            T099_S++;
                            D042_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | idle -> qi | [chan[3] != 255; partner[3] := (chan[3] % 20)].
                        T100_O++;
                        if(execute_transition_idle_1()) {
                            T100_S++;
                            D042_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dialing.
            private void exec_dialing() {
                D043_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[3] := 255].
                        T101_O++;
                        if(execute_transition_dialing_0()) {
                            T101_S++;
                            D043_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | dialing -> calling | true | partner[3] := 0.
                        T102_O++;
                        if(execute_transition_dialing_1()) {
                            T102_S++;
                            D043_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | dialing -> calling | true | partner[3] := 1.
                        T103_O++;
                        if(execute_transition_dialing_2()) {
                            T103_S++;
                            D043_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | dialing -> calling | true | partner[3] := 2.
                        T104_O++;
                        if(execute_transition_dialing_3()) {
                            T104_S++;
                            D043_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | dialing -> calling | true | partner[3] := 3.
                        T105_O++;
                        if(execute_transition_dialing_4()) {
                            T105_S++;
                            D043_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | dialing -> calling | true | partner[3] := 4.
                        T106_O++;
                        if(execute_transition_dialing_5()) {
                            T106_S++;
                            D043_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state calling.
            private void exec_calling() {
                D044_O++;
                // [N_DET.START]
                switch(random.nextInt(6)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | calling -> busy | partner[3] = 3.
                        T107_O++;
                        if(execute_transition_calling_0()) {
                            T107_S++;
                            D044_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | calling -> unobtainable | partner[3] = 4.
                        T108_O++;
                        if(execute_transition_calling_1()) {
                            T108_S++;
                            D044_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | calling -> ringback | partner[3] = 4.
                        T109_O++;
                        if(execute_transition_calling_2()) {
                            T109_S++;
                            D044_S++;
                            return;
                        }
                    }
                    case 3 -> {
                        // SLCO transition (p:0, id:3) | calling -> busy | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3].
                        T110_O++;
                        if(execute_transition_calling_3()) {
                            T110_S++;
                            D044_S++;
                            return;
                        }
                    }
                    case 4 -> {
                        // SLCO transition (p:0, id:4) | calling -> calling | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]].
                        T111_O++;
                        if(execute_transition_calling_4()) {
                            T111_S++;
                            D044_S++;
                            return;
                        }
                    }
                    case 5 -> {
                        // SLCO transition (p:0, id:5) | calling -> oalert | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)].
                        T112_O++;
                        if(execute_transition_calling_5()) {
                            T112_S++;
                            D044_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state busy.
            private void exec_busy() {
                D045_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | busy -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
                T113_O++;
                if(execute_transition_busy_0()) {
                    T113_S++;
                    D045_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state qi.
            private void exec_qi() {
                D046_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | qi -> talert | (chan[partner[3]] % 20) = 3.
                        T114_O++;
                        if(execute_transition_qi_0()) {
                            T114_S++;
                            D046_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | qi -> idle | [(chan[partner[3]] % 20) != 3; partner[3] := 255].
                        T115_O++;
                        if(execute_transition_qi_1()) {
                            T115_S++;
                            D046_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state talert.
            private void exec_talert() {
                D047_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | talert -> errorstate | dev != 1 or chan[3] = 255.
                        T116_O++;
                        if(execute_transition_talert_0()) {
                            T116_S++;
                            D047_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | talert -> tpickup | (chan[partner[3]] % 20) = 3.
                        T117_O++;
                        if(execute_transition_talert_1()) {
                            T117_S++;
                            D047_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | talert -> idle | (chan[partner[3]] % 20) != 3.
                        T118_O++;
                        if(execute_transition_talert_2()) {
                            T118_S++;
                            D047_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state unobtainable.
            private void exec_unobtainable() {
                D048_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | unobtainable -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
                T119_O++;
                if(execute_transition_unobtainable_0()) {
                    T119_S++;
                    D048_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state oalert.
            private void exec_oalert() {
                D049_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | oalert -> errorstate | (chan[3] % 20) != partner[3].
                        T120_O++;
                        if(execute_transition_oalert_0()) {
                            T120_S++;
                            D049_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | oalert -> oconnected | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1.
                        T121_O++;
                        if(execute_transition_oalert_1()) {
                            T121_S++;
                            D049_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | oalert -> dveoringout | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0.
                        T122_O++;
                        if(execute_transition_oalert_2()) {
                            T122_S++;
                            D049_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state errorstate.
            private void exec_errorstate() {
                D050_O++;
                // There are no transitions starting in state errorstate.
            }

            // Attempt to fire a transition starting in state oconnected.
            private void exec_oconnected() {
                D051_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[3] := 255; chan[partner[3]] := 255].
                T123_O++;
                if(execute_transition_oconnected_0()) {
                    T123_S++;
                    D051_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state dveoringout.
            private void exec_dveoringout() {
                D052_O++;
                // [N_DET.START]
                // SLCO transition (p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[3] := 255; partner[3] := ((partner[3] % 20) + 0 * 20)].
                T124_O++;
                if(execute_transition_dveoringout_0()) {
                    T124_S++;
                    D052_S++;
                    return;
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tpickup.
            private void exec_tpickup() {
                D053_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tpickup -> tconnected | [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)].
                        T125_O++;
                        if(execute_transition_tpickup_0()) {
                            T125_S++;
                            D053_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tpickup -> idle | [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255].
                        T126_O++;
                        if(execute_transition_tpickup_1()) {
                            T126_S++;
                            D053_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state tconnected.
            private void exec_tconnected() {
                D054_O++;
                // [N_DET.START]
                switch(random.nextInt(3)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 0; dev := 1].
                        T127_O++;
                        if(execute_transition_tconnected_0()) {
                            T127_S++;
                            D054_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 1; dev := 0].
                        T128_O++;
                        if(execute_transition_tconnected_1()) {
                            T128_S++;
                            D054_S++;
                            return;
                        }
                    }
                    case 2 -> {
                        // SLCO transition (p:0, id:2) | tconnected -> idle | [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255].
                        T129_O++;
                        if(execute_transition_tconnected_2()) {
                            T129_S++;
                            D054_S++;
                            return;
                        }
                    }
                }
                // [N_DET.END]
            }

            // Attempt to fire a transition starting in state ringback.
            private void exec_ringback() {
                D055_O++;
                // [N_DET.START]
                switch(random.nextInt(2)) {
                    case 0 -> {
                        // SLCO transition (p:0, id:0) | ringback -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1].
                        T130_O++;
                        if(execute_transition_ringback_0()) {
                            T130_S++;
                            D055_S++;
                            return;
                        }
                    }
                    case 1 -> {
                        // SLCO transition (p:0, id:1) | ringback -> calling | [record[3] != 255; partner[3] := record[3]].
                        T131_O++;
                        if(execute_transition_ringback_1()) {
                            T131_S++;
                            D055_S++;
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

                // Report all counts.
                logger.info("D042.O " + D042_O);
                logger.info("D042.F " + D042_F);
                logger.info("D042.S " + D042_S);
                logger.info("T099.O " + T099_O);
                logger.info("T099.F " + T099_F);
                logger.info("T099.S " + T099_S);
                logger.info("T100.O " + T100_O);
                logger.info("T100.F " + T100_F);
                logger.info("T100.S " + T100_S);
                logger.info("D043.O " + D043_O);
                logger.info("D043.F " + D043_F);
                logger.info("D043.S " + D043_S);
                logger.info("T101.O " + T101_O);
                logger.info("T101.F " + T101_F);
                logger.info("T101.S " + T101_S);
                logger.info("T102.O " + T102_O);
                logger.info("T102.F " + T102_F);
                logger.info("T102.S " + T102_S);
                logger.info("T103.O " + T103_O);
                logger.info("T103.F " + T103_F);
                logger.info("T103.S " + T103_S);
                logger.info("T104.O " + T104_O);
                logger.info("T104.F " + T104_F);
                logger.info("T104.S " + T104_S);
                logger.info("T105.O " + T105_O);
                logger.info("T105.F " + T105_F);
                logger.info("T105.S " + T105_S);
                logger.info("T106.O " + T106_O);
                logger.info("T106.F " + T106_F);
                logger.info("T106.S " + T106_S);
                logger.info("D044.O " + D044_O);
                logger.info("D044.F " + D044_F);
                logger.info("D044.S " + D044_S);
                logger.info("T107.O " + T107_O);
                logger.info("T107.F " + T107_F);
                logger.info("T107.S " + T107_S);
                logger.info("T108.O " + T108_O);
                logger.info("T108.F " + T108_F);
                logger.info("T108.S " + T108_S);
                logger.info("T109.O " + T109_O);
                logger.info("T109.F " + T109_F);
                logger.info("T109.S " + T109_S);
                logger.info("T110.O " + T110_O);
                logger.info("T110.F " + T110_F);
                logger.info("T110.S " + T110_S);
                logger.info("T111.O " + T111_O);
                logger.info("T111.F " + T111_F);
                logger.info("T111.S " + T111_S);
                logger.info("T112.O " + T112_O);
                logger.info("T112.F " + T112_F);
                logger.info("T112.S " + T112_S);
                logger.info("D045.O " + D045_O);
                logger.info("D045.F " + D045_F);
                logger.info("D045.S " + D045_S);
                logger.info("T113.O " + T113_O);
                logger.info("T113.F " + T113_F);
                logger.info("T113.S " + T113_S);
                logger.info("D046.O " + D046_O);
                logger.info("D046.F " + D046_F);
                logger.info("D046.S " + D046_S);
                logger.info("T114.O " + T114_O);
                logger.info("T114.F " + T114_F);
                logger.info("T114.S " + T114_S);
                logger.info("T115.O " + T115_O);
                logger.info("T115.F " + T115_F);
                logger.info("T115.S " + T115_S);
                logger.info("D047.O " + D047_O);
                logger.info("D047.F " + D047_F);
                logger.info("D047.S " + D047_S);
                logger.info("T116.O " + T116_O);
                logger.info("T116.F " + T116_F);
                logger.info("T116.S " + T116_S);
                logger.info("T117.O " + T117_O);
                logger.info("T117.F " + T117_F);
                logger.info("T117.S " + T117_S);
                logger.info("T118.O " + T118_O);
                logger.info("T118.F " + T118_F);
                logger.info("T118.S " + T118_S);
                logger.info("D048.O " + D048_O);
                logger.info("D048.F " + D048_F);
                logger.info("D048.S " + D048_S);
                logger.info("T119.O " + T119_O);
                logger.info("T119.F " + T119_F);
                logger.info("T119.S " + T119_S);
                logger.info("D049.O " + D049_O);
                logger.info("D049.F " + D049_F);
                logger.info("D049.S " + D049_S);
                logger.info("T120.O " + T120_O);
                logger.info("T120.F " + T120_F);
                logger.info("T120.S " + T120_S);
                logger.info("T121.O " + T121_O);
                logger.info("T121.F " + T121_F);
                logger.info("T121.S " + T121_S);
                logger.info("T122.O " + T122_O);
                logger.info("T122.F " + T122_F);
                logger.info("T122.S " + T122_S);
                logger.info("D050.O " + D050_O);
                logger.info("D050.F " + D050_F);
                logger.info("D050.S " + D050_S);
                logger.info("D051.O " + D051_O);
                logger.info("D051.F " + D051_F);
                logger.info("D051.S " + D051_S);
                logger.info("T123.O " + T123_O);
                logger.info("T123.F " + T123_F);
                logger.info("T123.S " + T123_S);
                logger.info("D052.O " + D052_O);
                logger.info("D052.F " + D052_F);
                logger.info("D052.S " + D052_S);
                logger.info("T124.O " + T124_O);
                logger.info("T124.F " + T124_F);
                logger.info("T124.S " + T124_S);
                logger.info("D053.O " + D053_O);
                logger.info("D053.F " + D053_F);
                logger.info("D053.S " + D053_S);
                logger.info("T125.O " + T125_O);
                logger.info("T125.F " + T125_F);
                logger.info("T125.S " + T125_S);
                logger.info("T126.O " + T126_O);
                logger.info("T126.F " + T126_F);
                logger.info("T126.S " + T126_S);
                logger.info("D054.O " + D054_O);
                logger.info("D054.F " + D054_F);
                logger.info("D054.S " + D054_S);
                logger.info("T127.O " + T127_O);
                logger.info("T127.F " + T127_F);
                logger.info("T127.S " + T127_S);
                logger.info("T128.O " + T128_O);
                logger.info("T128.F " + T128_F);
                logger.info("T128.S " + T128_S);
                logger.info("T129.O " + T129_O);
                logger.info("T129.F " + T129_F);
                logger.info("T129.S " + T129_S);
                logger.info("D055.O " + D055_O);
                logger.info("D055.F " + D055_F);
                logger.info("D055.S " + D055_S);
                logger.info("T130.O " + T130_O);
                logger.info("T130.F " + T130_F);
                logger.info("T130.S " + T130_S);
                logger.info("T131.O " + T131_O);
                logger.info("T131.F " + T131_F);
                logger.info("T131.S " + T131_S);
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
        logger.info("JSON {\"name\": \"Telephony\", \"settings\": \"test_models/Telephony.slco -running_time=30 -package_name=randompicknostructuresnolocks -no_deterministic_structures -no_locks -use_random_pick -performance_measurements\", \"classes\": {\"GlobalClass\": {\"name\": \"GlobalClass\", \"state_machines\": {\"User_0\": {\"name\": \"User_0\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D000\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[0] = 255; dev := 0; chan[0] := (0 + 0 * 20)]\", \"id\": \"T000\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[0] != 255; partner[0] := (chan[0] % 20)]\", \"id\": \"T001\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D001\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[0] := 255]\", \"id\": \"T002\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[0] := 0\", \"id\": \"T003\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[0] := 1\", \"id\": \"T004\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[0] := 2\", \"id\": \"T005\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[0] := 3\", \"id\": \"T006\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[0] := 4\", \"id\": \"T007\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D002\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[0] = 0\", \"id\": \"T008\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[0] = 4\", \"id\": \"T009\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[0] = 4\", \"id\": \"T010\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] = 255; record[partner[0]] := 0]\", \"id\": \"T011\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] != 255 and callforwardbusy[partner[0]] != 255; record[partner[0]] := 0; partner[0] := callforwardbusy[partner[0]]]\", \"id\": \"T012\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[0] != 0 and partner[0] != 4 and chan[partner[0]] = 255; record[partner[0]] := 0; chan[partner[0]] := (0 + 0 * 20); chan[0] := (partner[0] + 0 * 20)]\", \"id\": \"T013\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D003\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1]\", \"id\": \"T014\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D004\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[0]] % 20) = 0\", \"id\": \"T015\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[0]] % 20) != 0; partner[0] := 255]\", \"id\": \"T016\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D005\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[0] = 255\", \"id\": \"T017\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[0]] % 20) = 0\", \"id\": \"T018\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[0]] % 20) != 0\", \"id\": \"T019\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D006\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1]\", \"id\": \"T020\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D007\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[0] % 20) != partner[0]\", \"id\": \"T021\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 1\", \"id\": \"T022\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[0] % 20) = partner[0] and (chan[0] / 20) = 0\", \"id\": \"T023\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D008\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D009\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[0] := 255; chan[partner[0]] := 255]\", \"id\": \"T024\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D010\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[0] := 255; partner[0] := ((partner[0] % 20) + 0 * 20)]\", \"id\": \"T025\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D011\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[0]] % 20) = 0 and (chan[partner[0]] / 20) = 0; dev := 0; chan[partner[0]] := (0 + 1 * 20); chan[0] := (partner[0] + 1 * 20)]\", \"id\": \"T026\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[0]] = 255 or (chan[partner[0]] % 20) != 0; dev := 1; partner[0] := 255; chan[0] := 255]\", \"id\": \"T027\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D012\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T028\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[0] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T029\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[0] / 20) = 0; partner[0] := 255; chan[0] := 255]\", \"id\": \"T030\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D013\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[0] := 255; partner[0] := 255; dev := 1]\", \"id\": \"T031\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[0] != 255; partner[0] := record[0]]\", \"id\": \"T032\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}, \"User_1\": {\"name\": \"User_1\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D014\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[1] = 255; dev := 0; chan[1] := (1 + 0 * 20)]\", \"id\": \"T033\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[1] != 255; partner[1] := (chan[1] % 20)]\", \"id\": \"T034\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D015\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[1] := 255]\", \"id\": \"T035\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[1] := 0\", \"id\": \"T036\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[1] := 1\", \"id\": \"T037\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[1] := 2\", \"id\": \"T038\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[1] := 3\", \"id\": \"T039\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[1] := 4\", \"id\": \"T040\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D016\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[1] = 1\", \"id\": \"T041\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[1] = 4\", \"id\": \"T042\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[1] = 4\", \"id\": \"T043\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] = 255; record[partner[1]] := 1]\", \"id\": \"T044\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] != 255 and callforwardbusy[partner[1]] != 255; record[partner[1]] := 1; partner[1] := callforwardbusy[partner[1]]]\", \"id\": \"T045\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[1] != 1 and partner[1] != 4 and chan[partner[1]] = 255; record[partner[1]] := 1; chan[partner[1]] := (1 + 0 * 20); chan[1] := (partner[1] + 0 * 20)]\", \"id\": \"T046\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D017\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1]\", \"id\": \"T047\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D018\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[1]] % 20) = 1\", \"id\": \"T048\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[1]] % 20) != 1; partner[1] := 255]\", \"id\": \"T049\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D019\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[1] = 255\", \"id\": \"T050\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[1]] % 20) = 1\", \"id\": \"T051\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[1]] % 20) != 1\", \"id\": \"T052\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D020\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1]\", \"id\": \"T053\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D021\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[1] % 20) != partner[1]\", \"id\": \"T054\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 1\", \"id\": \"T055\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[1] % 20) = partner[1] and (chan[1] / 20) = 0\", \"id\": \"T056\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D022\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D023\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[1] := 255; chan[partner[1]] := 255]\", \"id\": \"T057\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D024\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[1] := 255; partner[1] := ((partner[1] % 20) + 0 * 20)]\", \"id\": \"T058\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D025\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[1]] % 20) = 1 and (chan[partner[1]] / 20) = 0; dev := 0; chan[partner[1]] := (1 + 1 * 20); chan[1] := (partner[1] + 1 * 20)]\", \"id\": \"T059\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[1]] = 255 or (chan[partner[1]] % 20) != 1; dev := 1; partner[1] := 255; chan[1] := 255]\", \"id\": \"T060\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D026\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T061\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[1] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T062\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[1] / 20) = 0; partner[1] := 255; chan[1] := 255]\", \"id\": \"T063\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D027\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[1] := 255; partner[1] := 255; dev := 1]\", \"id\": \"T064\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[1] != 255; partner[1] := record[1]]\", \"id\": \"T065\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}, \"User_2\": {\"name\": \"User_2\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D028\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[2] = 255; dev := 0; chan[2] := (2 + 0 * 20)]\", \"id\": \"T066\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[2] != 255; partner[2] := (chan[2] % 20)]\", \"id\": \"T067\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D029\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[2] := 255]\", \"id\": \"T068\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[2] := 0\", \"id\": \"T069\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[2] := 1\", \"id\": \"T070\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[2] := 2\", \"id\": \"T071\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[2] := 3\", \"id\": \"T072\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[2] := 4\", \"id\": \"T073\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D030\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[2] = 2\", \"id\": \"T074\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[2] = 4\", \"id\": \"T075\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[2] = 4\", \"id\": \"T076\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] = 255; record[partner[2]] := 2]\", \"id\": \"T077\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] != 255 and callforwardbusy[partner[2]] != 255; record[partner[2]] := 2; partner[2] := callforwardbusy[partner[2]]]\", \"id\": \"T078\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[2] != 2 and partner[2] != 4 and chan[partner[2]] = 255; record[partner[2]] := 2; chan[partner[2]] := (2 + 0 * 20); chan[2] := (partner[2] + 0 * 20)]\", \"id\": \"T079\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D031\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1]\", \"id\": \"T080\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D032\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[2]] % 20) = 2\", \"id\": \"T081\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[2]] % 20) != 2; partner[2] := 255]\", \"id\": \"T082\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D033\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[2] = 255\", \"id\": \"T083\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[2]] % 20) = 2\", \"id\": \"T084\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[2]] % 20) != 2\", \"id\": \"T085\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D034\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1]\", \"id\": \"T086\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D035\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[2] % 20) != partner[2]\", \"id\": \"T087\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 1\", \"id\": \"T088\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[2] % 20) = partner[2] and (chan[2] / 20) = 0\", \"id\": \"T089\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D036\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D037\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[2] := 255; chan[partner[2]] := 255]\", \"id\": \"T090\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D038\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[2] := 255; partner[2] := ((partner[2] % 20) + 0 * 20)]\", \"id\": \"T091\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D039\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[2]] % 20) = 2 and (chan[partner[2]] / 20) = 0; dev := 0; chan[partner[2]] := (2 + 1 * 20); chan[2] := (partner[2] + 1 * 20)]\", \"id\": \"T092\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[2]] = 255 or (chan[partner[2]] % 20) != 2; dev := 1; partner[2] := 255; chan[2] := 255]\", \"id\": \"T093\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D040\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T094\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[2] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T095\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[2] / 20) = 0; partner[2] := 255; chan[2] := 255]\", \"id\": \"T096\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D041\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[2] := 255; partner[2] := 255; dev := 1]\", \"id\": \"T097\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[2] != 255; partner[2] := record[2]]\", \"id\": \"T098\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}, \"User_3\": {\"name\": \"User_3\", \"states\": [\"idle\", \"dialing\", \"calling\", \"busy\", \"qi\", \"talert\", \"unobtainable\", \"oalert\", \"errorstate\", \"oconnected\", \"dveoringout\", \"tpickup\", \"tconnected\", \"ringback\"], \"decision_structures\": {\"idle\": {\"source\": \"idle\", \"id\": \"D042\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | idle -> dialing | [chan[3] = 255; dev := 0; chan[3] := (3 + 0 * 20)]\", \"id\": \"T099\", \"source\": \"idle\", \"target\": \"dialing\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | idle -> qi | [chan[3] != 255; partner[3] := (chan[3] % 20)]\", \"id\": \"T100\", \"source\": \"idle\", \"target\": \"qi\", \"priority\": 0, \"is_excluded\": false}}}, \"dialing\": {\"source\": \"dialing\", \"id\": \"D043\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dialing -> idle | true | [true; dev := 1; chan[3] := 255]\", \"id\": \"T101\", \"source\": \"dialing\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | dialing -> calling | true | partner[3] := 0\", \"id\": \"T102\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | dialing -> calling | true | partner[3] := 1\", \"id\": \"T103\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | dialing -> calling | true | partner[3] := 2\", \"id\": \"T104\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | dialing -> calling | true | partner[3] := 3\", \"id\": \"T105\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | dialing -> calling | true | partner[3] := 4\", \"id\": \"T106\", \"source\": \"dialing\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}, \"calling\": {\"source\": \"calling\", \"id\": \"D044\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | calling -> busy | partner[3] = 3\", \"id\": \"T107\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | calling -> unobtainable | partner[3] = 4\", \"id\": \"T108\", \"source\": \"calling\", \"target\": \"unobtainable\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | calling -> ringback | partner[3] = 4\", \"id\": \"T109\", \"source\": \"calling\", \"target\": \"ringback\", \"priority\": 0, \"is_excluded\": false}, \"3\": {\"name\": \"(p:0, id:3) | calling -> busy | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] = 255; record[partner[3]] := 3]\", \"id\": \"T110\", \"source\": \"calling\", \"target\": \"busy\", \"priority\": 0, \"is_excluded\": false}, \"4\": {\"name\": \"(p:0, id:4) | calling -> calling | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] != 255 and callforwardbusy[partner[3]] != 255; record[partner[3]] := 3; partner[3] := callforwardbusy[partner[3]]]\", \"id\": \"T111\", \"source\": \"calling\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}, \"5\": {\"name\": \"(p:0, id:5) | calling -> oalert | [partner[3] != 3 and partner[3] != 4 and chan[partner[3]] = 255; record[partner[3]] := 3; chan[partner[3]] := (3 + 0 * 20); chan[3] := (partner[3] + 0 * 20)]\", \"id\": \"T112\", \"source\": \"calling\", \"target\": \"oalert\", \"priority\": 0, \"is_excluded\": false}}}, \"busy\": {\"source\": \"busy\", \"id\": \"D045\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | busy -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1]\", \"id\": \"T113\", \"source\": \"busy\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"qi\": {\"source\": \"qi\", \"id\": \"D046\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | qi -> talert | (chan[partner[3]] % 20) = 3\", \"id\": \"T114\", \"source\": \"qi\", \"target\": \"talert\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | qi -> idle | [(chan[partner[3]] % 20) != 3; partner[3] := 255]\", \"id\": \"T115\", \"source\": \"qi\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"talert\": {\"source\": \"talert\", \"id\": \"D047\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | talert -> errorstate | dev != 1 or chan[3] = 255\", \"id\": \"T116\", \"source\": \"talert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | talert -> tpickup | (chan[partner[3]] % 20) = 3\", \"id\": \"T117\", \"source\": \"talert\", \"target\": \"tpickup\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | talert -> idle | (chan[partner[3]] % 20) != 3\", \"id\": \"T118\", \"source\": \"talert\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"unobtainable\": {\"source\": \"unobtainable\", \"id\": \"D048\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | unobtainable -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1]\", \"id\": \"T119\", \"source\": \"unobtainable\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"oalert\": {\"source\": \"oalert\", \"id\": \"D049\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oalert -> errorstate | (chan[3] % 20) != partner[3]\", \"id\": \"T120\", \"source\": \"oalert\", \"target\": \"errorstate\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | oalert -> oconnected | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 1\", \"id\": \"T121\", \"source\": \"oalert\", \"target\": \"oconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | oalert -> dveoringout | (chan[3] % 20) = partner[3] and (chan[3] / 20) = 0\", \"id\": \"T122\", \"source\": \"oalert\", \"target\": \"dveoringout\", \"priority\": 0, \"is_excluded\": false}}}, \"errorstate\": {\"source\": \"errorstate\", \"id\": \"D050\", \"transitions\": {}}, \"oconnected\": {\"source\": \"oconnected\", \"id\": \"D051\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | oconnected -> idle | true | [true; dev := 1; chan[3] := 255; chan[partner[3]] := 255]\", \"id\": \"T123\", \"source\": \"oconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"dveoringout\": {\"source\": \"dveoringout\", \"id\": \"D052\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | dveoringout -> idle | true | [true; dev := 1; chan[3] := 255; partner[3] := ((partner[3] % 20) + 0 * 20)]\", \"id\": \"T124\", \"source\": \"dveoringout\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tpickup\": {\"source\": \"tpickup\", \"id\": \"D053\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tpickup -> tconnected | [(chan[partner[3]] % 20) = 3 and (chan[partner[3]] / 20) = 0; dev := 0; chan[partner[3]] := (3 + 1 * 20); chan[3] := (partner[3] + 1 * 20)]\", \"id\": \"T125\", \"source\": \"tpickup\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tpickup -> idle | [chan[partner[3]] = 255 or (chan[partner[3]] % 20) != 3; dev := 1; partner[3] := 255; chan[3] := 255]\", \"id\": \"T126\", \"source\": \"tpickup\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"tconnected\": {\"source\": \"tconnected\", \"id\": \"D054\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 0; dev := 1]\", \"id\": \"T127\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | tconnected -> tconnected | [(chan[3] / 20) = 1 and dev = 1; dev := 0]\", \"id\": \"T128\", \"source\": \"tconnected\", \"target\": \"tconnected\", \"priority\": 0, \"is_excluded\": false}, \"2\": {\"name\": \"(p:0, id:2) | tconnected -> idle | [(chan[3] / 20) = 0; partner[3] := 255; chan[3] := 255]\", \"id\": \"T129\", \"source\": \"tconnected\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}}}, \"ringback\": {\"source\": \"ringback\", \"id\": \"D055\", \"transitions\": {\"0\": {\"name\": \"(p:0, id:0) | ringback -> idle | true | [true; chan[3] := 255; partner[3] := 255; dev := 1]\", \"id\": \"T130\", \"source\": \"ringback\", \"target\": \"idle\", \"priority\": 0, \"is_excluded\": false}, \"1\": {\"name\": \"(p:0, id:1) | ringback -> calling | [record[3] != 255; partner[3] := record[3]]\", \"id\": \"T131\", \"source\": \"ringback\", \"target\": \"calling\", \"priority\": 0, \"is_excluded\": false}}}}}}}}}");
    }
}