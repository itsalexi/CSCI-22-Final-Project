
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class CraftingGrid {
  private TileGrid craftingGrid;
  private TileGrid craftingGridTop;
  private TileGrid craftingItemsGrid;

  private ArrayList<Recipe> recipes;
  private GameCanvas canvas;
  private int currentPage = 0;

  private Sprite tilesSprites;
  private Sprite itemsSprites;

  private boolean isShop;

  private int[][] craftingGridMap = {
      { 2, 5, 5, 5, 6 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 3, 10, 10, 10, 7 },
      { 4, 9, 9, 9, 8 }
  };

  private int[][] craftingGridTopMap = {
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1 },
      { -1, 13, -1, 12, -1 },
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

  public CraftingGrid(ArrayList<Recipe> r, GameCanvas c, boolean s) {
    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    tilesSprites = new Sprite(tileMapFiles.getFiles(), 32);
    itemsSprites = new Sprite(itemFiles.getFiles(), 32);

    craftingGrid = new TileGrid(tilesSprites, craftingGridMap);
    craftingGridTop = new TileGrid(tilesSprites, craftingGridTopMap);
    craftingItemsGrid = new TileGrid(itemsSprites, craftingItemsMap);
    canvas = c;
    isShop = s;
    recipes = r;

  }

  private void updateCraftingItemsGrid() {
    int start = currentPage * 4;
    int end = Math.min(start + 4, recipes.size());
    for (int r = 0; r < 4; r++) {
      int baseRow = 2 + r;
      craftingItemsMap[baseRow][1] = -1;
      craftingItemsMap[baseRow][3] = -1;
      craftingGridTopMap[baseRow][2] = -1;
      craftingGridMap[baseRow][1] = 10;
      craftingGridMap[baseRow][3] = 10;
    }

    int recipesDrawn = 0;
    for (int i = start; i < end; i++) {
      Recipe recipe = recipes.get(i);

      int baseRow = 2 + recipesDrawn;
      int inputCol = 1;
      int outputCol = 3;

      craftingItemsMap[baseRow][inputCol] = recipe.getItemIn().getId();
      craftingItemsMap[baseRow][outputCol] = recipe.getItemOut().getId();

      craftingGridTopMap[baseRow][2] = 11;
      craftingGridMap[baseRow][inputCol] = 0;
      craftingGridMap[baseRow][outputCol] = 1;

      recipesDrawn++;
    }

  }

  private void drawCraftingQuantities(Graphics2D g2d) {
    double tileSize = craftingItemsGrid.getTileSize();

    int start = currentPage * 4;
    int end = Math.min(start + 4, recipes.size());

    int recipesDrawn = 0;
    for (int i = start; i < end; i++) {
      Recipe recipe = recipes.get(i);

      int baseRow = 2 + recipesDrawn;
      int inputCol = 1;
      int outputCol = 3;

      Item inputItem = recipe.getItemIn();
      if (inputItem.isStackable() && inputItem.getQuantity() > 1) {
        drawQuantityString(g2d, inputItem.getQuantity(), inputCol, baseRow, tileSize);
      }

      Item outputItem = recipe.getItemOut();
      if (outputItem.isStackable() && outputItem.getQuantity() > 1) {
        drawQuantityString(g2d, outputItem.getQuantity(), outputCol, baseRow, tileSize);
      }

      recipesDrawn++;
    }
  }

  private void drawQuantityString(Graphics2D g2d, int quantity, int col, int row, double tileSize) {
    String quantityString = Integer.toString(quantity);

    g2d.setFont(new Font("Minecraft", Font.PLAIN, (int) (25 * tileSize / 32)));
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

  public int[] getTileAtMouse(double mouseX, double mouseY) {
    double tileSize = craftingGrid.getTileSize();
    double gridWidth = craftingGridMap[0].length * tileSize;
    double gridHeight = craftingGridMap.length * tileSize;

    double xOffset = isShop ? 0 : (canvas.getWidth() - gridWidth);
    double yOffset = canvas.getHeight() - gridHeight;
    double localX = mouseX - xOffset;
    double localY = mouseY - yOffset;
    int tileX = (int) (localX / tileSize);
    int tileY = (int) (localY / tileSize);

    return new int[] { tileX, tileY };
  }

  public void forwardPage() {
    int totalPages = (int) Math.ceil(recipes.size() / 4.0);
    if (currentPage + 1 >= totalPages)
      return;
    currentPage++;
  }

  public void backwardPage() {
    if (currentPage <= 0)
      return;
    currentPage--;
  }

  public void draw(Graphics2D g2d) {
    updateCraftingItemsGrid();
    double tileSize = canvas.getWidth() * 32 / 800;
    double gridWidth = craftingGridMap[0].length * tileSize;
    double gridHeight = craftingGridMap.length * tileSize;

    tilesSprites.setSize(tileSize);
    itemsSprites.setSize(tileSize);
    craftingGrid.setTileSize(tileSize);
    craftingGridTop.setTileSize(tileSize);
    craftingItemsGrid.setTileSize(tileSize);

    double x = isShop ? 0 : (canvas.getWidth() - gridWidth);
    double y = canvas.getHeight() - gridHeight;

    if (canvas.getInventory().isOpen()) {
      g2d.translate(x, y);
      craftingGrid.draw(g2d);
      craftingGridTop.draw(g2d);
      craftingItemsGrid.draw(g2d);
      drawCraftingQuantities(g2d);
      g2d.translate(-x, -y);
    }

  }

  public int getCurrentPage() {
    return currentPage;
  }
}
