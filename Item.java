/**
 * The Item class represents an item in the game.
 * It manages item properties, quantities, and interactions.
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

import java.util.HashMap;
import java.util.Map;

public class Item {
  private int id, quantity;
  private static Map<Integer, ItemDetails> items = new HashMap<>();

  /**
   * Creates a new Item instance.
   * 
   * @param i the item ID
   * @param q the item quantity
   */
  public Item(int i, int q) {
    id = i;
    quantity = q;
  }

  /**
   * Sets the item quantity.
   * 
   * @param q the new quantity
   */
  public void setQuantity(int q) {
    quantity = q;
  }

  /**
   * Gets the item ID.
   * 
   * @return the item ID
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the item name.
   * 
   * @return the item name
   */
  public String getName() {
    ItemDetails details = items.get(id);
    return details != null ? details.getName() : "Unknown";
  }

  /**
   * Gets the item lore/description.
   * 
   * @return the item lore
   */
  public String getLore() {
    ItemDetails details = items.get(id);
    return details != null ? details.getLore() : "Unknown";
  }

  /**
   * Gets the item's action name.
   * 
   * @return the action name
   */
  public String getActionName() {
    ItemDetails details = items.get(id);
    return details != null ? details.getActionName() : "";
  }

  /**
   * Gets the item quantity.
   * 
   * @return the quantity
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Consumes one unit of the item if stackable.
   */
  public void consume() {
    if (isStackable()) {
      quantity -= 1;
    }
  }

  /**
   * Checks if the item is stackable.
   * 
   * @return true if the item is stackable
   */
  public boolean isStackable() {
    ItemDetails details = items.get(id);
    return details != null && details.isStackable();
  }

  /**
   * Registers a new item type.
   * 
   * @param id the item ID
   * @param details the item details
   */
  public static void registerItem(int id, ItemDetails details) {
    items.put(id, details);
  }

  /**
   * Gets the details for an item.
   * 
   * @param id the item ID
   * @return the item details
   */
  public static ItemDetails getItemDetails(int id) {
    return items.get(id);
  }
}
