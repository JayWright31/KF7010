 

import java.util.*;

/**
 * A player interface for a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public interface Player {
	/**
	 * In which direction should the actor move, given a vector of choices?
	 * 
	 * @param choices possible directions for the player to choose from
	 * @return the chosen direction
	 */
	public Direction move(Vector<Direction> choices);
}
