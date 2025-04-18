import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Random;

public class Animal {

  private double x, y;
  private double speed;
  private AnimalSprite sprite;

  public Animal(double posX, double posY, String t, int skin, int size) {
    x = posX;
    y = posY;
    speed = 1.0;
    String pathName = String.format("assets/animals/%s/%d/", t, skin);

    Map<String, String> animations = Map.of(
        "idle", pathName + "idle",
        "walk", pathName + "walk");

    Map<String, Integer> frames = Map.of(
        "idle", 4,
        "walk", 4);

    sprite = new AnimalSprite(animations, frames, size);
  }

  public void tick() {
    sprite.tick();
  }

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