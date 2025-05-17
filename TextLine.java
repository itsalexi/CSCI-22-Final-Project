
import java.awt.Color;

public class TextLine {
  private Color color;
  private String text;

  public TextLine(String t, Color c) {
    text = t;
    color = c;
  }

  public String getText() {
    return text;
  }

  public Color getColor() {
    return color;
  }
}
