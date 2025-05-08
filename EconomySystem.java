public class EconomySystem {
  private int balance, skillPoints;
  public GameCanvas canvas;

  public EconomySystem(GameCanvas c) {
    canvas = c;
    balance = 0;
    skillPoints = 0;
  }

  public int getBalance() {
    return balance;
  }

  public int getSkillPoints() {
    return skillPoints;
  }

  public void setSkillPoints(int s) {
    canvas.getWriter().send(String.format("SKILLPOINTS SET %s %d", canvas.getClient().getPlayerID(), s));
    skillPoints = s;
  }

  public void setBalance(int b) {
    canvas.getWriter().send(String.format("ECONOMY SET %s %d", canvas.getClient().getPlayerID(), b));
    balance = b;
  }
}
