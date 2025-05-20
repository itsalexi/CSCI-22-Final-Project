/**
 * The HoverInfo class manages the display of hover information in the game UI.
 * It handles text wrapping, positioning, and rendering of information boxes.
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class HoverInfo {
  private TileGrid hoverInfoGrid;
  private int[][] hoverInfoMap;
  private ArrayList<TextLine> lines;
  private boolean mapInitialized;
  private double posX, posY;

  /**
   * Creates a new HoverInfo instance.
   * 
   * @param l list of text lines to display
   * @param x x position of the hover info
   * @param y y position of the hover info
   */
  public HoverInfo(ArrayList<TextLine> l, double x, double y) {
    lines = l;
    posX = x;
    posY = y;
  }

  /**
   * Wraps text to fit within a width.
   * 
   * @param message the text to wrap
   * @return list of wrapped text lines
   */
  private ArrayList<String> wrapText(String message) {
    ArrayList<String> output = new ArrayList<>();
    String[] lines = message.split("[\n]");
    String curr = "";
    AffineTransform transform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(transform, true, true);
    Font font = new Font("Minecraft", Font.PLAIN, 16);
    for (int i = 0; i < lines.length; i++){
      String[] words = lines[i].split("[\s]");
      for (int j = 0; j < words.length; j++) {
        String temp = curr + " " + words[j];
        if (font.getStringBounds(temp, frc).getWidth() > 320) {
          output.add(curr);
          curr = words[j];
        } else {
          if (!curr.equals("")) {
            curr += " ";
          }
          curr += words[j];
        }
      }
      output.add(curr);
      curr = "";
    }
    return output;
  }

  /**
   * Initializes the grid for displaying hover information.
   * 
   * @param g2d the graphics context
   */
  private void initializeGrid(Graphics2D g2d) {
    int max = -1;
    Font font = new Font("Minecraft", Font.PLAIN, 16);

    ArrayList<TextLine> finalLines = new ArrayList<>();
    for (TextLine line : lines) {
      FontMetrics metrics = g2d.getFontMetrics(font);
      int width = metrics.stringWidth(line.getText());
      if (width > max) {
        max = width;

        if (max > 320) {
          ArrayList<String> tempLines = wrapText(line.getText());
          ArrayList<TextLine> tempTextLines = new ArrayList<>();
          for (String l : tempLines) {
            tempTextLines.add(new TextLine(l, line.getColor()));
          }
          finalLines.addAll(tempTextLines);
          max = 320;
        } else {
          finalLines.add(line);
        }
        continue;
      }
      finalLines.add(line);
    }

    lines = new ArrayList<>();
    lines.addAll(finalLines);

    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
    Sprite sprite = new Sprite(tileMapFiles.getFiles(), 32);

    int height = 2 + lines.size();
    int width = 3 + max / (int) sprite.getSize();

    hoverInfoMap = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (y == 0 && x == 0) {
          // top left
          hoverInfoMap[y][x] = 2;
        } else if (y == 0 && x == width - 1) {
          // top right
          hoverInfoMap[y][x] = 6;
        } else if (y == height - 1 && x == 0) {
          // bottom left
          hoverInfoMap[y][x] = 4;
        } else if (y == height - 1 && x == width - 1) {
          // bottom right
          hoverInfoMap[y][x] = 8;
        } else if (y == 0) {
          // top
          hoverInfoMap[y][x] = 5;
        } else if (y == height - 1) {
          // bottom
          hoverInfoMap[y][x] = 9;
        } else if (x == 0) {
          // left
          hoverInfoMap[y][x] = 3;
        } else if (x == width - 1) {
          // right
          hoverInfoMap[y][x] = 7;
        } else {
          // center
          hoverInfoMap[y][x] = 10;
        }
      }
    }

    hoverInfoGrid = new TileGrid(sprite, hoverInfoMap);
    mapInitialized = true;
  }

  /**
   * Draws the hover information box with text.
   * 
   * @param g2d the graphics context
   */
  public void draw(Graphics2D g2d) {
    if (!mapInitialized) {
      initializeGrid(g2d);
    }

    if (posX + hoverInfoMap[0].length * hoverInfoGrid.getTileSize() > 1024) {
      posX -= hoverInfoMap[0].length * hoverInfoGrid.getTileSize();
    }

    if (posY + hoverInfoMap.length * hoverInfoGrid.getTileSize() > 768) {
      posY -= (posY + hoverInfoMap.length * hoverInfoGrid.getTileSize()) - 768 + 30;
    }

    g2d.translate(posX, posY);
    hoverInfoGrid.draw(g2d);

    for (int i = 0; i < lines.size(); i++) {
      TextLine line = lines.get(i);
      Font font = new Font("Minecraft", Font.PLAIN, 16);
      g2d.setFont(font);
      g2d.setColor(line.getColor());
      g2d.drawString(line.getText(), 32, +((i + 2) * 32) - 8);
    }
    g2d.translate(-posX, -posY);

  }

}
