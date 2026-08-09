# Package Management Facility Simulator

## Overview
A multi-threaded Java simulation of a package routing facility. Each `Station` runs on its own thread and moves package groups between two shared `Conveyor` resources arranged in a ring. Stations must acquire locks on both their input and output conveyor before processing a package group, which creates realistic resource contention and potential deadlock scenarios that the program actively avoids.

## Concurrency Concepts Demonstrated
- **Fixed thread pool** (`ExecutorService`, max 10 threads) to run all stations
- **`ReentrantLock` + `tryLock()`** for non-blocking, deadlock-avoiding conveyor locking
- **Lock ordering** — each station always attempts the lower-numbered conveyor first, preventing circular-wait deadlocks
- **`CountDownLatch`** — ensures all stations finish initializing before any begin work
- **`CyclicBarrier`** — synchronizes all stations at a common "start" point, triggered by a control signal

## How It Works
1. Stations are arranged in a ring; each station's output conveyor is the next station's input conveyor.
2. On startup, each station initializes and counts down a shared `CountDownLatch`.
3. Once all stations are initialized, they rendezvous at a `CyclicBarrier`. The barrier's action prints the "Control Module: Sending Start Signal" message.
4. Each station then repeatedly tries to lock its input and output conveyors (in a consistent low-to-high order), move a package group, then release both locks — looping until its workload reaches zero.
5. If a station can't acquire its output lock, it releases what it holds and retries, avoiding deadlock.

## How to Run
```bash
javac Station.java Conveyor.java PMFSimulator.java
java PMFSimulator
```

Requires a `config.txt` in the working directory, formatted as:
```
<number of stations>
<workload for station 0>
<workload for station 1>
...
```
Example (3 stations):
```
3
2
3
4
```

## Sample Output
```
Package Management Facility Simulator

* * * * * * * * * * PACKAGE MANAGEMENT FACILITY SIMULATION BEGINS * * * * * * * * * *

There are 3 Routing Stations in this simulation run.
...
# # Routing Station S2: going offline – work completed! BYE! # #

All Stations Offline…Simulation Ends!
```
Full output available in [SimulationOutput.txt](./SimulationOutput.txt).
