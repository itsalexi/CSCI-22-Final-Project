/**
 * The ChatSystem class manages the in-game chat interface, handling message display, text input, and cursor blinking animations.
 * It provides functionality for message history, text wrapping, and real-time chat updates with username display.
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
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class ChatSystem {

  private List<String> messages;
  private String currentInput;
  private boolean chatOpen;
  private boolean cursorVisible;
  private Timer cursorBlinkTimer;
  private String user;

  /**
   * Constructs a new ChatSystem
   */
  public ChatSystem() {
    user = "";
    messages = new ArrayList<>();
    currentInput = "";
    chatOpen = false;
    cursorVisible = true;

    cursorBlinkTimer = new Timer(500, e -> {
      cursorVisible = !cursorVisible;
    });

    cursorBlinkTimer.start();
  }

  /**
   * Sets the username for the chat system.
   * 
   * @param u the username to set
   */
  public void setUsername(String u) {
    user = u;
  }

  /**
   * Gets the formatted message content with username prefix.
   * 
   * @return the formatted message string
   */
  public String getMessageContent() {
    return user + ": " + currentInput;
  }

  /**
   * Checks if the chat interface is currently open.
   * 
   * @return true if chat is open, false otherwise
   */
  public boolean isChatOpen() {
    return chatOpen;
  }

  /**
   * Sets the chat interface open state.
   * 
   * @param b true to open chat, false to close it
   */
  public void setChatOpen(boolean b) {
    chatOpen = b;
  }

  /**
   * Sets the current input text in the chat.
   * 
   * @param input the text to set as current input
   */
  public void setCurrentInput(String input) {
    this.currentInput = input;
  }

  /**
   * Wraps text to fit within the chat window width.
   * 
   * @param message the message to wrap
   * @return list of wrapped text lines
   */
  private ArrayList<String> wrapText(String message) {
    ArrayList<String> output = new ArrayList<>();
    String curr = "";
    AffineTransform transform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(transform, true, true);
    Font font = new Font("Minecraft", Font.PLAIN, 14);
    for (int i = 0; i < message.length(); i++) {
      if (font.getStringBounds(curr, frc).getWidth() > 380) {
        output.add(curr);
        curr = "";
      }
      curr += message.charAt(i);
    }
    output.add(curr);
    return output;
  }

  /**
   * Adds a new message to the chat history.
   * 
   * @param message the message to add
   */
  public void addMessage(String message) {
    messages.addAll(wrapText(message));
    while (messages.size() > 9) {
      messages.remove(0);
    }
  }

  /**
   * Draws the chat interface including message history and input box.
   * 
   * @param g2d the graphics context
   */
  public void draw(Graphics2D g2d) {
    int width = 400;
    int historyHeight = 200;
    int lineHeight = 20;
    int x = 10;
    int y = 10;

    g2d.setColor(chatOpen ? new Color(240, 240, 240, 200) : new Color(240, 240, 240, 99));
    g2d.fillRect(x, y, width, historyHeight);

    if (chatOpen) {
      ArrayList<String> wrappedInput = wrapText(currentInput);
      int inputHeight = Math.max(30, wrappedInput.size() * lineHeight + 10); 
      
      g2d.setColor(new Color(200, 255, 200, 99));
      g2d.fillRect(x, y + historyHeight + 10, width, inputHeight);

      g2d.setColor(Color.BLACK);
      g2d.drawRect(x, y, width, historyHeight);
      g2d.setColor(chatOpen ? Color.GREEN.darker() : Color.GRAY);
      g2d.drawRect(x, y + historyHeight + 10, width, inputHeight);
      g2d.setFont(new Font("Minecraft", Font.PLAIN, 14));
      g2d.setColor(Color.BLACK);
      
      int inputY = y + historyHeight + 30;
      for (String line : wrappedInput) {
        g2d.drawString(line, x + 10, inputY);
        inputY += lineHeight;
      }

      if (cursorVisible && chatOpen) {
        String lastLine = wrappedInput.isEmpty() ? "" : wrappedInput.get(wrappedInput.size() - 1);
        g2d.drawString("|", x + 10 + g2d.getFontMetrics().stringWidth(lastLine), inputY - lineHeight);
      }
    }
    g2d.setFont(new Font("Minecraft", Font.PLAIN, 14));
    g2d.setColor(Color.BLACK);
    int lineY = y + 20;
    for (String msg : messages) {
      g2d.drawString(msg, x + 10, lineY);
      lineY += 20;
    }
  }
}
