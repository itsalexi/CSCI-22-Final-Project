import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Inventory {
  private TileGrid inventoryGrid;
  private TileGrid itemsGrid;

  private Sprite tiles;
  private Sprite items;
  private boolean isOpen;
  private int activeHotbarSlot;
  private GameCanvas canvas;

  private Timer itemSwitchTimer;

  private long lastSwitchedItem;
  private float activeItemTextAlpha;

  private int[][] inventoryMap = {
      { 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7 },
      { 4, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8 },
      { 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7 },
      { 4, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8 }
  };

  private int[][] itemsMap = { { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } };

  private Item[] inventory;

  public Inventory(GameCanvas c) {

    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    items = new Sprite(itemFiles.getFiles(), 32);
    canvas = c;
    activeHotbarSlot = 0;
    activeItemTextAlpha = 0f;

    inventory = new Item[36];

    Item.registerItem(0, new ItemDetails("Watering Can", false, "water"));
    Item.registerItem(1, new ItemDetails("Iron Hoe", false, "hoe"));
    Item.registerItem(2, new ItemDetails("Wheat", true, null));
    Item.registerItem(3, new ItemDetails("Wheat Seeds", true, "plant wheat"));
    Item.registerItem(4, new ItemDetails("Potato", true, null));
    Item.registerItem(5, new ItemDetails("Potato Seeds", true, "plant potato"));
    Item.registerItem(6, new ItemDetails("Carrot", true, null));
    Item.registerItem(7, new ItemDetails("Carrot Seeds", true, "plant carrot"));
    Item.registerItem(8, new ItemDetails("Onion", true, null));
    Item.registerItem(9, new ItemDetails("Onion Seeds", true, "plant onion"));
    Item.registerItem(10, new ItemDetails("Strawberry", true, null));
    Item.registerItem(11, new ItemDetails("Strawberry Seeds", true, "plant strawberry"));
    Item.registerItem(12, new ItemDetails("Blueberry", true, null));
    Item.registerItem(13, new ItemDetails("Blueberry Seeds", true, "plant blueberry"));
    Item.registerItem(14, new ItemDetails("Coins", true, ""));

    tiles = new Sprite(tileMapFiles.getFiles(), 32);
    isOpen = false;
    inventoryGrid = new TileGrid(tiles, inventoryMap);
    itemsGrid = new TileGrid(items, itemsMap);
    lastSwitchedItem = System.currentTimeMillis();

    itemSwitchTimer = new Timer(1000 / 60, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Long delta = System.currentTimeMillis() - lastSwitchedItem;
        activeItemTextAlpha = (float) clamp(0, 1, (double) -delta / 500 + 4);
      }
    });
    itemSwitchTimer.start();

  }

  private double clamp(double left, double right, double value) {
    return Math.max(left, Math.min(right, value));
  }

  public Item[] getInventory() {
    return inventory;
  }

  public Item findItem(Item item) {
    for (int i = 0; i < inventory.length; i++) {
      if (inventory[i] == null)
        continue;
      if (inventory[i].getId() == item.getId() && !(inventory[i].getQuantity() <= 0)) {
        return inventory[i];
      }
    }
    return null;
  }

  public Item findUnfilledStack(Item item) {
    for (int i = 0; i < inventory.length; i++) {
      if (inventory[i] == null)
        continue;
      if (inventory[i].getId() == item.getId()) {
        if (inventory[i].getQuantity() < 64) {
          return inventory[i];
        }
      }
    }
    return null;
  }

  public int getQuantity(int id) {
    Item item = new Item(id, 0);
    int quantity = 0;
    for (int i = 0; i < inventory.length; i++) {
      if (inventory[i] == null)
        continue;
      if (inventory[i].getId() == item.getId()) {
        quantity += inventory[i].getQuantity();
      }
    }
    return quantity;
  }

  public void removeItem(int id, int quantity) {

    Item item = new Item(id, quantity);

    Item curr = findItem(item);
    while (quantity > 0 && curr != null) {
      if (curr.getQuantity() < quantity) {
        quantity -= curr.getQuantity();
        curr.setQuantity(0);
      } else {
        curr.setQuantity(curr.getQuantity() - quantity);
        return;
      }
      curr = findItem(item);
    }
    canvas.syncInventoryBulk();

  }

  public void addItem(int id, int quantity) {
    Item item = new Item(id, quantity);

    if (item.isStackable()) {
      Item inventoryItem = findUnfilledStack(item);
      if (inventoryItem != null) {
        int stackQuantity = inventoryItem.getQuantity();
        int spaceLeft = 64 - stackQuantity;

        int toAdd = Math.min(quantity, spaceLeft);
        inventoryItem.setQuantity(stackQuantity + toAdd);
        quantity -= toAdd;
      }

      while (quantity >= 64) {
        int slot = getEmptySlot();
        if (slot == -1) {
          canvas.dropItem(new Item(id, quantity), quantity);
          return;
        }
        inventory[slot] = new Item(id, 64);
        quantity -= 64;
      }

      if (quantity > 0) {
        int slot = getEmptySlot();
        if (slot == -1) {
          canvas.dropItem(new Item(id, quantity), quantity);
        } else {
          inventory[slot] = new Item(id, quantity);
        }
      }

    } else {
      for (int i = 0; i < quantity; i++) {
        int slot = getEmptySlot();
        if (slot == -1) {
          canvas.dropItem(new Item(id, 1), 1);
        } else {
          inventory[slot] = new Item(id, 1);
        }
      }
    }
    canvas.syncInventoryBulk();

  }

  public void setItem(int slot, Item item) {
    inventory[slot] = item;
  }

  public Item getItem(int slot) {
    return inventory[slot];
  }

  public Item getActiveItem() {
    return inventory[activeHotbarSlot];
  }

  public int getActiveHotbarSlot() {
    return activeHotbarSlot;
  }

  public void setActiveHotbarSlot(int slot) {
    lastSwitchedItem = System.currentTimeMillis();
    activeHotbarSlot = slot;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public void setOpen(boolean open) {
    isOpen = open;
  }

  public int[] getTileAtMouse(double mouseX, double mouseY) {
    double tileSize = inventoryGrid.getTileSize();
    double gridWidth = inventoryMap[0].length * tileSize;
    double gridHeight = inventoryMap.length * tileSize;

    double xOffset = (canvas.getWidth() - gridWidth) / 2;
    double yOffsetHotbar = canvas.getHeight() - gridHeight;

    double localX = mouseX - xOffset;
    double localY = mouseY - yOffsetHotbar;
    int tileX = (int) (localX / tileSize);
    int tileY = (int) (localY / tileSize);

    if (tileX >= 0 && tileX < 11 && tileY >= 0 && tileY < 8) {
      return new int[] { tileX, tileY };
    }

    if (isOpen) {
      double yOffsetInventory = yOffsetHotbar + 30;
      localY = mouseY - yOffsetInventory;
      tileY = (int) (localY / tileSize);

      if (tileX >= 0 && tileX < 11 && tileY >= 0 && tileY < 8) {
        return new int[] { tileX, tileY };
      }
    }

    return null;
  }

  private int[] getGridFromInventory(int i) {
    int[] grid = { 0, 0 };
    grid[0] = i % 9;

    if (i < 9) {
      grid[1] = 6;
    } else {
      grid[1] = ((i - 9) / 9) + 1;
    }
    return grid;
  }

  public void drawQuantities(Graphics2D g2d) {
    double tileSize = inventoryGrid.getTileSize();

    for (int i = 0; i < inventory.length; i++) {
      if (!isOpen && i > 8) {
        return;
      }
      Item item = inventory[i];

      int[] coords = getGridFromInventory(i);
      if (coords == null)
        continue;

      int row = coords[1];
      int col = coords[0] + 1;

      itemsGrid.setTileAt(row, col, inventory[i] == null ? -1 : inventory[i].getId());

      if (item != null) {
        if (!item.isStackable())
          continue;

        String quantityString = Integer.toString(inventory[i].getQuantity());

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Minecraft", 1, (int) (25 * tileSize / 32)));

        FontMetrics fm = g2d.getFontMetrics();

        int stringWidth = fm.stringWidth(quantityString);
        float quantityLabelX = (float) ((col + 1) * tileSize) - stringWidth;
        float quantityLabelY = (float) ((row + 1) * tileSize);

        g2d.setColor(Color.BLACK);
        g2d.drawString(quantityString, quantityLabelX + 1, quantityLabelY - 1);
        g2d.drawString(quantityString, quantityLabelX + 1, quantityLabelY + 1);
        g2d.drawString(quantityString, quantityLabelX - 1, quantityLabelY - 1);
        g2d.drawString(quantityString, quantityLabelX - 1, quantityLabelY + 1);
        g2d.setColor(Color.WHITE);

        g2d.drawString(quantityString, quantityLabelX, quantityLabelY);

      }
    }
  }

  public void draw(Graphics2D g2d) {
    double tileSize = canvas.getWidth() * 32 / 800;
    tiles.setSize(tileSize);
    items.setSize(tileSize);
    inventoryGrid.setTileSize(tileSize);
    itemsGrid.setTileSize(tileSize);

    double gridWidth = inventoryMap[0].length * tileSize;
    double gridHeight = inventoryMap.length * tileSize;

    double x = (canvas.getWidth() - gridWidth) / 2;
    double y = canvas.getHeight() - gridHeight;

    if (getActiveItem() != null) {
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, activeItemTextAlpha));

      g2d.setColor(Color.WHITE);
      g2d.setFont(new Font("Minecraft", 1, (int) (20 * tileSize / 32)));
      String text = getActiveItem().getName();
      int textWidth = g2d.getFontMetrics().stringWidth(text);

      float itemTextX = (float) (x + (gridWidth / 2) - (textWidth / 2));
      float itemTextY = (float) (y + (gridHeight / 2) + 40 / 32 * tileSize);

      g2d.setColor(Color.BLACK);
      g2d.drawString(text, itemTextX + 1, itemTextY - 1);
      g2d.drawString(text, itemTextX + 1, itemTextY + 1);
      g2d.drawString(text, itemTextX - 1, itemTextY - 1);
      g2d.drawString(text, itemTextX - 1, itemTextY + 1);
      g2d.setColor(Color.WHITE);

      g2d.drawString(text, itemTextX, itemTextY);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    for (int i = 0; i < inventory.length; i++) {

      Item item = inventory[i];

      int[] coords = getGridFromInventory(i);
      if (coords == null)
        continue;

      int row = coords[1];
      int col = coords[0] + 1;

      itemsGrid.setTileAt(row, col, inventory[i] == null ? -1 : inventory[i].getId());

      if (item != null) {
        if (item.getQuantity() <= 0) {
          inventory[i] = null;
        }
      }

    }
    // unhighlight previous
    for (int i = 1; i <= 9; i++) {
      inventoryMap[6][i] = 0;
    }

    // highlight hotbar slot
    inventoryMap[6][1 + activeHotbarSlot] = 1;

    // System.out.printf("%d, %d\n", x, y);

    g2d.translate(x, y);

    if (isOpen) {
      inventoryGrid.draw(g2d);
      itemsGrid.draw(g2d);
      drawQuantities(g2d);
    } else {
      inventoryGrid.drawRows(g2d, 5, 7);
      itemsGrid.drawRows(g2d, 5, 7);
      drawQuantities(g2d);
    }

    g2d.translate(-x, -y);

  }

  public Sprite getItemSprites() {
    return items;
  }

  public int getSlotFromGrid(int x, int y) {
    if (y == 6) {
      return x - 1;
    }
    return (y - 1) * 9 + (x - 1) + 9;
  }

  public int getEmptySlot() {
    for (int i = 0; i < 36; i++) {
      if (getItem(i) == null) {
        return i;
      }
    }
    return -1;
  }
}
