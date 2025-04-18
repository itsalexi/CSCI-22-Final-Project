public class Item {
  private String name;
  private int id, quantity;
  private boolean stackable;
  private String actionName;
  

  public Item(String n, int i, int q, boolean isS, String a) {
    name = n;
    id = i;
    quantity = q;
    stackable = isS;
    actionName = a;
  }

  public String getActionName(){
    return actionName;
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

  public void consume(){
    if(isStackable()){
      quantity -= 1;
    }
  }

  public boolean isStackable() {
    return stackable;
  }
}
