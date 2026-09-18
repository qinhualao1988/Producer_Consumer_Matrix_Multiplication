# Producer-Consumer Matrix Multiplication

A multithreaded Java application that performs matrix multiplication using the **Producer-Consumer pattern** and a **synchronized bounded buffer**.

The program divides matrix multiplication into smaller work items. A producer thread creates these work items and places them into a shared buffer, while multiple consumer threads retrieve and process the work items concurrently.

The completed submatrix results are assembled into the final result matrix and compared with a sequential matrix multiplication result for verification.

## Key Features

- Implements the Producer-Consumer pattern in Java
- Uses one producer thread and multiple consumer threads
- Manages consumer threads with `ExecutorService`
- Implements a bounded circular buffer for sharing work items
- Coordinates threads using `synchronized`, `wait()`, and `notifyAll()`
- Divides matrix multiplication into smaller submatrix operations
- Supports configurable matrix dimensions, split size, buffer size, and number of consumers
- Verifies the result using sequential matrix multiplication
- Reports producer, consumer, and buffer activity statistics

## Project Structure

```text
├── Main.java
├── Producer.java
├── Consumer.java
├── SharedBuffer.java
├── WorkItem.java
├── MatrixGenerator.java
├── config.properties
└── .gitignore
```

### Main.java

The entry point of the application.

It loads settings from `config.properties`, generates the input matrices, creates the shared buffer, starts the producer, manages the consumer threads, and outputs the final results and runtime statistics.

It also performs standard sequential matrix multiplication for result verification.

### Producer.java

The producer divides matrix A by rows and matrix B by columns according to the configured split size.

Each pair of submatrices is stored in a `WorkItem` and placed into the shared buffer. Completed submatrix results are then copied into their corresponding positions in the final result matrix.

### Consumer.java

Consumer threads retrieve `WorkItem` objects from the shared buffer and perform the corresponding submatrix multiplication.

After completing a calculation, the consumer stores the result in the work item and marks the work item as completed.

### SharedBuffer.java

Implements a bounded circular buffer shared between the producer and consumer threads.

Thread coordination is handled using:

- `synchronized`
- `wait()`
- `notifyAll()`

The buffer also tracks how many times it becomes full or empty during execution.

### WorkItem.java

Represents an individual matrix multiplication task.

Each work item stores:

- A subset of rows from matrix A
- A subset of columns from matrix B
- The calculated submatrix result
- Row and column positions in the final matrix
- Completion status

### MatrixGenerator.java

Generates matrices containing random integer values for testing the application.

## Configuration

Program parameters can be modified in `config.properties`.

Example:

```properties
M = 10
N = 10
P = 10

SplitSize = 3

MaxBuffSize = 5
NumConsumer = 2

MaxProducerSleepTime = 20
MaxConsumerSleepTime = 80
```

These settings control:

- Matrix dimensions
- Matrix split size
- Maximum shared-buffer capacity
- Number of consumer threads
- Maximum simulated producer sleep time
- Maximum simulated consumer sleep time

## How It Works

```text
        Matrix A + Matrix B
                 |
                 v
             Producer
                 |
          creates WorkItems
                 |
                 v
        +-----------------+
        |  Shared Buffer  |
        | bounded/circular|
        +-----------------+
                 |
                 v
        Consumer Thread Pool
          /      |      \
         C1      C2     ...
          \      |      /
                 v
        Submatrix Results
                 |
                 v
          Final Matrix C
                 |
                 v
       Sequential Verification
```

1. `Main` generates matrices A and B using the configured dimensions.
2. The producer divides the matrix multiplication problem into smaller work items.
3. Each `WorkItem` contains the required rows from matrix A and columns from matrix B.
4. The producer places the work items into the shared bounded buffer.
5. Consumer threads retrieve work items from the buffer.
6. Each consumer performs its assigned submatrix multiplication.
7. Completed results are copied into their corresponding positions in the final matrix C.
8. The program also performs sequential matrix multiplication and outputs the result for comparison.

## Runtime Statistics

The application reports several execution statistics, including:

- Total simulation time
- Average producer sleep time
- Average consumer sleep time
- Number of producer threads
- Number of consumer threads
- Shared-buffer size
- Total number of work items produced
- Total number of work items consumed
- Number of work items processed by each consumer
- Number of times the buffer was full
- Number of times the buffer was empty

## Technologies and Concepts

- Java
- Multithreading
- Producer-Consumer Pattern
- Thread Synchronization
- `ExecutorService`
- `synchronized`
- `wait()` / `notifyAll()`
- Bounded Circular Buffer
- Matrix Multiplication

## Purpose

This project demonstrates core Java concurrent programming concepts by applying the Producer-Consumer pattern to matrix multiplication. It focuses on work decomposition, thread coordination, shared-resource synchronization, and distributing computational tasks across multiple consumer threads.