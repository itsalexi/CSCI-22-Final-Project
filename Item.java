import java.util.HashMap;
import java.util.Map;

public class Item {
  private int id, quantity;
  private static Map<Integer, ItemDetails> items = new HashMap<>();

  public Item(int i, int q) {
    id = i;
    quantity = q;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    ItemDetails details = items.get(id);
    return details != null ? details.getName() : "Unknown";
  }

  public String getActionName() {
    ItemDetails details = items.get(id);
    return details != null ? details.getActionName() : "";
  }

  public int getQuantity() {
    return isStackable() ? quantity : 1;
  }

  public void consume() {
    if (isStackable()) {
      quantity -= 1;
    }
  }

  public boolean isStackable() {
    ItemDetails details = items.get(id);
    return details != null && details.isStackable();
  }

  public static void registerItem(int id, ItemDetails details) {
    items.put(id, details);
  }

  public static ItemDetails getItemDetails(int id) {
    return items.get(id);
  }
}
