import java.awt.*;
import javax.swing.*;

public class SetupFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cards;
    private GameStarter starter;

    public SetupFrame(GameStarter s) {
        super("Garden of Eden");
        starter = s;

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        MainMenu mainMenu = new MainMenu(starter, this);
        ConnectionScreen connectionScreen = new ConnectionScreen(starter, this);

        cards.add(mainMenu, "MainMenu");
        cards.add(connectionScreen, "ConnectionScreen");

        add(cards);
    }

    public void setUpGUI() {
        setPreferredSize(new Dimension(1024, 768));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
        showMainMenu();
    }

    public void showConnectionScreen() {
        cardLayout.show(cards, "ConnectionScreen");
    }

    public void showMainMenu() {
        cardLayout.show(cards, "MainMenu");
    }

    public SetupFrame getSetupFrame() {
        return this;
    }
}
