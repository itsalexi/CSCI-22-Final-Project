import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class Player {

    private double x, y;
    private double speed;
    private PlayerSprite sprite;
    private long animStartTime;

    private int type;
    private String username;

    private Map<String, PlayerAction> playerActions;
    private String activeTool;

    public Player(String u, int t) {
        x = 0;
        y = 0;
        speed = 2.0;
        type = t;
        username = u;
        activeTool = "hoe";
        String pathName = String.format("assets/characters/%d/", type);

        Map<String, String> animations = Map.of(
                "idle", pathName + "idle",
                "walk", pathName + "walk",
                "hoe", pathName + "hoe",
                "water", pathName + "watering");

        Map<String, Integer> frames = Map.of(
                "idle", 4,
                "walk", 6,
                "hoe", 6,
                "water", 8);

        sprite = new PlayerSprite(animations, frames);
        playerActions = new HashMap<>();
        playerActions.put("hoe", new PlayerAction("hoe", 750));
        playerActions.put("water", new PlayerAction("watering", 750));

    }

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

    public void useAction(String name) {
        if (isDoingAction())
            return;
        PlayerAction action = playerActions.get(name);
        action.setRunning(true);
        animStartTime = System.currentTimeMillis();
        sprite.setAnimationState(name);
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, x, y);
    }

    public void setAnimationState(String state) {
        sprite.setAnimationState(state);
    }


    public String getAnimationState() {
        return sprite.getAnimationState();
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getWidth() {
        return sprite.getWidth();
    }

    public double getHeight() {
        return sprite.getHeight();
    }

    public double getSpeed() {
        return speed;
    }

    public double getX() {
        return x;
    }

    public Rectangle2D getHitboxAt(double newX, double newY) {
        double offsetX = sprite.getWidth() * sprite.getHScale() * 8 / 32;
        double offsetY = sprite.getHeight() * sprite.getVScale() * 10 / 32;

        return new Rectangle2D.Double(
                newX + offsetX,
                newY + offsetY,
                sprite.getWidth(),
                sprite.getHeight());
    }

    public double getY() {
        return y;
    }

    public String getDirection() {
        return sprite.getDirection();
    }

    public void setDirection(String direction) {
        sprite.setDirection(direction);
    }

    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
    }

}