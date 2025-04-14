
import java.io.*;
import java.net.*;
import java.util.Map;

public class GameStarter {

    private static String playerId;
    private static GameCanvas canvas;
    private Map<String, Player> otherPlayers;

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
            out.writeUTF("JOIN");
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
            double x, y;

            switch (type) {
                case "JOIN_SUCCESS":
                    playerId = parts[1];
                    x = Double.parseDouble(parts[2]);
                    y = Double.parseDouble(parts[3]);
                    canvas.getPlayer().setPosition(x, y);
                    break;

                case "JOIN_ANNOUNCE":
                case "MOVE": {
                    String id = parts[1];
                    if (id.equals(playerId))
                        break;
                    x = Double.parseDouble(parts[2]);
                    y = Double.parseDouble(parts[3]);
                    String dir = parts[4];
                    String state = parts[5];

                    canvas.updatePlayer(id, x, y, dir, state);
                    break;
                }

                case "LEAVE":
                    String id = parts[1];
                    canvas.removePlayer(id);
                    break;
            }
        }
    }

    public String getPlayerID() {
        return playerId;
    }

    public static void main(String[] args) {
        new GameStarter().start();
    }

}
