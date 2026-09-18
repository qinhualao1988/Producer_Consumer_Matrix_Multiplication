package finalProject;

import java.util.ArrayList;
// 04/14 edited: modify producer class to use getter and setter of workItem class

public class Producer implements Runnable {

    // declare variables based on the requirements
    private SharedBuffer buffer;
    private int[][] matrixA;
    private int[][] matrixB;

    private int splitSize; // the number to split rows of matrix A and columns of matrix B, read from configuration file
    private int MaxProducerSleepTime; //read from configuration file
    private int producedCount = 0; // count the number of produced work item
    private int totalSleepTime = 0; // will be used to calculate the average sleep time of producer thread

    private ArrayList<WorkItem> allItem = new ArrayList<>(); // save all the work items created by producer, will be used to check if all work items are finished and copy subC to final matrix C

    private int[][] finalMatrixC; // the final result matrix C, will be updated when each work item is finished




    public Producer(SharedBuffer buffer, int[][] matrixA, int[][] matrixB, int splitSize, int MaxProducerSleepTime) {
        this.buffer = buffer;
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.splitSize = splitSize;
        this.MaxProducerSleepTime = MaxProducerSleepTime;

        this.finalMatrixC = new int[matrixA.length][matrixB[0].length]; //row size: row size of matrix A; column size: column size of matrix B

    }

    @Override
    public void run() {
        int rowA = matrixA.length;
        int colA = matrixA[0].length;
        int rowB = matrixB.length;
        int colB = matrixB[0].length;

        //split matrixA into sub-matrices and put them in the buffer
        for (int lowA = 0; lowA < rowA; lowA += splitSize) {
            int highA  = Math.min(lowA + splitSize, rowA); // make sure the high index does not exceed the row size of matrix A
            int subRows = highA - lowA; // the number of rows in the sub-matrix of A, will be used to create work item

            int[][] subA = new int[subRows][colA]; 
            // copy values from matrix A to subA
            for (int i = 0; i < subRows; i++) {
                for (int j = 0; j < colA; j++) {
                    subA[i][j] = matrixA[lowA + i][j];
                }
            }
            // split matrix B
            for (int lowB = 0; lowB < colB; lowB += splitSize) {
                int highB = Math.min(lowB + splitSize, colB);
                int subCols = highB - lowB;

                // copy values from matrix B to subB
                int[][] subB = new int[rowB][subCols];
                for(int i = 0; i < rowB; i++) {
                    for (int j = 0; j < subCols; j++) {
                        subB[i][j] = matrixB[i][lowB + j];
                    }
                }
                
                WorkItem item = new WorkItem(subA, subB, lowA, highA - 1, lowB, highB - 1);// create work item
                
                allItem.add(item); // save for later checking
                buffer.put(item); // push it into buffer

                producedCount++; //track nuber of workItem

                int sleepTime = (int)(Math.random() * (MaxProducerSleepTime + 1)); // generate sleepTime
                totalSleepTime += sleepTime; //get total sleep time in each workItem

                System.out.println("Producer puts rows " + lowA + "- " + (highA - 1) + " and columns " + lowB + "- " + (highB - 1) + " to buffer");

                
                while(!allItemFinished()) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    
                    }
                }
                
                // copy subC to final matrix C when all work items are finished
                for(WorkItem items : allItem) {
                    if(items.isDone()) {
                        copySubCToMatrixC(items);
                    }
                }
            }
        }

    }

    // methods
    public int getProducedCount() {
        return producedCount;
    } 

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public double averageProducerSleepTime() {
        return  totalSleepTime / producedCount;
    }

    public int[][] getMatrixC() {
        return finalMatrixC;
    }

    public boolean allItemFinished() {
        for (WorkItem item: allItem) {
            if(!item.isDone()) {
                return false;
            }
        }
        return true;
    }

    public void copySubCToMatrixC(WorkItem item) {
        for (int i = 0; i < item.getSubC().length; i++) {
            for (int j = 0; j < item.getSubC()[0].length; j++) {
                // the position of each value in final matrix C is determined by the low row index and low column index of the work item
                finalMatrixC[item.getLowA() + i][item.getLowB() + j] = item.getSubC()[i][j]; 
            }
        }
    }

}