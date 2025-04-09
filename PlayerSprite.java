import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Timer;

public class PlayerSprite {

  private Sprite idleSprites;
  private Sprite walkSprites;
  private Sprite hoeSprites;

  private String direction;
  private Sprite currentSprite;
  private int currentFrame;
  private Timer animationTimer;
  private String animationState; // "idle" or "walk"
  private HashMap<String, Integer> animationFrames;
  private HashMap<String, Sprite> spritesMap;

  public PlayerSprite(String idlePath, String walkPath, String hoePath) {
    SpriteFiles playerIdleFiles = new SpriteFiles(idlePath);
    SpriteFiles playerWalkFiles = new SpriteFiles(walkPath);
    SpriteFiles playerHoeFiles = new SpriteFiles(hoePath);
    animationFrames = new HashMap<>();
    spritesMap = new HashMap<>();

    animationFrames.put("idle", 4);
    animationFrames.put("walk", 6);
    animationFrames.put("hoe", 6);

    idleSprites = new Sprite(playerIdleFiles.getFiles(), 64);
    walkSprites = new Sprite(playerWalkFiles.getFiles(), 64);
    hoeSprites = new Sprite(playerHoeFiles.getFiles(), 64);

    spritesMap.put("idle", idleSprites);
    spritesMap.put("walk", walkSprites);
    spritesMap.put("hoe", hoeSprites);

    currentSprite = idleSprites;
    currentFrame = 0;
    animationState = "idle";
    direction = "DOWN";

    startAnimation();
  }

  private void startAnimation() {
    animationTimer = new Timer(125, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateAnimation();
      }
    });
    animationTimer.start();
  }

  private void updateAnimation() {
    int framesPerDirection = animationFrames.get(animationState);

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

  public void updateFrameForDirection() {
    int framesPerDirection = animationFrames.get(animationState);

    switch (direction) {
      case "DOWN":
        currentFrame = 0;
        break;
      case "UP":
        currentFrame = framesPerDirection;
        break;
      case "RIGHT":
      case "LEFT":
        currentFrame = 2 * framesPerDirection;
        break;
    }

    currentSprite.setSprite(currentFrame);
  }

  public void setAnimationState(String state) {
    if (!animationState.equals(state)) {
      animationState = state;
      currentSprite = spritesMap.get(state);
      updateFrameForDirection();
    }
  }

  public void setDirection(String newDirection) {
    if (!direction.equals(newDirection)) {
      direction = newDirection;

      updateFrameForDirection();

      boolean shouldFlip = direction.equals("LEFT");
      idleSprites.setFlippedHorizontal(shouldFlip);
      walkSprites.setFlippedHorizontal(shouldFlip);
      hoeSprites.setFlippedHorizontal(shouldFlip);
    }
  }

  public void draw(Graphics2D g2d, double x, double y) {
    currentSprite.setPosition(x, y);
    currentSprite.draw(g2d);
  }

  public void stopAnimation() {
    if (animationTimer != null) {
      animationTimer.stop();
    }
  }

  public String getDirection() {
    return direction;
  }

  public double getHScale() {
    return currentSprite.getHScale();
  }

  public double getVScale() {
    return currentSprite.getVScale();
  }

  public double getWidth() {
    return currentSprite.getWidth();
  }

  public double getHeight() {
    return currentSprite.getHeight();
  }

}