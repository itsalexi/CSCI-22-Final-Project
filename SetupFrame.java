/**
 * The SetupFrame class manages the game's setup and initialization window.
 * It handles the main menu, connection screen, and audio setup.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version 20 May 2025
 * 
 * I have not discussed the Java language code in my program 
 * with anyone other than my instructor or the teaching assistants 
 * assigned to this course.
 * 
 * I have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in my program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of my program.
 */

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
    private GameAudio gameAudio;

    /**
     * Creates a new SetupFrame instance.
     * Initializes the main menu, connection screen, and audio system.
     * 
     * @param s the GameStarter instance that manages game initialization
     */
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

    /**
     * Sets up the GUI properties and displays the window.
     * Configures window size, close operation, and visibility.
     */
    public void setUpGUI() {
        setPreferredSize(new Dimension(1024, 768));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
        showMainMenu();
    }

    /**
     * Gets the game audio manager.
     * 
     * @return the GameAudio instance
     */
    public GameAudio getGameAudio() {
        return gameAudio;
    }

    /**
     * Initializes the sound system by loading sound effects and background music.
     * Sets up the audio manager with shuffled background music.
     */
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

    /**
     * Plays a sound effect by its code.
     * 
     * @param soundCode the code of the sound to play
     */
    public void playSound(String soundCode) {
        Sound sound = sounds.get(soundCode);
        sound.play();
    }

    /**
     * Shows the connection screen.
     */
    public void showConnectionScreen() {
        cardLayout.show(cards, "ConnectionScreen");
    }

    /**
     * Shows the main menu.
     */
    public void showMainMenu() {
        cardLayout.show(cards, "MainMenu");
    }

    /**
     * Gets this SetupFrame instance.
     * 
     * @return this SetupFrame instance
     */
    public SetupFrame getSetupFrame() {
        return this;
    }
}
