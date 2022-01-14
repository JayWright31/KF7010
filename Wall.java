 

/**
 * A wall in a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public class Wall extends Occupant {
	/**
	 * Constructor with cell currently occupied
	 * 
	 * @param cell the cell occupied by this wall (Occupant constructor checks for null)
	 */
	public Wall(Cell cell) {
		super(cell);
	}

	/**
	 * Checks if this occupant is a wall
	 * 
	 * @return true
	 */
	@Override
	public boolean isWall() {
		return true;
	}

	/**
	 * Gets the character to use for display purposes for this cell
	 * 
	 * @return character to use for display purposes for this cell
	 */
	@Override
	public char getDisplay() {
		return Sokoban.WALL;
	}
}
