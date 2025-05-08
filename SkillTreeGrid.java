public class SkillTreeGrid {
    
    private SkillTreeSystem skillTree;
    private TileGrid skillTreeGrid;
    private TileGrid skillIconsGrid;
    private GameCanvas canvas;

    private Sprite tilesSprites;
    private Sprite itemsSprites;

    private int[][] skillTreeGridMap = {
        {2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6},
        {3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7},
        {3, 10, 10, 10, 10, 0, 10, 10, 10, 10, 7},
        {3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7},
        {3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7},
        {3, 10, 10, 0, 10, 10, 10, 0, 10, 10, 7},
        {3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7},
        {3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7},
        {3, 10, 0, 10, 0, 10, 0, 10, 0, 10, 7},
        {3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7},
        {4, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8}
    };

    private int[][] skillIconsGridMap = {
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    };

    public SkillTreeGrid(GameCanvas c) {
        SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
        SpriteFiles itemFiles = new SpriteFiles("assets/items");
        tilesSprites = new Sprite(tileMapFiles.getFiles(), 32);
        itemsSprites = new Sprite(itemFiles.getFiles(), 32);

        skillTreeGrid = new TileGrid(tilesSprites, skillTreeGridMap);
        skillIconsGrid = new TileGrid(itemsSprites, skillIconsGridMap);

        canvas = c;
    }

    public int[] getTileAtMouse(double mouseX, double mouseY) {
        double tileSize = skillTreeGrid.getTileSize();
        double gridWidth = skillTreeGridMap[0].length * tileSize;
        double gridHeight = skillTreeGridMap.length * tileSize;

        double x = (canvas.getWidth() - gridWidth) / 2;
        double y = (canvas.getHeight() - gridHeight) / 2;
        double localX = mouseX - x;
        double localY = mouseY - y;
        int tileX = (int) (localX / tileSize);
        int tileY = (int) (localY / tileSize);

        return new int[] { tileX, tileY };
    }
}
