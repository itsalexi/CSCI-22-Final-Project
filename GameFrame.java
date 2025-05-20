/**
 * The GameFrame class manages the main game window and frame.
 * It handles the window setup, size, and contains the game canvas.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 20, 2025
 * 
 * I have not discussed the Java language code in my program 
 * with anyone other than my instructor or the teaching assistants 
 * assigned to this course.
 * 
 * I have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in my program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of my program.
 */

import java.awt.Dimension;
import javax.swing.*;

public class GameFrame {

    private JFrame f;
    private GameCanvas gc;

    /**
     * Creates a new GameFrame with initialized window and canvas.
     */
    public GameFrame() {
        f = new JFrame();
        gc = new GameCanvas();
    }

    /**
     * Sets up the game window with title, size, and other properties.
     */
    public void setUpGUI() {
        f.add(gc);
        f.setPreferredSize(new Dimension(1024, 768));
        f.setTitle("Garden of Eden");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    }

    /**
     * Gets the game canvas.
     * 
     * @return game canvas
     */
    public GameCanvas getCanvas() {
        return gc;
    }
}
