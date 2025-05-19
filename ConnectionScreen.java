/**
 * The ConnectionScreen class represents the initial connection screen. It is where players
 * can enter server details, choose their character, and connect to the game.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 19, 2025
 * 
 * I have not discussed the Java language code in my program 
 * with anyone other than my instructor or the teaching assistants 
 * assigned to this course.
 * 
 * I have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in my program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of my program.
 */

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ConnectionScreen extends JPanel {
  private JTextField ipField, portField, usernameField;
  private JButton connectButton, leftButton, rightButton;
  private GameStarter starter;
  private SetupFrame frame;

  private int skin = 0;
  private final int maxSkins = 5;
  private Sprite currentAvatar;
  private Sprite background;

  private ArrayList<JTextField> textfields;

  /**
   * Constructs a new ConnectionScreen with server connection fields and character selection.
   * 
   * @param s the GameStarter instance
   * @param f the SetupFrame instance
   */
  public ConnectionScreen(GameStarter s, SetupFrame f) {
    starter = s;
    frame = f;
    textfields = new ArrayList<>();
    SpriteFiles bgFiles = new SpriteFiles("assets/backgrounds/");
    background = new Sprite(bgFiles.getFiles(), true);
    background.setSprite((int) (Math.random() * bgFiles.getFiles().size()));

    setLayout(null);
    setSize(1024, 768);

    ipField = new JTextField("localhost");
    portField = new JTextField("25565");
    usernameField = new JTextField("Player1");

    ipField.setBounds(getWidth() / 2 - 160, 420, 320, 30);
    portField.setBounds(getWidth() / 2 - 160, 460, 320, 30);
    usernameField.setBounds(getWidth() / 2 - 160, 500, 320, 30);

    textfields.add(ipField);
    textfields.add(portField);
    textfields.add(usernameField);

    for (JTextField textfield : textfields) {
      textfield.setBackground(Color.BLACK);
      textfield.setForeground(Color.WHITE);
      textfield.setFont(new Font("Minecraft", Font.PLAIN, 16));
      textfield.setCaretColor(Color.WHITE);
      int padding = 4;
      textfield.setBorder(new CompoundBorder(
          new LineBorder(Color.WHITE, 2),
          new EmptyBorder(padding, padding, padding, padding)));
    }

    connectButton = new JButton("Connect");
    connectButton.setBounds(getWidth() / 2 - 160, 560, 330, 60);

    leftButton = new JButton("<");
    rightButton = new JButton(">");

    leftButton.setIcon(new ImageIcon(
        new ImageIcon("assets/buttons/button_02.png").getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));

    rightButton.setIcon(new ImageIcon(
        new ImageIcon("assets/buttons/button_03.png").getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));

    connectButton.setIcon(new ImageIcon("assets/buttons/button_04.png"));
    leftButton.setBorderPainted(false);
    leftButton.setContentAreaFilled(false);
    leftButton.setFocusPainted(false);
    rightButton.setBorderPainted(false);
    rightButton.setContentAreaFilled(false);
    connectButton.setFocusPainted(false);
    connectButton.setBorderPainted(false);
    connectButton.setContentAreaFilled(false);
    connectButton.setFocusPainted(false);
    leftButton.setBounds(512 - 50 - 100, 300, 100, 100);
    rightButton.setBounds(512 + 50, 300, 100, 100);

    add(ipField);
    add(portField);
    add(usernameField);
    add(connectButton);
    add(leftButton);
    add(rightButton);

    loadAvatar();

    leftButton.addActionListener(e -> {
      if (skin > 0)
        skin--;
      loadAvatar();
      repaint();
      frame.playSound("ui_click");
    });

    rightButton.addActionListener(e -> {
      if (skin < maxSkins - 1)
        skin++;
      loadAvatar();
      repaint();
      frame.playSound("ui_click");

    });

    connectButton.addActionListener(e -> {
      String ip = ipField.getText();
      String port = portField.getText();
      String username = usernameField.getText();

      starter.setConnectionInfo(ip, port, username, skin);
      starter.startGame();
      f.setVisible(false);
      frame.playSound("ui_click");
      frame.getGameAudio().stop();

    });
  }

  /**
   * Loads the current avatar sprite based on the selected skin.
   */
  private void loadAvatar() {
    String path = "assets/characters/" + skin + "/avatar/";
    SpriteFiles spriteFiles = new SpriteFiles(path);
    currentAvatar = new Sprite(spriteFiles.getFiles(), 128);
  }

  /**
   * Paints the connection screen with background, avatar, and UI elements.
   * 
   * @param g the graphics context
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    background.draw(g2d);
    g2d.setColor(new Color(0, 0, 0, 150));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    if (currentAvatar != null) {
      int avatarX = getWidth() / 2 - (int) currentAvatar.getWidth();
      int avatarY = 250;
      g2d.translate(avatarX, avatarY);
      currentAvatar.draw(g2d);
      g2d.translate(-avatarX, -avatarY);
    }

    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Minecraft", Font.BOLD, 18));

    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth("Character: 0");
    g2d.drawString("Character: " + skin, (getWidth() / 2) - (stringWidth / 2), 230);
  }
}
