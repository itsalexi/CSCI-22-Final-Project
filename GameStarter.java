
import java.io.*;
import java.net.*;

public class GameStarter {

    private static String playerId;
    private static GameCanvas canvas;
    private static String username;
    private static int skin;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void start() {

        try {
            socket = new Socket("localhost", 25565);
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
                    System.out.println("[Server] " + msg);
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
                    System.out.println(state);
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
                    System.out.println(name);

                    if (name.equals("DONE")) {
                        canvas.initializeWorld();
                    } else {
                        SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap");
                        Sprite tiles = new Sprite(tileMapFiles.getFiles(), 32);
                        TileGrid tg = new TileGrid(tiles, parseTileMapString(msg));
                        canvas.setTileGrid(name, tg);
                    }
                    break;
                case "UPDATE":
                    String layer = parts[1];
                    int tileId = Integer.parseInt(parts[2]);
                    int tileX = Integer.parseInt(parts[3]);
                    int tileY = Integer.parseInt(parts[4]);

                    canvas.updateTileGrid(layer, tileX, tileY, tileId);

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

    public static void main(String[] args) {
        if (args.length == 2) {
            username = args[0];
            skin = Integer.parseInt(args[1]);
        }

        new GameStarter().start();
    }

}
