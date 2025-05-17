import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class LevelingSystem {
  private double xp;
  private int level;
  private EconomySystem skillPoints;

  private static final double BASE_XP = 100;
  private static final double GROWTH_RATE = 1.2;

  private long lastXPGain;

  private Timer xpTextTimer;
  private float xpTextAlpha;

  public LevelingSystem(EconomySystem sp) {
    xp = 0;
    level = 0;
    skillPoints = sp;
    lastXPGain = System.currentTimeMillis();
    xpTextAlpha = 0;

    xpTextTimer = new Timer(1000 / 60, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Long delta = System.currentTimeMillis() - lastXPGain;
        xpTextAlpha = (float) clamp(0, 1, (double) -delta / 500 + 4);
      }
    });
    xpTextTimer.start();
  }

  private double clamp(double left, double right, double value) {
    return Math.max(left, Math.min(right, value));
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

  public void draw() {

  }

}
