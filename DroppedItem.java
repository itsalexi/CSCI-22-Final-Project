/**
 * The DroppedItem class represents items that have been dropped in the game world.
 * It manages the properties of dropped items, including position and quantity.
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

import java.awt.Graphics2D;

public class DroppedItem {
  private double posX, posY;
  private int id, itemId, quantity;
  private Sprite itemSprites;

  /**
   * Creates a new DroppedItem.
   * 
   * @param x x position
   * @param y y position
   * @param iId item id
   * @param q quantity
   * @param dId dropped item id
   */
  public DroppedItem(double x, double y, int iId, int q, int dId) {

    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    itemSprites = new Sprite(itemFiles.getFiles(), 24);
    posX = x;
    posY = y;
    id = dId;
    quantity = q;
    itemId = iId;
  }

  /**
   * Draws the dropped item.
   * 
   * @param g2d graphics context
   */
  public void draw(Graphics2D g2d) {
    g2d.translate(posX, posY);
    itemSprites.setSprite(itemId);
    itemSprites.draw(g2d);
    g2d.translate(-posX, -posY);
  }

  /**
   * Sets the position.
   * 
   * @param x x position
   * @param y y position
   */
  public void setPosition(double x, double y) {
    posX = x;
    posY = y;

  }

  /**
   * Sets the quantity.
   * 
   * @param q quantity
   */
  public void setQuantity(int q) {
    quantity = q;
  }

  /**
   * Gets the x position.
   * 
   * @return x position
   */
  public double getX() {
    return posX;

  }

  /**
   * Gets the y position.
   * 
   * @return y position
   */
  public double getY() {
    return posY;
  }

  /**
   * Gets the quantity.
   * 
   * @return quantity
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Gets the dropped item id.
   * 
   * @return dropped item id
   */
  public int getDroppedItemId() {
    return id;
  }

  /**
   * Gets the item id.
   * 
   * @return item id
   */
  public int getItemId() {
    return itemId;
  }

  /**
   * Gets the sprite size.
   * 
   * @return sprite size
   */
  public double getSpriteSize() {
    return itemSprites.getSize();
  }
}
