/**
 * The ItemDetails class stores detailed information about an item type.
 * It manages item properties such as name, description, and behavior.
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

public class ItemDetails {
  private String name, lore, actionName;
  private boolean stackable;

  /**
   * Creates a new ItemDetails instance.
   * 
   * @param n the item name
   * @param s whether the item is stackable
   * @param aN the action name
   * @param l the item lore/description
   */
  public ItemDetails(String n, boolean s, String aN, String l) {
    name = n;
    stackable = s;
    actionName = aN;
    lore = l;
  }

  /**
   * Gets the item name.
   * 
   * @return the item name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the item lore/description.
   * 
   * @return the item lore
   */
  public String getLore() {
    return lore;
  }

  /**
   * Gets the item's action name.
   * 
   * @return the action name
   */
  public String getActionName() {
    return actionName;
  }

  /**
   * Checks if the item is stackable.
   * 
   * @return true if the item is stackable
   */
  public boolean isStackable() {
    return stackable;
  }
}
