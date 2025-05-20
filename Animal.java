/**
 * The Animal class represents a movable entity in the game world that can perform
 * random actions like walking and idling. It extends the Entity class.
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
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Random;


public class Animal extends Entity {

  private double speed;
  private AnimalSprite sprite;
  private float alpha;

  /**
   * Constructs a new Animal with specified position, type, skin, and size.
   * 
   * @param posX the x-coordinate position
   * @param posY the y-coordinate position
   * @param t the type of animal
   * @param skin the skin variant of the animal
   * @param size the size of the animal
   */
  public Animal(double posX, double posY, String t, int skin, int size) {
    super(posX, posY);
    speed = 1.0;
    alpha = 1;
    String pathName = String.format("assets/animals/%s/%d/", t, skin);

    Map<String, String> animations = Map.of(
        "idle", pathName + "idle",
        "walk", pathName + "walk");

    Map<String, Integer> frames = Map.of(
        "idle", 4,
        "walk", 4);

    sprite = new AnimalSprite(animations, frames, size);
  }

  @Override
  public void tick() {
    sprite.tick();
  }

  /**
   * Sets the opacity of the animal sprite
   * 
   * @param a the opacity of the animal
   */
  public void setOpacity(float a) {
    alpha = a;
  }

  /**
   * Performs a random action for the animal, either idling or walking in a random direction.
   * 
   * @param canvas the game canvas to check for collisions and boundaries
   */
  public void randomAction(GameCanvas canvas) {
    Random rand = new Random();
    if (rand.nextInt(60) == 0) {
      boolean shouldIdle = rand.nextBoolean();

      if (shouldIdle) {
        setDirection("LEFT");
        setAnimationState("idle");
      } else {
        String[] directions = { "UP", "DOWN", "LEFT", "RIGHT" };
        String randomDirection = directions[rand.nextInt(directions.length)];
        setDirection(randomDirection);
        setAnimationState("walk");
      }
    }

    if (getAnimationState().equals("walk")) {
      double nextX = x;
      double nextY = y;

      switch (getDirection()) {
        case "UP" -> nextY -= speed;
        case "DOWN" -> nextY += speed;
        case "LEFT" -> nextX -= speed;
        case "RIGHT" -> nextX += speed;
      }
      boolean outOfBounds = nextX < 0 || nextY < 0
          || nextX + getWidth() > canvas.getWidth()
          || nextY + getHeight() > canvas.getHeight();

      Rectangle2D temp = getHitboxAt(nextX, nextY);
      if (!outOfBounds && !canvas.isColliding(temp)) {
        x = nextX;
        y = nextY;
      } else {
        setAnimationState("idle");
      }
    }
  }

  @Override
  public void draw(Graphics2D g2d) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    sprite.draw(g2d, x, y);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
  }

  @Override
  public void setAnimationState(String state) {
    super.setAnimationState(state);
    sprite.setAnimationState(state);
  }

  /**
   * Sets the movement speed of the animal.
   * 
   * @param s the new speed value
   */
  public void setSpeed(double s) {
    speed = s;
  }

  @Override
  public double getWidth() {
    return sprite.getWidth();
  }

  @Override
  public double getHeight() {
    return sprite.getHeight();
  }

  /**
   * Gets the current movement speed of the animal.
   * 
   * @return the current speed value
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Gets the x-coordinate position of the animal.
   * 
   * @return the x-coordinate
   */
  public double getX() {
    return x;
  }

  /**
   * Calculates the hitbox of the animal at a specified position.
   * 
   * @param newX the x-coordinate to calculate hitbox at
   * @param newY the y-coordinate to calculate hitbox at
   * @return the hitbox rectangle at the specified position
   */
  public Rectangle2D getHitboxAt(double newX, double newY) {
    double offsetX = sprite.getWidth() * sprite.getHScale() * 8 / 32;
    double offsetY = sprite.getHeight() * sprite.getVScale() * 10 / 32;

    return new Rectangle2D.Double(
        newX + offsetX,
        newY + offsetY,
        sprite.getWidth(),
        sprite.getHeight());
  }

  /**
   * Gets the y-coordinate position of the animal.
   * 
   * @return the y-coordinate
   */
  public double getY() {
    return y;
  }

  /**
   * Gets the current direction the animal is facing.
   * 
   * @return the current direction
   */
  public String getDirection() {
    return sprite.getDirection();
  }

  /**
   * Sets the direction the animal is facing.
   * 
   * @param direction the new direction to face
   */
  public void setDirection(String direction) {
    sprite.setDirection(direction);
  }

  /**
   * Sets the position of the animal.
   * 
   * @param newX the new x-coordinate
   * @param newY the new y-coordinate
   */
  public void setPosition(double newX, double newY) {
    x = newX;
    y = newY;
  }

}