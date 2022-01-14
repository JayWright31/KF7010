 

/**
 * An abstract cell occupant in a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public abstract class Occupant {
	public Occupant(Cell cell) {
		/**
		 * Constructor with cell currently occupied
		 * 
		 * @param cell the cell occupied by this actor (cannot be null)
		 */
		if (cell == null)
			throw new IllegalArgumentException("cell cannot be null");
		this.cell = cell;
	}

	/**
	 * Checks if this occupant is an actor (overridden by Actor)
	 * 
	 * @return false
	 */
	public boolean isActor() {
		return false;
	}

	/**
	 * Checks if this occupant is a box (overridden by Box)
	 * 
	 * @return false
	 */
	public boolean isBox() {
		return false;
	}

	/**
	 * Checks if this occupant is a wall (overridden by Wall)
	 * 
	 * @return false
	 */
	public boolean isWall() {
		return false;
	}

	/**
	 * Gets the character to use for display purposes for this cell
	 * 
	 * @return character to use for display purposes for this cell
	 */
	public abstract char getDisplay();

	/**
	 * Changes the cell for this occupant
	 * 
	 * @param cell the cell occupied by this occupant (cannot be null)
	 */
	void setCell(Cell cell) {
		if (cell == null)
			throw new IllegalArgumentException("cell cannot be null");
		this.cell = cell;
	}

	/**
	 * Check if this occupant is safe (from getting stuck to a box) if it moves in the given direction
	 * (overridden by Box)
	 * 
	 * @param dir the direction to check
	 * @return false
	 */
	public boolean isStuckSafe(Direction dir) {
		return false;
	}

	/**
	 * Checks if the occupant can move to the next cell in a given direction
	 * (overridden by Actor and Box)
	 * 
	 * @param dir the direction to check
	 * @return false
	 */
	public boolean canMove(Direction dir) {
		return false;
	}

	/**
	 * If it is safe, move the occupant to the next cell in a given direction
	 * 
	 * @param dir the direction to move
	 */
	public void move(Direction dir) {
		if (!canMove(dir))
			throw new IllegalArgumentException("cannot move "+dir);
		Cell next = cell.getCell(dir);
		if (!next.isEmpty())
			next.move(dir);
		cell.setOccupant(null);
		next.setOccupant(this);
	}

	/**
	 * Checks if this cell is a target occupied by a box
	 * (overridden by Box)
	 * 
	 * @return false
	 */
	public boolean onTarget() {
		return false;
	}

	/**
	 * A String representation of the Occupant
	 * 
	 * @return the String representation
	 */
	@Override
	public String toString() {
		return ""+getDisplay();
	}

	/**
	 * A factory method to construct an Occupant based on the display character and cell to be occupied.
     * The display character must be valid.
	 * 
	 * @param display the display character
	 * @param cell the cell to be occupied
	 */
	public static Occupant getInstance(char display, Cell cell) {
		if (!Sokoban.validDisplay(display))
			throw new IllegalArgumentException("not valid display character for an Occupant");
		if (cell == null)
			throw new IllegalArgumentException("cell cannot be null");
		if (cell.isTarget() && (display != Sokoban.TARGET_BOX) && (display != Sokoban.TARGET_ACTOR))
			throw new IllegalArgumentException("if cell is target, display must be '"+Sokoban.TARGET_BOX+"' or '"+Sokoban.TARGET_ACTOR+"'");
		if (display == Sokoban.WALL)
			return new Wall(cell);
		else if (display == Sokoban.BOX || display == Sokoban.TARGET_BOX)
			return new Box(cell);
		else // is ACTOR or TARGET_ACTOR
			return new Actor(cell);
	}

	protected Cell cell = null;
}
