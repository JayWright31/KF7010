 

/**
 * A box in a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public class Box extends Occupant {
	/**
	 * Constructor with cell currently occupied
	 * 
	 * @param cell the cell occupied by this box (Occupant constructor checks for null)
	 */
	public Box(Cell cell) {
		super(cell);
	}

	/**
	 * Checks if this occupant is a box
	 * 
	 * @return true
	 */
	@Override
	public boolean isBox() {
		return true;
	}

	/**
	 * Gets the character to use for display purposes for this cell
	 * 
	 * @return character to use for display purposes for this cell
	 */
	@Override
	public char getDisplay() {
		return (cell.isTarget()) ? Sokoban.TARGET_BOX : Sokoban.BOX;
	}

	/**
	 * Checks if the cell occupied by this box is a target
	 * 
	 * @return is the cell occupied by this box a target?
	 */
	@Override
	public boolean onTarget() {
		return cell.isTarget();
	}

	/**
	 * Check if this box is safe (from getting stuck to another box) is it moves in the given direction
	 * 
	 * @param dir the direction to check
	 * @return can this box move in the given direction without getting stuck on another box?
	 */
	@Override
	public boolean isStuckSafe(Direction dir) {
		Cell next = cell.getCell(dir);
		if (next == null)
			throw new SokobanException("next cannot be null");
		if (!next.isEmpty())
			throw new SokobanException("next must be empty");
		if (!next.isWallSafe())
			throw new SokobanException("next must be wall safe");
		if (next.isTarget()) // okay to get stuck on a target
			return true;
		Cell nextAhead = next.getCell(dir);
		Cell nextLeft  = next.getCell(dir.left());
		Cell nextRight = next.getCell(dir.right());
		// might get stuck if we move next to a box
		boolean stuckSafe = (nextAhead.hasBox() || nextLeft.hasBox() || nextRight.hasBox()) ? false : true;
		// ... but not if the box ahead can be moved, or we still can
		if (nextAhead.hasBox() && (next.isMoveableBoxLocation(cell, dir) || nextAhead.isMoveableBoxLocation(cell, next, dir)))
			stuckSafe = true;
		// ... but not if the box on the left can be moved, or we still can
		if (nextLeft.hasBox() && (next.isMoveableBoxLocation(cell, dir.left()) || nextLeft.isMoveableBoxLocation(cell, next, dir.left())))
			stuckSafe = true;
		// ... but not if the box on the right can be moved, or we still can
		if (nextRight.hasBox() && (next.isMoveableBoxLocation(cell, dir.right()) || nextRight.isMoveableBoxLocation(cell, next, dir.right())))
			stuckSafe = true;
		return stuckSafe;
	}

	/**
	 * Checks if the box can move to the next cell in a given direction
	 * 
	 * @param dir the direction to check
	 * @return can the box  move to the next cell in a given direction?
	 */
	@Override
	public boolean canMove(Direction dir) {
		Cell next = cell.getCell(dir);
		return (next != null) && next.isEmpty() && next.isWallSafe() && isStuckSafe(dir);
	}
}
