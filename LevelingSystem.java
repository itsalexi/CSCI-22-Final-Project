import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class LevelingSystem {
  private double xp;
  private int level;
  private EconomySystem skillPoints;
  private GameCanvas canvas;

  private static final double BASE_XP = 100;
  private static final double GROWTH_RATE = 1.2;

  private long lastXPGain;

  private Timer xpTextTimer;
  private float xpTextAlpha;
  private String status;

  public LevelingSystem(EconomySystem sp, GameCanvas c) {
    xp = 90;
    level = 0;
    skillPoints = sp;
    lastXPGain = System.currentTimeMillis();
    xpTextAlpha = 0;
    canvas = c;
    status = "";

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
    lastXPGain = System.currentTimeMillis();
    xp += amount;
    status = String.format("+%s (%s/%s)", amount, xp, xpToNextLevel());
    while (xp >= xpToNextLevel()) {
      xp -= xpToNextLevel();
      level++;
      canvas.playLocalSound("level_up");
      if (skillPoints != null) {
        skillPoints.setSkillPoints(skillPoints.getSkillPoints() + 1);
      }
      status = String.format("Level %s -> [%s]", level - 1, level);
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

  public void draw(Graphics2D g2d) {

    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, xpTextAlpha));

    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Minecraft", 1, 24));

    int textWidth = g2d.getFontMetrics().stringWidth(status);
    float itemTextX = (canvas.getWidth()) / 2 - (textWidth / 2);
    float itemTextY = canvas.getHeight() - 100;

    g2d.setColor(Color.BLACK);
    g2d.drawString(status, itemTextX + 1, itemTextY - 1);
    g2d.drawString(status, itemTextX + 1, itemTextY + 1);
    g2d.drawString(status, itemTextX - 1, itemTextY - 1);
    g2d.drawString(status, itemTextX - 1, itemTextY + 1);
    g2d.setColor(Color.WHITE);

    g2d.drawString(status, itemTextX, itemTextY);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

  }

}
