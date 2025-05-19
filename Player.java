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

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(double s) {
        baseSpeed = s;
    }

    public int getReach() {
        return reach;
    }

    public void setReach(int r) {
        reach = r;
    }

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

    public String getActiveTool() {
        return activeTool;
    }

    public void setActiveTool(String t) {
        activeTool = t;
    }

    public boolean isDoingAction() {
        for (PlayerAction action : playerActions.values()) {
            if (action.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public Map<String, PlayerAction> getPlayerActions() {
        return playerActions;
    }

    public void useAction(String name) {
        if (isDoingAction())
            return;
        PlayerAction action = playerActions.get(name);
        action.setRunning(true);
        animStartTime = System.currentTimeMillis();
        sprite.setAnimationState(name);
    }

    @Override
    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, x, y);
    }

    @Override
    public void setAnimationState(String state) {
        super.setAnimationState(state);
        sprite.setAnimationState(state);
    }

    @Override
    public void setDirection(String direction) {
        super.setDirection(direction);
        sprite.setDirection(direction);
    }

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

    public Rectangle2D getSpriteDimensions() {
        return new Rectangle2D.Double(
                x + getWidth() * 10 / 32,
                y + getHeight() * 6 / 32,
                getWidth() * 22 / 32,
                getHeight() * 26 / 32);
    }

    public String getId() {
        return id;
    }

    public Map<String, Boolean> getActiveDirections() {
        return activeDirections;
    }

    @Override
    public double getWidth() {
        return sprite.getWidth() * sprite.getHScale();
    }

    @Override
    public double getHeight() {
        return sprite.getHeight() * sprite.getVScale();
    }

    public double getSpeed() {
        return speed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getDirection() {
        return sprite.getDirection();
    }

    public void setDirectionStatus(String direction, Boolean isMoving) {
        activeDirections.replace(direction, isMoving);
    }

    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
    }
}