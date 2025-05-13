import java.awt.Dimension;
import javax.swing.*;

public class SetupFrame {

    private JFrame f;
    private MainMenu mm;

    public SetupFrame(GameStarter s) {
        f = new JFrame();
        mm = new MainMenu(s, this);
    }

    public void setUpGUI() {
        f.add(mm);
        f.setPreferredSize(new Dimension(1024, 768));
        f.setTitle("Garden of Eden");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    }

    public JFrame getSetupFrame() {
        return f;
    }

}
