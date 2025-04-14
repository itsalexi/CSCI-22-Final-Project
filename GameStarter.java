
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
                    x = Double.parseDouble(parts[3]);
                    y = Double.parseDouble(parts[4]);
                    dir = parts[5];
                    canvas.actionPlayer(id, x, y, dir, action);
                    break;
                }
                case "LEAVE":
                    id = parts[1];
                    canvas.removePlayer(id);
                    break;
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
