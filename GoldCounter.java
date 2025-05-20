/**
 * The GoldCounter class manages the display of the player's gold balance in the game UI.
 * It handles the rendering of the gold counter with a custom sprite and text.
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

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class GoldCounter {
  private GameCanvas canvas;
  private Sprite goldUI;

  /**
   * Creates a new GoldCounter instance.
   * 
   * @param c the game canvas to draw on
   */
  public GoldCounter(GameCanvas c) {
    canvas = c;
    ArrayList<File> goldUIFiles = new ArrayList<>();
    goldUIFiles.add(new File("assets/ui/gold.png"));
    goldUI = new Sprite(goldUIFiles, true);
  }

  /**
   * Draws the gold counter UI with the current balance.
   * 
   * @param g2d the graphics context to draw with
   */
  public void draw(Graphics2D g2d) {
    double x = (canvas.getWidth() - goldUI.getWidth());
    g2d.translate(x, 0);
    goldUI.draw(g2d);

    String goldString = Integer.toString(canvas.getEconomySystem().getBalance());

    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Minecraft", 1, 25));

    float goldLabelX = 50;
    float goldLabelY = 30;

    g2d.setColor(Color.BLACK);
    g2d.drawString(goldString, goldLabelX + 1, goldLabelY - 1);
    g2d.drawString(goldString, goldLabelX + 1, goldLabelY + 1);
    g2d.drawString(goldString, goldLabelX - 1, goldLabelY - 1);
    g2d.drawString(goldString, goldLabelX - 1, goldLabelY + 1);
    g2d.setColor(Color.WHITE);

    g2d.drawString(goldString, goldLabelX, goldLabelY);

    g2d.translate(-x, 0);
  }
}
