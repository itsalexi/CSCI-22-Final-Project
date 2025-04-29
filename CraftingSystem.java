
import java.util.ArrayList;

public class CraftingSystem {

  private ArrayList<Recipe> recipes;
  private Inventory inventory;

  public CraftingSystem(ArrayList<Recipe> r, Inventory i) {
    recipes = r;
    inventory = i;
  }

  public void craft(int recipeID) {
    Recipe recipe = recipes.get(recipeID);
    Item itemIn = recipe.getItemIn();
    Item itemOut = recipe.getItemOut();
    boolean craftable = isCraftable(recipeID);

    if (craftable) {

      inventory.addItem(itemOut.getId(), itemOut.getQuantity());
      inventory.removeItem(itemIn.getId(), itemIn.getQuantity());
    }
  }

  public boolean isCraftable(int recipeID) {
    Recipe recipe = recipes.get(recipeID);
    Item itemIn = recipe.getItemIn();
    if (itemIn.getQuantity() <= inventory.getQuantity(itemIn.getId())) {
      return true;
    }
    return false;
  }

}
