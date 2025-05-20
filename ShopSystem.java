/**
 * The ShopSystem class manages the game's trading and shop functionality.
 * It handles item trades between the player and the shop, including buying and selling.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version 20 May 2025
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

public class ShopSystem {

  private ArrayList<Recipe> trades;
  private Inventory inventory;
  private EconomySystem balance;

  /**
   * Creates a new ShopSystem instance.
   * 
   * @param t the list of available trades
   * @param i the player's inventory
   * @param eS the economy system for managing currency
   */
  public ShopSystem(ArrayList<Recipe> t, Inventory i, EconomySystem eS) {
    trades = t;
    inventory = i;
    balance = eS;
  }

  /**
   * Attempts to execute a trade with the specified ID.
   * Handles both buying (coin -> item) and selling (item -> coin) transactions.
   * 
   * @param tradeID the ID of the trade to execute
   * @return true if the trade was successful, false otherwise
   */
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

  /**
   * Checks if a trade is possible based on the player's resources.
   * For buying: checks if player has enough coins
   * For selling: checks if player has enough items
   * 
   * @param tradeID the ID of the trade to check
   * @return true if the trade is possible, false otherwise
   */
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
