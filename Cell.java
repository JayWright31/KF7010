 

import java.util.*;

/**
 * A cell in a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version October 2021
 */
public class Cell implements Comparable<Cell> {
	/**
	 * Constructor
	 * 
	 * @param display the character to use for display purposes for this cell
	 * @param puzzle the parent Sokoban puzzle (cannot be null)
	 * @param row this cell's row in the Sokoban puzzle (numbered from 0)
	 * @param col this cell's column in the Sokoban puzzle (numbered from 0)
	 */
	public Cell(char display, Sokoban puzzle, int row, int col) {
		if (puzzle == null)
			throw new IllegalArgumentException("puzzle cannot be null");
		if ((row<0) || (row>=puzzle.getNumRows()))
			throw new IllegalArgumentException("invalid row");
		if ((col<0) || (col>=puzzle.getNumCols()))
			throw new IllegalArgumentException("invalid col");
		this.puzzle = puzzle;
		this.row    = row;
		this.col    = col;
		this.target = (display == Sokoban.TARGET || display == Sokoban.TARGET_BOX || display == Sokoban.TARGET_ACTOR) ? true : false;
		this.occ    = (display == Sokoban.EMPTY || display == Sokoban.TARGET) ? null : Occupant.getInstance(display, this);
		
	}

	/**
	 * Compares the cell with another only on the basis of rows and columns
	 * 
	 * @param other the other cell
	 * @return -1 if either this row or column are smaller; 1 if either this row or column are larger; otherwise 0
	 */
	@Override
	public int compareTo(Cell other) {
		// consider row and col only
		if ((row < other.row) || (col < other.col))
			return -1;
		else if ((row > other.row) || (col > other.col))
			return 1;
		else
			return 0;
	}

