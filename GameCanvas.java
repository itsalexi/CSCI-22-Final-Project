import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.*;

public class GameCanvas extends JComponent {

    private Timer repaintTimer;
    private Tile testSprite;
    private TileGrid fg;

    public GameCanvas() {
        repaintTimer = new Timer(60 / 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        repaintTimer.start();

        this.setPreferredSize(new Dimension(800, 600));

        ArrayList<File> listF = new ArrayList<>();
        for( File f : new File("assets").listFiles()){
            listF.add(f);
        }
        listF.sort(new Comparator<File>() {
            @Override
            public int compare(File a, File b){
                int a_num = Integer.parseInt(a.getName().split("[\\_\\.]")[1]);
                int b_num = Integer.parseInt(b.getName().split("[\\_\\.]")[1]);
                return a_num > b_num ? 1 : (a_num < b_num ? -1 : 0);
            }
        });
        for( File f : listF ){
            System.out.println(f.getName());
        }
        testSprite = new Tile(listF, 32);
        // Layer: Layer 1
        int[][] tilemap = {
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 },
                { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 },
                { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 },
                { 0, -1, -1, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, 0, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, -1, -1, 0, -1, 0, -1, 0, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };

        fg = new TileGrid(testSprite, tilemap);

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        fg.draw(g2d);
    }

}
