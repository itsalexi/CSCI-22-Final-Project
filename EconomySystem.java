public class EconomySystem {
  private int balance;
  public GameCanvas canvas;

  public EconomySystem(GameCanvas c) {
    canvas = c;
    balance = 0;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int b) {
    canvas.getWriter().send(String.format("ECONOMY SET %s %d", canvas.getClient().getPlayerID(), b));
    balance = b;
  }
}
