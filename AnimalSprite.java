import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AnimalSprite {

  private String direction;
  private String animationState;
  private Sprite currentSprite;
  private int currentFrame;
  private long lastFrameTime;
  private long animationSpeed;

  private final Map<String, Integer> animationFrames;
  private final Map<String, Sprite> spritesMap;

  public AnimalSprite(Map<String, String> spritePaths, Map<String, Integer> frameCounts, int size) {
    animationFrames = new HashMap<>(frameCounts);
    spritesMap = new HashMap<>();

    for (Map.Entry<String, String> entry : spritePaths.entrySet()) {
      String state = entry.getKey();
      String path = entry.getValue();
      SpriteFiles files = new SpriteFiles(path);
      spritesMap.put(state, new Sprite(files.getFiles(), size));
    }

    direction = "LEFT";
    animationState = "idle";
    currentSprite = spritesMap.get("idle");
    currentFrame = 0;
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
    int frames = animationFrames.get(animationState);

    int index = (currentFrame % frames + 1) % frames;
    setFrameForDirectionAndIndex(direction, index);
  }

  private void setFrameForDirectionAndIndex(String dir, int index) {
    int frames = animationFrames.get(animationState);

    int base;
    if (animationState.equals("idle")) {
      base = 0;
    } else {
      base = switch (dir) {
        case "UP" -> 2 * frames;
        case "DOWN" -> frames;
        default -> 0;
      };
    }

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

      boolean shouldFlip = newDirection.equals("RIGHT");
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
