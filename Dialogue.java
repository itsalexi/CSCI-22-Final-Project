import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class Dialogue {
  private Sprite dialogueBox;
  private Sprite avatar;
  private String name;
  private String message;
  private double x, y;
  private Font nameFont;
  private Font messageFont;
  private Color nameColor;
  private Color messageColor;

  private final double NAME_X_OFFSET = 25;
  private final double NAME_Y_OFFSET = 25;
  private final double MESSAGE_X_OFFSET = 20;
  private final double MESSAGE_Y_OFFSET = 60;
  private final double AVATAR_X_OFFSET = 400;
  private final double AVATAR_Y_OFFSET = -40;

  public Dialogue(double posX, double posY, String n, String m, Sprite a) {
    x = posX;
    y = posY;
    name = n;
    message = m;
    avatar = a;

    ArrayList<File> dialogueBoxFiles = new ArrayList<>();
    dialogueBoxFiles.add(new File("assets/ui/dialogue.png"));
    dialogueBox = new Sprite(dialogueBoxFiles, true);
    dialogueBox.setPosition(x, y);

    nameFont = new Font("Arial", Font.BOLD, 16);
    
    messageFont = new Font("Arial", Font.PLAIN, 14);
    nameColor = Color.WHITE;
    messageColor = Color.BLACK;

    if (avatar != null) {
      avatar.setPosition(x + AVATAR_X_OFFSET, y + AVATAR_Y_OFFSET);
    }
  }

  public Dialogue(double x, double y, String name, String message) {
    this(x, y, name, message, null);
  }

  public void draw(Graphics2D g2d) {
    dialogueBox.draw(g2d);

    Font originalFont = g2d.getFont();
    Color originalColor = g2d.getColor();

    g2d.setFont(nameFont);
    g2d.setColor(nameColor);
    g2d.drawString(name, (float) (x + NAME_X_OFFSET), (float) (y + NAME_Y_OFFSET));

    g2d.setFont(messageFont);
    g2d.setColor(messageColor);
    g2d.drawString(message, (float) (x + MESSAGE_X_OFFSET), (float) (y + MESSAGE_Y_OFFSET));

    if (avatar != null) {
      avatar.draw(g2d);
    }

    g2d.setFont(originalFont);
    g2d.setColor(originalColor);
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public void setMessage(String m) {
    message = m;
  }

  public String getMessage() {
    return message;
  }

  public void setAvatar(Sprite a) {
    avatar = a;
    if (this.avatar != null) {
      this.avatar.setPosition(x + AVATAR_X_OFFSET, y + AVATAR_Y_OFFSET);
    }
  }

  public Sprite getAvatar() {
    return avatar;
  }

  public void setPosition(double newX, double newY) {
    x = newX;
    y = newY;

    dialogueBox.setPosition(x, y);
    if (avatar != null) {
      avatar.setPosition(x + AVATAR_X_OFFSET, y + AVATAR_Y_OFFSET);
    }
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setNameFont(Font font) {
    nameFont = font;
  }

  public void setMessageFont(Font font) {
    messageFont = font;
  }

  public void setNameColor(Color color) {
    nameColor = color;
  }

  public void setMessageColor(Color color) {
    messageColor = color;
  }
}