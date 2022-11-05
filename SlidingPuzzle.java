import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class SlidingPuzzle implements ActionListener {

    //Board Dimension
    private static final int DIM = 3;

    //Number of cells in the board
    private static final int SIZE = DIM * DIM;

    //String array of ordered numbers corresponding to a win
    final String[] WIN = new String[SIZE-1];

    //Height of the board
    private static final int HEIGHT = 300;

    //Width of the board
    private static final int WIDTH = 300;

    //Set the last cell to be the initially empty cell
    private int emptyCell = DIM * DIM;

    //Puzzle Board of size (3 X 3)
    private JButton[][] board = new JButton[DIM][DIM];
    private JFrame frame;
    private JPanel panel = new JPanel();

    public SlidingPuzzle() {

        // Initialize the win array in order
        for (int i = 1; i < SIZE; i++) {
            WIN[i-1] = Integer.toString(i);
        }
        
        System.out.println("Win State:" + Arrays.asList(WIN) );
    }

    public static void main(String[] args) {
        SlidingPuzzle game = new SlidingPuzzle();
        game.initializeBoard();

    }

   
    //i for row
    //j for column
    
    private int getIndex(int i, int j) {       
        return ((i * DIM) + j);  // i * 3 + j        

    }

    
    /*Generates the random initial state for the game
    and assigns a unique random number to each square */
     
    private void initializeBoard() {
        ArrayList<Integer> initialList = new ArrayList<Integer>(SIZE);

        // Repeat till solvable initial board is created
        for (boolean isSolvable = false; isSolvable == false;) {

            // create ordered list
            initialList = new ArrayList<Integer>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                initialList.add(i, i);
            }

            // Shuffle the list
            Collections.shuffle(initialList);

            //Check whether the list is solvable
            isSolvable = isSolvable(initialList);
        }
        System.out.println("Initial Board state:" + initialList);

        // Assigns unique random number to each square        
        for (int index = 0; index < SIZE; index++) {
            final int ROW = index / DIM;  // row number from index
            final int COL = index % DIM;   // column number from index 
            board[ROW][COL] = new JButton(String.valueOf(initialList.get(index)));
            // Initializes the empty square and hide it
            if (initialList.get(index) == 0) {
                emptyCell = index;
                board[ROW][COL].setVisible(false);
            }

            // Decorating each square
            board[ROW][COL].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            board[ROW][COL].setBackground(Color.BLACK);
            board[ROW][COL].setForeground(Color.GREEN);
            board[ROW][COL].addActionListener(this);
            panel.add(board[ROW][COL]);
        }

        // Initializes the Frame
        frame = new JFrame("Shuffle Game");
        frame.setLocation(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(HEIGHT, WIDTH);

        // Initializes the panel
        panel.setLayout(new GridLayout(DIM, DIM));
        panel.setBackground(Color.GRAY);

        // Initializes the content pane
        java.awt.Container content = frame.getContentPane();
        content.add(panel, BorderLayout.CENTER);
        content.setBackground(Color.GRAY);
        frame.setVisible(true);
    }

    /**
     * Verifies the board for solvability as laid out at
     * http://mathworld.wolfram.com/15Puzzle.html 
     * @param list, 9 elements from 0-8, with no repeated elements
     * @return true, if the initial board can be solved
     * false, if the initial board is not solvable
     */
    private boolean isSolvable(ArrayList<Integer> list) {
    	
    	if(list.size() != 9)
    	{
    		System.err.println("isSolvable function works only" +
    			    "with a list having 0-9 as values");
    	}
    	
        int inversionSum = 0;  // If this sum is even it is solvable
        for (int i = 0; i < list.size(); i++) {
            // For empty square add row number to inversionSum                
            if (list.get(i) == 0) {
                inversionSum += ((i / DIM) + 1);  //add Row number
                continue;
            }

            int count = 0;
            for (int j = i + 1; j < list.size(); j++) {
                // No need need to count for empty square
                if (list.get(j) == 0) {
                    continue;
                } else if (list.get(i) > list.get(j)) { // If any element is greater 
                    count++;                            // than seed increase the 
                }                                       // inversionSum                    
            }
            inversionSum += count;
        }

        // if inversionSum is even return true, otherwise false
        return ((inversionSum % 2) == 0) ? true : false;
    }

    /**
     * If any button in the board is pressed, it will perform the
     * required actions associated with the button. Actions like
     * checking isAdjacent(), swapping using swapWithEmpty() and also
     * checks to see whether the game is finished or not.
     * 
     * @param event, event performed by the player
     * @throws IllegalArgumentException, if the index = -1 
     */
    public void actionPerformed(ActionEvent event) throws IllegalArgumentException {
        JButton buttonPressed = (JButton) event.getSource();
        int index = indexOf(buttonPressed.getText());
        if (index == -1) {
            throw (new IllegalArgumentException("Index should be between 0-8"));
        }
        int row = index / DIM;
        int column = index % DIM;

        // If pressed button in same row or same column
        makeMove(row, column);

        // If the game is finished, "You Win the Game" dialog will appear
        if (isFinished()) {
            JOptionPane.showMessageDialog(null, "You Win The Game.");
        }
    }

    /**
     * Gives the index by processing the text on square
     * @param cellNum, number on the button
     * @return the index of the button
     */
    private int indexOf(String cellNum) {

        for (int ROW = 0; ROW < board.length; ROW++) {
            for (int COL = 0; COL < board[ROW].length; COL++) {
                if (board[ROW][COL].getText().equals(cellNum)) {
                    return (getIndex(ROW, COL));
                }
            }
        }
        return -1;   // Wrong input returns -1

    }

    /**
     * Checks the row or column with empty square
     * @return true, if we pressed the button in same row or column 
     *              as empty square
     *         false, otherwise
     */
    private boolean makeMove(int row, int col) {
        final int emptyRow = emptyCell / DIM;  // Empty cell row number
        final int emptyCol = emptyCell % DIM;   // Empty cell column number
        int rowDiff = emptyRow - row;
        int colDiff = emptyCol - col;
        boolean isInRow = (row == emptyRow);
        boolean isInCol = (col == emptyCol);
        boolean isNotDiagonal = (isInRow || isInCol);

        if (isNotDiagonal) {
            int diff = Math.abs(colDiff);
    
            // -ve diff, move row left
            if (colDiff < 0 & isInRow) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow][emptyCol + i].setText(
                            board[emptyRow][emptyCol + (i + 1)].getText());
                }

            } // + ve Diff, move row right
            else if (colDiff > 0 & isInRow) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow][emptyCol - i].setText(
                            board[emptyRow][emptyCol - (i + 1)].getText());
                }
            }

            diff = Math.abs(rowDiff);

            // -ve diff, move column up
            if (rowDiff < 0 & isInCol) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow + i][emptyCol].setText(
                            board[emptyRow + (i + 1)][emptyCol].getText());
                }

            } // + ve Diff, move column down
            else if (rowDiff > 0 & isInCol) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow - i][emptyCol].setText(
                            board[emptyRow - (i + 1)][emptyCol].getText());
                }
            }

            // Swap the empty square with the given square
            board[emptyRow][emptyCol].setVisible(true);
            board[row][col].setText(Integer.toString(0));
            board[row][col].setVisible(false);
            emptyCell = getIndex(row, col);
        }

        return true;
    }

    /**
     * Checks where game is finished or not
     * @return true, if the board is in final state
     *         false, if the board is not in final state 
     */
    private boolean isFinished() {
        // Check 1-15 elements whether they are in right position or not
        for (int index = WIN.length - 1; index >= 0; index--) {
            String number = board[index / DIM][index % DIM].getText();           
            if (!number.equals(WIN[index])) {
                return false;       // If any of the index is not aligned 

            }
        }
        return true;
    }
}