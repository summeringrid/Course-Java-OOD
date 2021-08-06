package cs5004.marblesolitaire.model;

/**
 * MarbleSolitaireModelImpl is the implementation of MarbleSolitaireModel.
 * It designs a suitable representation of the Marble Solitaire game.
 *
 * @author 'Ingrid' Xiaoying Liu
 * @version 2.0
 * @since 7-31-2021
 *
 * Requirement: Think carefully about what fields and types you will need
 * and how possible values of the fields correspond to game states.
 *
 */
public class MarbleSolitaireModelImpl implements MarbleSolitaireModel {
    /**
     * Define the armThink of the game board, and the coordinate (sRow, sCol) of the marble's position.
     */
    public int armThick;
    public int sRow;
    public int sCol;
    public int size;
    public Marbles centerCell;
    public Marbles[][] board;

    /**
     * This 1st constructor takes no parameters, and initialize the game board with arm thickness = 3
     * and empty slot at the center.
     */
    public MarbleSolitaireModelImpl() {
        this(3,3,3);
    }

    /**
     * This 2nd constructor takes two parameters: sRow and sCol, and it initialize the game board
     * with the arm thickness = 3 and the empty slot is at the position (sRow, sCol).
     * @param sRow an integer of the center's row
     * @param sCol an integer of the center's column
     * @throws IllegalArgumentException when specified an invalid position of the center
     */
    public MarbleSolitaireModelImpl(int sRow, int sCol) throws IllegalArgumentException {
        this(3, sRow, sCol);
        if (!(this.onBoard(sRow, sCol))) {
            throw new IllegalArgumentException("Invalid empty cell position (" + sRow + ", " + sCol + ")");
        }
    }

    /**
     * This 3rd constructor takes the arm thickness as its only parameter and initialize a game board
     * by setting the empty slot at the center.
     * @param armThick a positive odd number
     * @throws IllegalArgumentException when arm thickness is not a positive odd number.
     */
    public MarbleSolitaireModelImpl(int armThick) throws IllegalArgumentException{
        this(armThick, armThick, armThick);
        if (armThick <= 0 || armThick % 2 == 0) {
            throw new IllegalArgumentException("arm thickness is not a positive odd number:" + armThick);
        }
    }

    /**
     * This 4th constructor takes the arm thickness, row and column of the empty slot in that order.
     * @param armThick a positive odd number
     * @param sRow an integer of the center's row
     * @param sCol an integer of the center's column
     * @throws IllegalArgumentException when arm thickness is not a positive odd number, or empty cell is invalid.
     */
    public MarbleSolitaireModelImpl(int armThick, int sRow, int sCol) throws IllegalArgumentException{
        this.armThick = armThick;
        this.sRow = sRow;
        this.sCol = sCol;

        this.size = (armThick * 2) + 1;
        generateBoard(sRow, sCol);

        if (armThick <= 0 || armThick % 2 == 0) {
            throw new IllegalArgumentException("arm thickness is not a positive odd number:" + armThick);
        }
        if (!(this.onBoard(sRow, sCol))) {
            throw new IllegalArgumentException("Invalid empty cell position (" + sRow + ", " + sCol + ")");
        }
    }

    /**
     * Move a single marble from a given position to another given position.
     * @param fromRow the row number of the position to be moved from
     *                (starts at 0)
     * @param fromCol the column number of the position to be moved from
     *                (starts at 0)
     * @param toRow the row number of the position to be moved to
     *              (starts at 0)
     * @param toCol the column number of the position to be moved to
     *              (starts at 0)
     * @throws IllegalArgumentException if any invalid move is generated
     */
    @Override
    public void move(int fromRow, int fromCol, int toRow, int toCol) throws IllegalArgumentException {
        /**
         * A move is valid if all these conditions are true:
         * (a) the “from” and “to” positions are valid
         * (b) there is a marble at the specified “from” position
         * (c) the “to” position is empty
         * (d) the “to” and “from” positions are exactly two positions away (horizontally or vertically)
         * (e) there is a marble in the slot between the “to” and “from” positions.
         */
        if (validMove(fromRow, fromCol, toRow, toCol)) {
            this.board[fromRow][fromCol].gone = true;
            centerCell.gone = true;
            this.board[toRow][toCol].gone = false;
        } else {
            throw new IllegalArgumentException("Move must be valid");
        }
    }

