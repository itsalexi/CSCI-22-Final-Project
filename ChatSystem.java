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

  public void setUsername(String u) {
    user = u;
  }

  public String getMessageContent() {
    return user + ": " + currentInput;
  }

  public boolean isChatOpen() {
    return chatOpen;
  }

  public void setChatOpen(boolean b) {
    chatOpen = b;
  }

  public void setCurrentInput(String input) {
    this.currentInput = input;
  }

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

  public void addMessage(String message) {
    messages.addAll(wrapText(message));
    while (messages.size() > 5) {
      messages.remove(0);
    }
  }

  public void draw(Graphics2D g2d) {
    int width = 400;
    int historyHeight = 200;
    int inputHeight = 30;
    int x = 10;
    int y = 10;

    g2d.setColor(chatOpen ? new Color(240, 240, 240, 200) : new Color(240, 240, 240, 99));
    g2d.fillRect(x, y, width, historyHeight);

    if (chatOpen) {
      g2d.setColor(new Color(200, 255, 200, 99));
      g2d.fillRect(x, y + historyHeight + 10, width, inputHeight);

      g2d.setColor(Color.BLACK);
      g2d.drawRect(x, y, width, historyHeight);
      g2d.setColor(chatOpen ? Color.GREEN.darker() : Color.GRAY);
      g2d.drawRect(x, y + historyHeight + 10, width, inputHeight);
      g2d.setFont(new Font("Minecraft", Font.PLAIN, 14));
      g2d.setColor(Color.BLACK);
      g2d.drawString(currentInput, x + 10, y + historyHeight + 30);

    }
    g2d.setFont(new Font("Minecraft", Font.PLAIN, 14));
    g2d.setColor(Color.BLACK);
    int lineY = y + 20;
    for (String msg : messages) {
      g2d.drawString(msg, x + 10, lineY);
      lineY += 20;
    }

    if (cursorVisible && chatOpen) {
      g2d.drawString("|", x + 10 + g2d.getFontMetrics().stringWidth(currentInput), y + historyHeight + 30);
    }
  }
}
