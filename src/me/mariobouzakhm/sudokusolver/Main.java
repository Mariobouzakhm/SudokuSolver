package me.mariobouzakhm.sudokusolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String filename = "veryHard3x3.txt";
        try {
            //File that contains the Sudoku. Should be a .txt file.
            File file = new File(filename);

            //Represents the number of boxes on each row.
            int size = 3;
            //Load the grid from the File.
            int[][] grid = loadSudoku(file, size);

            //Creates a new Sudoku Object to solve it.
            Sudoku sudoku = new Sudoku(size, grid);

            //Prints the grid before solving the Sudoku.
            System.out.println("Before Solve: ");
            printSudoku(sudoku);

            //Register the time before solving.
            long startTime = System.nanoTime();

            //Solves the Sudoku
            sudoku.solve();

            //Register the time after solving and allows us to compute the time taken in nanoseconds.
            long endTime = System.nanoTime();
            System.out.println("Took "+(endTime - startTime) + " ns");

            //Prints the Sudoku after solving it.
            System.out.println("After Solve: ");
            printSudoku(sudoku);
        }
        catch(FileNotFoundException ex) {
            System.out.println("Could not find the file with specified name.");
        }
    }

    //Load the 2D grid of the Sudoku from a file.
    private static int[][] loadSudoku(File file, int size) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        int count = 0;

        int[][] grid = new int[size*size][size*size];

        while(scanner.hasNextLine() && count < 9) {
            String line = scanner.nextLine();
            char[] characters = line.toCharArray();

            for(int i = 0; i < 9; i++) {
                grid[count][i] = Integer.parseInt(String.valueOf(characters[i]));
            }

            count++;
        }

        return grid;
    }

    //Method that prints the Sudoku in a grid form to the console.
    private static void printSudoku(Sudoku s) {
        int[][] grid = s.getGrid();
        int row = 0;
        for(int i = 0; i < 11; i++) {
            if(i == 3 || i == 7) {
                System.out.println("---------------------");
            }
            else {
                int[] part1 = getSection(grid, row, 0, 2);
                int[] part2 = getSection(grid, row, 3, 5);
                int[] part3 = getSection(grid, row, 6, 8);

                String part1Str = part1[0] + " " + part1[1] + " " + part1[2];
                String part2Str = part2[0] + " " + part2[1] + " " + part2[2];
                String part3Str = part3[0] + " " + part3[1] + " " + part3[2];

                String result = part1Str + " | " + part2Str + " | " + part3Str;

                System.out.println(result);

                row++;
            }
        }
    }

    //Given the grid, returns a particular section of a particular row.
    private static int[] getSection(int[][] grid, int row, int startIndex, int endIndex) {
        int[] section = new int[endIndex - startIndex + 1];

        int index = 0;
        for(int i = startIndex; i < endIndex + 1; i++) {
            section[index] = grid[row][i];
            index++;
        }

        return section;
    }
}
