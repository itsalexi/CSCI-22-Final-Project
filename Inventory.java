import java.awt.*;

public class Inventory {
  private TileGrid inventoryGrid;
  private TileGrid hotbarGrid;
  private TileGrid itemsGrid;

  private Sprite tiles;
  private Sprite items;
  private boolean isOpen;
  private int activeHotbarSlot;

  private int[][] inventoryMap = {
      { 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6 },
      { 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6 }
  };

  private int[][] itemsMap = { { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } };

  private Item[] inventory;

  public Inventory() {
    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    items = new Sprite(itemFiles.getFiles(), 32);

    activeHotbarSlot = 0;

    inventory = new Item[36];
    inventory[0] = new Item("Hoe", 1, 1, false, "hoe");
    inventory[1] = new Item("Watering Can", 0, 1, false, "water");
    tiles = new Sprite(tileMapFiles.getFiles(), 32);
    isOpen = false;
    inventoryGrid = new TileGrid(tiles, inventoryMap);
    itemsGrid = new TileGrid(items, itemsMap);

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
    activeHotbarSlot = slot;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public void setOpen(boolean open) {
    isOpen = open;
  }

  public int[] getTileAtMouse(double mouseX, double mouseY) {
    int tileSize = 32;
    int gridWidth = inventoryMap[0].length * tileSize;
    int gridHeight = inventoryMap.length * tileSize;

    int xOffset = (800 - gridWidth) / 2;
    int yOffsetHotbar = 600 - gridHeight - 40;

    double localX = mouseX - xOffset;
    double localY = mouseY - yOffsetHotbar;
    int tileX = (int) (localX / tileSize);
    int tileY = (int) (localY / tileSize);

    if (tileX >= 0 && tileX < 11 && tileY >= 0 && tileY < 8) {
      return new int[] { tileX, tileY, 0 };
    }

    if (isOpen) {
      int yOffsetInventory = yOffsetHotbar + 30;
      localY = mouseY - yOffsetInventory;
      tileY = (int) (localY / tileSize);

      if (tileX >= 0 && tileX < 11 && tileY >= 0 && tileY < 8) {
        return new int[] { tileX, tileY, 1 };
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

  public void draw(Graphics2D g2d) {

    for (int i = 0; i < inventory.length; i++) {

      int[] coords = getGridFromInventory(i);
      if (coords == null)
        continue;

      int row = coords[1];
      int col = coords[0] + 1;

      itemsGrid.setTileAt(row, col, inventory[i] == null ? -1 : inventory[i].getId());

    }
    // unhighlight previous
    for (int i = 1; i <= 9; i++) {
      inventoryMap[6][i] = 0;
    }

    // highlight hotbar slot
    inventoryMap[6][1 + activeHotbarSlot] = 1;

    int tileSize = 32;
    int gridWidth = inventoryMap[0].length * tileSize;
    int gridHeight = inventoryMap.length * tileSize;

    int x = (800 - gridWidth) / 2;
    int y = 600 - gridHeight - 40;
    g2d.translate(x, y);

    if (isOpen) {
      inventoryGrid.draw(g2d);
      itemsGrid.draw(g2d);
    } else {
      inventoryGrid.drawRows(g2d, 5, 7);
      itemsGrid.drawRows(g2d, 5, 7);
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
