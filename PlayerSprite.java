import java.awt.*;
import java.util.HashMap;

public class PlayerSprite {

  private Sprite idleSprites;
  private Sprite walkSprites;
  private Sprite hoeSprites;

  private String direction;
  private Sprite currentSprite;
  private int currentFrame;
  private long lastFrameTime;
  private long animationSpeed;
  private String animationState;

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
    animationSpeed = 125;
    lastFrameTime = System.currentTimeMillis();

    updateFrameForDirection();
  }

  public void tick() {
    long now = System.currentTimeMillis();
    if (now - lastFrameTime >= animationSpeed) {
      advanceFrame();
      lastFrameTime = now;
    }
  }

  private void advanceFrame() {
    int framesPerDirection = animationFrames.get(animationState);
    int localFrame = (currentFrame % framesPerDirection + 1) % framesPerDirection;
    setFrameForDirectionAndIndex(direction, localFrame);
  }

  private void setFrameForDirectionAndIndex(String dir, int index) {
    int framesPerDirection = animationFrames.get(animationState);
    int base = switch (dir) {
      case "UP" -> framesPerDirection;
      case "RIGHT", "LEFT" -> 2 * framesPerDirection;
      default -> 0;
    };
    currentFrame = base + index;
    currentSprite.setSprite(currentFrame);
  }

  public void updateFrameForDirection() {
    setFrameForDirectionAndIndex(direction, 0);
  }

  public void setAnimationState(String state) {
    if (!animationState.equals(state)) {
      animationState = state;
      currentSprite = spritesMap.get(state);
      currentFrame = 0;
      updateFrameForDirection();
      lastFrameTime = System.currentTimeMillis();
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

  public String getDirection() {
    return direction;
  }

  public String getAnimationState() {
    return animationState;
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
