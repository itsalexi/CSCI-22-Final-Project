import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerSprite {

  private Sprite idleSprites;
  private Sprite walkSprites;
  private Sprite currentSprite;
  private int currentFrame;
  private Timer animationTimer;
  private String animationState; // "idle" or "walk"
  private String direction; // "DOWN", "UP", "RIGHT", "LEFT"

  public PlayerSprite(String idlePath, String walkPath) {
    SpriteFiles playerIdleFiles = new SpriteFiles(idlePath);
    SpriteFiles playerWalkFiles = new SpriteFiles(walkPath);

    idleSprites = new Sprite(playerIdleFiles.getFiles());
    walkSprites = new Sprite(playerWalkFiles.getFiles());
    currentSprite = idleSprites;
    currentFrame = 0;
    animationState = "idle";
    direction = "DOWN";

    startAnimation();
  }

  private void startAnimation() {
    animationTimer = new Timer(100, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateAnimation();
      }
    });
    animationTimer.start();
  }

  private void updateAnimation() {
    int framesPerDirection = animationState.equals("idle") ? 4 : 6;

    switch (direction) {
      case "DOWN":
        currentFrame = (currentFrame + 1) % framesPerDirection;
        break;
      case "UP":
        currentFrame = (currentFrame + 1) % framesPerDirection + framesPerDirection;
        break;
      case "RIGHT":
        currentFrame = (currentFrame + 1) % framesPerDirection + 2 * framesPerDirection;
        break;
      case "LEFT":
        currentFrame = (currentFrame + 1) % framesPerDirection + 2 * framesPerDirection;
        break;
    }

    currentSprite.setSprite(currentFrame);
  }

  public void setAnimationState(String state) {
    if (!animationState.equals(state)) {
      animationState = state;
      currentFrame = 0;
      currentSprite = animationState.equals("idle") ? idleSprites : walkSprites;
    }
  }

  public void setDirection(String newDirection) {
    if (!direction.equals(newDirection)) {
      direction = newDirection;
      currentFrame = 0;
    }
  }

  public void draw(Graphics2D g2d, double x, double y) {
    if (direction.equals("LEFT")) {
      BufferedImage frame = currentSprite.getCurrentSprite();
      int width = frame.getWidth();
      int height = frame.getHeight();
      g2d.drawImage(frame, (int) x + width, (int) y, -width, height, null);
    } else {
      currentSprite.setPosition(x, y);
      currentSprite.draw(g2d);
    }
  }

  public void stopAnimation() {
    if (animationTimer != null) {
      animationTimer.stop();
    }
  }
}