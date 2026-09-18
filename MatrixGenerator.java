package finalProject;

import java.util.Random;

public class MatrixGenerator {
    
    // method to generate a matrix with random integers
    public static int[][] generateMatrix(int rows, int cols) {

        Random rand = new Random();
        int[][] matrix = new int[rows][cols];
       
        //iterate through the matrix and fill it with random integers
        for (int i = 0; i < rows; i++) { // rows
            for (int j = 0; j < cols; j++) { // columns
                matrix[i][j] = rand.nextInt(10); // Generate random integers between 0 and 9
            }
        }

        return matrix;
    }// end method
}// end class
        
