/**
 * The TileGrid class manages a grid of tiles for rendering the game world.
 * It handles tile placement, drawing, and collision detection.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 20, 2025
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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class TileGrid {

    private Sprite tiles;
    private int width, height;
    private double tileSize;
    private int[][] map;
    private float[][] alphaMap;
    private Rectangle2D[] hitboxes;
    private double boundsX, boundsY, boundsWidth, boundsHeight;
    private boolean drawWithinBounds;

    /**
     * Creates a new TileGrid instance with specified tiles, map, and hitboxes.
     * 
     * @param tiles the sprite containing tile images
     * @param map the 2D array representing the tile layout
     * @param hb array of hitboxes for collision detection
     */
    public TileGrid(Sprite tiles, int[][] map, Rectangle2D[] hb) {
        this.tiles = tiles;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = tiles.getSize();
        hitboxes = hb;
        boundsX = 0;
        boundsY = 0;
        boundsWidth = width * tileSize;
        boundsHeight = height * tileSize;
        drawWithinBounds = false;
        alphaMap = new float[height][width];
        for (int i=0; i < height; i++) {
            for (int j=0; j < width; j++) {
                alphaMap[i][j] = 1f;
            }
        }
    }

    /**
     * Creates a new TileGrid instance with specified tiles and map.
     * Initializes an empty hitbox array.
     * 
     * @param tiles the sprite containing tile images
     * @param map the 2D array representing the tile layout
     */
    public TileGrid(Sprite tiles, int[][] map) {
        this.tiles = tiles;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = tiles.getSize();
        hitboxes = new Rectangle2D[512];
        boundsX = 0;
        boundsY = 0;
        boundsWidth = width * tileSize;
        boundsHeight = height * tileSize;
        drawWithinBounds = false;
        alphaMap = new float[height][width];
        for (int i=0; i < height; i++) {
            for (int j=0; j < width; j++) {
                alphaMap[i][j] = 1f;
            }
        }
    }

    /**
     * Gets the width of the tile grid.
     * 
     * @return the number of columns in the grid
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the tile grid.
     * 
     * @return the number of rows in the grid
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the bounds of the tiles that the tile grid will draw.
     * 
     * @param x the x position of the bounds
     * @param y the y position of the bounds
     * @param w the width of the bounds
     * @param h the height of the bounds
     */
    public void setBounds(double x, double y, double w, double h) {
        boundsX = x;
        boundsY = y;
        boundsWidth = w;
        boundsHeight = h;
    }

    /**
     * Sets the opacity of a specific tile
     * 
     * @param tileX the x coordinate of the tile
     * @param tileY the y coordinate of the tile
     * @param alpha the opacity of the tile
     */
    public void setTileOpacity(int tileX, int tileY, float alpha) {
        alphaMap[tileY][tileX] = alpha;
    }

    /**
     * Sets if the tile grid should only draw within the bounds.
     * 
     * @param b if the tile grid should only draw within the bounds
     */
    public void drawWithinBounds(boolean b) {
        drawWithinBounds = b;
    }

    /**
     * Draws the tile grid within the bounds.
     * 
     * @param g2d the graphics context
     */
    public void draw(Graphics2D g2d) {

        Rectangle2D bounds = new Rectangle2D.Double(
            boundsX,
            boundsY,
            boundsWidth,
            boundsHeight
        );

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] != -1) {
                    Rectangle2D tile = new Rectangle2D.Double(
                        j * tileSize,
                        i * tileSize,
                        tileSize,
                        tileSize
                    );

                    if (!tile.intersects(bounds) && drawWithinBounds) {
                        continue;
                    }
                    
                    tiles.setPosition(j * tileSize, i * tileSize);
                    tiles.setSprite(map[i][j]);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaMap[i][j]));
                    tiles.draw(g2d);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                }
            }
        }
    }

    /**
     * Gets the array of hitboxes for collision detection.
     * 
     * @return array of hitboxes
     */
    public Rectangle2D[] getHitboxes() {
        return hitboxes;
    }

    /**
     * Gets the hitbox for a specific tile.
     * 
     * @param row the row index
     * @param col the column index
     * @return the hitbox for the tile, or null if invalid position
     */
    public Rectangle2D getTileHitBoxAt(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width)
            return null;

        int tileIndex = map[row][col];
        if (tileIndex == -1)
            return null;

        Rectangle2D hitbox = getHitboxes()[tileIndex];
        double x = col * tileSize;
        double y = row * tileSize;

        if (hitbox != null) {
            return new Rectangle2D.Double(x + hitbox.getX(), y + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
        }

        if (tiles.isNoScaling()) {
            return new Rectangle2D.Double(x, y, tiles.getWidth(), tiles.getHeight());
        }
        return new Rectangle2D.Double(x, y, tileSize, tileSize);
    }

    /**
     * Gets the tile ID at a specific position.
     * 
     * @param row the row index
     * @param col the column index
     * @return the tile ID, or -1 if invalid position
     */
    public int getTileAt(int row, int col) {
        return map[row][col];
    }

    /**
     * Sets the tile ID at a specific position.
     * 
     * @param row the row index
     * @param col the column index
     * @param val the new tile ID
     */
    public void setTileAt(int row, int col, int val) {
        map[row][col] = val;
    }

    /**
     * Draws a range of rows in the tile grid.
     * 
     * @param g2d the graphics context
     * @param startRow the first row to draw
     * @param endRow the last row to draw
     */
    public void drawRows(Graphics2D g2d, int startRow, int endRow) {
        startRow = Math.max(0, startRow);
        endRow = Math.min(height - 1, endRow);

        for (int i = startRow; i <= endRow; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] != -1) {
                    tiles.setPosition(j * tileSize, i * tileSize);
                    tiles.setSprite(map[i][j]);
                    tiles.draw(g2d);
                }
            }
        }
    }

    /**
     * Gets the size of each tile.
     * 
     * @return the tile size
     */
    public double getTileSize() {
        return tileSize;
    }

    /**
     * Sets the size of each tile.
     * 
     * @param s the new tile size
     */
    public void setTileSize(double s) {
        tileSize = s;
    }
}
