import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class TileGrid {

    private Sprite tiles;
    private int width, height;
    private double tileSize;
    private int[][] map;
    private Rectangle2D[] hitboxes;

    public TileGrid(Sprite tiles, int[][] map, Rectangle2D[] hb) {
        this.tiles = tiles;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = tiles.getSize();
        hitboxes = hb;
    }

    public TileGrid(Sprite tiles, int[][] map) {
        this.tiles = tiles;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = tiles.getSize();
        hitboxes = new Rectangle2D[512];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Graphics2D g2d) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] != -1) {
                    tiles.setPosition(j * tileSize, i * tileSize);
                    tiles.setSprite(map[i][j]);
                    tiles.draw(g2d);
                }
            }
        }
    }

    public Rectangle2D[] getHitboxes() {
        return hitboxes;
    }

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

    public int getTileAt(int row, int col) {
        return map[row][col];
    }

    public void setTileAt(int row, int col, int val) {
        map[row][col] = val;
    }

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

    public double getTileSize() {
        return tileSize;
    }

    public void setTileSize(double s) {
        tileSize = s;
    }
}
