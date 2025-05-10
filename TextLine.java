
import java.awt.Color;

public class TextLine {
  private Color color;
  private Color shadowColor;
  private String text;

  public TextLine(String t, Color c, Color sc) {
    text = t;
    color = c;
    shadowColor = sc;
  }

  public String getText() {
    return text;
  }

  public Color getColor() {
    return color;
  }
}
