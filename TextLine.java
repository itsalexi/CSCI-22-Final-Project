/**
 * The TextLine class represents a line of text with associated color.
 * It is used for displaying text in the game interface.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version 20 May 2025
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

public class TextLine {
  private Color color;
  private String text;

  /**
   * Creates a new TextLine instance with specified text and color.
   * 
   * @param t the text content
   * @param c the color of the text
   */
  public TextLine(String t, Color c) {
    text = t;
    color = c;
  }

  /**
   * Gets the text content.
   * 
   * @return the text string
   */
  public String getText() {
    return text;
  }

  /**
   * Gets the text color.
   * 
   * @return the color of the text
   */
  public Color getColor() {
    return color;
  }

  /**
   * Sets the text color.
   * 
   * @param c the new color for the text
   */
  public void setColor(Color c) {
    color = c;
  }
}
