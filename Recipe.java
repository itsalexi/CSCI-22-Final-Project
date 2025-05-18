public class Recipe {
  private Item itemIn;
  private Item itemOut;

  public Recipe(Item in, Item out) {
    itemIn = in;
    itemOut = out;
  }

  public Item getItemIn() {
    return itemIn;
  }

  public Item getItemOut() {
    return itemOut;
  }

  public void setItemOut(Item out) {
    itemOut = out;
  }

  public void setItemIn(Item in) {
    itemIn = in;
  }
}
