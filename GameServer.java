import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

  private ServerSocket ss;
  private int numPlayers;
  private int maxPlayers;

  private Map<String, PlayerConnection> players;
  private int[][] groundMap;
  private int[][] edgeMap;
  private int[][] foliageMap;

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

    // Layer: Ground
    groundMap = new int[][] {
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 },
        { 36, 36, -1, -1, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 },
        { 36, 36, -1, 153, 153, 153, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36,
            36 },
        { 36, 36, -1, 153, 153, 153, 153, 153, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1,
            -1, -1, -1, 36,
            36 },
        { 36, 36, -1, -1, 153, 153, 153, 153, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, 153,
            153, 153, -1, -1,
            36 },
        { 36, 36, 36, -1, -1, 153, 153, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, 153,
            153, 153, 153, -1,
            36 },
        { 36, 36, 36, 36, -1, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1, -1,
            -1, 153, -1, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            -1, 153, -1, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            -1, -1, -1, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1, -1, -1,
            36, 36, 36, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1, 150, 153, 153, -1,
            36, 36, 36,
            36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, 154, 154, 153, 153,
            -1, -1, 36, 36,
            36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1, -1, -1, -1, -1,
            -1, 36, 36, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 },
        { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
            36, 36, 36, 36 }
    };

    // Layer: Water Edges
    edgeMap = new int[][] {
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, 138, 133, 133, 133, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1 },
        { -1, -1, 182, -1, -1, -1, 117, 133, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1,
            -1 },
        { -1, -1, 182, -1, -1, -1, -1, -1, 148, -1, -1, -1, -1, -1, -1, -1, -1, -1, 138, 133,
            133, 133, 140, -1,
            -1 },
        { -1, -1, 162, 102, -1, -1, -1, -1, 148, -1, -1, -1, -1, -1, -1, -1, -1, -1, 182, -1,
            -1, -1, 117, 140,
            -1 },
        { -1, -1, -1, 162, 102, -1, -1, 101, 164, -1, -1, -1, -1, -1, -1, -1, -1, -1, 182, -1,
            -1, -1, -1, 148,
            -1 },
        { -1, -1, -1, -1, 162, 163, 163, 164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 162, 163,
            163, 102, -1,
            148, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            182, -1, 148,
            -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            162, 163, 164,
            -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 138, 139, 139,
            140, -1, -1, -1,
            -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 138, 133, 116, -1, -1,
            148, -1, -1, -1,
            -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 182, -1, -1, -1, -1, 117,
            140, -1, -1,
            -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 162, 163, 163, 163, 163,
            163, 164, -1, -1,
            -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 }
    };

    // Layer: Foliage
    foliageMap = new int[][] {
        { -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1,
            -1, -1, 94, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 79, -1, -1, -1, -1, -1, -1, 85,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, 85 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, 76, 94, -1, -1, -1, -1, -1, -1, 94, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 85, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, 82 },
        { -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, 85, -1, -1, 94, -1, 79, -1, -1, -1, 76, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 76, -1, -1, 85,
            -1, -1, -1, -1 },
        { -1, -1, -1, 79, -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, 82, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 85, -1, -1, -1, -1, -1, 76,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, 76, -1, -1, -1, -1, -1, -1, -1, -1, 94, -1, -1, -1, -1,
            -1, -1, 85, -1 },
        { -1, -1, -1, -1, -1, -1, 94, -1, -1, 94, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, 82, -1, -1 },
        { -1, -1, -1, 76, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, 79, -1 },
        { -1, -1, -1, -1, -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, 94, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, 76, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1 }
    };

    try {
      ss = new ServerSocket(25565);
      System.out.println("===== GAME SERVER STARTED =====");
    } catch (IOException e) {
      System.out.println("IOException from GameServer");
    }
  }

  private String tileMapToString(String name, int[][] map) {
    String output = "TILEMAP " + name + " ";

    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[i].length; j++) {
        output += map[i][j];

        if (j < map[i].length - 1) {
          output += ",";
        }
      }

      if (i < map.length - 1) {
        output += " | ";
      }
    }

    return output;
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
          send(tileMapToString("ground", groundMap));
          send(tileMapToString("edge", edgeMap));
          send(tileMapToString("foliage", foliageMap));
          send("TILEMAP DONE");
          for (Map.Entry<String, PlayerState> entry : playerStates.entrySet()) {
            String otherId = entry.getKey();
            if (!otherId.equals(playerId)) {
              PlayerState other = entry.getValue();
              send("JOIN_ANNOUNCE " + otherId + " " + other.username + " "
                  + other.skin + " "
                  + other.x + " " + other.y + " "
                  + other.direction
                  + " " + other.state);
            }
          }

          broadcast(
              "JOIN_ANNOUNCE " + playerId + " " + username + " " + skin + " "
                  + spawnX + " "
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
          String id = parts[1];
          String tool = parts[2];
          int x = Integer.parseInt(parts[3]);
          int y = Integer.parseInt(parts[4]);

          if (tool.equals("HOE")) {
            System.out.println("HE USED A HOE");
            if (foliageMap[y][x] != -1) {
              foliageMap[y][x] = -1;
              broadcastAll("UPDATE foliage -1 " + x + " " + y);
            }
          }
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

  private void broadcastAll(String msg) {
    for (PlayerConnection pc : players.values()) {
      pc.send(msg);

    }
  }

  public static void main(String[] args) {
    GameServer gs = new GameServer();
    gs.acceptConnections();
  }
}
