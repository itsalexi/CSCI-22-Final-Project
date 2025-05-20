/**
 * The Entity class is an abstract base class for all game entities.
 * It provides the blueprint for movement, animation states, and collision detection.
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
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected double x, y;
    protected double speed;
    protected String direction;
    protected String animationState;

    /**
     * Creates a new Entity.
     * 
     * @param posX x position
     * @param posY y position
     */
    public Entity(double posX, double posY) {
        x = posX;
        y = posY;
        speed = 1.0;
        direction = "DOWN";
        animationState = "idle";
    }

    /**
     * Updates the entity state.
     */
    public abstract void tick();

    /**
     * Draws the entity.
     * 
     * @param g2d graphics context
     */
    public abstract void draw(Graphics2D g2d);

    /**
     * Gets the hitbox at position.
     * 
     * @param newX x position
     * @param newY y position
     * @return hitbox
     */
    public abstract Rectangle2D getHitboxAt(double newX, double newY);

    /**
     * Gets the width.
     * 
     * @return width
     */
    public abstract double getWidth();

    /**
     * Gets the height.
     * 
     * @return height
     */
    public abstract double getHeight();

    /**
     * Sets the position.
     * 
     * @param newX x position
     * @param newY y position
     */
    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
    }

    /**
     * Sets the speed.
     * 
     * @param s speed
     */
    public void setSpeed(double s) {
        speed = s;
    }

    /**
     * Gets the speed.
     * 
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Gets the x position.
     * 
     * @return x position
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y position.
     * 
     * @return y position
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the direction.
     * 
     * @return direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the direction.
     * 
     * @param d direction
     */
    public void setDirection(String d) {
        direction = d;
    }

    /**
     * Gets the animation state.
     * 
     * @return animation state
     */
    public String getAnimationState() {
        return animationState;
    }

    /**
     * Sets the animation state.
     * 
     * @param state animation state
     */
    public void setAnimationState(String state) {
        animationState = state;
    }
} 