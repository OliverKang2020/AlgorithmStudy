Copyright (c) Junho Kang

### How To Work
You can try updating parameters in main.c. 
I already have set up two types of configurations for two provided Integrate tasks.
See the "Generalization" section below for the details.

Do the following for compile and execution:
- gcc -o output main.c MonteCarloCalculation.c
- ./output

### Generalization
The configurable parameters are:
   - Number of Random Points
   - Number of Threads
   - Top Boundary Value of Integral
   - Bottom Boundary Value of Integral
   - Function for calculation

### Evaluate and Discuss
Executed both solutions over varying thread counts. 

#### 1) execution time vs number of threads
The trend was that the greater the number of threads is, the lower the execution time is.
Not always true, but it shows the best efficiency when the number of threads is 12 to 16 in my case. 

#### 2) integral value vs number of threads
There was no noticeable difference with runs on different number of threads.
It's only for managing work in parallel and it should not affect the actual result of the calculation.


### The memory model used by your solution:
The `MonteCarloCalculation` module receives the number of threads externally and generates the amount of threads. The thread routine is executed accordingly.

The Monte Carlo requires a bunch of random points to calculate provided function.
So, each thread routine calculates the function by dividing the range by the number of threads.

To be specific, each thread routine generates random values from the divided part of random points. And it calculates provided function from each random value and performs summation.

As This job is only for the split random points on the calculation, every thread routine should accumulate its own result to cover the whole random points.

In summary,
private data: thread count, random value and index to be calculated, summation value in a limited part. 
data shared in shared/global memory: accumulated summation to cover the whole range, mutex, configuration data (that consists of the number of threads, number of random points, boundary values of integral), 

### Thread communication used by your solution (i.e: thread initialization and aggregation):
As an argument of thread creation(initialization), thread number has been passed to thread routine. Each thread routine determines which part of random points it will generate according to the thread number.

All threads are created without delay on for-loop at a time.
In the end, all the threads do `join` so the calculation can't be complete if any of the thread routines are running. It waits for every thread to be complete, and it proceeds to the remaining work in main after all threads are done.


### Thread synchronization and locking required for your solution:
Each thread routine performs a summation of function results for a specific part of random points.
In order to complete the Monte Carlo calculation, the whole summations should be accumulated.
Thus, accumulating each summation should be synchronized as the whole threads access the accumulated space and it needs to be prevented from conflict. I have mutex-locked for the synchronization so only a single thread can do the accumulation at a time.### The memory model used by your solution:
The `MonteCarloCalculation` module receives the number of threads externally and generates the amount of threads. The thread routine is executed accordingly.

The Monte Carlo requires a bunch of random points to calculate provided function.
So, each thread routine calculates the function by dividing the range by the number of threads.

To be specific, each thread routine generates random values from the divided part of random points. And it calculates provided function from each random value and performs summation.

As This job is only for the split random points on the calculation, every thread routine should accumulate its own result to cover the whole random points.

In summary,
private data: thread count, random value and index to be calculated, summation value in a limited part. 
data shared in shared/global memory: accumulated summation to cover the whole range, mutex, configuration data (that consists of the number of threads, number of random points, boundary values of integral), 

### Thread communication used by your solution (i.e: thread initialization and aggregation):
As an argument of thread creation(initialization), thread number has been passed to thread routine. Each thread routine determines which part of random points it will generate according to the thread number.

All threads are created without delay on for-loop at a time.
In the end, all the threads do `join` so the calculation can't be complete if any of the thread routines are running. It waits for every thread to be complete, and it proceeds to the remaining work in main after all threads are done.


### Thread synchronization and locking required for your solution:
Each thread routine performs a summation of function results for a specific part of random points.
In order to complete the Monte Carlo calculation, the whole summations should be accumulated.
Thus, accumulating each summation should be synchronized as the whole threads access the accumulated space and it needs to be prevented from conflict. I have mutex-locked for the synchronization so only a single thread can do the accumulation at a time.
