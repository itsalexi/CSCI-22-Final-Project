import java.awt.*;
import java.util.ArrayList;

public class SkillTreeGrid {

    private SkillTreeSystem skillTree;
    private TileGrid skillTreeGrid;
    private TileGrid skillIconsGrid;
    private GameCanvas canvas;

    private Sprite tilesSprites;
    private Sprite itemsSprites;

    private boolean isOpen;

    private int[][] skillPositions = {
            { 2, 5 },
            { 5, 3 },
            { 5, 7 },
            { 8, 2 },
            { 8, 4 },
            { 8, 6 },
            { 8, 8 }
    };

    private int[][] skillTreeGridMap = {
            { 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6 },
            { 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7 },
            { 3, 10, 10, 10, 10, 0, 10, 10, 10, 10, 7 },
            { 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7 },
            { 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7 },
            { 3, 10, 10, 0, 10, 10, 10, 0, 10, 10, 7 },
            { 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7 },
            { 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7 },
            { 3, 10, 0, 10, 0, 10, 0, 10, 0, 10, 7 },
            { 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 7 },
            { 4, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8 }
    };

    private int[][] skillIconsGridMap = {
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
    };

    public SkillTreeGrid(SkillTreeSystem sys, GameCanvas c) {
        SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap/inventory");
        SpriteFiles itemFiles = new SpriteFiles("assets/items");
        tilesSprites = new Sprite(tileMapFiles.getFiles(), 32);
        itemsSprites = new Sprite(itemFiles.getFiles(), 32);

        skillTreeGrid = new TileGrid(tilesSprites, skillTreeGridMap);
        skillIconsGrid = new TileGrid(itemsSprites, skillIconsGridMap);

        skillTree = sys;
        canvas = c;
        isOpen = false;
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

    public int[][] getSkillPositions() {
        return skillPositions;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void updateSkillTree() {
        ArrayList<Skill> skills = skillTree.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            int x = skillPositions[i][0];
            int y = skillPositions[i][1];

            if (skills.get(i).isUnlocked()) {
                skillTreeGridMap[x][y] = 1;
            } else {
                skillTreeGridMap[x][y] = 0;
            }

            skillIconsGridMap[x][y] = skills.get(i).getIcon();
        }
    }

    private void drawSkillLevels(Graphics2D g2d) {
        for (int i = 0; i < skillTree.getSkills().size(); i++) {
            Skill currSkill = skillTree.getSkills().get(i);
            if (currSkill.isUnlocked()) {
                double tileSize = skillTreeGrid.getTileSize();
                int x = skillPositions[i][1];
                int y = skillPositions[i][0];
                drawLabel(g2d, Integer.toString(currSkill.getLevel()), x, y, tileSize);
            }
        }
    }

    private void drawLabel(Graphics2D g2d, String label, int col, int row, double tileSize) {
        g2d.setFont(new Font("Minecraft", Font.PLAIN, (int) (25 * tileSize / 32)));
        FontMetrics fm = g2d.getFontMetrics();

        int stringWidth = fm.stringWidth(label);
        float quantityLabelX = (float) ((col + 1) * tileSize) - stringWidth;
        float quantityLabelY = (float) ((row + 1) * tileSize);

        g2d.setColor(Color.BLACK);
        g2d.drawString(label, quantityLabelX + 1, quantityLabelY - 1);
        g2d.drawString(label, quantityLabelX + 1, quantityLabelY + 1);
        g2d.drawString(label, quantityLabelX - 1, quantityLabelY - 1);
        g2d.drawString(label, quantityLabelX - 1, quantityLabelY + 1);

        g2d.setColor(Color.WHITE);
        g2d.drawString(label, quantityLabelX, quantityLabelY);
    }

    public void draw(Graphics2D g2d) {
        updateSkillTree();

        double tileSize = canvas.getWidth() * 32 / 800;
        double gridWidth = skillTreeGridMap[0].length * tileSize;
        double gridHeight = skillTreeGridMap.length * tileSize;

        tilesSprites.setSize(tileSize);
        itemsSprites.setSize(tileSize);
        skillTreeGrid.setTileSize(tileSize);
        skillIconsGrid.setTileSize(tileSize);

        double x = (canvas.getWidth() - gridWidth) / 2;
        double y = (canvas.getHeight() - gridHeight) / 2;

        if (isOpen()) {
            g2d.translate(x, y);
            skillTreeGrid.draw(g2d);
            skillIconsGrid.draw(g2d);
            drawSkillLevels(g2d);
            g2d.translate(-x, -y);
        }
    }
}
