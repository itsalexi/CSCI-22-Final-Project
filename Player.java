import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player {

    private double x, y;
    private double speed;
    private PlayerSprite sprite;

    public Player() {
        x = 0;
        y = 0;
        speed = 2.0;
        sprite = new PlayerSprite("assets/player/idle", "assets/player/walk");
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, x, y);
    }

    public void setAnimationState(String state) {
        sprite.setAnimationState(state);
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