    /**
     * Determine and return if the game is over or not, based on if there's more moves can be made.
     * @return true if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver() {
        for (int row = 0; row < this.board.length - 1; row++) {
            for (int col = 0; col  < this.board[row].length - 1; col++) {
                if (row + 2 < size && validMove(row, col, row + 2, col)) {
                    return false;
                } else if ( row - 2 > 0 && validMove(row, col, row - 2, col)) {
                    return false;
                } else if (col + 2 < size && validMove(row, col, row, col + 2)) {
                    return false;
                } else if (col - 2 > 0 && validMove(row, col, row, col - 2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The getGameState method is used to print the game state in the format of characters(' ', 'O' or '_').
     * @return a String
     *
     * Note: The string you return should not have a newline at the end of the last line.
     *      (O, _ or space for a marble, empty and invalid position respectively).
     */
    @Override
    public String getGameState() {
        String initial = "";

        for (int row = 0; row < this.size; row++) {
            boolean cutPlaceHolder = false;
            for (int col = 0; col < this.board[row].length; col++) {
                if (this.board[row][col] == null) {
                    if (!cutPlaceHolder) {
                        initial += "  ";
                    }
                } else {
                    if (col == 0  || this.board[row][col - 1] == null) {
                        initial += this.board[row][col].status();   // first marble on the board in each row
                    } else {
                        initial += " " + this.board[row][col].status();
                    }
                    cutPlaceHolder = true;
                }
            }
            if ((row != this.board.length - 1)) {
                //if ((row != this.size - 1)) {
                initial += "\n";
            }
        }
        return initial;
    }

    /**
     * The getScore method returns the number of marbles currently on the board.
     * @return current score: the number of marbles currently on the board
     */
    @Override
    public int getScore() {
        int count = 0;
        for (int i = 0; i < this.size; i++ ) {
            for (int k = 0; k < this.size; k++) {
                if ((this.board[i][k] != null) && this.board[i][k].status().equals("O")) {
                    count++;
                }
            }
        }
        return count;
    }


