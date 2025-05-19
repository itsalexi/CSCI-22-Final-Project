/**
 * The CraftingSystem class manages the crafting mechanics and recipe interactions.
 * It handles the creation of items from recipes and verifies crafting requirements with inventory management.
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

import java.util.ArrayList;

public class CraftingSystem {

  private ArrayList<Recipe> recipes;
  private Inventory inventory;

  /**
   * Creates a new CraftingSystem.
   * 
   * @param r recipes
   * @param i inventory
   */
  public CraftingSystem(ArrayList<Recipe> r, Inventory i) {
    recipes = r;
    inventory = i;
  }

  /**
   * Attempts to craft an item using the specified recipe.
   * 
   * @param recipeID recipe index
   * @return true if crafting successful
   */
  public boolean craft(int recipeID) {
    Recipe recipe = recipes.get(recipeID);
    Item itemIn = recipe.getItemIn();
    Item itemOut = recipe.getItemOut();
    boolean craftable = isCraftable(recipeID);

    if (craftable) {

      inventory.addItem(itemOut.getId(), itemOut.getQuantity());
      inventory.removeItem(itemIn.getId(), itemIn.getQuantity());
      return true;
    }
    return false;
  }

  /**
   * Checks if a recipe can be crafted with current inventory.
   * 
   * @param recipeID recipe index
   * @return true if craftable
   */
  public boolean isCraftable(int recipeID) {
    Recipe recipe = recipes.get(recipeID);
    Item itemIn = recipe.getItemIn();
    if (itemIn.getQuantity() <= inventory.getQuantity(itemIn.getId())) {
      return true;
    }
    return false;
  }

}
