import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;

public class Player {

    private double x, y;
    private double speed;
    private PlayerSprite sprite;
    private Timer hoeTimer;
    private boolean isHoeing;

    public Player() {
        x = 0;
        y = 0;
        speed = 2.0;
        sprite = new PlayerSprite("assets/player/idle", "assets/player/walk", "assets/player/hoe");
        isHoeing = false;

        hoeTimer = new Timer(750, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isHoeing = false;
                sprite.setAnimationState("idle");
                hoeTimer.stop();
            }
        });
        hoeTimer.setRepeats(false);
    }

    public void useHoe() {
        if (!isHoeing) {
            isHoeing = true;
            sprite.setAnimationState("hoe");
            hoeTimer.restart();
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