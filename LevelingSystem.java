/**
 * The LevelingSystem class manages player progression and experience.
 * It handles level calculations, experience gain, level-up effects, and visual feedback.
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class LevelingSystem {
  private double xp;
  private int level;
  private EconomySystem skillPoints;
  private GameCanvas canvas;

  private static final double BASE_XP = 100;
  private static final double GROWTH_RATE = 1.2;

  private long lastXPGain;

  private Timer xpTextTimer;
  private float xpTextAlpha;
  private String status;

  private double xpBoost;

  /**
   * Creates a new LevelingSystem instance.
   * Initializes the XP system with animation timer and default values.
   * 
   * @param sp the economy system for skill points
   * @param c the game canvas
   */
  public LevelingSystem(EconomySystem sp, GameCanvas c) {
    xp = 0;
    level = 0;
    skillPoints = sp;
    lastXPGain = System.currentTimeMillis();
    xpTextAlpha = 0;
    canvas = c;
    status = "";
    xpBoost = 0;

    xpTextTimer = new Timer(1000 / 60, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Long delta = System.currentTimeMillis() - lastXPGain;
        xpTextAlpha = (float) clamp(0, 1, (double) -delta / 500 + 4);
      }
    });
    xpTextTimer.start();
  }

  /**
   * Clamps a value between a minimum and maximum.
   * 
   * @param left the minimum value
   * @param right the maximum value
   * @param value the value to clamp
   * @return the clamped value
   */
  private double clamp(double left, double right, double value) {
    return Math.max(left, Math.min(right, value));
  }

  /**
   * Calculates the XP required for the next level.
   * 
   * @return the XP required for next level
   */
  public double xpToNextLevel() {
    return BASE_XP * Math.pow(GROWTH_RATE, level);
  }

  /**
   * Sets the XP boost multiplier.
   * 
   * @param boost the new XP boost multiplier
   */
  public void setXPBoost(double boost) {
    xpBoost = boost;
  }

  /**
   * Sets the current XP amount.
   * 
   * @param amount the new XP amount
   */
  public void setXP(double amount) {
    xp = amount;
  }

  /**
   * Adds XP to the player's total and handles level-ups.
   * Includes visual feedback, sound effects, and server synchronization.
   * 
   * @param amount the amount of XP to add
   */
  public void addXP(double amount) {
    amount = amount * xpBoost;
    lastXPGain = System.currentTimeMillis();
    xp += amount;
    status = String.format("+%.1f (%.1f/%.1f)", amount, xp, xpToNextLevel());
    while (xp >= xpToNextLevel()) {
      xp -= xpToNextLevel();
      level++;
      canvas.playLocalSound("level_up");
      if (skillPoints != null) {
        skillPoints.setSkillPoints(skillPoints.getSkillPoints() + 1);
      }
      status = String.format("Level %s -> [%s]", level - 1, level);
    }
    canvas.getWriter().send("LEVELS SET XP " + canvas.getPlayer().getId() + " " + xp);
    canvas.getWriter().send("LEVELS SET LVL " + canvas.getPlayer().getId() + " " + level);
  }

  /**
   * Sets the player's level.
   * 
   * @param l the new level
   */
  public void setLevel(int l) {
    level = l;
  }

  /**
   * Gets the current XP amount.
   * 
   * @return the current XP
   */
  public double getXP() {
    return xp;
  }

  /**
   * Gets the current level.
   * 
   * @return the current level
   */
  public int getLevel() {
    return level;
  }

  /**
   * Draws the XP and level information on screen.
   * 
   * @param g2d the graphics context
   */
  public void draw(Graphics2D g2d) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, xpTextAlpha));

    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Minecraft", 1, 24));

    int textWidth = g2d.getFontMetrics().stringWidth(status);
    float itemTextX = (canvas.getWidth()) / 2 - (textWidth / 2);
    float itemTextY = canvas.getHeight() - 100;

    g2d.setColor(Color.BLACK);
    g2d.drawString(status, itemTextX + 1, itemTextY - 1);
    g2d.drawString(status, itemTextX + 1, itemTextY + 1);
    g2d.drawString(status, itemTextX - 1, itemTextY - 1);
    g2d.drawString(status, itemTextX - 1, itemTextY + 1);
    g2d.setColor(Color.WHITE);

    g2d.drawString(status, itemTextX, itemTextY);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
  }
}
