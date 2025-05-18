
import java.util.ArrayList;

public class ShopSystem {

  private ArrayList<Recipe> trades;
  private Inventory inventory;
  private EconomySystem balance;

  public ShopSystem(ArrayList<Recipe> t, Inventory i, EconomySystem eS) {
    trades = t;
    inventory = i;
    balance = eS;
  }

  public boolean trade(int tradeID) {
    Recipe trade = trades.get(tradeID);
    Item itemIn = trade.getItemIn();
    Item itemOut = trade.getItemOut();

    boolean tradeable = isTradeable(tradeID);
    if (tradeable) {
      // coin -> item
      if (itemIn.getId() == 14) {
        balance.setBalance(balance.getBalance() - itemIn.getQuantity());
        inventory.addItem(itemOut.getId(), itemOut.getQuantity());
      } else {
        // item -> coin
        balance.setBalance(balance.getBalance() + itemOut.getQuantity());
        inventory.removeItem(itemIn.getId(), itemIn.getQuantity());
      }
      return true;

    }
    return false;
  }

  public boolean isTradeable(int tradeID) {
    Recipe trade = trades.get(tradeID);
    Item itemIn = trade.getItemIn();

    if (itemIn.getId() == 14) {
      if (balance.getBalance() - itemIn.getQuantity() >= 0) {
        return true;
      }
    } else {
      if (itemIn.getQuantity() <= inventory.getQuantity(itemIn.getId())) {
        return true;
      }
    }

    return false;
  }

}
