
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class GameStarter {

    private static String playerId;
    private static GameCanvas canvas;
    private String username, ip, port;
    private int skin;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void start() {
        SetupFrame sf = new SetupFrame(this);
        sf.setUpGUI();
    }

    public void startGame() {

        try {
            socket = new Socket(ip, Integer.parseInt(port));
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            GameFrame frame = new GameFrame();
            canvas = frame.getCanvas();
            canvas.setClient(this);
            canvas.setServerOut(out);
            out.writeUTF(String.format("JOIN %s %d", username, skin));
            out.flush();

            new Thread(new ReadFromServer(in)).start();

            frame.setUpGUI();

        } catch (IOException e) {
            System.out.println("IOException in GameStarter");
        }
    }

    private static int[][] parseTileMapString(String tilemapString) {
        String[] rows = tilemapString.split(" ", 3)[2].split(" \\| ");
        int[][] map = new int[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split(",");
            map[i] = new int[cols.length];
            for (int j = 0; j < cols.length; j++) {
                map[i][j] = Integer.parseInt(cols[j]);
            }
        }

        return map;
    }

    private static class ReadFromServer implements Runnable {
        private DataInputStream in;

        public ReadFromServer(DataInputStream in) {
            this.in = in;
            System.out.println("[Client] ReadFromServer thread started.");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg = in.readUTF();
                    handleMessage(msg);
                }
            } catch (IOException e) {
                System.out.println("[Client] Disconnected from server.");
            }
        }

        private void handleMessage(String msg) {
            String[] parts = msg.split(" ");
            String type = parts[0];
            String id, dir, state, action;
            double x, y;

            switch (type) {
                case "JOIN_SUCCESS":
                    playerId = parts[1];
                    x = Double.parseDouble(parts[2]);
                    y = Double.parseDouble(parts[3]);
                    int balance = Integer.parseInt(parts[4]);
                    int skillPoints = Integer.parseInt(parts[5]);
                    int level = Integer.parseInt(parts[6]);
                    double xp = Double.parseDouble(parts[7]);

                    canvas.getEconomySystem().setBalance(balance);
                    canvas.getEconomySystem().setSkillPoints(skillPoints);
                    canvas.getLevelingSystem().setLevel(level);
                    canvas.getLevelingSystem().setXP(xp);
                    canvas.getPlayer().setPosition(x, y);
                    break;

                case "JOIN_ANNOUNCE":
                    id = parts[1];
                    if (id.equals(playerId))
                        break;
                    String username = parts[2];
                    int skin = Integer.parseInt(parts[3]);
                    x = Double.parseDouble(parts[4]);
                    y = Double.parseDouble(parts[5]);
                    dir = parts[6];
                    state = parts[7];
                    canvas.addPlayer(id, username, skin, x, y, dir, state);
                    break;
                case "MOVE": {
                    id = parts[1];
                    if (id.equals(playerId))
                        break;
                    x = Double.parseDouble(parts[2]);
                    y = Double.parseDouble(parts[3]);
                    dir = parts[4];
                    state = parts[5];

                    canvas.updatePlayer(id, x, y, dir, state);
                    break;
                }

                case "ACTION": {
                    id = parts[1];
                    action = parts[2];
                    int tileX = Integer.parseInt(parts[3]);
                    int tileY = Integer.parseInt(parts[4]);
                    dir = parts[5];
                    canvas.actionPlayer(id, tileX, tileY, dir, action);
                    break;
                }
                case "LEAVE":
                    id = parts[1];
                    canvas.removePlayer(id);
                    break;
                case "TILEMAP":
                    action = parts[0];
                    String name = parts[1];

                    if (name.equals("DONE")) {
                        canvas.initializeWorld();
                    } else {
                        String tilemapType;

                        Rectangle2D[] defaultBox = new Rectangle2D[512];
                        defaultBox[138] = new Rectangle2D.Double(14, 13.3, 18.45, 22.03);
                        defaultBox[139] = new Rectangle2D.Double(0, 14, 32, 18.45);
                        defaultBox[182] = new Rectangle2D.Double(14, 0, 18.03, 32);
                        defaultBox[162] = new Rectangle2D.Double(14, 0, 17.77, 20.77);
                        defaultBox[133] = new Rectangle2D.Double(0, 12, 32, 20);
                        defaultBox[148] = new Rectangle2D.Double(0, 0, 14, 32);
                        defaultBox[162] = new Rectangle2D.Double(14, 0, 16, 16.5);
                        defaultBox[147] = new Rectangle2D.Double(14, 0, 16, 32);
                        defaultBox[163] = new Rectangle2D.Double(0, 0, 32, 16.5);
                        defaultBox[164] = new Rectangle2D.Double(0, 0, 14, 16.5);
                        defaultBox[117] = new Rectangle2D.Double(0, 12, 32, 19.51);
                        defaultBox[116] = new Rectangle2D.Double(0, 12, 32, 16.5);
                        defaultBox[101] = new Rectangle2D.Double(0, 0, 32, 16.5);
                        defaultBox[102] = new Rectangle2D.Double(0, 0, 32, 16.5);
                        defaultBox[140] = new Rectangle2D.Double(0, 12, 14, 20);

                        Rectangle2D[] treeBox = new Rectangle2D[512];
                        treeBox[0] = new Rectangle2D.Double(10, 25, 14, 18);
                        treeBox[1] = new Rectangle2D.Double(10, 25, 14, 18);

                        Rectangle2D[] hitboxes = new Rectangle2D[512];

                        switch (name) {
                            case "farm":
                                tilemapType = "assets/tilemap/crops";
                                break;
                            case "tree":
                                tilemapType = "assets/tilemap/tree";
                                hitboxes = treeBox;
                                break;
                            default:
                                tilemapType = "assets/tilemap/world";
                                hitboxes = defaultBox;
                                break;
                        }
                        System.out.println(tilemapType);
                        SpriteFiles tileMapFiles = new SpriteFiles(tilemapType);
                        Sprite tiles;

                        if (name.equals("tree")) {
                            tiles = new Sprite(tileMapFiles.getFiles(), true, true);
                            System.out.println(tiles.getHeight());
                            System.out.println(tiles.getWidth());

                        } else {
                            tiles = new Sprite(tileMapFiles.getFiles(), 32);
                        }

                        TileGrid tg = new TileGrid(tiles, parseTileMapString(msg), hitboxes);
                        canvas.setTileGrid(name, tg);
                    }
                    break;
                case "UPDATE":
                    String layer = parts[1];
                    int tileId = Integer.parseInt(parts[2]);
                    int tileX = Integer.parseInt(parts[3]);
                    int tileY = Integer.parseInt(parts[4]);

                    if (canvas.isLoaded()) {
                        canvas.updateTileGrid(layer, tileX, tileY, tileId);
                    }
                    break;
                case "INVENTORY":
                    action = parts[1];
                    if (action.equals("ADD")) {
                        int itemId = Integer.parseInt(parts[3]);
                        int quantity = Integer.parseInt(parts[4]);
                        canvas.getInventory().addItem(itemId, quantity);
                    }

                    if (action.equals("LOAD")) {
                        int i = 3;
                        while (i < parts.length) {
                            String[] data = parts[i].split(",");
                            int slot = Integer.parseInt(data[0]);
                            int itemId = Integer.parseInt(data[1]);
                            int quantity = Integer.parseInt(data[2]);

                            if (itemId == -1 || quantity <= 0) {
                                canvas.getInventory().setItem(slot, null);
                            } else {
                                canvas.getInventory().setItem(slot, new Item(itemId, quantity));
                            }
                            i++;
                        }
                    }
                    break;
                case "ANIMAL":
                    action = parts[1];
                    if (action.equals("ADD")) {
                        String animalId = parts[2];
                        String animalName = parts[3];
                        int animalType = Integer.parseInt(parts[4]);
                        x = Double.parseDouble(parts[5]);
                        y = Double.parseDouble(parts[6]);
                        int size = Integer.parseInt(parts[7]);
                        String direction = parts[8];
                        state = parts[9];
                        canvas.addAnimal(animalId, animalName, animalType, x, y, size, direction, state);
                    } else if (action.equals("MOVE")) {
                        String animalId = parts[2];
                        x = Double.parseDouble(parts[3]);
                        y = Double.parseDouble(parts[4]);
                        String direction = parts[5];
                        state = parts[6];
                        if (canvas.getAnimals().containsKey(animalId)) {
                            canvas.updateAnimal(animalId, x, y, direction, state);
                        }
                    }
                    break;
                case "JOIN_FAILED":
                    System.out.println("[Client] Failed to join: " + msg.substring(12));
                    System.exit(0);
                    break;
                case "ITEMDROP":
                    action = parts[1];
                    if (action.equals("CREATE")) {
                        x = Double.parseDouble(parts[2]);
                        y = Double.parseDouble(parts[3]);
                        int itemId = Integer.parseInt(parts[4]);
                        int quantity = Integer.parseInt(parts[5]);
                        int droppedItemId = Integer.parseInt(parts[6]);
                        canvas.addDroppedItem(x, y, itemId, quantity, droppedItemId);
                    }
                    if (action.equals("EDIT")) {
                        x = Double.parseDouble(parts[2]);
                        y = Double.parseDouble(parts[3]);
                        int itemId = Integer.parseInt(parts[4]);
                        int quantity = Integer.parseInt(parts[5]);
                        int droppedItemId = Integer.parseInt(parts[6]);
                        canvas.updateDroppedItem(x, y, itemId, quantity, droppedItemId);
                    }
                    if (action.equals("PICKUP")) {
                        int droppedItemId = Integer.parseInt(parts[3]);
                        canvas.getDroppedItems().remove(droppedItemId);
                    }
                    break;
                case "PLAY_SOUND":
                    String soundCode = parts[1];
                    x = Double.parseDouble(parts[2]);
                    y = Double.parseDouble(parts[3]);

                    canvas.playSound(soundCode, x, y);
                    break;
                case "CHAT":
                    String content = "";
                    int start = msg.indexOf('(');
                    int end = msg.lastIndexOf(')');
                    if (start != -1 && end != -1 && end > start) {
                        content = msg.substring(start + 1, end).trim();
                    }

                    canvas.getChatSystem().addMessage(content);
                    break;
                case "ECONOMY":
                    action = parts[1];
                    int amount = Integer.parseInt(parts[3]);

                    if (action.equals("ADD")) {
                        EconomySystem es = canvas.getEconomySystem();
                        es.setBalance(es.getBalance() + amount);
                        ChatSystem cs = canvas.getChatSystem();
                        cs.addMessage("You have received " + amount + " coins!");
                    }
                    break;
                case "SKILLTREE":
                    action = parts[1];
                    if (action.equals("LOAD")) {
                        String[] data = parts[2].split(",");
                        int[] levels = new int[7];
                        for (int i = 0; i < 7; i++) {
                            levels[i] = Integer.parseInt(data[i]);
                        }
                        canvas.setLevels(levels);
                    }
            }
        }

    }

    public String getPlayerID() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public int getSkin() {
        return skin;
    }

    public void setConnectionInfo(String addr, String p, String u, int s) {
        ip = addr;
        port = p;
        username = u;
        skin = s;
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        final GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final List<String> AVAILABLE_FONT_FAMILY_NAMES = Arrays.asList(GE.getAvailableFontFamilyNames());
        try {
            final List<File> LIST = Arrays.asList(
                    new File("assets/fonts/Minecraft.ttf"));
            for (File LIST_ITEM : LIST) {
                if (LIST_ITEM.exists()) {
                    Font FONT = Font.createFont(Font.TRUETYPE_FONT, LIST_ITEM);
                    if (!AVAILABLE_FONT_FAMILY_NAMES.contains(FONT.getFontName())) {
                        GE.registerFont(FONT);
                    }
                }
            }
        } catch (FontFormatException | IOException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }

        new GameStarter().start();
    }

}
