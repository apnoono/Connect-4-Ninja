import java.util.Random;

public class MyAgent extends Agent
{
    private Random r;
    private static int centerColumnIndex;
    private static int totalRows;
    private static int totalColumns;
    private static final int SEARCH_TARGET = 3;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     * 
     * @param game The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public MyAgent(Connect4Game game, boolean iAmRed)
    {
        super(game, iAmRed);
        r = new Random();
        centerColumnIndex = myGame.getColumnCount() / 2;
        totalRows = myGame.getRowCount();
        totalColumns = myGame.getColumnCount();
    }

    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * 
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * 
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if:
     * - No token was place into the game.
     * - More than one token was placed into the game.
     * - A previous token was removed from the game.
     * - The color of a previous token was changed.
     * - There are empty spaces below where the token was placed.
     * 
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     * 
     */
    public void move()
    {  
        int nextMove = this.canWin(iAmRed);
        if (nextMove == -1)
        {
            nextMove = this.canWin(!iAmRed);
        }
        if (nextMove > -1)
        {
            this.moveOnColumn(nextMove);
        }
        
        else
        {   
            if (getLowestEmptyIndex(myGame.getColumn(centerColumnIndex)) > 2)
            {
                this.moveOnColumn(centerColumnIndex);
            }
            else if (!getSlot(myGame.getRowCount() -1, centerColumnIndex + 1).getIsFilled())
            {
                this.moveOnColumn(centerColumnIndex + 1);
            }
            else if (!getSlot(myGame.getRowCount() -1, centerColumnIndex - 1).getIsFilled())
            {
                this.moveOnColumn(centerColumnIndex - 1);
            }
            else if (!getSlot(myGame.getRowCount() - 2, centerColumnIndex + 1).getIsFilled() && getLowestEmptyIndex(myGame.getColumn(centerColumnIndex + 1)) == totalRows - 2)
            {
                this.moveOnColumn(centerColumnIndex + 1);
            }
            else if (!getSlot(myGame.getRowCount() - 2, centerColumnIndex - 1).getIsFilled() && getLowestEmptyIndex(myGame.getColumn(centerColumnIndex - 1)) == totalRows - 2)
            {
                this.moveOnColumn(centerColumnIndex - 1);
            }
            else
            {
                this.moveOnColumn(randomMove());
            }
        }
    }
   
    /**
     * Returns the column that would allow a winning move
     * 
     * @return the index of the winning column
     */
    public int canWin(boolean isRed)
    {
        int playColumn = vertCheck(isRed); //check vertically for wins
        if (playColumn == -1)
        {
            playColumn = horizCheck(isRed); // check horizontally for wins
        }
        if (playColumn == -1)
        {
            playColumn = diagCheck(isRed); // check diagonally for wins
        }
        return playColumn;
    }

    
    /**
     * Returns index of a vertical Connect Four
     * 
     * @return index, a column index value if player can land a winning move by vertical Connect Four; -1 otherwise
     */
    public int vertCheck(boolean isRed)
    {
        int index = -1;
        for (int i = 0; i < totalColumns; i++)
        {
            Connect4Column column = myGame.getColumn(i);
            //if column is not full, but contains at least three filled slots, check column
            if (!column.getIsFull() && getLowestEmptyIndex(column) < column.getRowCount() - 3)
            {
                int highestFilledIndex = getLowestEmptyIndex(column) + 1;
        
                //This column is a win for the red player if isRed is true,
                //and a win for yellow player if isRed is false *thanks lwebb https://discussions.udacity.com/t/final-project-review/27596*
                if (column.getSlot(highestFilledIndex).getIsRed() == isRed
                    && column.getSlot(highestFilledIndex + 1).getIsRed() == isRed
                    && column.getSlot(highestFilledIndex + 2).getIsRed() == isRed)
                {
                    index = i;
                }
            }
        }
        return index;
    }
    
