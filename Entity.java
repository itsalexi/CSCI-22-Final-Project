import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected double x, y;
    protected double speed;
    protected String direction;
    protected String animationState;

    public Entity(double posX, double posY) {
        x = posX;
        y = posY;
        speed = 1.0;
        direction = "DOWN";
        animationState = "idle";
    }

    public abstract void tick();
    public abstract void draw(Graphics2D g2d);
    public abstract Rectangle2D getHitboxAt(double newX, double newY);
    public abstract double getWidth();
    public abstract double getHeight();

    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
    }

    public void setSpeed(double s) {
        speed = s;
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
        return direction;
    }

    public void setDirection(String d) {
        direction = d;
    }

    public String getAnimationState() {
        return animationState;
    }

    public void setAnimationState(String state) {
        animationState = state;
    }
} 