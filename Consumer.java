package finalProject;
//04/14 edited: modify consumer class to use getter and setter of workItem class

public class Consumer implements Runnable {

    //variables
    private SharedBuffer buffer;
    private boolean stop = false;

    private int consumerCount = 0; // count the number of consumed work item

    private int MaxConsumerSleepTime; // sleep time of the consumer thread between the two sub-matrices multiplication
    private int totalConsumerSleepTime = 0; // will be used to calculate the average sleep time of consumer thread
    //constructor
    public Consumer(SharedBuffer buffer, int MaxConsumerSleepTime) {
        this.buffer = buffer;
        this.MaxConsumerSleepTime = MaxConsumerSleepTime;
    }


    @Override
    public void run() {
        while (!stop) {
            WorkItem item = buffer.get();

            if(item != null) {

                System.out.println("Consumer " + Thread.currentThread().getName() + " gets rows " + item.getLowA() + " - " + item.getHighA() + " of matrix A and columns " + item.getLowB() + " - " + item.getHighB() + " of B from the buffer");              
  
                calculate(item); // call method to calculate the submatrix multiplication

                consumerCount++; // count number of consumed work item

                item.setDone(true);

                //set up sleep time
                int consumerSleepTime = (int)(Math.random() * (MaxConsumerSleepTime + 1));
                totalConsumerSleepTime += consumerSleepTime;

                try {
                    Thread.sleep(consumerSleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }                
                
                // print result
                System.out.println("consumer finishes calculating");
                printMatrix(item.getSubA());
                System.out.println("X");
                printMatrix(item.getSubB());
                System.out.println("=>");
                printMatrix(item.getSubC());

                
            }
        }
    }

    //calculate matrix multiplication
    public void calculate(WorkItem item) {
        int rowA = item.getSubA().length;
        int colA = item.getSubA()[0].length;
        int colB = item.getSubB()[0].length;

        item.setSubC(new int[rowA][colB]);

        for (int i = 0; i < rowA; i++) {
            for (int j = 0; j < colB; j++) {
                for (int k = 0; k < colA; k++) {
                    // the value of each position in subC is the sum of multiplication of corresponding values in subA and subB
                    item.getSubC()[i][j] += item.getSubA()[i][k] * item.getSubB()[k][j];
                }
            }
        }
    }
    
    //matrix printing
    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("[ "); 
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("]");
        }
    }
    
    // methods
    public void stopConsumer() {
    stop = true;
    }
    
    public int getTotalConsumerSleepTime() {
        return totalConsumerSleepTime;
    }

    public int getConsumedCount() {
        return consumerCount;
    }

    public double avgConsumerSleepTime() {
        return totalConsumerSleepTime / consumerCount;
    }

}


