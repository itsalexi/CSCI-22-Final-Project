import java.awt.Graphics2D;

public class TileGrid {

    private Sprite sprite;
    private int width, height, tileSize;
    private int[][] map;

    public TileGrid(Sprite sprite, int[][] map){
        this.sprite = sprite;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = (int) sprite.getWidth();
    }

    public void draw(Graphics2D g2d){
       
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j ++){
                if (map[i][j] != -1) {
                    sprite.setPosition((double) (j * tileSize), (double) (i * tileSize));
                    sprite.setSprite(map[i][j]);
                    sprite.draw(g2d);
                }
            }
        }
    }
    
}
