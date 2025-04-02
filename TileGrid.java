import java.awt.Graphics2D;

public class TileGrid {

    private Tile tiles;
    private int width, height;
    private double tileSize;
    private int[][] map;

    public TileGrid(Tile tiles, int[][] map){
        this.tiles = tiles;
        this.map = map;
        height = map.length;
        width = map[0].length;
        tileSize = tiles.getTileSize();
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
    
}