    //++++++++++++++++++++++++++++++++++++++++++//
    //++++++++++++++ Helper Methods ++++++++++++//
    //++++++++++++++++++++++++++++++++++++++++++//
    /**
     * Helper method I - generateBoard helps to initialize the game board as a 2D matrix with target size.
     * @param emptyR initialized start center row number
     * @param emptyC initialized start center column number
     */
    protected void generateBoard(int emptyR, int emptyC) {
        board = new Marbles[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
//                if (!isValid(row, col)) {
                if (!onBoard(row, col)) {
                    board[row][col] = null;
                } else board[row][col] = new Marbles();
            }
        }
        this.board[emptyR][emptyC].gone = true;
    }

    /**
     * Helper method II onBoard determines whether the given target position is a valid or not.
     * @param row the y position of the given position.
     * @param col the x position of the given position.
     * @return whether or not the given posn is a valid position on the board.
     */
    protected boolean onBoard(int row, int col) {
        if (!((row < ((armThick + 1)/2)  && col < (armThick + 1)/2) || // top left
                (row < ((armThick + 1)/2)  && col >= ((3*armThick + 1)/2)) || // top right
                (row >= ((3*armThick + 1)/2) && col < ((armThick + 1)/2)) || // bottom left
                (row >= ((3*armThick + 1)/2) && col >= ((3*armThick + 1)/2)) || // bottom right
                row < 0 || row > (this.size-1) || col > (this.size-1) || col < 0)) return true;
        else return false;
    }

    /**
     * The validMove determines whether or not the given move action is valid.
     * I turned it into public method in hw6 to verify the valid input in the controller.
     * @param fromRow the row position of the marble before it is moved.
     * @param fromCol the column position of the marble before it is moved.
     * @param toRow the row position of the marble after it is moved.
     * @param toCol the column position of the marble after it is moved.
     * @return whether or not the given move action is valid.
     */
    public boolean validMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!(onBoard(fromRow, fromCol))
                || !(onBoard(toRow, toCol))
                || (!(!this.board[fromRow][fromCol].gone && this.board[toRow][toCol].gone))
                || (!((Math.abs(toRow - fromRow) == 2 && fromCol == toCol)
                || (Math.abs(toCol - fromCol) == 2 && toRow == fromRow)))) {
            return false;
        } else {
            this.centerCell = this.board[fromRow + (toRow - fromRow) / 2][(fromCol + (toCol - fromCol) / 2)];
            return !(this.centerCell.gone);
        }
    }


    public String toString() {
        if (this.isGameOver()) System.out.print("Game over!");
        // todo check if here is right:
        //else System.out.print("No ^^ Keep going!");
        System.out.println(this.getGameState());
        System.out.println("Score: " + this.getScore());
        return "";
    }

    //++++++++++++++++++++++++++++++++++++++++++//
    //+++++++++ HW6 new added functions ++++++++//
    //++++++++++++++++++++++++++++++++++++++++++//

    /**
     * The isValidFrom helps to determine whether the given from slot is valid or not
     * @param fromRow from position's row
     * @param fromCol from position's col
     * @return true if the slot is valid from position, otherwise return false
     */
    public boolean isValidFrom(int fromRow, int fromCol) {
        return (onBoard(fromRow, fromCol) &&
                hasValidTo(fromRow, fromCol));
    }


    /**
     * Helper function to see if the from position has a valid to position to go.
     * @param fromRow from position's row
     * @param fromCol from position's row
     * @return true if there is a valid to position, otherwise return false
     */
    protected boolean hasValidTo(int fromRow, int fromCol) {
        return (!this.board[fromRow - 1][fromCol].gone) && (this.board[fromRow - 2][fromCol].gone) ||     //up
                (!this.board[fromRow][fromCol + 1].gone) && (this.board[fromRow][fromCol + 2].gone) ||    //right
                (!this.board[fromRow][fromCol - 1].gone) && (this.board[fromRow][fromCol - 2].gone) ||    //left
                (!this.board[fromRow + 1][fromCol].gone) && (this.board[fromRow + 2][fromCol].gone);      //down
    }


    //++++++++++++++++++++++++++++++++++++++++++//
    //++++++++++++++ Getter/Setter +++++++++++++//
    //++++++++++++++++++++++++++++++++++++++++++//
    public int getArmThick() {
        return armThick;
    }

    public void setArmThick(int armThick) {
        this.armThick = armThick;
    }

    public int getsRow() {
        return sRow;
    }

    public void setsRow(int sRow) {
        this.sRow = sRow;
    }

    public int getsCol() {
        return sCol;
    }

    public void setsCol(int sCol) {
        this.sCol = sCol;
    }


    /* Main function for small tests */
    public static void main(String[] args) {
        System.out.println("The main function in MarbleSolitaireModelImpl runs...");

        System.out.println("\n******** Test on the board initialization ********");
        MarbleSolitaireModelImpl modelTest0 = new MarbleSolitaireModelImpl(1);
        System.out.println("When arm thick = 1: ");
        System.out.println(modelTest0.getGameState());

        MarbleSolitaireModelImpl modelTest1 = new MarbleSolitaireModelImpl();
        System.out.println("\nWhen arm thick is default: ");
        System.out.println(modelTest1.getGameState());

        MarbleSolitaireModelImpl modelTest2 = new MarbleSolitaireModelImpl(5);
        System.out.println("\nWhen arm thick is 5: ");
        System.out.println(modelTest2.getGameState());

        MarbleSolitaireModelImpl modelTest3 = new MarbleSolitaireModelImpl(7);
        System.out.println("\nWhen arm thick is 7: ");
        System.out.println(modelTest3.getGameState());

        MarbleSolitaireModelImpl modelTest4 = new MarbleSolitaireModelImpl(9);
        System.out.println("\nWhen arm thick is 9: ");
        System.out.println(modelTest4.getGameState());


        System.out.println("\n\n********* Let's start a game! **************");
        System.out.println(modelTest1);

        modelTest1.move(5,3,3,3);
        modelTest1.move(2,3,4,3);
        modelTest1.move(3,1,3,3);
        modelTest1.move(0,3,2,3);
        modelTest1.move(3,3,5,3);
        System.out.println(modelTest1);
//        System.out.println("6, 3 is valid from?-- " + modelTest1.isValidFrom(6, 3));
//        System.out.println("4, 1 is valid from?-- " + modelTest1.isValidFrom(4, 1));
//        System.out.println("3, 5 is valid from?-- " + modelTest1.isValidFrom(3, 5));
//        System.out.println("1, 2 is valid from?-- " + modelTest1.isValidFrom(1, 2));
//        modelTest1.move(1, 2, 3,2);
//        System.out.println(modelTest1);
//        modelTest1.move(6,3,4,3);
//        System.out.println(modelTest1);

        modelTest1.move(3,5,3,3);
        modelTest1.move(3,3,1,3);
        modelTest1.move(2,1,2,3);
        modelTest1.move(2,4,2,2);
//        System.out.println(modelTest1);
//        modelTest1.move(2,6,2,4);
//        modelTest1.move(4,6,2,6);
//        modelTest1.move(4,4,4,6);
//        modelTest1.move(4,2,4,4);
//        modelTest1.move(6,2,4,2);
//        System.out.println(modelTest1);
//        modelTest1.move(5,4,3,4);
//        modelTest1.move(4,1,4,3);
//        modelTest1.move(1,2,3,2);
//        modelTest1.move(2,4,4,4);
//        modelTest1.move(4,4,4,2);
//        modelTest1.move(3,2,5,2);
//        modelTest1.move(0,4,2,4);
//        System.out.println(modelTest1);

    }
}
