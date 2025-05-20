/**
 * The PlayerSprite class manages the animation and rendering of player sprites in the game.
 * It handles sprite states, directions, and frame animations for the player character.
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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerSprite {

  private String direction;
  private String animationState;
  private Sprite currentSprite;
  private int currentFrame;
  private long lastFrameTime;
  private long animationSpeed;

  private final Map<String, Integer> animationFrames;
  private final Map<String, Sprite> spritesMap;

  /**
   * Constructs a new PlayerSprite with specified sprite paths and frame counts.
   * 
   * @param spritePaths map of animation states to their sprite paths
   * @param frameCounts map of animation states to their frame counts
   */
  public PlayerSprite(Map<String, String> spritePaths, Map<String, Integer> frameCounts) {
    animationFrames = new HashMap<>(frameCounts);
    spritesMap = new HashMap<>();

    for (Map.Entry<String, String> entry : spritePaths.entrySet()) {
      String state = entry.getKey();
      String path = entry.getValue();
      SpriteFiles files = new SpriteFiles(path);
      spritesMap.put(state, new Sprite(files.getFiles(), 64));
    }

    direction = "DOWN";
    animationState = "idle";
    currentSprite = spritesMap.get("idle");
    currentFrame = 0;
    animationSpeed = 125;
    lastFrameTime = System.currentTimeMillis();

    updateFrameForDirection();
  }

  /**
   * Updates the animation frame based on the current time and animation state.
   */
  public void tick() {
    long now = System.currentTimeMillis();
    if (now - lastFrameTime >= animationSpeed) {
      advanceFrame();
      lastFrameTime = now;
    }
  }

  /**
   * Advances to the next frame in the animation sequence.
   */
  private void advanceFrame() {
    int frames = animationFrames.get(animationState);
    int index = (currentFrame + 1) % frames;
    setFrameForDirectionAndIndex(direction, index);
  }

  /**
   * Sets the current frame based on direction and index.
   * 
   * @param dir the direction of the sprite
   * @param index the frame index
   */
  private void setFrameForDirectionAndIndex(String dir, int index) {
    int frames = animationFrames.get(animationState);
    int base = switch (dir) {
      case "UP" -> frames;
      case "RIGHT", "LEFT" -> 2 * frames;
      default -> 0;
    };
    currentFrame = base + index;
    currentSprite.setSprite(currentFrame);
  }

  /**
   * Updates the current frame based on the current direction.
   */
  public void updateFrameForDirection() {
    setFrameForDirectionAndIndex(direction, 0);
  }

  /**
   * Sets the animation state of the sprite.
   * 
   * @param state the new animation state
   */
  public void setAnimationState(String state) {
    if (!animationState.equals(state) && spritesMap.containsKey(state)) {
      animationState = state;
      currentSprite = spritesMap.get(state);
      currentFrame = 0;
      updateFrameForDirection();
      lastFrameTime = System.currentTimeMillis();
    }
  }

  /**
   * Sets the direction of the sprite and updates its appearance.
   * 
   * @param newDirection the new direction to face
   */
  public void setDirection(String newDirection) {
    if (!direction.equals(newDirection)) {
      direction = newDirection;
      updateFrameForDirection();

      boolean shouldFlip = newDirection.equals("LEFT");
      for (Sprite sprite : spritesMap.values()) {
        sprite.setFlippedHorizontal(shouldFlip);
      }
    }
  }

  /**
   * Draws the sprite at the specified position.
   * 
   * @param g2d the graphics context
   * @param x the x-coordinate
   * @param y the y-coordinate
   */
  public void draw(Graphics2D g2d, double x, double y) {
    currentSprite.setPosition(x, y);
    currentSprite.draw(g2d);
  }

  /**
   * Gets the current direction of the sprite.
   * 
   * @return the current direction
   */
  public String getDirection() {
    return direction;
  }

  /**
   * Gets the current animation state.
   * 
   * @return the current animation state
   */
  public String getAnimationState() {
    return animationState;
  }

  /**
   * Gets the horizontal scale of the sprite.
   * 
   * @return the horizontal scale
   */
  public double getHScale() {
    return currentSprite.getHScale();
  }

  /**
   * Gets the vertical scale of the sprite.
   * 
   * @return the vertical scale
   */
  public double getVScale() {
    return currentSprite.getVScale();
  }

  /**
   * Gets the width of the sprite.
   * 
   * @return the width
   */
  public double getWidth() {
    return currentSprite.getWidth();
  }

  /**
   * Gets the height of the sprite.
   * 
   * @return the height
   */
  public double getHeight() {
    return currentSprite.getHeight();
  }
}
