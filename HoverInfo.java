
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

  public HoverInfo(ArrayList<TextLine> l, double x, double y) {
    lines = l;
    posX = x;
    posY = y;
  }

  private ArrayList<String> wrapText(String message) {
    ArrayList<String> output = new ArrayList<>();
    String curr = "";
    AffineTransform transform = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(transform, true, true);
    Font font = new Font("Minecraft", Font.PLAIN, 24);
    for (int i = 0; i < message.length(); i++) {
      if (font.getStringBounds(curr, frc).getWidth() > 320) {
        output.add(curr);
        curr = "";
      }
      curr += message.charAt(i);
    }
    output.add(curr);
    return output;
  }

  private void initializeGrid(Graphics2D g2d) {
    int max = -1;
    Font font = new Font("Minecraft", Font.PLAIN, 24);

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

  public void draw(Graphics2D g2d) {
    if (!mapInitialized) {
      initializeGrid(g2d);
    }

    if (posX + hoverInfoMap[0].length * 32 > 1024) {
      posX -= hoverInfoMap[0].length * 32;
    }

    g2d.translate(posX, posY);
    hoverInfoGrid.draw(g2d);

    for (int i = 0; i < lines.size(); i++) {
      TextLine line = lines.get(i);
      Font font = new Font("Minecraft", Font.PLAIN, 24);
      g2d.setFont(font);
      g2d.setColor(line.getColor());
      g2d.drawString(line.getText(), 32, +((i + 2) * 32) - 8);
    }
    g2d.translate(-posX, -posY);

  }

}
