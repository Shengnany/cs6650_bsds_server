# Assignment 2

Undern the `assignment` folder, there are two projects `Client` and `Server`
In the `Client` folder, there are two packages named `part1` (for client part1) and `part2` (for client part2) 

## Server Design
Package DataAccessLayer:

DBCPDataSource.java: Connection manager class to establish connections between my project and MySQL database.

LiftRideDao.java: DAO class with a record insertion operation.

Package Model:
LiftRide.java: LiftRide POJO
RequestBody.java: A wrapper class for POST request body

Package Servlet:
StatisticsServlet.java: map /skiers/* path
ResortsServlet.java: map /resorts/* path 
SkiersServlet.java: map /statistics path

## Client Part1 and Client Part2

**Entry class: "ClientApp.java"**

Run the prgoram, the command line will ask for the following information:

> Please Enter numThreads, numSkiers, numLifts, numRuns, seperated by a white space

> Please Enter IP and port, seperated by a white space


**Class that represent 3 phases: "Phase.java"**

The program will start phase 1 and then phase 2 and followed by phase 3, after a certain number of threads have finished in the previous phase


**Thread that will make a post request to the server: "PostThread.java"**
The url that the post thread is sent to: 
/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}

Each phase will start `numThreads` threads. Each thread will handle `numRequests` requests. Each 

**Object that records successful posts and failed ones: "PhaseStatus.java"**

**COnfiguration statistics: "Config.jav"**


### Client part2


**latency calculation: "PerformanceAnalyzer.java"**
Contains all the calculations about the latency.


### TestThread.java
Run a simple test oon how long a single request takes to estimate this latency. 
The test send 10000 requests from a single thread to do this. 

10000 req/thread with 1 thread:  

Number of successful posts: 10000
Number of unsuccessful posts: 0
Wall time: 424seconds
Throughput: 23 requests/sec






