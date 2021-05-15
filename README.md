# How to use
The Sudoku Class is the heart of the program. It takes a 2D array of integers and solves the sudoku. The solution is stored in the <b>grid</b> instance variable field.
The Main Class contains the main() method which runs the program. To change which sudoku is to be solved you could enter the full path of the file in the <b>filename</b> variable.
The method will print the sudoku before and after the solve. It will also display the time taken by the algorithm to solve the sudoku in nano seconds.

# Sudoku Solving
This program uses the fact that bitwise/mathematical operations are way faster than operations of data structures (arrays, ArrayList, etc..).
To use the primitive types to their full extent, we benefit from the fact that an integers is composed of 32 bits to store information in the number. (Bitfields essentially).

Example:

For each row of the Sudoku the Sudoku class stores an int that describes which numbers the row contains. We only use the last 9 bits to store this infromation.

-> 110010010 signifies that the numbers 2, 5, 8 and 9 can be found on this particular row.

When a number is added to this row, we update this bitfield to reflect this change using the OR bitwise operation. Let's say we added the number 4 to the row. The following operation is performed:

-> 110010010 | 000001000 = 110011010 is the number that now represents the changed row.

Checking if a particular row contains a certain number is also done using bitwise operations.

-> 110011010 & 000001000 = 000001000 > 0. This means that number 4 is already in the row we are checking.

## 1st Step: initialFields() method:
This is the first method that is ran by the program when trying to solve the sudoku. It iterates through the sudoku and memorizes which numbers each row, column and box contains. This process is extremely important
as these fields will be constantly updated when testing values for the sudoku to make sure that we are bot breaching any sudoku rule.

## 2nd Step: solveOneSolution() method:
This is the method that solves the sudoku. There are some sudoku that can be solved using multiple ways. The method searches for the first solution it finds and then returns it. It does not check for additional solutions.
The sudoku is solved using a backtracking method meaning that it apply a value for an empty cell and see if it will lead to an eventual solution. If not it backtracks and changes the value of the initial cell.
This means that this method is calls recursively by itself a big number of times.

A pseudo-code of this method is as follow:
```
initializeNextChoices()
nextCell <- determineNextCell().

if nextCell null:
  return True #A Solution has been found

else:
  for each number from 1 to 9:
    cell = num
    
    update fields that store information
    
    if(solveOneSolution()) # Solution has been found
      return true
      
    cell = 0
    remove the update performed on the fields.
```



### Important Method: initializeChoices():
This is a method that is called by the solveOneSolution() at each iteration of the backtracking algorithm. This method basically computed the number of choices that are available 
for each cell. This computation is also performed using bitwise operations by applynig the OR operator to the numbers that stores information for the row, column and box of the cell.
As we only care about the 9 last digits meaning that every digit of these that is not occupied is a potential choice. (Counting the number of 0 in the previously mentioned number).
When performing this computation, it stores the cell with the lowest number of possibilities so that it be used in the backtracking algorithm leading to a lower number of calls.

### Important Method: determineNextCell():
This method determines how fast the algorithm runs. By choosing cells strategically, instead of choosing them from left to right, top to bottom, we can significantly decrease 
the time that it takes for the algorithm to run. 

The method first checks if there is what I call a single candidate on the grid meaning it is a number in each box that can only be placed in one cell. This is one of the few times 
where the choice is certain meaning it cuts the time needed significantly if multiple numbers are found. No need to backtrack on these numbers.
If no such number is found, then the next cell selected is the one found in the initializeChoice() method, the cell that has the lowest number of numbers to choose from.

# File Format
The following is an example of a file that contains a sudoku to solve:
```
400207050
003100000
029000004
000000042
270604018
640000000
100000280
000001400
080506001
```

The file to read should be a .txt file ,and the zeros represent an empty cell.
