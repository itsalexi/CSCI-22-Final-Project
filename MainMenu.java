import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JComponent {
  private Sprite background;

  private GameStarter starter;
  private Sprite playButton;

  private int buttonX = 352;
  private int buttonY = 490;
  private int buttonWidth;
  private int buttonHeight;
  private SetupFrame frame;

  public MainMenu(GameStarter s, SetupFrame f) {
    starter = s;
    frame = f;
    SpriteFiles bgFiles = new SpriteFiles("assets/backgrounds/");
    SpriteFiles buttonFiles = new SpriteFiles("assets/buttons/");
    background = new Sprite(bgFiles.getFiles(), true);
    playButton = new Sprite(buttonFiles.getFiles(), true);

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
  }

  private void handlePlayButtonClick() {
    starter.startGame();
    frame.getSetupFrame().setVisible(false);
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    background.draw(g2d);

    g2d.translate(buttonX, buttonY);
    playButton.draw(g2d);
    g2d.translate(-buttonX, -buttonY);
  }
}
