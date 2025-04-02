import java.awt.*;

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

    public void move(String direction) {
        sprite.setAnimationState("walk");
        sprite.setDirection(direction);

        switch (direction) {
            case "UP":
                y -= speed;
                break;
            case "DOWN":
                y += speed;
                break;
            case "LEFT":
                x -= speed;
                break;
            case "RIGHT":
                x += speed;
                break;
        }
    }

    public void stop() {
        sprite.setAnimationState("idle");
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, x, y);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}