import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;

    private Map<String, PlayerConnection> players;

    private static class PlayerState {
        double x, y;
        String direction, state, username;
        int skin;
    }

    private static Map<String, PlayerState> playerStates;

    public GameServer() {
        numPlayers = 0;
        maxPlayers = 15;
        players = new HashMap<>();
        playerStates = new HashMap<>();

        try {
            ss = new ServerSocket(25565);
            System.out.println("===== GAME SERVER STARTED =====");
        } catch (IOException e) {
            System.out.println("IOException from GameServer");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections.");
            while (numPlayers < maxPlayers) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                String tempId = "player" + (numPlayers + 1);
                PlayerConnection pc = new PlayerConnection(tempId, s, in, out);
                players.put(tempId, pc);

                new Thread(pc).start();

                System.out.println("Player #" + (numPlayers + 1) + " has connected.");
                numPlayers++;
            }
        } catch (IOException e) {
            System.out.println("IOException from acceptConnections()");
        }
    }

    private class PlayerConnection implements Runnable {
        private String playerId;
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;

        public PlayerConnection(String id, Socket s, DataInputStream inS, DataOutputStream outS) {
            playerId = id;
            socket = s;
            in = inS;
            out = outS;
        }

        public void run() {
            try {
                while (true) {
                    String msg = in.readUTF();
                    System.out.println(playerId + ": " + msg);
                    handleMessage(msg);
                }
            } catch (IOException e) {
                System.out.println(playerId + " disconnected.");
                players.remove(playerId);
                playerStates.remove(playerId);
                broadcast("LEAVE " + playerId, playerId);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        private void handleMessage(String msg) {
            String[] parts = msg.split(" ");
            String type = parts[0];

            switch (type) {
                case "JOIN": {
                    String username = parts[1];
                    int skin = Integer.parseInt(parts[2]);

                    int spawnX = 100;
                    int spawnY = 300;

                    PlayerState ps = new PlayerState();
                    ps.x = spawnX;
                    ps.y = spawnY;
                    ps.direction = "DOWN";
                    ps.state = "idle";
                    ps.username = username;
                    ps.skin = skin;
                    playerStates.put(playerId, ps);

                    send("JOIN_SUCCESS " + playerId + " " + spawnX + " " + spawnY);
                    for (Map.Entry<String, PlayerState> entry : playerStates.entrySet()) {
                        String otherId = entry.getKey();
                        if (!otherId.equals(playerId)) {
                            PlayerState other = entry.getValue();
                            send("JOIN_ANNOUNCE " + otherId + " " + other.username + " " + other.skin + " "
                                    + other.x + " " + other.y + " "
                                    + other.direction
                                    + " " + other.state);
                        }
                    }

                    broadcast(
                            "JOIN_ANNOUNCE " + playerId + " " + username + " " + skin + " " + spawnX + " "
                                    + spawnY + " DOWN idle",
                            playerId);
                    break;
                }

                case "MOVE": {
                    String id = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String dir = parts[4];
                    String state = parts[5];

                    PlayerState ps = playerStates.get(id);
                    if (ps != null) {
                        ps.x = x;
                        ps.y = y;
                        ps.direction = dir;
                        ps.state = state;
                    }

                    broadcast(msg, playerId);
                    break;
                }
                case "ACTION": {
                    broadcast(msg, playerId);
                    break;
                }

                case "UPDATE": {
                    broadcast(msg, playerId);
                    break;
                }
            }
        }

        public void send(String msg) {
            try {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException ignored) {
            }
        }
    }

    private void broadcast(String msg, String senderId) {
        for (PlayerConnection pc : players.values()) {
            if (!pc.playerId.equals(senderId)) {
                pc.send(msg);
            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
