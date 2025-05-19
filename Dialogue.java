/**
 * The Dialogue class manages the display of dialogue boxes with text and avatars.
 * It handles the text formatting, positioning for the dialogues.
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

  /**
   * Creates a new Dialogue with avatar.
   * 
   * @param posX x position
   * @param posY y position
   * @param n name
   * @param m message
   * @param a avatar sprite
   */
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

    nameFont = new Font("Minecraft", Font.PLAIN, 16);

    messageFont = new Font("Minecraft", Font.PLAIN, 14);
    nameColor = Color.WHITE;
    messageColor = Color.BLACK;

    if (avatar != null) {
      avatar.setPosition(x + AVATAR_X_OFFSET, y + AVATAR_Y_OFFSET);
    }
  }

  /**
   * Creates a new Dialogue without avatar.
   * 
   * @param x x position
   * @param y y position
   * @param name name
   * @param message message
   */
  public Dialogue(double x, double y, String name, String message) {
    this(x, y, name, message, null);
  }

  /**
   * Draws the dialogue box and its contents.
   * 
   * @param g2d graphics context
   */
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

  /**
   * Sets the name.
   * 
   * @param n name
   */
  public void setName(String n) {
    name = n;
  }

  /**
   * Gets the name.
   * 
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the message.
   * 
   * @param m message
   */
  public void setMessage(String m) {
    message = m;
  }

  /**
   * Gets the message.
   * 
   * @return message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the avatar.
   * 
   * @param a avatar sprite
   */
  public void setAvatar(Sprite a) {
    avatar = a;
    if (this.avatar != null) {
      this.avatar.setPosition(x + AVATAR_X_OFFSET, y + AVATAR_Y_OFFSET);
    }
  }

  /**
   * Gets the avatar.
   * 
   * @return avatar sprite
   */
  public Sprite getAvatar() {
    return avatar;
  }

  /**
   * Sets the position.
   * 
   * @param newX x position
   * @param newY y position
   */
  public void setPosition(double newX, double newY) {
    x = newX;
    y = newY;

    dialogueBox.setPosition(x, y);
    if (avatar != null) {
      avatar.setPosition(x + AVATAR_X_OFFSET, y + AVATAR_Y_OFFSET);
    }
  }

  /**
   * Gets the x position.
   * 
   * @return x position
   */
  public double getX() {
    return x;
  }

  /**
   * Gets the y position.
   * 
   * @return y position
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the name font.
   * 
   * @param font font
   */
  public void setNameFont(Font font) {
    nameFont = font;
  }

  /**
   * Sets the message font.
   * 
   * @param font font
   */
  public void setMessageFont(Font font) {
    messageFont = font;
  }

  /**
   * Sets the name color.
   * 
   * @param color color
   */
  public void setNameColor(Color color) {
    nameColor = color;
  }

  /**
   * Sets the message color.
   * 
   * @param color color
   */
  public void setMessageColor(Color color) {
    messageColor = color;
  }
}