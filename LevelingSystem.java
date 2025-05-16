public class LevelingSystem {
  private double xp;
  private int level;
  private EconomySystem skillPoints;

  public LevelingSystem() {
    xp = 0;
    level = 0;
  }

  // public double xpToNextLevel() {
  // }

  public void addXP(double xp) {
    // add xp, if level increased, increase the level, and add a skill point

  }

  public void setLevel(int l) {
    level = l;
  }

  public double getXP() {
    return xp;
  }

  public int getLevel() {
    return level;
  }

}
