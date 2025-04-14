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

        f.setTitle("Placeholder");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    public GameCanvas getCanvas() {
        return gc;
    }
}
