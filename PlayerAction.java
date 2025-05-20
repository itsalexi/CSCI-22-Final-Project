/**
 * The PlayerAction class represents an action that a player can perform in the game.
 * It manages the animation and duration of player actions like hoeing, watering, and planting.
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
public class PlayerAction {
  private String animationName;
  private long duration;
  private boolean running;

  /**
   * Creates a new PlayerAction instance.
   * 
   * @param an the name of the animation to play for this action
   * @param d the duration of the action in milliseconds
   */
  public PlayerAction(String an, long d) {
    animationName = an;
    duration = d;
    running = false;
  }

  /**
   * Gets the name of the animation associated with this action.
   * 
   * @return the animation name
   */
  public String getName() {
    return animationName;
  }

  /**
   * Sets whether this action is currently running.
   * 
   * @param r true if the action is running, false otherwise
   */
  public void setRunning(boolean r) {
    running = r;
  }

  /**
   * Checks if this action is currently running.
   * 
   * @return true if the action is running, false otherwise
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Gets the duration of this action.
   * 
   * @return the duration in milliseconds
   */
  public long getDuration() {
    return duration;
  }
}