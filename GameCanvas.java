import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class GameCanvas extends JComponent{

    private Timer repaintTimer;
    private Sprite testSprite;

    public GameCanvas(){
        repaintTimer = new Timer(60/1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                repaint();
            } 
        });
        repaintTimer.start();

        this.setPreferredSize(new Dimension(800, 600));

        File f = new File("assets/test.jpg");
        ArrayList<File> listF = new ArrayList<>();
        listF.add(f);
        testSprite = new Sprite(listF);
        testSprite.setPosition(100, 100);
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        testSprite.draw(g2d);
    }
    
}
