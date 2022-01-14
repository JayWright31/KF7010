import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.ImageIcon.*;

/**
 * A graphical representation of a single Sokoban cell
 *
 * @author Jay Wright
 * @version December 2021
 */
public class CellPanel extends JPanel implements KeyListener, MouseListener
{
    
    /**
     * Constructor for objects of class CellPanel
     */
    public CellPanel(SokobanGUI sokobanGUI, int r, int c, char currentChar) {
        if(sokobanGUI == null)
            throw new SokobanException("gui cannot be null");
        if ((r<1) || (r>sokobanGUI.numRows()))
            throw new SokobanException("invalid number of rows: " + r);
        if ((c<1) || (c>sokobanGUI.numCols()))
            throw new SokobanException("invalid number of cols: " + r);
        
        gui = sokobanGUI;
        row = r;
        col = c;
        
        //initialise the image icons
        wall = new ImageIcon(new ImageIcon("images/wall.jpg").getImage().getScaledInstance(100,150, Image.SCALE_SMOOTH));
        
        Border blackBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(blackBorder);
        
        changeBackground(currentChar);
        
        setOpaque(true);
        
        addMouseListener(this);
        addKeyListener(this);
        setPreferredSize(new Dimension(50,50));
    }
    
    /**
     * Clears cell text
     */
    public void clear(char current) {
        changeBackground(current);
    }
    
    
    //required methods from MouseListener
    @Override
    public void mouseClicked(MouseEvent event) {}
    @Override
    public void mousePressed(MouseEvent event) {}
    @Override
    public void mouseReleased(MouseEvent event) {}
    @Override
    public void mouseExited(MouseEvent event) {
        gui.setStatus("");
    }
    
    /**
     * Mouse has entered a panel so make that the focus
     */
    @Override
    public void mouseEntered(MouseEvent event) {
        requestFocusInWindow();
    }
    
    //required methods from KeyListener
    @Override
    public void keyPressed(KeyEvent event) {}
    @Override
    public void keyReleased(KeyEvent event) {}
    
    /**
     * Gets the key pressed by the user and calls the required method
     */
    @Override
    public void keyTyped(KeyEvent event) {
        char command = Character.toUpperCase(event.getKeyChar());
        if (command == W_CHAR){
            gui.setStatus("You Moved NORTH");
            gui.move(Direction.NORTH);
            return;
        } if (command == A_CHAR) {
            gui.setStatus("You Moved WEST");
            gui.move(Direction.WEST);
            return;
        } else if (command == S_CHAR) {
            gui.setStatus("You Moved SOUTH");
            gui.move(Direction.SOUTH);
            return;
        } else if (command == D_CHAR) {
            gui.setStatus("You Moved EAST");
            gui.move(Direction.EAST);
            return;
        } else {
            gui.setStatus("Invalid Move");
            return;
        }
    }
    
    /**
     * Changes the background colour of a cell based on the gamefile character of the cell
     */
    public void changeBackground(char character) {
        if (character == WALL_CHAR){
            JLabel wallLabel = new JLabel(wall);
            this.add(wallLabel);
            //this.setBackground(WALL);
        }
        if (character == BOX_CHAR)
            this.setBackground(BOX);
        if (character == ACTOR_CHAR)
            this.setBackground(ACTOR);
        if (character == TARGET_CHAR)
            this.setBackground(TARGET);
        if (character == EMPTY_CHAR)
            this.setBackground(EMPTY);
        if (character == TARGET_BOX_CHAR)
            this.setBackground(TARGET_BOX);
        if (character == TARGET_ACTOR_CHAR)
            this.setBackground(TARGET_ACTOR);
        
            
    }
    
    
    private SokobanGUI gui;
    private Sokoban game;
    private int row;
    private int col;
    
    //image icons
    private ImageIcon wall;
    
    //colours
    //private static final Color WALL = Color.gray;
    private static final Color BOX = Color.orange;
    private static final Color ACTOR = Color.blue;
    private static final Color TARGET = Color.cyan;
    private static final Color EMPTY = Color.white;
    private static final Color TARGET_BOX = Color.green;
    private static final Color TARGET_ACTOR = Color.magenta;
    
    private static final char W_CHAR = 'W';
    private static final char A_CHAR = 'A';
    private static final char S_CHAR = 'S';
    private static final char D_CHAR = 'D';
    
    public static final char WALL_CHAR         = '#';
    public static final char BOX_CHAR          = '$';
    public static final char ACTOR_CHAR        = '@';
    public static final char TARGET_CHAR       = '.';
    public static final char EMPTY_CHAR        = ' ';
    public static final char TARGET_BOX_CHAR   = '*';
    public static final char TARGET_ACTOR_CHAR = '+';
}
