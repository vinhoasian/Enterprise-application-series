import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Station implements Runnable {

    private static final int MAX_WORK_SLEEP  = 1000;  // ms – simulates moving packages
    private static final int MAX_IDLE_SLEEP  = 1000;  // ms – idle between groups

    private final int          stationID;
    private       int          workload;
    private final Conveyor     inputConveyor;   // lower-numbered conveyor (in ring)
    private final Conveyor     outputConveyor;  // higher-numbered conveyor (in ring)
    private final int          numStations;

    // Synchronization objects shared across all stations
    private final CountDownLatch initLatch;   // wait until all initialized
    private final CyclicBarrier  startBarrier; // wait for control signal

    /**
     * @param stationID      station index (0-based)
     * @param workload       number of package groups to move
     * @param inputConveyor  the input conveyor for this station
     * @param outputConveyor the output conveyor for this station
     * @param numStations    total stations in simulation
     * @param initLatch      CountDownLatch counted down after each station initializes
     * @param startBarrier   CyclicBarrier all stations wait on before entering critical section
     */
    public Station(int stationID, int workload,
                   Conveyor inputConveyor, Conveyor outputConveyor,
                   int numStations,
                   CountDownLatch initLatch, CyclicBarrier startBarrier) {
        this.stationID      = stationID;
        this.workload       = workload;
        this.inputConveyor  = inputConveyor;
        this.outputConveyor = outputConveyor;
        this.numStations    = numStations;
        this.initLatch      = initLatch;
        this.startBarrier   = startBarrier;
    }

    // -------------------------------------------------------------------------
    // Output helpers
    // -------------------------------------------------------------------------

    private void printInputAssigned() {
        System.out.println("Routing Station S" + stationID
                + ": Input conveyor assigned to conveyor number C"
                + inputConveyor.getConveyorId() + ".");
    }

    private void printOutputAssigned() {
        System.out.println("Routing Station S" + stationID
                + ": Output conveyor assigned to conveyor number C"
                + outputConveyor.getConveyorId() + ".");
    }

    private void printWorkloadSet() {
        System.out.println("Routing Station S" + stationID
                + " Has Total Workload of " + workload + " Package Groups.");
    }

    private void printLockedInput() {
        System.out.println("Routing Station S" + stationID
                + ": Currently holds lock on input conveyor C"
                + inputConveyor.getConveyorId() + ".");
    }

    private void printLockedOutput() {
        System.out.println("Routing Station S" + stationID
                + ": Currently holds lock on output conveyor C"
                + outputConveyor.getConveyorId() + ".");
    }

    private void printUnlockedInput() {
        System.out.println("Routing Station S" + stationID
                + ": Unlocks/releases input conveyor C"
                + inputConveyor.getConveyorId() + ".");
    }

    private void printUnlockedOutput() {
        System.out.println("Routing Station S" + stationID
                + ": Unlocks/releases output conveyor C"
                + outputConveyor.getConveyorId() + ".");
    }

    // Unable to lock output; prints which station holds the lock
    private void printUnableToLockOutput() {
        System.out.println("Routing Station S" + stationID
                + ": UNABLE TO LOCK OUTPUT CONVEYOR C" + outputConveyor.getConveyorId()
                + ". SYNCHRONIZATION ISSUE: Station " + outputConveyor.getOwnerName() + " currently holds the lock on output conveyor C"
                + outputConveyor.getConveyorId()
                + " – Station S" + stationID
                + " releasing lock on input conveyor C"
                + inputConveyor.getConveyorId() + ".");
    }

    private void printGoingOffline() {
        System.out.println("# # Routing Station S" + stationID
                + ": going offline – work completed! BYE! # #");
    }

    private void printHardAtWork() {
        System.out.println("* * Routing Station S" + stationID
                + ": * * CURRENTLY HARD AT WORK MOVING PACKAGES. * *");
    }

    private void printWorkflowComplete() {
        System.out.println("Routing Station S" + stationID
                + ": Package group completed - " + workload
                + " package groups remaining to move.");
    }

    private void printSignalReceived() {
        System.out.println("Routing Station S" + stationID
                + ": Signal Received From Control – S" + stationID + " Online.");
    }

    // -------------------------------------------------------------------------
    // Runnable
    // -------------------------------------------------------------------------

    @Override
    public void run() {
        // Set thread name for use in diagnostics
        Thread.currentThread().setName("S" + stationID);

        // --- Initialization phase (before critical section) ---
        System.out.println("%% %% ROUTING STATION S" + stationID
                + " Initializing Conveyors %% %% %%");
        printInputAssigned();
        printOutputAssigned();
        printWorkloadSet();
        System.out.println("%% %% %% ROUTING STATION S" + stationID
                + ": Awaiting Signal From Control To Begin Operations. %% %% %%");

        // Count down the latch to signal this station is initialized
        initLatch.countDown();

        // Wait for all stations to initialize (latch reaches 0) before proceeding
        try {
            initLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Wait at the CyclicBarrier – all stations arrive here together
        // (the barrier action in PMFSimulator prints the "signal sent" message)
        try {
            startBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Each station prints its own "online" message after barrier releases
        printSignalReceived();

        // --- Critical section loop ---
        while (workload > 0) {
            // Determine lock order: always acquire lower-ID conveyor first (modulo arithmetic)
            Conveyor first, second;
            if (inputConveyor.getConveyorId() < outputConveyor.getConveyorId()) {
                first  = inputConveyor;
                second = outputConveyor;
            } else {
                first  = outputConveyor;
                second = inputConveyor;
            }

            System.out.println("Routing Station S" + stationID
                    + ": Entering Lock Acquisition Phase.");

            // Try to acquire the first (lower-numbered) conveyor
            if (first.tryAcquire()) {
                // Acquired first lock
                if (first == inputConveyor) {
                    printLockedInput();
                } else {
                    printLockedOutput();
                }

                // Try to acquire the second (higher-numbered) conveyor
                if (second.tryAcquire()) {
                    // Acquired both locks
                    if (second == outputConveyor) {
                        printLockedOutput();
                    } else {
                        printLockedInput();  // rare case
                    }

                    System.out.println("* * * * * * Routing Station S" + stationID
                            + ": Holds locks on both input conveyor C"
                            + inputConveyor.getConveyorId()
                            + " and output conveyor C"
                            + outputConveyor.getConveyorId() + ". * * * * * *");

                    printHardAtWork();

                    // Simulate moving packages (random time)
                    System.out.println("Routing Station S" + stationID
                            + ": Currently moving packages into station on input conveyor C"
                            + inputConveyor.getConveyorId() + ".");
                    System.out.println("Routing Station S" + stationID
                            + ": Currently moving packages out of station on output conveyor C"
                            + outputConveyor.getConveyorId() + ".");
                    try {
                        Thread.sleep((int) (Math.random() * MAX_WORK_SLEEP));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    workload--;
                    printWorkflowComplete();

                    // Release locks
                    System.out.println("Routing Station S" + stationID
                            + ": Entering Lock Release Phase.");
                    printUnlockedInput();
                    first.release();
                    printUnlockedOutput();
                    second.release();

                    if (workload == 0) {
