import java.awt.Graphics2D;
import java.util.ArrayList;

public class TileGrid {

    private Sprite sprite;
    private int width, height, tileSize;
    private int[][] map;

    public TileGrid(Sprite sprite, int[][] map){
        this.sprite = sprite;
        this.map = map;
        width = map.length;
        height = map[0].length;
        tileSize = (int) sprite.getWidth();
    }

    public void draw(Graphics2D g2d){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j ++){
                sprite.setPosition((double) (i * tileSize), (double) (j * tileSize));
                sprite.setSprite(map[i][j]);
                sprite.draw(g2d);
            }
        }
    }
    
}
