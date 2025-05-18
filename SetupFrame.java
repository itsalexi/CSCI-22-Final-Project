import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class SetupFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cards;
    private GameStarter starter;

    private ArrayList<Sound> bgm;
    private Map<String, Sound> sounds;
    private GameAudio gameAudio;;

    public SetupFrame(GameStarter s) {
        super("Garden of Eden");
        starter = s;
        bgm = new ArrayList<>();
        sounds = new HashMap<>();
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        MainMenu mainMenu = new MainMenu(starter, this);
        ConnectionScreen connectionScreen = new ConnectionScreen(starter, this);

        cards.add(mainMenu, "MainMenu");
        cards.add(connectionScreen, "ConnectionScreen");
        setupSounds();
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

    public GameAudio getGameAudio() {
        return gameAudio;
    }

    private void setupSounds() {
        ArrayList<File> sfxList = new ArrayList<>();
        sfxList.addAll(Arrays.asList(new File("assets/sfx/").listFiles()));
        for (File f : sfxList) {
            sounds.put(f.getName().substring(0, f.getName().lastIndexOf(".")), new Sound(f));
        }

        ArrayList<File> bgmList = new ArrayList<>();
        bgmList.addAll(Arrays.asList(new File("assets/bgm/").listFiles()));
        for (File f : bgmList) {
            bgm.add(new Sound(f));
        }
        Collections.shuffle(bgm);
        gameAudio = new GameAudio(bgm);
        gameAudio.start();
    }

    public void playSound(String soundCode) {
        Sound sound = sounds.get(soundCode);
        sound.play();
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
