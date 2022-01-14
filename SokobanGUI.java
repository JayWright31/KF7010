import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 * A graphical representation of a game of Sokoban
 *
 * @author Jay Wright
 * @version December 2021
 */
public class SokobanGUI extends JPanel implements Observer, ActionListener
{
    /**
     * Constructor for objects of class SokobanGUI
     */
    public SokobanGUI()
    {
        game = new Sokoban(new File(FILENAME));
        game.addObserver(this);
        makeFrame();
        //System.out.print(game);
    }

    /**
     * This method creates the new game frame, initialises a new JPanel for the grid of game cells and initialises a new
     * CellPanel object for each cell
     */
    private void makeFrame() {
        numRows = numRows(); //get the number of rows in the game screen
        numCols = numCols(); //get the number of columns in the game screen
        makeCharArray(); //build an ArrayList of Strings from the game screen file

        int row, col;
        //create a new grid panel and individual game cells
        gameGrid = new JPanel(new GridLayout(numRows, numCols));
        gameCells = new CellPanel[numRows+1][numCols+1];

        /*loop through each row and column and create a new CellPanel passing a reference to this GUI, the cell's row and
        column, and the character representing what type of cell it is*/
        for (row=1; row<=numRows; row++) {
            for (col=1; col<=numCols; col++) {
                char current = getCharFromArray(row, col);
                gameCells[row][col] = new CellPanel(this, row, col, current);
                gameGrid.add(gameCells[row][col]);
            }
        }

        //set the layout of the GUI panel and add the gameGrid of cells
        setLayout(new BorderLayout());
        add(gameGrid, BorderLayout.NORTH);

        //create the control buttons
        hint = new JButton("Hint");
        hint.addActionListener(this);
        clear = new JButton("Clear");
        clear.addActionListener(this);
        undo = new JButton("Undo");
        undo.addActionListener(this);
        save = new JButton("Save");
        save.addActionListener(this);
        load = new JButton("Load");
        load.addActionListener(this);

        //create a new panel for the control buttons and add the buttons to it
        JPanel controlButtons = new JPanel(new FlowLayout());
        controlButtons.add(hint);
        controlButtons.add(clear);
        controlButtons.add(undo);
        controlButtons.add(save);
        controlButtons.add(load);
        add(controlButtons, BorderLayout.CENTER);

        //create a text area to show the game status and add it to the GUI panel at the bottom
        status = new JTextArea();
        add(new JScrollPane(status), BorderLayout.SOUTH);

        //register this class with the observable Sokoban class
        game.addObserver(this);

        //create a new stack for the moves made by the player
        moves = new Stack<Direction>();
        validMoves();

        //add a key listener to the panel and make it visible
        //addKeyListener(this);
        setVisible(true);
    }

    /**
     * Updates the CellPanels when the model code changes. When the update is called it gets the character of the Cell passed in
     * and passes that to the changeBackground() method of the CellPanel to update its appearance
     * 
     * @param obs is the Obserable
     * @param obj is the Cell
     */
    @Override
    public void update(Observable obs, Object obj) {
        if (obj == null)
            throw new SokobanException("Cell is null"); //custom exception for handling a null cell rather than a nullPointer
        Cell c = (Cell) obj;
        if (c.isEmpty()) {
            //do nothing if the cell is empty
        }
        //update the CellPanel with the new character
        char updateChar = c.getDisplay();
        gameCells[c.getRow()+1][c.getCol()+1].changeBackground(updateChar);
    }

    /**
     * Action Event to get the button presses for undo, clear, save, and load
     * 
     * @param event is the action event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == hint)
            JOptionPane.showMessageDialog(null, "Use the WASD keys to move the Orange boxes to the Light Blue Target Squares. Don't get stuck!", "Hint", JOptionPane.INFORMATION_MESSAGE);
        else if (event.getSource() == clear)
            clear();
        else if (event.getSource() == undo)
            undo();
        else if (event.getSource() == save)
            save();
        else if (event.getSource() == load)
            load();
    }

    /**
     * Clears the game back to a new game. Loops through the grid of CellPanel and resets their appearence. Then a new 
     * Sokoban game is called and is registered with the observable. Finally a new stack of moves is made
     */
    private void clear() {
        setStatus("Game Cleared");
        for (int row=1; row<=numRows; row++) {
            for (int col=1; col<=numCols; col++) {
                char current = getCharFromArray(row, col);
                gameCells[row][col].clear(current);
            }
        }
        game = new Sokoban(new File(FILENAME));
        game.addObserver(this);
        moves = new Stack<Direction>();
    }

