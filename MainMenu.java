import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JComponent {
  private Sprite background;

  private GameStarter starter;
  private Sprite playButton;
  private Sprite gameLogo;

  private int buttonX = 352;
  private int buttonY = 490;
  private int logoX;
  private int logoY;
  private int centerX;
  private int centerY;
  private int buttonWidth;
  private int buttonHeight;
  private SetupFrame frame;

  public MainMenu(GameStarter s, SetupFrame f) {
    starter = s;
    frame = f;

    SpriteFiles bgFiles = new SpriteFiles("assets/backgrounds/");
    SpriteFiles buttonFiles = new SpriteFiles("assets/buttons/");
    SpriteFiles logoFiles = new SpriteFiles("assets/ui/mainmenu/");

    background = new Sprite(bgFiles.getFiles(), true);
    playButton = new Sprite(buttonFiles.getFiles(), true);
    gameLogo = new Sprite(logoFiles.getFiles(), 300);

    background.setSprite((int) (Math.random() * bgFiles.getFiles().size()));
    logoX = 512 - (int) (gameLogo.getWidth() * gameLogo.getHScale() / 2);
    centerX = 512 - (int) (gameLogo.getWidth() * gameLogo.getHScale() / 2);
    logoY = 100;
    centerY = 100;
    buttonWidth = (int) playButton.getWidth();
    buttonHeight = (int) playButton.getHeight();
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getX() >= buttonX && e.getX() <= buttonX + buttonWidth &&
            e.getY() >= buttonY && e.getY() <= buttonY + buttonHeight) {
          handlePlayButtonClick();
        }
      }
    });

    addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        logoX = centerX + (int) ((centerX - x) * 0.05);
        logoY = centerY + (int) ((centerY - y) * 0.05);
        repaint();
      }

      @Override
      public void mouseDragged(MouseEvent e) {
      }

    });
  }

  private void handlePlayButtonClick() {
    frame.showConnectionScreen();
    frame.playSound("ui_click");
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    background.draw(g2d);

    g2d.translate(buttonX, buttonY);
    playButton.draw(g2d);
    g2d.translate(-buttonX, -buttonY);

    g2d.translate(logoX, logoY);
    gameLogo.draw(g2d);
    g2d.translate(-logoX, -logoY);
  }
}
