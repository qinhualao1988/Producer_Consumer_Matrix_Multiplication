package finalProject;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static int M; 
    static int N;
    static int P;
    static int splitSize;
    static int maxBuffSize;
    static int maxProducerSleepTime;
    static int maxConsumerSleepTime;
    static int numConsumer;

    static int numProducer = 1;
    public static void main(String[] args) {

        // read and load configuration file
        readConfig("config.properties");

        // [checked] generate two random matrixes A and B
        int [][] matrixA = MatrixGenerator.generateMatrix(M, N);
        int [][] matrixB = MatrixGenerator.generateMatrix(N, P);
        // output matrix A and B for reference
        System.out.println("First matrix: ");
        printMatrix(matrixA);
        System.out.println("Second matrix:");
        printMatrix(matrixB);
        System.out.println("===============================");
        
        // create shared buffer
        SharedBuffer buffer = new SharedBuffer(maxBuffSize); //with required limited size from the configuration file

        /*
        System.out.println("Matrix A:");
        matrixGenerator.printMatrix(matrixA);
        System.out.println("Matrix B:");
        matrixGenerator.printMatrix(matrixB);
        */

        //[checked] producer testing
        Producer producer = new Producer(buffer, matrixA, matrixB, splitSize, maxProducerSleepTime);
        Thread producerThread = new Thread(producer); // new producer thread
        producerThread.start();

        //consuer need to seperate multiple threads based on the number of consumer
        ExecutorService executor = Executors.newFixedThreadPool(numConsumer); //create thread pool
        long startTime = System.currentTimeMillis();
        //submit consumers
        Consumer[] consumers = new Consumer[numConsumer];
        // 
        for (int i = 0; i < numConsumer; i++) {
            consumers[i] = new Consumer(buffer, maxConsumerSleepTime);
            executor.execute(consumers[i]);
        }
        /*
        Thread consumerThread00 = new Thread(consumer00); 
        consumerThread00.start();
        */

        try {
            producerThread.join();   // wait producer finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // stop consumer
        for (int i = 0; i < numConsumer; i++) {
            consumers[i].stopConsumer();
        }

        long endTime = System.currentTimeMillis(); //record ending time
        executor.shutdownNow(); // stop all consumer threads
        /*
        consumer00.stopConsumer();     // tell consumer to stop
        consumerThread00.interrupt();  // wake it if waiting

        try {
            consumerThread00.join();   // wait consumer exit
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        
        // regular process of computing matrix multiplication for comparison
        int[][] verifiedrResult = regularMatrixMultiplication(matrixA, matrixB);
    

        System.out.println("Producer successfully assembly all the results from consumer threads.");
        System.out.println("========================================================");
        System.out.println("Final result of parallel matrix multiplication: ");
        Consumer.printMatrix(producer.getMatrixC()); // output final result of matrix multiplication
        //regular matrix multiplication result output start here
        System.out.println("Verified result of sequential matrix multiplication: ");
        printMatrix(verifiedrResult);
        System.out.println("========================================================");
        //need normal way to calculate the matrix multiplication result for testing
        
        //get consumer avg sleeping time
        double avg = avgConsumerSleepTime(consumers);

        //print out required data 
        System.out.println("Simulation time: " + (endTime - startTime) + "ms");
        System.out.println("Average  producer thread sleep time: " + producer.averageProducerSleepTime() + "ms");
        System.out.println("Average  consumer thread sleep time: " + avg + "ms");
        System.out.println("number of Producer threads" + numProducer);
        System.out.println("number of consumer thread: " + consumers.length);
        System.out.println("size of buffer: " + maxBuffSize);
        System.out.println("total number of items produced: " + producer.getProducedCount());

        int totalConsumed = 0;
        for (int i = 0; i < consumers.length; i++) {
            totalConsumed += consumers[i].getConsumedCount();
        }
        System.out.println("Total number of items consumed: " + totalConsumed);
        
        for(int i = 0; i < consumers.length; i++) {
            System.out.println("consumer" + i + ": " + consumers[i].getConsumedCount());
        }
        
        
        System.out.println("number of times buffer was full: " + buffer.getFullTime());
        System.out.println("number of times buffer was empty: " + buffer.getEmptyTime());
        
    }

    // regularmatrix multiplication method
    public static int[][] regularMatrixMultiplication(int[][] A, int[][] B) {
        int m = A.length;
        int n = A[0].length;
        int p  = B[0].length;

        int[][] c = new int[m][p];

        for(int i = 0; i < m; i++) {
            for(int j = 0; j < p; j++) {
                c[i][j] = 0;
                for(int k = 0; k < n; k++) {
                    c[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return c;
    }

    public static void printMatrix(int[][] matrix) {
        for(int i = 0; i < matrix.length; i++) {
            System.out.print("[ ");
            for(int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("]");
        }
    }
    // methods 
    public static double avgConsumerSleepTime(Consumer[] consumer) {

        int totalSleepTime = 0;
        int totalSleepCount = 0;

        for(int i = 0; i < consumer.length; i++) {
            totalSleepTime += consumer[i].getTotalConsumerSleepTime();
            totalSleepCount += consumer[i].getConsumedCount();
        }
        double avgConsumerSleepTime = 0;
        if(totalSleepCount != 0) {
            avgConsumerSleepTime = totalSleepTime / totalSleepCount;
        }
        return avgConsumerSleepTime;
    }
    // method to read configuration file and load parameters
    public static void readConfig(String filename) {
        Properties property = new Properties();

        try (InputStream input = Main.class.getResourceAsStream(filename);) {

            property.load(input);

            M = Integer.parseInt(property.getProperty("M"));
            N = Integer.parseInt(property.getProperty("N"));
            P = Integer.parseInt(property.getProperty("P"));

            splitSize = Integer.parseInt(property.getProperty("SplitSize"));
            maxBuffSize = Integer.parseInt(property.getProperty("MaxBuffSize"));
            numConsumer = Integer.parseInt(property.getProperty("NumConsumer"));

            maxProducerSleepTime = Integer.parseInt(property.getProperty("MaxProducerSleepTime"));
            maxConsumerSleepTime = Integer.parseInt(property.getProperty("MaxConsumerSleepTime"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