    /**
     * Undo the last move made by the player or computer
     */
    private void undo() {
        if (moves.empty()) {
            setStatus("No moves to undo!"); //checks if any moves have been made
            return;
        }
        Direction lastMove = moves.pop(); //remove the most recent assigned value
        Stack<Direction> oldStack = moves; //create new temporary stack before clearing
        clear(); //clears the game which creates a new stack
        for (Direction d : oldStack) { //cycles through the old stack
            game.move(d); //automatically makes each move in the stack
            moves.push(d); //stores the moves made in the newly created stack
        }
        //setStatus("Last move undone!");
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
            setStatus("Game Saved To File");
        } catch(IOException e) {
            setStatus("Error - save unsuccessful");
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
            setStatus("Game Loaded from file");
        }
        catch (IOException e) {
            setStatus("Error loading from file");
        }
    } 
    
    /**
     * If it is safe, move the actor to the next cell in a given direction
     * 
     * @param dir the direction to move
     */
    public void move(Direction dir) {
        if (!game.canMove(dir)) {
            setStatus("invalid move");
            return;
        }
        game.move(dir);
        moves.push(dir);
        validMoves();
        if (game.onTarget()) {
            setStatus("Level Won!");
            gameWinOption();
        }
    }
    
    /**
     * Creates a dialogue box that allows the player to move on to the next round
     */
    private void gameWinOption() {
        optionResponse = JOptionPane.showConfirmDialog(null, "Play next round?", "Game Won", JOptionPane.YES_NO_OPTION);
        if (optionResponse == 0) {
            int levelToPlay = currentGameLevel + 1;
            FILENAME = "screens/screen." + levelToPlay;
            currentGameLevel = levelToPlay;
            gameFrame.getContentPane().remove(gui);
            gui = new SokobanGUI();
            gameFrame.add(gui);
            gameFrame.getContentPane().invalidate();
            gameFrame.getContentPane().validate();
        }
    }

    /**
     * gets the number of rows from the Sokoban model code and assigns it to a local attribute
     */
    public int numRows() {
        numRows = game.getNumRows();
        return numRows;
    }

    /**
     * gets the number of rows from the Sokoban model code and assigns it to a local attribute
     */
    public int numCols() {
        numCols = game.getNumCols();
        return numCols;
    }

    /**
     * Updates the status textbox with the string passed as an argument
     * @param text is the updated status text
     */
    public void setStatus (String text) {
        status.setText(text);
    }

    /**
     * Called at the beginning of the level, this method creates an ArrayList of Strings from the game screen text file. The 
     * screenfile is scanned with a Scanner and then the Scanner is looped through to add each line as a String to the Array
     */
    private void makeCharArray() {
        gameArray = new ArrayList<>();
        File gameFile = new File(FILENAME);
        String gameString = fileAsString(gameFile);
        Scanner fileScanner = new Scanner(gameString);

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (line.length() > 0) {
                gameArray.add(line);
            }
        }
    }

    /**
     * Returns a single character representing a cell on the grid of gamesquares. The local String line grabs the current row
     * from the ArrayList and the chararacter char display is assigned to the character in the column
     * 
     * @param gameRow the row requested
     * @param gameCol the column requested
     * @return the character representing the cell
     */
    private char getCharFromArray(int gameRow, int gameCol) {
        int currentRow = gameRow-1;
        int currentCol = gameCol-1;
        String line = gameArray.get(currentRow);
        char display = (currentCol < line.length()) ? line.charAt(currentCol) : Sokoban.EMPTY;
        return display;
    }

    /**
     * Convert a file into a String
     * 
     * @param file the file
     * @return the file as a string
     */
    public static String fileAsString(File file) {
        if (file == null)
            throw new IllegalArgumentException("file cannot be null");
        Scanner      fscnr = null;
        StringBuffer sb    = new StringBuffer();
        try {
            fscnr = new Scanner(file);
            while (fscnr.hasNextLine())
                sb.append(fscnr.nextLine()+"\n");
        } catch(IOException e) {
            throw new SokobanException(""+e);
        } finally {
            if (fscnr != null)
                fscnr.close();
        }
        return sb.toString();
    }
    
    /**
     * Gets all the valid moves the player can make and displays them in the status box
     */
    private void validMoves() {
        String playerCanMoveTo = "You can move "+game.canMove();
        setStatus(playerCanMoveTo);
    }

    /**
     * Main method called to start the game. A new frame is created and a new GUI is created and added
     */
    public static void main(String[] args) {
        FILENAME = "screens/screen.2";
        currentGameLevel = 1;
        gameFrame = new JFrame("Sokoban");
        gui = new SokobanGUI();
        gameFrame.add(gui);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);

        gameFrame.setVisible(true);
    }

    //Attributes
    private static SokobanGUI gui;
    private static JFrame gameFrame;
    private Sokoban game = null;
    
    private CellPanel[][] gameCells = null;
    private JPanel gameGrid = null;
    //the buttons
    private JButton hint = null;
    private JButton clear = null;
    private JButton undo = null;
    private JButton save = null;
    private JButton load = null;
    
    private JTextArea status = null;
    private int numRows;
    private int numCols;
    private int gameFileRows;
    private int gameFileCols;
    
    private Stack<Direction> moves = null;
    private ArrayList<String> gameArray = null;
    private char currentChar;

    private int optionResponse;
    private static int currentGameLevel;

    private static final String SAVEGAME = "savegame1.txt";

    private static String  FILENAME; //the level to play
}
