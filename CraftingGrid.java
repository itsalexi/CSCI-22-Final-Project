public class CraftingGrid {
  private TileGrid craftingGrid;
  private TileGrid craftingGridTop;
  private TileGrid craftingItemsGrid;

  private Sprite tilesSprites;
  private Sprite itemsSprites;
  private int[][] craftingGridMap = {
      { 2, 5, 5, 5, 6 },
      { 3, 10, 10, 10, 7 },
      { 3, 0, 10, 1, 7 },
      { 3, 0, 10, 1, 7 },
      { 3, 0, 10, 1, 7 },
      { 3, 0, 10, 1, 7 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 4, 9, 9, 9, 8 }
  };

  private int[][] craftingGridTopMap = {
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, 11, -1, -1 },
      { -1, -1, 11, -1, -1 },
      { -1, -1, 11, -1, -1 },
      { -1, -1, 11, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, 12, -1, 13, -1 },
      { -1, -1, -1, -1, -1 }
  };

  private int[][] craftingItemsMap = {
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 }
  };

  public CraftingGrid() {
    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    tilesSprites = new Sprite(tileMapFiles.getFiles(), 32);
    itemsSprites = new Sprite(itemFiles.getFiles());

    craftingGrid = new TileGrid(tilesSprites, craftingGridMap);
    craftingGridTop = new TileGrid(tilesSprites, craftingGridTopMap);
    craftingItemsGrid = new TileGrid(itemsSprites, craftingItemsMap);
  }
}
