import java.awt.*;

public class Inventory {
  private TileGrid inventoryGrid;
  private TileGrid hotbarGrid;

  private Sprite tiles;
  private boolean isOpen;
  private int activeHotbarSlot;

  private int[][] inventoryMap = {
      { 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
  };

  private int[][] hotbarMap = {
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
      { 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10 },
      { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
      { 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6 }
  };

  public Inventory() {
    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
    tiles = new Sprite(tileMapFiles.getFiles(), 32);
    isOpen = true;
    inventoryGrid = new TileGrid(tiles, inventoryMap);
    hotbarGrid = new TileGrid(tiles, hotbarMap);
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

  public void draw(Graphics2D g2d) {
    // System.out.println(activeHotbarSlot);
    // unhighlight previous
    for (int i = 1; i <= 9; i++) {
      hotbarMap[6][i] = 0;
    }

    // highlight hotbar slot
    hotbarMap[6][1 + activeHotbarSlot] = 1;

    int tileSize = 32;
    int gridWidth = inventoryMap[0].length * tileSize;
    int gridHeight = inventoryMap.length * tileSize;

    int x = (800 - gridWidth) / 2;
    int y = 600 - gridHeight - 20;
    g2d.translate(x, y);
    hotbarGrid.draw(g2d);
    g2d.translate(-x, -y);

    if (isOpen) {
      y += 30;
      g2d.translate(x, y);
      inventoryGrid.draw(g2d);
    }
    g2d.translate(-x, -y);
  }
}
