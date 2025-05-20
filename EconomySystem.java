/**
 * The EconomySystem class manages player balance and skill points.
 * It handles currency transactions and skill points for the player.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version 20 May 2025
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

public class EconomySystem {
  private int balance, skillPoints;
  public GameCanvas canvas;

  /**
   * Creates a new EconomySystem.
   * 
   * @param c game canvas
   */
  public EconomySystem(GameCanvas c) {
    canvas = c;
    balance = 0;
    skillPoints = 0;
  }

  /**
   * Gets the balance.
   * 
   * @return balance
   */
  public int getBalance() {
    return balance;
  }

  /**
   * Gets the skill points.
   * 
   * @return skill points
   */
  public int getSkillPoints() {
    return skillPoints;
  }

  /**
   * Sets the skill points.
   * 
   * @param s skill points
   */
  public void setSkillPoints(int s) {
    canvas.getWriter().send(String.format("SKILLPOINTS SET %s %d", canvas.getClient().getPlayerID(), s));
    skillPoints = s;
  }

  /**
   * Sets the balance.
   * 
   * @param b balance
   */
  public void setBalance(int b) {
    canvas.getWriter().send(String.format("ECONOMY SET %s %d", canvas.getClient().getPlayerID(), b));
    balance = b;
  }
}
