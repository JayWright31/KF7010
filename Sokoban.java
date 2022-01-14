
import java.io.*;
import java.util.*;

/**
* A Sokoban puzzle.
* 
* @author Dr Mark C. Sinclair
* @version September 2021
*/
@SuppressWarnings("deprecation")
public class Sokoban extends Observable {
    /**
     * Construct a Sokoban puzzle from a standard Sokoban screen file
     * 
     * @param file the file
     */
    public Sokoban(File file) {
        this(fileAsString(file));
    }

    /**
     * Construct a Sokoban puzzle from a standard Sokoban screen file passed as a String
     * 
     * @param screen the screen file as a String
     */
    public Sokoban(String screen) {
        if (screen == null)
            throw new IllegalArgumentException("screen cannot be null");
        startScreen            = screen;
        Scanner           scnr = null;
        ArrayList<String> lines = new ArrayList<>();
        scnr = new Scanner(screen);
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            if (line.length() > 0) {
                lines.add(line);
                numRows++;
                if (line.length() > numCols)
                    numCols = line.length();
            }
        }
        scnr.close();
        cells = new Cell[numRows][numCols];
        for (int row=0; row<numRows; row++) {
            String line = lines.get(row);
            for (int col=0; col<numCols; col++) {
                char display = (col < line.length()) ? line.charAt(col) : Sokoban.EMPTY;
                cells[row][col] = new Cell(display, this, row, col);
                if (display == ACTOR || display == TARGET_ACTOR)
                    actorCell = cells[row][col];
            }
        }
        checkValid();
    }

    /**
     * Some basic validity checks
     */
    private void checkValid() {
        assert numBoxes() == numTargets() : "number of boxes and targets must be equal";
        assert numActors() == 1 : "must be exactly one actor";
        assert actorCell.hasActor() : "actorCell must be valid";
    }

    /**
     * Reset to the starting state
     */
    public void clear() {
        if (startScreen == null)
            throw new IllegalStateException("startScreen cannot be null");
        Scanner           scnr = null;
        ArrayList<String> lines = new ArrayList<>();
        scnr = new Scanner(startScreen);
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            if (line.length() > 0)
                lines.add(line);
        }
        scnr.close();
        for (int row=0; row<numRows; row++) {
            String line = lines.get(row);
            for (int col=0; col<numCols; col++) {
                char display = (col < line.length()) ? line.charAt(col) : Sokoban.EMPTY;
                if (cells[row][col].getDisplay() != display) {
                    cells[row][col].setDisplay(display);
                    if (display == ACTOR || display == TARGET_ACTOR)
                        actorCell = cells[row][col];
                    trace("clear: changing display in ("+row+","+col+")");
                }
            }
        }
        checkValid();
    }

    /**
     * Gets the number of cell rows
     * 
     * @return the number of cell rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Gets the number of cell columns
     * 
     * @return the number of cell columns
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Get a cell from the Sokoban puzzle
     * 
     * @param row row number (starts from 0)
     * @param col column number (starts from 0)
     * @return the requested cell
     */
    Cell getCell(int row, int col) {
        if ((row < 0) || (row >= numRows) || (col < 0) || (col >= numCols))
            return null;
        return cells[row][col];
    }

    /**
     * Get the actor cell from the Sokoban puzzle
     * 
     * @return the requested cell
     */
    Cell getActorCell() {
        return actorCell;
    }

    /**
     * Count the number of targets in the Sokoban puzzle
     * 
     * @return the number of targets
     */
    public int numTargets() {
        int num = 0;
        for (int row=0; row<numRows; row++)
            for (int col=0; col<numCols; col++)
                if (cells[row][col].isTarget())
                    num++;
        return num;
    }

    /**
     * Count the number of boxes in the Sokoban puzzle
     * 
     * @return the number of boxes
     */
    public int numBoxes() {
        int num = 0;
        for (int row=0; row<numRows; row++)
            for (int col=0; col<numCols; col++)
                if (cells[row][col].hasBox())
                    num++;
        return num;
    }

    /**
     * Count the number of actors in the Sokoban puzzle (should be one)
     * 
     * @return the number of actors
     */
    public int numActors() {
        int num = 0;
        for (int row=0; row<numRows; row++)
            for (int col=0; col<numCols; col++)
                if (cells[row][col].hasActor())
                    num++;
        return num;
    }

    /**
     * Count the number of boxes on target cells in the Sokoban puzzle
     * 
     * @return the number of boxes on target cells
     */
    public int numOnTarget() {
        int num = 0;
        for (int row=0; row<numRows; row++)
            for (int col=0; col<numCols; col++)
                if (cells[row][col].onTarget())
                    num++;
        return num;
    }

    /**
     * Are all the boxes on target in the Sokoban puzzle?
     * 
     * @return are all the boxes on target?
     */
    public boolean onTarget() {
        return numOnTarget() == numTargets();
    }

    /**
     * Checks if the actor can move to the next cell in a given direction
     * 
     * @param dir the direction to check
     * @return can the actor move to the next cell in a given direction?
     */
    public boolean canMove(Direction dir) {
        return actorCell.canMove(dir);
    }

    /**
     * In which directions can the actor move?
     * 
     * @return a vector of available directions
     */
    public Vector<Direction> canMove() {
        Vector<Direction> dirs = new Vector<>();
        for (Direction dir : Direction.values()) {
            if (canMove(dir))
                dirs.add(dir);
        }
        return dirs;
    }

    /**
     * If it is safe, move the actor to the next cell in a given direction
     * 
     * @param dir the direction to move
     */
    public void move(Direction dir) {
        if (!canMove(dir))
            throw new IllegalArgumentException("cannot move "+dir);
        Cell oldActorCell = actorCell;
        actorCell.move(dir);
        actorCell = actorCell.getCell(dir);
        Cell next = actorCell.getCell(dir);
        if (!actorCell.hasActor())
            throw new IllegalStateException("actorCell must have Actor");
        setChanged();
        notifyObservers(oldActorCell); // where actor was
        setChanged();
        notifyObservers(actorCell);    // where actor is now
        if (next != null) {
            setChanged();
            notifyObservers(next); // to where box may have been pushed
        }
    }

    /**
     * A String representation of the Sokoban puzzle
     * 
     * @return the String representation
     */
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        for (int row=0; row<numRows; row++) {
            for (int col=0; col<numCols; col++)
                b.append(""+cells[row][col]);
            b.append("\n");
        }
        return b.toString();
    }

    /**
     * Check if this is a valid display character for a Sokoban puzzle?
     * 
     * @param c the character to check
     * @param is this a valid display character?
     */
    public static boolean validDisplay(char c) {
        // not valid for TARGET or EMPTY
        return ((c == WALL) || (c == BOX) || (c == TARGET_BOX) || (c == ACTOR) || (c == TARGET_ACTOR));
    }

    /**
     * Convert a file into a String
     * 
     * @param file the file
     * @return the file as a string
     */
    public static String fileAsString(File file) {
        if (file == null)
            throw new IllegalArgumentException("file cannot be null");
        Scanner      fscnr = null;
        StringBuffer sb    = new StringBuffer();
        try {
            fscnr = new Scanner(file);
            while (fscnr.hasNextLine())
                sb.append(fscnr.nextLine()+"\n");
        } catch(IOException e) {
            throw new SokobanException(""+e);
        } finally {
            if (fscnr != null)
                fscnr.close();
        }
        return sb.toString();
    }

    /**
     * A trace method for debugging (active when traceOn is true)
     * 
     * @param s the string to output
     */
    public static void trace(String s) {
        if (traceOn)
            System.out.println("trace: " + s);
    }

    public static final char WALL         = '#';
    public static final char BOX          = '$';
    public static final char ACTOR        = '@';
    public static final char TARGET       = '.';
    public static final char EMPTY        = ' ';
    public static final char TARGET_BOX   = '*';
    public static final char TARGET_ACTOR = '+';

    private int      numRows     = 0;
    private int      numCols     = 0;
    private Cell     actorCell   = null;
    private Cell[][] cells       = null;
    private String   startScreen = null;

    private static boolean traceOn = false; // for debugging
}
