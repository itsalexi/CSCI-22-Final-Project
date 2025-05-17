public class LevelingSystem {
  private double xp;
  private int level;
  private EconomySystem skillPoints;

  private static final double BASE_XP = 100;
  private static final double GROWTH_RATE = 1.2;

  public LevelingSystem() {
    xp = 0;
    level = 0;
  }

  public double xpToNextLevel() {
    return BASE_XP * Math.pow(GROWTH_RATE, level);
  }

  public void addXP(double amount) {
    xp += amount;
    while (xp >= xpToNextLevel()) {
      xp -= xpToNextLevel();
      level++;
      if (skillPoints != null) {
        skillPoints.setSkillPoints(skillPoints.getSkillPoints() + 1);
      }
    }
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
