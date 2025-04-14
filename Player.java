import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player {

    private double x, y;
    private double speed;
    private PlayerSprite sprite;
    private boolean isHoeing;
    private final long hoeDuration = 750;
    private long hoeStartTime;
    private int type;
    private String username;

    public Player(String u, int t) {
        x = 0;
        y = 0;
        speed = 2.0;
        type = t;
        username = u;
        String pathName = String.format("assets/characters/%d/", type);
        sprite = new PlayerSprite(pathName + "idle", pathName + "walk", pathName + "hoe");
        isHoeing = false;
    }

    public void tick() {
        sprite.tick();
        if (isHoeing) {
            long now = System.currentTimeMillis();
            if (now - hoeStartTime >= hoeDuration) {
                isHoeing = false;
                sprite.setAnimationState("idle");
            }
        }
    }

    public void useHoe() {
        if (!isHoeing) {
            isHoeing = true;
            hoeStartTime = System.currentTimeMillis();
            sprite.setAnimationState("hoe");
        }
    }

    public boolean isHoeing() {
        return isHoeing;
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