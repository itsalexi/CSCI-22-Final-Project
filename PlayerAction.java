public class PlayerAction {
  private String animationName;
  private long duration;
  private boolean running;

  public PlayerAction(String an, long d) {
    animationName = an;
    duration = d;
    running = false;
  }

  public String getName() {
    return animationName;
  }

  public void setRunning(boolean r) {
    running = r;
  }
  public boolean isRunning() {
    return running;
  }

  public long getDuration() {
    return duration;
  }
}