	/**
	 * Test for equality with another cell, considering row and column only
	 * 
	 * @param obj the other cell
	 * @return do the two cells have equal row and columns?
	 */
	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof Cell))
			return false;
		Cell other = (Cell) obj;
		if ((row == other.row) && (col == other.col))
			return true;
		else
			return false;
	}

	/**
	 * Gets the cell row
	 * 
	 * @return the cell row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Gets the cell column
	 * 
	 * @return the cell column
	 */
	public int getCol() {
		return col;
	}
	
	/**
	 * Changes the display for this cell
	 * 
	 * @param display the character to use for display purposes for this cell
	 */
	void setDisplay(char display) {
		this.target = (display == Sokoban.TARGET || display == Sokoban.TARGET_BOX || display == Sokoban.TARGET_ACTOR) ? true : false;
		this.occ    = (display == Sokoban.EMPTY || display == Sokoban.TARGET) ? null : Occupant.getInstance(display, this);
	}
	
	/**
	 * Gets the cell display character
	 * 
	 * @return the character to use for display purposes for this cell
	 */
	
	public char getDisplay() {
		return toString().charAt(0);
	}

	/**
	 * Changes the occupant for this cell (can be null)
	 * 
	 * @param occ the occupant for this cell
	 */
	void setOccupant(Occupant occ) {
		this.occ = occ;
		if (occ != null)
			occ.setCell(this);
	}

	/**
	 * Checks if this cell is a target
	 * 
	 * @return is this cell a target?
	 */
	public boolean isTarget() {
		return target;
	}

	/**
	 * Checks if this cell is empty
	 * 
	 * @return is this cell empty?
	 */
	public boolean isEmpty() {
		return (occ == null);
	}

	/**
	 * Checks if this cell is occupied by an actor
	 * 
	 * @return is this cell occupied by an actor?
	 */
	public boolean hasActor() {
		return (occ == null) ? false : occ.isActor();
	}

	/**
	 * Checks if this cell is occupied by a box
	 * 
	 * @return is this cell occupied by a box?
	 */
	public boolean hasBox() {
		return (occ == null) ? false : occ.isBox();
	}

	/**
	 * Checks if this cell is occupied by a wall
	 * 
	 * @return is this cell occupied by a wall?
	 */
	public boolean hasWall() {
		return (occ == null) ? false : occ.isWall();
	}

	/**
	 * Get the next cell in a given direction
	 * 
	 * @param dir the direction to check
	 * @return the cell in a given direction (or null if off the grid)
	 */
	Cell getCell(Direction dir) {
		if (dir == Direction.NORTH)
			return puzzle.getCell(row-1, col);
		else if (dir == Direction.SOUTH)
			return puzzle.getCell(row+1, col);
		else if (dir == Direction.EAST)
			return puzzle.getCell(row, col+1);
		else // WEST
			return puzzle.getCell(row, col-1);
	}

	/**
	 * Count the number of adjacent cells occupied by walls
	 * 
	 * @return the number of adjacent walls
	 */
	public int numAdjacentWalls() {
		int num = 0;
		for (Direction dir : Direction.values()) {
			Cell next = getCell(dir);
			if ((next != null) && next.hasWall())
				num++;
		}
		return num;
	}

	/**
	 * If this Cell was empty, would it be safe to push a box into it?
	 * (Do the walls make it impossible to push it out of this cell?)
	 * 
	 * @return if this Cell was empty, would it be safe to push a box into it?
	 */
	public boolean isWallSafe() {
		if (hasWall())
			return false;
		else if (isTarget())
			return true;
		else if (numAdjacentWalls() >= 3)
			return false;
		else if (numAdjacentWalls() == 2) {
			// two parallel Walls
			Cell north = getCell(Direction.NORTH);
			Cell south = getCell(Direction.SOUTH);
			Cell east  = getCell(Direction.EAST);
			Cell west  = getCell(Direction.WEST);
			if (((north != null) && north.hasWall() && (south != null) && south.hasWall()) ||
					((east != null) && east.hasWall() && (west != null) && west.hasWall()))
				return true;
			else // two Walls at 90 degrees
				return false;
		} else  // only one or zero adjacent Walls
			return true;
	}

	/**
	 * Is it safe (from getting stuck to another box) to move a box in the given direction?
	 * 
	 * @param dir the direction to check
	 * @return is it safe (from getting stuck to another box) to move a box in the given direction?
	 */
	public boolean isStuckSafe(Direction dir) {
		return isEmpty() ? false : occ.isStuckSafe(dir);
	}

	/**
	 * Could a box at this location move perpendicular to the indicated direction?
	 * The location does not currently have to contain a box (this should be separately tested if required)
	 * Origin is the cell where the original box will move from.
	 * (Even if it currently contains a box, origin is treated as empty)
	 * 
	 * @param origin where the original box will move from (cannot be null)
	 * @param dir the direction to check
	 * @return if a box at this location could move perpendicular to the indicated direction?
	 */
	public boolean isMoveableBoxLocation(Cell origin, Direction dir) {
		if (origin == null)
			throw new IllegalArgumentException("origin cannot be null");
		TreeSet<Cell> visited = new TreeSet<Cell>();
		return isMoveableBoxLocation(origin, dir, visited);
	}

	/**
	 * Could a box at this location move perpendicular to the indicated direction?
	 * The location does not currently have to contain a box (this should be separately tested if required)
	 * Origin is the cell where the original box will move from.
	 * (Even if it currently contains a box, origin is treated as empty)
	 * Assume the taboo cell has already been visited (cannot be null)
	 * 
	 * @param origin where the original box will move from (cannot be null)
	 * @param taboo a cell that is already assumed to have been visited
	 * @param dir the direction to check
	 * @return if a box at this location could move perpendicular to the indicated direction?
	 */
	public boolean isMoveableBoxLocation(Cell origin, Cell taboo, Direction dir) {
		if (origin == null)
			throw new IllegalArgumentException("origin cannot be null");
		if (taboo == null)
			throw new IllegalArgumentException("taboo cannot be null");
		if (taboo.equals(this))
			throw new IllegalArgumentException("taboo cannot be this cell");
		TreeSet<Cell> visited = new TreeSet<Cell>();
		visited.add(taboo);
		return isMoveableBoxLocation(origin, dir, visited);
	}

	/**
	 * Could a box at this location move perpendicular to the indicated direction?
	 * The location does not currently have to contain a box (this should be separately tested if required)
	 * Origin is the cell where the original box will move from.
	 * (Even if it currently contains a box, origin is treated as empty)
	 * The recursion has already checked all the cells in visited
	 * 
	 * @param origin where the original box will move from (cannot be null)
	 * @param dir the direction to check
	 * @param the set of cells already visited
	 * @return if a box at this location could move perpendicular to the indicated direction?
	 */
	private boolean isMoveableBoxLocation(Cell origin, Direction dir, TreeSet<Cell> visited) {
		if (origin == null)
			throw new IllegalArgumentException("origin cannot be null");
		if (visited == null)
			throw new IllegalArgumentException("visited cannot be null");
		Cell left  = getCell(dir.left());
		Cell right = getCell(dir.right());

		//check for loops
		if (visited.contains(this))
			return false;
		visited.add(this);
		boolean visitedLeft  = visited.contains(left);
		boolean visitedRight = visited.contains(right);

		// could move left
		if ((left != null) && (!visitedLeft) && (((left.isEmpty() || left.hasActor() || left.equals(origin)) && left.isWallSafe()) ||
				(left.hasBox() && left.isMoveableBoxLocation(origin, dir.left(), visited))) &&
				(right != null) && (!visitedRight) && (right.isEmpty() || right.hasActor() || right.equals(origin) ||
						(right.hasBox() && right.isMoveableBoxLocation(origin, dir.right(), visited))))
			return true;
		// could move right
		else if ((right != null) && (!visitedRight) && (((right.isEmpty() || right.hasActor() || right.equals(origin)) && right.isWallSafe()) ||
				(right.hasBox() && right.isMoveableBoxLocation(origin, dir.right(), visited))) &&
				(left != null) && (!visitedLeft) && (left.isEmpty() || left.hasActor() || left.equals(origin) || 
						(left.hasBox() && left.isMoveableBoxLocation(origin, dir.left(), visited))))
			return true;
		return false;
	}

	/**
	 * Checks if the cell occupant can move to the next cell in a given direction
	 * 
	 * @param dir the direction to check
	 * @return can the cell occupant move to the next cell in a given direction?
	 */
	public boolean canMove(Direction dir) {
		return isEmpty() ? false : occ.canMove(dir);
	}

	/**
	 * If it is safe, move the cell occupant to the next cell in a given direction
	 * 
	 * @param dir the direction to move
	 */
	public void move(Direction dir) {
		if (!canMove(dir))
			throw new IllegalArgumentException("cannot move "+dir);
		occ.move(dir);
	}

	/**
	 * Checks if this cell is occupied by a box on a target
	 * 
	 * @return is this cell occupied by a box on a target?
	 */
	public boolean onTarget() {
		return isEmpty() ? false : occ.onTarget();
	}

	/**
	 * A String representation of the Cell
	 * 
	 * @return the String representation
	 */
	@Override
	public String toString() {
		if (isEmpty() && !isTarget())
			return ""+Sokoban.EMPTY;
		else if (isEmpty() && isTarget())
			return ""+Sokoban.TARGET;
		else
			return ""+occ;
	}

	/**
	 * A String representation of the Cell (useful for debugging)
	 * 
	 * @return the String representation
	 */
	public String toStringFull() {
		StringBuffer b = new StringBuffer("Cell(");
		b.append(row);
		b.append(",");
		b.append(col);
		b.append(",");
		b.append(""+this);
		b.append(")");
		return b.toString();
	}

	/**
	 * A String representation of a TreeSet of Cells (useful for debugging)
	 * 
	 * @param visited a TreeSet of Cells (cannot be null)
	 * @return the String representation
	 */
	public static String TreeSetToString(TreeSet<Cell> visited) {
		if (visited == null)
			throw new IllegalArgumentException("visited cannot be null");
		StringBuffer b = new StringBuffer("[");
		Iterator<Cell> iter = visited.iterator();
		while (iter.hasNext()) {
			b.append(iter.next().toStringFull());
			if (iter.hasNext())
				b.append(",");
		}
		b.append("]");
		return b.toString();
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

	private Sokoban  puzzle = null;
	private int      row;
	private int      col;
	private Occupant occ    = null;
	private boolean  target = false;

	private static boolean   traceOn = false; // for debugging
}
