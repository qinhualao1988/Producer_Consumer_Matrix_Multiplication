package finalProject;


public class SharedBuffer {

    private int count, in, out; // the number of items, indexes of in and out pointers
    private int MAX_BUFFER_SIZE; // the max number of items in buffer

    private WorkItem[] buffer; // the shared buffer to store shared integers   

    private int fullCount = 0; // the number of times buffer was full
    private int emptyCount = 0; // the number of times buffer was empty

    // constructor to initialize the buffer with a specified size
    public SharedBuffer(int maxSize) {
        this.MAX_BUFFER_SIZE = maxSize;
        count = 0; in = 0; out = 0;
        buffer = new WorkItem[MAX_BUFFER_SIZE];
    }

    public synchronized WorkItem get() {
        WorkItem item;
        while (count == 0) { //buffer is empty
            emptyCount++;  // increment the count of empty slots
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
             }

        }
        // retrieve the item from the buffer, update pointer and count
        item = buffer[out];
        out = (out + 1) % MAX_BUFFER_SIZE;
        count--;
        // notify the producer
        notifyAll();

        return item;
    }

    public synchronized void put(WorkItem value) {
        while (count == MAX_BUFFER_SIZE) { // buffer is full

            fullCount++;  // increment the count of full slots
            
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
             }
        }
        // put the item into the buffer, update pointer and count
        buffer[in] = value;
        in = (in + 1) % MAX_BUFFER_SIZE;
        count++;
        // notify the consumer
        notifyAll();
    }

    //methods
    public int getFullTime() {
        return fullCount;
    } // for stats to get the total # of times buffer was full

    public int getEmptyTime() {
        return emptyCount;
    } // for stats to get the total # of times buffer was empty

}
