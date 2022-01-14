 

import java.io.*;
import java.util.*;

/**
 * A text-based user interface for a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair with additional code written by Jay Wright
 * @version September 2021
 */
public class SokobanUI {
    /**
     * Default constructor
     */
    public SokobanUI() {
        scnr   = new Scanner(System.in);
        puzzle = new Sokoban(new File(FILENAME));
        player = new RandomPlayer();
        moves = new Stack<Direction>();
    }

    /**
     * Main control loop.  This displays the puzzle, then enters a loop displaying a menu,
     * getting the user command, executing the command, displaying the puzzle and checking
     * if further moves are possible
     */
    public void menu() {
        String command = "";
        System.out.print(puzzle);
        while (!command.equalsIgnoreCase("Quit") && !puzzle.onTarget())  {
            displayMenu();
            command = getCommand();
            execute(command);
            System.out.print(puzzle);
            if (puzzle.onTarget())
                System.out.println("puzzle is complete");
            trace("onTarget: "+puzzle.numOnTarget());
        }
    }

    /**
     * Display the user menu
     */
    private void displayMenu()  {
        System.out.println("Commands are:");
        System.out.println("   Move North         [W]");
        System.out.println("   Move South         [S]");
        System.out.println("   Move East          [D]");
        System.out.println("   Move West          [A]");
        System.out.println("   Player move        [P]");
        System.out.println("   Undo move          [U]");
        System.out.println("   Restart puzzle [Clear]");
        System.out.println("   Save to file    [Save]");
        System.out.println("   Load from file  [Load]");
        System.out.println("   To end program  [Quit]");    
    }

    /**
     * Get the user command
     * 
     * @return the user command string
     */
    private String getCommand() {
        System.out.print ("Enter command: ");
        return scnr.nextLine();
    }

    /**
     * Execute the user command string
     * 
     * @param command the user command string
     */
    private void execute(String command) {
        if (command.equalsIgnoreCase("Quit")) {
            System.out.println("Program closing down");
            System.exit(0);
        } else if (command.equalsIgnoreCase("W")) {
            north();
        } else if (command.equalsIgnoreCase("S")) {
            south();
        } else if (command.equalsIgnoreCase("D")) {
            east();
        } else if (command.equalsIgnoreCase("A")) {
            west();
        } else if (command.equalsIgnoreCase("P")) {
            playerMove();
        } else if (command.equalsIgnoreCase("U")) {
            undo();
        } else if (command.equalsIgnoreCase("Clear")) {
            clear();
        } else if (command.equalsIgnoreCase("Save")) {
            save();
        } else if (command.equalsIgnoreCase("Load")) {
            load();
        } else {
            System.out.println("Unknown command (" + command + ")");
        }
    }

    /**
     * Move the actor north
     */
    private void north() {
        move(Direction.NORTH);
    }

    /**
     * Move the actor south
     */
    private void south() {
        move(Direction.SOUTH);
    }

    /**
     * Move the actor east
     */
    private void east() {
        move(Direction.EAST);
    }

    /**
     * Move the actor west
     */
    private void west() {
        move(Direction.WEST);
    }

    /**
     * Move the actor according to the computer player's choice
     */
    private void playerMove() {
        Vector<Direction> choices = puzzle.canMove();
        Direction         choice  = player.move(choices);
        move(choice);
        moves.push(choice);
    }
    
    /**
     * Clears the game back to a new game
     */
    private void clear() {
        puzzle = new Sokoban(new File(FILENAME));
        moves = new Stack<Direction>();
        System.out.println("Game Cleared");
    }
    
    /**
     * Undo the last move made by the player or computer
     */
    private void undo() {
        if (moves.empty()) {
            System.out.println("Nothing to undo"); //checks if any moves have been made
            return;
        }
        Direction lastMove = moves.pop(); //remove the most recent assigned value
        Stack<Direction> oldStack = moves; //create new temporary stack before clearing
        clear(); //clears the game which creates a new stack
        for (Direction d : oldStack) { //cycles through the old stack
            puzzle.move(d); //automatically makes each move in the stack
            moves.push(d); //stores the moves made in the newly created stack
        }
    }
    
    /**
     * Saves the current game to a text file that can be loaded later
     */
    private void save() {
        try {
            PrintStream printStream = new PrintStream(new File(SAVEGAME)); 
            for (Direction move : moves)
                printStream.println(move);
            printStream.close();
            System.out.println("Game save successful");
        } catch(IOException e) {
            System.out.println("Error - save unsuccessful");
        }
    }
    
    /**
     * Loads a game from the SAVEGAME text file by reading each line of the file and calling the relative move() method
     */
    private void load() {
        try {
            Scanner fileScan = new Scanner(new File(SAVEGAME));
            clear();
            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine();
                if (line.equals("NORTH"))
                    move(Direction.NORTH);
                else if (line.equals("EAST"))
                    move(Direction.EAST);
                else if (line.equals("WEST"))
                    move(Direction.WEST);
                else if (line.equals("SOUTH"))
                    move(Direction.SOUTH);
            }
            fileScan.close();
            System.out.println("Game Loaded from file");
        }
        catch (IOException e) {
            System.out.println("Error loading from file");
        }
    }

    /**
     * If it is safe, move the actor to the next cell in a given direction
     * 
     * @param dir the direction to move
     */
    private void move(Direction dir) {
        if (!puzzle.canMove(dir)) {
            System.out.println("invalid move");
            return;
        }
        puzzle.move(dir);
        moves.push(dir);
        //System.out.println(""+dir);
        if (puzzle.onTarget())
            System.out.println("game won!");
    }

    public static void main(String[] args) {
        SokobanUI ui = new SokobanUI();
        ui.menu();
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

    private Scanner scnr           = null;
    private Sokoban puzzle         = null;
    private Player  player         = null;
    private Stack<Direction> moves = null; //stack of moves
    
    private static final String SAVEGAME = "sokobanTextUI.txt"; //savegame text file

    private static String  FILENAME = "screens/screen.1"; //the level to play

    private static boolean   traceOn = false; // for debugging
}
