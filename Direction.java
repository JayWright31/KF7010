
/**
 * Compass directions in a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 */
public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    /**
     * The direction to the left
     * 
     * @return dir the direction to the left
     */
    public Direction left() {
        switch(this) {
            case NORTH:
            return WEST;
            case SOUTH:
            return EAST;
            case EAST:
            return NORTH;
            default: // WEST
            return SOUTH;
        }
    }

    /**
     * The direction to the right
     * 
     * @return dir the direction to the right
     */
    public Direction right() {
        switch(this) {
            case NORTH:
            return EAST;
            case SOUTH:
            return WEST;
            case EAST:
            return SOUTH;
            default: // WEST
            return NORTH;
        }
    }

    /**
     * The direction in reverse
     * 
     * @return dir the direction in reverse
     */
    public Direction reverse() {
        switch(this) {
            case NORTH:
            return SOUTH;
            case SOUTH:
            return NORTH;
            case EAST:
            return WEST;
            default: // WEST
            return EAST;
        }
    }

    public static Direction fromString(String dirString) {
        switch(dirString) {
            case "NORTH":
            return NORTH;
            case "SOUTH":
            return SOUTH;
            case "EAST":
            return EAST;
            case "WEST":
            return WEST;
            default:
            return null;
        }
    }

    /**
     * 
     */

    public String toStringForFile() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Direction(" );
        return buffer.toString();
    }
}
