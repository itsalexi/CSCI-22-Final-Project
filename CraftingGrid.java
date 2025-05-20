/**
 * The CraftingGrid class manages the crafting interface where players can view and interact with crafting recipes.
 * It handles recipe display, navigation, item quantities, and the visual layout of the crafting grid.
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

  /**
   * Constructs a new CraftingGrid with specified recipes and game canvas.
   * 
   * @param r the list of available recipes
   * @param c the game canvas
   * @param s is shop
   */
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

  /**
   * Updates the crafting items grid with current page's recipes.
   */
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

  /**
   * Draws the quantity indicators for stackable items in the crafting grid.
   * 
   * @param g2d the graphics context
   */
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

  /**
   * Draws a quantity string for an item at the specified grid position.
   * 
   * @param g2d the graphics context
   * @param quantity the quantity to display
   * @param col the column position
   * @param row the row position
   * @param tileSize the size of each tile
   */
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

  /**
   * Gets the grid coordinates at the specified mouse position.
   * 
   * @param mouseX the x-coordinate of the mouse
   * @param mouseY the y-coordinate of the mouse
   * @return array containing coordinates
   */
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

  /**
   * Advances to the next page of recipes.
   */
  public void forwardPage() {
    int totalPages = (int) Math.ceil(recipes.size() / 4.0);
    if (currentPage + 1 >= totalPages)
      return;
    currentPage++;
    canvas.playLocalSound("ui_click");
  }

  /**
   * Returns to the previous page of recipes.
   */
  public void backwardPage() {
    if (currentPage <= 0)
      return;
    currentPage--;
    canvas.playLocalSound("ui_click");

  }

  /**
   * Draws the crafting grid interface with all its components.
   * 
   * @param g2d the graphics context
   */
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

  /**
   * Gets the current page number of the recipe display.
   * 
   * @return the current page number
   */
  public int getCurrentPage() {
    return currentPage;
  }
}
