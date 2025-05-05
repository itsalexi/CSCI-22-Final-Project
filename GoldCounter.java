import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class GoldCounter {
  private GameCanvas canvas;
  private Sprite goldUI;

  public GoldCounter(GameCanvas c) {
    canvas = c;
    ArrayList<File> goldUIFiles = new ArrayList<>();
    goldUIFiles.add(new File("assets/ui/gold.png"));
    goldUI = new Sprite(goldUIFiles, true);
  }

  public void draw(Graphics2D g2d) {
    double x = (canvas.getWidth() - goldUI.getWidth());
    g2d.translate(x, 0);
    goldUI.draw(g2d);

    String goldString = Integer.toString(canvas.getEconomySystem().getBalance());

    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", 1, 25));

    float goldLabelX = 50;
    float goldLabelY = 30;

    g2d.setColor(Color.BLACK);
    g2d.drawString(goldString, goldLabelX + 1, goldLabelY - 1);
    g2d.drawString(goldString, goldLabelX + 1, goldLabelY + 1);
    g2d.drawString(goldString, goldLabelX - 1, goldLabelY - 1);
    g2d.drawString(goldString, goldLabelX - 1, goldLabelY + 1);
    g2d.setColor(Color.WHITE);

    g2d.drawString(goldString, goldLabelX, goldLabelY);

    g2d.translate(-x, 0);
  }
}
