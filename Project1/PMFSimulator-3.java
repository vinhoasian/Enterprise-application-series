import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Name: Vinh Vu
 * Class: PMFSimulator.java
 */
public class PMFSimulator {

    private static final int MAX = 10; // Requirement 4: fixed thread pool upper limit

    public static void main(String[] args) {

        System.out.println("\n\tSummer 2026 – Project 1 – Package Management Facility Simulator\n");
        System.out.println("\n* * * * * * * * * * PACKAGE MANAGEMENT FACILITY SIMULATION BEGINS * * * * * * * * * *\n");

        // --- Read configuration file ---
        File configFile = new File("config.txt");
        int numStations = 0;
        int[] workloads = null;

        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            numStations = Integer.parseInt(br.readLine().trim());
            workloads = new int[numStations];
            for (int i = 0; i < numStations; i++) {
                workloads[i] = Integer.parseInt(br.readLine().trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading config.txt: " + e.getMessage());
            return;
        }

        System.out.println("\tThe parameters for this simulation run are:\n");
        System.out.println("\tThere are " + numStations + " Routing Stations in this simulation run.\n");

        // --- Build conveyor ring ---
        // Station i uses conveyor i as input and conveyor (i+1) % numStations as output
        // Each conveyor is shared between station i (output) and station (i+1)%n (input)
        Conveyor[] conveyors = new Conveyor[numStations];
        for (int i = 0; i < numStations; i++) {
            conveyors[i] = new Conveyor(i);
        }

        // --- Synchronization objects ---
        // Requirement 6: CountDownLatch – counts down once per station initialization
        CountDownLatch initLatch = new CountDownLatch(numStations);

        // Requirement 7: CyclicBarrier – all stations + main thread meet here
        // Barrier action (runs when all parties arrive) prints the control signal message
        CyclicBarrier startBarrier = new CyclicBarrier(numStations, () ->
            System.out.println("\n\t>>> Control Module: All Stations Initialized – Sending Start Signal <<<\n")
        );

        // --- Create stations ---
        // Station i: input = conveyor[i], output = conveyor[(i+1) % n]
        // Lock order enforced inside Station.run() by always locking lower-ID conveyor first
        ArrayList<Station> stations = new ArrayList<>();
        for (int i = 0; i < numStations; i++) {
            int inputId  = i;
            int outputId = (i + 1) % numStations;
            Station s = new Station(
                    i,
                    workloads[i],
                    conveyors[inputId],
                    conveyors[outputId],
                    numStations,
                    initLatch,
                    startBarrier
            );
            stations.add(s);
            System.out.println("\tRouting Station S" + i
                    + " Has Total Workload Of " + workloads[i] + " Package Groups.");
        }
        System.out.println();

        // --- Execute via fixed thread pool of size MAX (10) ---
        ExecutorService executor = Executors.newFixedThreadPool(MAX);

        for (Station s : stations) {
            executor.execute(s);
        }

        executor.shutdown();

        // Wait for all threads to finish
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Output 13 – simulation ends
        System.out.println("\nAll Stations Offline…Simulation Ends!");
        System.out.println("\n* * * ALL WORKLOADS COMPLETE * * * PACKAGE MANAGEMENT FACILITY SIMULATION TERMINATES * * * * * * * * * *\n");
        System.out.println("\n * %% * %% * %% SIMULATION ENDS %% * %% * %% *\n");
    }
}
