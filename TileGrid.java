import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class TileGrid {

    private Sprite tiles;
    private int width, height;
    private double tileSize;
    private int[][] map;
    private ArrayList<Rectangle2D> hitboxes;


    public TileGrid(Sprite tiles, int[][] map){
        this.tiles = tiles;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = tiles.getSize();
        hitboxes = new ArrayList<>();

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Graphics2D g2d){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j ++){
                if (map[i][j] != -1) {
                    tiles.setPosition(j * tileSize, i * tileSize);
                    tiles.setSprite(map[i][j]);
                    tiles.draw(g2d);
                }
            }
        }
    }
    public Rectangle2D getTileHitBox(int tileIndex) {
        return hitboxes.get(tileIndex);
    }
    
}
