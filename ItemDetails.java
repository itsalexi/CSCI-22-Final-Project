public class ItemDetails {
  private String name;
  private boolean stackable;
  private String actionName;

  public ItemDetails(String n, boolean s, String aN) {
    name = n;
    stackable = s;
    actionName = aN;
  }

  public String getName() {
    return name;
  }

  public boolean isStackable() {
    return stackable;
  }

  public String getActionName() {
    return actionName;
  }
}
