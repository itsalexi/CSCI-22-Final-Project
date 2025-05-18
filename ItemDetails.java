public class ItemDetails {
  private String name;
  private boolean stackable;
  private String actionName;
  private String lore;

  public ItemDetails(String n, boolean s, String aN, String l) {
    name = n;
    stackable = s;
    actionName = aN;
    lore = l;
  }

  public String getName() {
    return name;
  }

  public String getLore() {
    return lore;
  }

  public boolean isStackable() {
    return stackable;
  }

  public String getActionName() {
    return actionName;
  }
}
