import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameCanvas extends JComponent{

    private Timer repaintTimer;

    public GameCanvas(){
        repaintTimer = new Timer(60/1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                repaint();
            } 
        });
        repaintTimer.start();
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;


    }
    
}
