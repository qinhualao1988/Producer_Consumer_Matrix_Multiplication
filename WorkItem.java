package finalProject;

public class WorkItem {
    //04/14 edited: declare each variable as private, and use them by setter and getter
    //
    private int[][] subA; // sub-rows of matrix A
    private int[][] subB; // sub-columns of matrix B
    private int[][] subC; // matrix multiplication result of subA X subB

    private int lowA; //low row index copied from matrix A
    private int highA; //high row index copied from matrix A

    private int lowB; //low column index copied from matrix B
    private int highB; //high column index copied from matrix B

    private boolean bDone; // flag set to false when producer creates the work item, and 
                           // set to true when consumer processes the work item.
    // constructor
    public WorkItem(int[][] subA, int[][] subB, int lowA, int highA, int lowB, int highB) {
        this.subA = subA;
        this.subB = subB;
        this.lowA = lowA;
        this.highA = highA;
        this.lowB = lowB;
        this.highB = highB;
        this.bDone = false;
    }

    //04/14 edited: add getterr and setter for producer and consumer class
    //getters 
    public int[][] getSubA() {
        return subA;
    }
    
    public int[][] getSubB() {
        return subB;
    }

    public int[][] getSubC() {
        return subC;
    }

    public int getLowA() {
        return lowA;
    }

    public int getHighA() {
        return highA;
    }

    public int getLowB() {
        return lowB;
    }

    public int getHighB() {
        return highB;
    }

    public boolean isDone() {
        return bDone;
    }

    //setters
    public void setSubA(int[][] subA) {
        this.subA = subA;
    }

    public void setSubB(int[][] subB) {
        this.subB = subB;
    }

    public void setSubC(int[][] subC) {
        this.subC = subC;
    }

    public void setDone(boolean done) {
        this.bDone = done;
    }

    public void setLowA(int lowA) {
        this.lowA = lowA;
    }

    public void setHighA(int highA) {
        this.highA = highA;
    }

    public void setLowB(int lowB) {
        this.lowB = lowB;
    }

    public void setHighB(int highB) {
        this.highB = highB; 
    }

}
