 

/**
 * A problem-specific run time exception for a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
@SuppressWarnings("serial")
public class SokobanException extends RuntimeException {
	/**
	 * Constructor with explanatory message
	 * 
	 * @param message the explanatory message
	 */
	public SokobanException(String msg) {
		super(msg);
	}
}
