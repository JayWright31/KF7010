 

/**
 * An actor in a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public class Actor extends Occupant {
	/**
	 * Constructor with cell currently occupied
	 * 
	 * @param cell the cell occupied by this actor (Occupant constructor checks for null)
	 */
	public Actor(Cell cell) {
		super(cell);
	}

	/**
	 * Checks if this occupant is an actor
	 * 
	 * @return true
	 */
	@Override
	public boolean isActor() {
		return true;
	}

	/**
	 * Gets the character to use for display purposes for this cell
	 * 
	 * @return character to use for display purposes for this cell
	 */
	@Override
	public char getDisplay() {
		return (cell.isTarget()) ? Sokoban.TARGET_ACTOR : Sokoban.ACTOR;
	}

	/**
	 * Checks if the actor can move to the next cell in a given direction
	 * 
	 * @param dir the direction to check
	 * @return can the actor  move to the next cell in a given direction?
	 */
	@Override
	public boolean canMove(Direction dir) {
		Cell next = cell.getCell(dir);
		return (next != null) && (next.isEmpty() || next.canMove(dir));
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

	private static boolean traceOn = false; // for debugging
}
