package me.mariobouzakhm.sudokusolver;

public class Sudoku {
    //Size of the Sudoku.
    private int SIZE;

    //Numbers of digits that can fit inside a single box.
    private int N;

    //2D Array that represents the grid of the sudoku.
    private int grid[][];

    //Arrays that contain the information required to solve the sudoku.
    private int[] columnDigits;
    private int[] rowDigits;
    private int[] boxDigits;

    //Number of choices for each cell on the grid.
    private int[][] numberOfChoices;

    //Number of times a digit can be chosen inside a box.
    private int[][] choicesByBox;


    //Stores the next cell with least digits and next cell with brute force.
    private int[] nextPositionLowest;
    private int lowestChoices;

    private int[] nextBruteForce;


    public Sudoku(int size, int[][] grid) {
        this.SIZE = size;
        this.N = size*size;
        this.grid = grid;
    }

    private void initializeFields() {
        columnDigits = new int[this.N];
        rowDigits = new int[this.N];
        boxDigits = new int[this.N];

        this.numberOfChoices = new int[this.N][this.N];

        for(int i = 0; i < this.N; i++) {
            for(int j = 0; j < this.N; j++) {
                int value = this.grid[i][j];
                int box = determineBox(i, j) - 1;
                if(value != 0) {
                    int binaryValue = 1 << (value - 1);
                    rowDigits[i] = rowDigits[i] | binaryValue;
                    columnDigits[j] = columnDigits[j] | binaryValue;
                    boxDigits[box] = boxDigits[box] | binaryValue;
                }
            }
        }
    }

    private void initializeChoices() {
        nextBruteForce = null;
        nextPositionLowest = null;
        lowestChoices = Integer.MAX_VALUE;

        choicesByBox = new int[this.N][this.N];

        for(int i = 0; i < this.N; i++) {
            for (int j = 0; j < this.N; j++) {
                int num = this.grid[i][j];
                int box = determineBox(i, j) - 1;
                if(num == 0) {
                    if(nextBruteForce == null) {
                        nextBruteForce = new int[] {i, j};
                    }

                    int value = (rowDigits[i] | columnDigits[j] | boxDigits[box]);

                    int inverted = ~(value);

                    int choices = countNumberOfOnes(inverted, box);
                    if(nextPositionLowest == null || lowestChoices > choices) {
                        lowestChoices = choices;
                        nextPositionLowest = new int[] {i, j};
                    }

                    this.numberOfChoices[i][j] = choices;
                }
                else {
                    this.numberOfChoices[i][j] = 0;
                }
            }
        }
    }

    public void solve() {
        initializeFields();
        solveOneSolution();
    }

    private boolean solveOneSolution() {
        initializeChoices();
        int[] pos = determineNextCell();
        if(pos == null) {
            return true;
        }

        int row = pos[0];
        int column = pos[1];
        int box = determineBox(row, column) - 1;

        if(pos.length == 3) {
            this.grid[row][column] = pos[2];
            int binaryValue = 1 << (pos[2] - 1);

            rowDigits[row] = rowDigits[row] | binaryValue;
            columnDigits[column] = columnDigits[column] | binaryValue;
            boxDigits[box] = boxDigits[box] | binaryValue;

            if(solveOneSolution()) {
                return true;
            }

            this.grid[row][column] = 0;
            rowDigits[row] = rowDigits[row] & ~(binaryValue);
            columnDigits[column] = columnDigits[column] & ~(binaryValue);
            boxDigits[box] = boxDigits[box] & ~(binaryValue);
        }
        else  {
            for(int i = 1; i < this.N+1; i++) {
                if(!checkConflict(row, column, box, i)) {
                    this.grid[row][column] = i;
                    int binaryValue = 1 << (i - 1);

                    rowDigits[row] = rowDigits[row] | binaryValue;
                    columnDigits[column] = columnDigits[column] | binaryValue;
                    boxDigits[box] = boxDigits[box] | binaryValue;

                    if(solveOneSolution()) {
                        return true;
                    }

                    this.grid[row][column] = 0;
                    rowDigits[row] = rowDigits[row] & ~(binaryValue);
                    columnDigits[column] = columnDigits[column] & ~(binaryValue);
                    boxDigits[box] = boxDigits[box] & ~(binaryValue);
                }
            }
        }


        return false;
    }

    private int[] determineNextCell() {
        int[] unique = determineUniqueCandidate();
        if(unique != null) {
            return unique;
        }

        return determineCellWithLeastPossibilities();
    }

    private int[] determineCellWithLeastPossibilities() {
        if(nextPositionLowest != null) {
            return nextPositionLowest;
        }

        return nextBruteForce;
    }

    private int[] determineUniqueCandidate() {
        for(int i = 0; i < this.N; i++) {
            for(int j = 0; j < this.N; j++) {
                int num = this.choicesByBox[i][j];
                if(num == 1) {
                    int startRow = (i / this.SIZE) * this.SIZE;
                    int endRow = startRow + (this.SIZE - 1);

                    int startColumn = (i % this.SIZE) * this.SIZE;
                    int endColumn = startColumn + (this.SIZE - 1);

                    for(int startIndex = startRow; startIndex < endRow + 1; startIndex++) {
                        for(int startCIndex = startColumn; startCIndex < endColumn + 1; startCIndex++) {
                            if(!checkConflict(startIndex, startCIndex, i, j+1) && this.grid[startIndex][startCIndex] == 0) {
                                return new int[] {startIndex, startCIndex, j+1};
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean checkConflict(int row, int column, int box, int num) {
        if (checkRowConflict(row, num)) {
            return true;
        }
        if (checkColumnConflict(column, num)) {
            return true;
        }

        if (checkBoxConflict(box, num)) {
            return true;
        }

        return false;
    }

    private boolean checkRowConflict(int row, int num) {
        return (rowDigits[row] & 1 <<(num - 1)) != 0;
    }

    private boolean checkColumnConflict(int column, int num) {
        return (columnDigits[column] & 1 << (num - 1)) != 0;
    }

    private boolean checkBoxConflict(int box, int num) {
        return (boxDigits[box] & 1 << (num - 1)) != 0;
    }


    private int countNumberOfOnes(int value, int box) {
        int count = 0;
        for(int i = 1; i <=  this.N; i++) {
            if ((value & (1 << (i - 1))) != 0) {
                choicesByBox[box][i-1]++;
                count++;
            }
        }

        return count;
    }

    private int determineBox(int row, int column) {
        int boxX = row / this.SIZE;
        int boxY = (column / this.SIZE) + 1;

        return boxX * this.SIZE + boxY;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return SIZE;
    }

    public void printCurrentGrid() {

    }
}