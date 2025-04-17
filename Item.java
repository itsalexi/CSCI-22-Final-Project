public class Item {
  private String name;
  private int id, quantity;
  private boolean stackable;

  public Item(String n, int i, int q, boolean isS) {
    name = n;
    id = i;
    quantity = q;
    stackable = isS;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    if (!stackable) {
      return 1;
    }

    return quantity;
  }

  public boolean isStackable() {
    return stackable;
  }
}