    /**
     * Returns index of a horizontal Connect Four
     * 
     * @return index, a column index value if player can land a winning move by horizontal Connect Four; -1 otherwise
     */
    public int horizCheck(boolean isRed)
    {
        int count = 0;
        int index = -1;
        int i = 0;
        int j = 0;
        Connect4Slot currentSlot = null;
        
        for (i = totalRows - 1; i >= 0; i--)
        {
            for (j = 0; j < totalColumns; j++)
            {
                currentSlot = getSlot(i, j);
                //slot has target color
                if (currentSlot.getIsFilled() && currentSlot.getIsRed() == isRed) 
                {
                    count++;
                    //threshold reached, start checking for wins
                    if (count == SEARCH_TARGET)
                    {
                        // ooo?
                        if (getSlot(i, j + 1) instanceof Connect4Slot
                        && !getSlot(i, j + 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + 1)) == i
                        && getSlot(i, j - 1).getIsRed() == isRed
                        && getSlot(i, j - 2).getIsRed() == isRed)
                        {
                            index = j + 1;
                        }
                        
                        // oo?o
                        else if (!getSlot(i, j - 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 1)) == i
                        && getSlot(i, j - 2).getIsRed() == isRed
                        && getSlot(i, j - 3).getIsRed() == isRed)
                        {
                            index = j - 1;
                        }
                        // o?oo
                        else if (getSlot(i, j - 1).getIsRed() == isRed
                        &&!getSlot(i, j - 2).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 2)) == i
                        && getSlot(i, j - 3).getIsRed() == isRed)
                        {
                            index = j - 2;
                        }                        
                        //?ooo
                        else if (getSlot(i, j - 1).getIsRed() == isRed
                        && getSlot(i, j - 2).getIsRed() == isRed
                        && getSlot(i, j - 3) instanceof Connect4Slot
                        && !getSlot(i, j - 3).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 3)) == i)
                        {
                            index = j - 3;
                        }
                        //no winning sequence, decrement count    
                        else
                        {
                            count--;
                        }
                    }
                }
                //slot filled with non-target color or is empty
                else
                {
                    //slot is non-target
                    if (currentSlot.getIsFilled() && currentSlot.getIsRed() == !isRed)
                    {
                        count = 0;
                    }
                }
            }
            //reset count for next row
            count = 0;
        }
        return index;
    }
    
    
    
    /**
     * Returns index of the column which can Connect4 diagonally
     * 
     * @return index, a column index value if player can land a winning move by diagonal Connect Four; -1 otherwise
     */
    public int diagCheck(boolean isRed)
    {
        int index = negativeDiagCheck(isRed);
        if (index == -1)
        {
            index = positiveDiagCheck(isRed);
        }
        return index;
    }
    
    
    
    
    // NEGATIVE DIAGONAL
    
    
    
    
    /**
     * Returns the column index which can Connect4 a negative sloping diagonal
     * 
     * @return index, an int representing the column index to play
     */
    public int negativeDiagCheck(boolean isRed)
    {
        int index = lesserNegDiagCheck(isRed);
        if (index == -1)
        {
            index = greaterNegDiagCheck(isRed);
        }
        return index;
    }
    
    /**
     * Returns the column index which can Connect4 a subset of negative diagonals (scanning top to bottom, left to right)
     * 
     * @return index, the index of a column, which can win one of four possible negative diagonals
     */
    public int greaterNegDiagCheck(boolean isRed)
    {
        int count = 0;
        int index = -1;
        int i = 0;
        Connect4Slot currentSlot = null;
        
        for (int j = 0; j < totalColumns - SEARCH_TARGET; j++)
        {
            currentSlot = getSlot(i, j + i);
            while (currentSlot instanceof Connect4Slot)
            {
                //slot has target color
                if (currentSlot.getIsFilled() && currentSlot.getIsRed() == isRed)
                {
                    count++;                    
                    //threshold reached, start checking for wins
                    if (count == SEARCH_TARGET)
                    {
                        // ooo?
                        if (getSlot(i + 1, j + i + 1) instanceof Connect4Slot
                        && !getSlot(i + 1, j + i + 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + i + 1)) == i + 1
                        && getSlot(i - 1, j + i - 1).getIsRed() == isRed
                        && getSlot(i - 2, j + i - 2).getIsRed() == isRed)
                        {
                            index = j + i + 1;
                        }
                    
                        // oo?o
                        else if (!getSlot(i - 1, j + i - 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + i - 1)) == i - 1
                        && getSlot(i - 2, j + i - 2).getIsRed() == isRed
                        && getSlot(i - 3, j + i - 3).getIsRed() == isRed)
                        {
                            index = j + i - 1;
                        }
                        // o?oo
                        else if (getSlot(i - 1, j + i - 1).getIsRed() == isRed
                        && !getSlot(i - 2, j + i - 2).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + i - 2)) == i - 2
                        && getSlot(i - 3, j + i - 3).getIsRed() == isRed)
                        {
                            index = j + i - 2;
                        }
                        //?ooo
                        else if (getSlot(i - 1, j + i - 1).getIsRed() == isRed
                        && getSlot(i - 2, j + i - 2).getIsRed() == isRed
                        && getSlot(i - 3, j + i - 3) instanceof Connect4Slot
                        && !getSlot(i - 3, j + i - 3).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + i - 3)) == i - 3)
                        {
                            index = j + i - 3;
                        }
                        //no winning sequence, decrement count
                        else
                        {
                            count--;
                        }
                    }
                }
                //slot filled with non-target color or is empty
                else
                {
                    //slot is non-target
                    if (currentSlot.getIsFilled() && currentSlot.getIsRed() == !isRed)
                    {
                        count = 0;
                    }
                }
                i++;
                currentSlot = getSlot(i, j + i);
            }
            //reset index and count for next diagonal
            i = 0;
            count = 0;
        }
        return index;
    }

    /**
     * Returns the column index which can Connect4 a subset of negative diagonals (scanning top to bottom, left to right)
     * 
     * @return index, the index of a column, which can win one of the remaining negative diagonals
     */
    public int lesserNegDiagCheck(boolean isRed)
    {
        int count = 0;
        int index = -1;
        int j = 0;
        Connect4Slot currentSlot = null;
        
        for (int i = 0; i < totalRows - SEARCH_TARGET; i++)
        {
            currentSlot = getSlot(i + j, j);
            while (currentSlot instanceof Connect4Slot)
            {
                //slot has target color
                if (currentSlot.getIsFilled() && currentSlot.getIsRed() == isRed)
                {
                    count++;                    
                    //threshold reached, start checking for wins
                    if (count == SEARCH_TARGET)
                    {
                        // ooo?
                        if (getSlot(i + j + 1, j + 1) instanceof Connect4Slot
                        && !getSlot(i + j + 1, j + 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + 1)) == i + j + 1
                        && getSlot(i + j- 1, j - 1).getIsRed() == isRed
                        && getSlot(i + j - 2, j - 2).getIsRed() == isRed)
                        {
                            index = j + 1;
                        }
                        
                        // oo?o
                        else if (!getSlot(i + j - 1, j - 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 1)) == i + j - 1
                        && getSlot(i + j - 2, j - 2).getIsRed() == isRed
                        && getSlot(i + j - 3, j - 3).getIsRed() == isRed)
                        {
                            index = j - 1;
                        }
                        // o?oo
                        else if (getSlot(i + j - 1, j - 1).getIsRed() == isRed
                        && !getSlot(i + j - 2, j - 2).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 2)) == i + j - 2
                        && getSlot(i + j - 3, j - 3).getIsRed() == isRed)
                        {
                            index = j - 2;
                        }
                        //?ooo
                        else if (getSlot(i + j - 1, j - 1).getIsRed() == isRed
                        && getSlot(i + j - 2, j - 2).getIsRed() == isRed
                        && getSlot(i + j - 3, j - 3) instanceof Connect4Slot
                        && !getSlot(i + j - 3, j - 3).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 3)) == i + j - 3)
                        {
                            index = j - 3;
                        }
                        //no winning sequence, decrement count
                        else
                        {
                            count--;
                        }
                    }
                }
                //slot filled with non-target color or is empty
                else
                {
                    //slot is non-target
                    if (currentSlot.getIsFilled() && currentSlot.getIsRed() == !isRed)
                    {
                        count = 0;
                    }
                }
                j++;
                currentSlot = getSlot(i + j, j);
            }
            //reset index and count for next diagonal
            j = 0;
            count = 0;
        }
        return index;
    }
    
 
    // POSITIVE DIAGONAL
    
         
    
    /**
     * Returns the column index which can Connect4 a positive sloping diagonal
     * 
     * @return index, an int representing the column index to play
     */
    public int positiveDiagCheck(boolean isRed)
    {
        int index = lesserPosDiagCheck(isRed);
        if (index == -1)
        {
            index = greaterPosDiagCheck(isRed);
        }
        return index;
    }
    
    /**
     * Returns the column index which can Connect4 a subset of positive diagonals (scanning top to bottom, right to left)
     * 
     * @return index, the index of a column, which can win one of four possible positive diagonals
     */
    public int greaterPosDiagCheck(boolean isRed)
    {
        int count = 0;
        int index = -1;
        int i = 0;
        Connect4Slot currentSlot = null;
        
        for (int j = totalColumns - 1; j >= SEARCH_TARGET; j--)
        {
            currentSlot = getSlot(i, j - i);
            while (currentSlot instanceof Connect4Slot)
            {
                //slot has target color
                if (currentSlot.getIsFilled() && currentSlot.getIsRed() == isRed)
                {
                    count++;                    
                    //threshold reached, start checking for wins
                    if (count == SEARCH_TARGET)
                    {
                        // ?ooo
                        if (getSlot(i + 1, j - i - 1) instanceof Connect4Slot
                        && !getSlot(i + 1, j - i - 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - i - 1)) == i + 1
                        && getSlot(i - 1, j - i + 1).getIsRed() == isRed
                        && getSlot(i - 2, j - i + 2).getIsRed() == isRed)
                        {
                            index = j - i - 1;
                        }
                    
                        // o?oo
                        else if (!getSlot(i - 1, j - i + 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - i + 1)) == i - 1
                        && getSlot(i - 2, j - i + 2).getIsRed() == isRed
                        && getSlot(i - 3, j - i + 3).getIsRed() == isRed)
                        {
                            index = j - i + 1;
                        }
                        // oo?o
                        else if (getSlot(i - 1, j - i + 1).getIsRed() == isRed
                        && !getSlot(i - 2, j - i + 2).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - i + 2)) == i - 2
                        && getSlot(i - 3, j - i + 3).getIsRed() == isRed)
                        {
                            index = j - i + 2;
                        }
                        //ooo?
                        else if (getSlot(i - 1, j - i + 1).getIsRed() == isRed
                        && getSlot(i - 2, j - i + 2).getIsRed() == isRed
                        && getSlot(i - 3, j - i + 3) instanceof Connect4Slot
                        && !getSlot(i - 3, j - i + 3).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - i + 3)) == i - 3)
                        {
                            index = j - i + 3;
                        }
                        //no winning sequence, decrement count
                        else
                        {
                            count--;
                        }
                    }
                }
                //slot filled with non-target color or is empty
                else
                {
                    //slot is non-target
                    if (currentSlot.getIsFilled() && currentSlot.getIsRed() == !isRed)
                    {
                        count = 0;
                    }
                }
                i++;
                currentSlot = getSlot(i, j - i);
            }
            //reset index and count for next diagonal
            i = 0;
            count = 0;
        }
        return index;
    }
    
    /**
     * Returns the column index which can Connect4 a subset of positive diagonals (scanning top to bottom, right to left)
     * 
     * @return index, the index of a column, which can win one of the remaining possible positive diagonals
     */
    public int lesserPosDiagCheck(boolean isRed)
    {
        int count = 0;
        int index = -1;
        int j = totalColumns - 1;
        Connect4Slot currentSlot = null;
        
        for (int i = totalRows; i < totalRows + SEARCH_TARGET; i++)
        {
            currentSlot = getSlot(i - j, j);
            while (currentSlot instanceof Connect4Slot)
            {
                //slot has target color
                if (currentSlot.getIsFilled() && currentSlot.getIsRed() == isRed)
                {
                    count++;                    
                    //threshold reached, start checking for wins
                    if (count == SEARCH_TARGET)
                    {
                        // ?ooo
                        if (getSlot(i - j + 1, j - 1) instanceof Connect4Slot
                        && !getSlot(i - j + 1, j - 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j - 1)) == i - j + 1
                        && getSlot(i - j - 1, j + 1).getIsRed() == isRed
                        && getSlot(i - j - 2, j + 2).getIsRed() == isRed)
                        {
                            index = j - 1;
                        }
                    
                        // o?oo
                        else if (!getSlot(i - j - 1, j + 1).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + 1)) == i - j - 1
                        && getSlot(i - j - 2, j + 2).getIsRed() == isRed
                        && getSlot(i - j - 3, j + 3).getIsRed() == isRed)
                        {
                            index = j + 1;
                        }
                        // oo?o
                        else if (getSlot(i - j - 1, j + 1).getIsRed() == isRed
                        && !getSlot(i - j - 2, j + 2).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + 2)) == i - j - 2
                        && getSlot(i - j - 3, j + 3).getIsRed() == isRed)
                        {
                            index = j + 2;
                        }
                        //ooo?
                        else if (getSlot(i - j - 1, j + 1).getIsRed() == isRed
                        && getSlot(i - j - 2, j + 2).getIsRed() == isRed
                        && getSlot(i - j - 3, j + 3) instanceof Connect4Slot
                        && !getSlot(i - j - 3, j + 3).getIsFilled()
                        && getLowestEmptyIndex(myGame.getColumn(j + 3)) == i - j - 3)
                        {
                            index = j + 3;
                        }
                        //no winning sequence, decrement count
                        else
                        {
                            count--;
                        }
                    }
                }
                //slot filled with non-target color or is empty
                else
                {
                    //slot is non-target
                    if (currentSlot.getIsFilled() && currentSlot.getIsRed() == !isRed)
                    {
                        count = 0;
                    }
                }
                j--;
                currentSlot = getSlot(i - j, j);
            }
            //reset index and count for next diagonal
            j = totalColumns - 1;
            count = 0;
        }
        return index;
    }
            
    /**
     * Returns a Connect4Slot of row x column y
     * 
     * @param x - the row index
     * @param y - the column index
     * @return a Connect4Slot object
     */
    public Connect4Slot getSlot(int x, int y)
    {
        if (myGame.getColumn(y) instanceof Connect4Column && myGame.getColumn(y).getSlot(x) instanceof Connect4Slot)
        {
            return myGame.getColumn(y).getSlot(x);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns the index of the top empty slot in a particular column.
     * 
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for  (int i = 0; i < column.getRowCount(); i++)
        {
            if (!column.getSlot(i).getIsFilled())
            {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     * 
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber)
    {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
                                                                                                  // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            }
            else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }
    
    /**
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     * 
     * @return a random valid move.
     */
    public int randomMove()
    {
        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1)
        {
            i = r.nextInt(myGame.getColumnCount());
        }
        return i;
    }

    /**
     * Returns the column that would allow the agent to win.
     * 
     * You might want your agent to check to see if it has a winning move available to it so that
     * it can go ahead and make that move. Implement this method to return what column would
     * allow the agent to win.
     *
     * @return the column that would allow the agent to win.
     */
    public int iCanWin()
    {
        return -1;
    }


    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "Andrew";
    }
}