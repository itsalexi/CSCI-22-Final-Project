import java.awt.Dimension;
import javax.swing.*;

public class GameFrame {

    private JFrame f;
    private GameCanvas gc;

    public GameFrame() {
        f = new JFrame();
        gc = new GameCanvas();
    }

    public void setUpGUI() {
        f.add(gc);
        f.setPreferredSize(new Dimension(1024, 768));
        f.setTitle("Garden of Eden");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);

    }

    public GameCanvas getCanvas() {
        return gc;
    }
}
