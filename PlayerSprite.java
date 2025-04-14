import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerSprite {

  private String direction;
  private String animationState;
  private Sprite currentSprite;
  private int currentFrame;
  private long lastFrameTime;
  private long animationSpeed;

  private final Map<String, Integer> animationFrames;
  private final Map<String, Sprite> spritesMap;

  public PlayerSprite(Map<String, String> spritePaths, Map<String, Integer> frameCounts) {
    this.animationFrames = new HashMap<>(frameCounts);
    this.spritesMap = new HashMap<>();

    for (Map.Entry<String, String> entry : spritePaths.entrySet()) {
      String state = entry.getKey();
      String path = entry.getValue();
      SpriteFiles files = new SpriteFiles(path);
      spritesMap.put(state, new Sprite(files.getFiles(), 64));
    }

    this.direction = "DOWN";
    this.animationState = "idle";
    this.currentSprite = spritesMap.get("idle");
    this.currentFrame = 0;
    this.animationSpeed = 125;
    this.lastFrameTime = System.currentTimeMillis();

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
    int frames = animationFrames.get(animationState);
    int index = (currentFrame % frames + 1) % frames;
    setFrameForDirectionAndIndex(direction, index);
  }

  private void setFrameForDirectionAndIndex(String dir, int index) {
    int frames = animationFrames.get(animationState);
    int base = switch (dir) {
      case "UP" -> frames;
      case "RIGHT", "LEFT" -> 2 * frames;
      default -> 0;
    };
    currentFrame = base + index;
    currentSprite.setSprite(currentFrame);
  }

  public void updateFrameForDirection() {
    setFrameForDirectionAndIndex(direction, 0);
  }

  public void setAnimationState(String state) {
    if (!animationState.equals(state) && spritesMap.containsKey(state)) {
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

      boolean shouldFlip = newDirection.equals("LEFT");
      for (Sprite sprite : spritesMap.values()) {
        sprite.setFlippedHorizontal(shouldFlip);
      }
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
