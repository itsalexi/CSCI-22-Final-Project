/**
 * The Recipe class represents a crafting recipe in the game.
 * It handles the input and output items for crafting.
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

public class Recipe {
  private Item itemIn;
  private Item itemOut;

  /**
   * Creates a new Recipe with specified input and output items.
   * 
   * @param in the input item required for crafting
   * @param out the output item produced by crafting
   */
  public Recipe(Item in, Item out) {
    itemIn = in;
    itemOut = out;
  }

  /**
   * Gets the input item required for this recipe.
   * 
   * @return the input item
   */
  public Item getItemIn() {
    return itemIn;
  }

  /**
   * Gets the output item produced by this recipe.
   * 
   * @return the output item
   */
  public Item getItemOut() {
    return itemOut;
  }

  /**
   * Sets the output item for this recipe.
   * 
   * @param out the new output item
   */
  public void setItemOut(Item out) {
    itemOut = out;
  }

  /**
   * Sets the input item required for this recipe.
   * 
   * @param in the new input item
   */
  public void setItemIn(Item in) {
    itemIn = in;
  }
}
