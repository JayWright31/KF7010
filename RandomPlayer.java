 

import java.util.*;

/**
 * A rando player for a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public class RandomPlayer implements Player {
	/**
	 * Default constructor
	 */
	public RandomPlayer() {
		rnd = new Random();
	}

	/**
	 * Select a random direction from the vector of choices.
	 * 
	 * @param choices possible directions for the player to choose from
	 * @return a random direction
	 */
	@Override
	public Direction move(Vector<Direction> choices) {
		if (choices == null)
			throw new IllegalArgumentException("cannot have null choices");
		if (choices.isEmpty())
			throw new IllegalArgumentException("cannot have empty choices");
		int size = choices.size();
		int idx  = rnd.nextInt(size);
		return choices.get(idx);
	}

	private Random rnd = null;
}
