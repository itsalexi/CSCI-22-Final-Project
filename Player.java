/**
 * The Player class represents a player entity in the game.
 * It manages player movement, animations, actions, and interactions with the game world.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 19, 2025
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
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    private double baseSpeed;
    private double speed;
    private PlayerSprite sprite;
    private long animStartTime;

    private int type;
    private String id;
    private int reach;

    private Map<String, PlayerAction> playerActions;
    private String activeTool;
    private Map<String, Boolean> activeDirections;

    /**
     * Creates a new Player instance.
     * Initializes player properties, animations, and actions.
     * 
     * @param u the player's username/ID
     * @param t the player's skin type
     */
    public Player(String u, int t) {
        super(0, 0);
        baseSpeed = 2;
        speed = 2;
        reach = 2;
        type = t;
        id = u;
        activeTool = "hoe";
        String pathName = String.format("assets/characters/%d/", type);

        Map<String, String> animations = Map.of(
                "idle", pathName + "idle",
                "walk", pathName + "walk",
                "hoe", pathName + "hoe",
                "water", pathName + "watering", "plant", pathName + "plant", "swim", pathName + "swim", "swim_idle",
                pathName + "swim_idle");

        Map<String, Integer> frames = Map.of(
                "idle", 4,
                "walk", 6,
                "hoe", 6,
                "water", 8, "plant", 5, "swim", 4, "swim_idle", 4);

        activeDirections = new HashMap<>(Map.of(
                "UP", false,
                "DOWN", false,
                "LEFT", false,
                "RIGHT", false));

        sprite = new PlayerSprite(animations, frames);
        playerActions = new HashMap<>();
        playerActions.put("hoe", new PlayerAction("hoe", 750));
        playerActions.put("water", new PlayerAction("watering", 750));
        playerActions.put("plant", new PlayerAction("plant", 750));
    }

    /**
     * Gets the base movement speed.
     * 
     * @return the base speed value
     */
    public double getBaseSpeed() {
        return baseSpeed;
    }

    /**
     * Sets the base movement speed.
     * 
     * @param s the new base speed value
     */
    public void setBaseSpeed(double s) {
        baseSpeed = s;
    }

    /**
     * Gets the player's reach distance.
     * 
     * @return the reach distance
     */
    public int getReach() {
        return reach;
    }

    /**
     * Sets the player's reach distance.
     * 
     * @param r the new reach distance
     */
    public void setReach(int r) {
        reach = r;
    }

    /**
     * Updates player state and animations.
     */
    @Override
    public void tick() {
        sprite.tick();
        for (PlayerAction action : playerActions.values()) {
            if (action.isRunning()) {
                long now = System.currentTimeMillis();
                if (now - animStartTime >= action.getDuration()) {
                    action.setRunning(false);
                    sprite.setAnimationState("idle");
                }
            }
        }
    }

    /**
     * Gets the currently active tool.
     * 
     * @return the active tool name
     */
    public String getActiveTool() {
        return activeTool;
    }

    /**
     * Sets the active tool.
     * 
     * @param t the new active tool name
     */
    public void setActiveTool(String t) {
        activeTool = t;
    }

    /**
     * Checks if the player is currently performing any action.
     * 
     * @return true if an action is in progress
     */
    public boolean isDoingAction() {
        for (PlayerAction action : playerActions.values()) {
            if (action.isRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the map of available player actions.
     * 
     * @return the map of player actions
     */
    public Map<String, PlayerAction> getPlayerActions() {
        return playerActions;
    }

    /**
     * Initiates a player action if no other action is in progress.
     * 
     * @param name the name of the action to perform
     */
    public void useAction(String name) {
        if (isDoingAction())
            return;
        PlayerAction action = playerActions.get(name);
        action.setRunning(true);
        animStartTime = System.currentTimeMillis();
        sprite.setAnimationState(name);
    }

    /**
     * Draws the player sprite at the current position.
     * 
     * @param g2d the graphics context
     */
    @Override
    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, x, y);
    }

    /**
     * Sets the current animation state.
     * 
     * @param state the new animation state
     */
    @Override
    public void setAnimationState(String state) {
        super.setAnimationState(state);
        sprite.setAnimationState(state);
    }

    /**
     * Sets the player's movement direction.
     * 
     * @param direction the new direction
     */
    @Override
    public void setDirection(String direction) {
        super.setDirection(direction);
        sprite.setDirection(direction);
    }

    /**
     * Gets the player's hitbox at a specified position.
     * 
     * @param newX the x coordinate
     * @param newY the y coordinate
     * @return the hitbox rectangle
     */
    @Override
    public Rectangle2D getHitboxAt(double newX, double newY) {
        double offsetX = sprite.getWidth() * sprite.getHScale() * 12 / 32;
        double offsetY = sprite.getHeight() * sprite.getVScale() * 20 / 32;

        return new Rectangle2D.Double(
                newX + offsetX,
                newY + offsetY,
                sprite.getWidth() - sprite.getWidth() * sprite.getHScale() * 8 / 32,
                sprite.getHeight() - sprite.getHeight() * sprite.getVScale() * 8 / 32);
    }

    /**
     * Gets the sprite's visual dimensions.
     * 
     * @return the sprite dimensions rectangle
     */
    public Rectangle2D getSpriteDimensions() {
        return new Rectangle2D.Double(
                x + getWidth() * 10 / 32,
                y + getHeight() * 6 / 32,
                getWidth() * 22 / 32,
                getHeight() * 26 / 32);
    }

    /**
     * Gets the player's ID.
     * 
     * @return the player ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the map of active movement directions.
     * 
     * @return the map of active directions
     */
    public Map<String, Boolean> getActiveDirections() {
        return activeDirections;
    }

    /**
     * Gets the player's width.
     * 
     * @return the width value
     */
    @Override
    public double getWidth() {
        return sprite.getWidth() * sprite.getHScale();
    }

    /**
     * Gets the player's height.
     * 
     * @return the height value
     */
    @Override
    public double getHeight() {
        return sprite.getHeight() * sprite.getVScale();
    }

    /**
     * Gets the current movement speed.
     * 
     * @return the speed value
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Gets the x coordinate.
     * 
     * @return the x position
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y coordinate.
     * 
     * @return the y position
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the current movement direction.
     * 
     * @return the direction
     */
    public String getDirection() {
        return sprite.getDirection();
    }

    /**
     * Sets the status of a movement direction.
     * 
     * @param direction the direction to update
     * @param isMoving whether the direction is active
     */
    public void setDirectionStatus(String direction, Boolean isMoving) {
        activeDirections.replace(direction, isMoving);
    }

    /**
     * Sets the player's position.
     * 
     * @param newX the new x coordinate
     * @param newY the new y coordinate
     */
    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
    }
}