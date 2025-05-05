
import java.awt.Graphics2D;

public class DroppedItem {
  private double posX, posY;
  private int id, itemId, quantity;
  private Sprite itemSprites;

  public DroppedItem(double x, double y, int iId, int q, int dId) {

    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    itemSprites = new Sprite(itemFiles.getFiles(), 32);
    posX = x;
    posY = y;
    id = dId;
    quantity = q;
    itemId = iId;
  }

  public void draw(Graphics2D g2d) {
    g2d.translate(posX, posY);
    itemSprites.setSprite(itemId);
    itemSprites.draw(g2d);
    g2d.translate(-posX, -posY);
  }

  public void setPosition(double x, double y) {
    posX = x;
    posY = y;

  }

  public void setQuantity(int q) {
    quantity = q;
  }

  public double getX() {
    return posX;

  }

  public double getY() {
    return posY;
  }

  public int getQuantity() {
    return quantity;
  }

  public int getDroppedItemId() {
    return id;
  }
}
