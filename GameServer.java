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
  private int[][] treeMap;
  private FarmingSystem farmSystem;

  private static class PlayerState {
    double x, y;
    String direction, state, username;
    int skin;
  }

  private static class AnimalState {
    double x, y;
    String id, name, direction, state;
    int type, size;
  }

  private static Map<String, PlayerState> playerStates;
  private static Map<String, AnimalState> animalStates;
  private String animalControllerId = null;

  private WorldGenerator worldGen;

  public GameServer() {
    numPlayers = 0;
    maxPlayers = 15;
    players = new HashMap<>();
    playerStates = new HashMap<>();
    animalStates = new HashMap<>();
    Random rand = new Random();

    farmSystem = new FarmingSystem(100);
    worldGen = new WorldGenerator(rand.nextInt(), 100, 100);
    groundMap = worldGen.getGroundMap();
    edgeMap = worldGen.getEdgeMap();
    foliageMap = worldGen.getFoliageMap();
    treeMap = worldGen.getTreeMap();

    // // Layer: Ground
    // groundMap = new int[][] {
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 },
    // { 36, 36, -1, -1, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 },
    // { 36, 36, -1, 153, 153, 153, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36, 36,
    // 36, 36, 36,
    // 36 },
    // { 36, 36, -1, 153, 153, 153, 153, 153, -1, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, -1, -1,
    // -1, -1, -1, 36,
    // 36 },
    // { 36, 36, -1, -1, 153, 153, 153, 153, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // -1, 153,
    // 153, 153, -1, -1,
    // 36 },
    // { 36, 36, 36, -1, -1, 153, 153, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // -1, 153,
    // 153, 153, 153, -1,
    // 36 },
    // { 36, 36, 36, 36, -1, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1,
    // -1, -1,
    // -1, 153, -1, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // -1, 153, -1, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // -1, -1, -1, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1,
    // -1, -1,
    // 36, 36, 36, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1, 150,
    // 153, 153, -1,
    // 36, 36, 36,
    // 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, 154, 154,
    // 153, 153,
    // -1, -1, 36, 36,
    // 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, -1, -1, -1, -1,
    // -1, -1,
    // -1, 36, 36, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 },
    // { 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
    // 36, 36,
    // 36, 36, 36, 36 }
    // };

    // // Layer: Water Edges
    // edgeMap = new int[][] {
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, 138, 133, 133, 133, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1, -1,
    // -1, -1, -1, -1,
    // -1 },
    // { -1, -1, 182, -1, -1, -1, 117, 133, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1,
    // -1 },
    // { -1, -1, 182, -1, -1, -1, -1, -1, 148, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // 138, 133,
    // 133, 133, 140, -1,
    // -1 },
    // { -1, -1, 162, 102, -1, -1, -1, -1, 148, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // 182, -1,
    // -1, -1, 117, 140,
    // -1 },
    // { -1, -1, -1, 162, 102, -1, -1, 101, 164, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // 182, -1,
    // -1, -1, -1, 148,
    // -1 },
    // { -1, -1, -1, -1, 162, 163, 163, 164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // 162, 163,
    // 163, 102, -1,
    // 148, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // 182, -1, 148,
    // -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // 162, 163, 164,
    // -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 138,
    // 139, 139,
    // 140, -1, -1, -1,
    // -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 138, 133, 116,
    // -1, -1,
    // 148, -1, -1, -1,
    // -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 182, -1, -1,
    // -1, -1, 117,
    // 140, -1, -1,
    // -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 162, 163, 163,
    // 163, 163,
    // 163, 164, -1, -1,
    // -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 }
    // };

    // // Layer: Foliage
    // foliageMap = new int[][] {
    // { -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, -1, -1, -1, -1, 82, -1,
    // -1, -1,
    // -1, -1, 94, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 79, -1, -1, -1, -1, -1,
    // -1, 85,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, 85 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, 76, 94, -1, -1, -1, -1, -1, -1, 94, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 85, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, 82 },
    // { -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, 85, -1, -1, 94, -1, 79, -1, -1, -1, 76, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 76, -1,
    // -1, 85,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, 79, -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, 82, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 85, -1, -1, -1, -1,
    // -1, 76,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, 76, -1, -1, -1, -1, -1, -1, -1, -1, 94, -1, -1,
    // -1, -1,
    // -1, -1, 85, -1 },
    // { -1, -1, -1, -1, -1, -1, 94, -1, -1, 94, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, 82, -1, -1 },
    // { -1, -1, -1, 76, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, 79, -1 },
    // { -1, -1, -1, -1, -1, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, 94, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, 76, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    // -1, -1,
    // -1, -1, -1, -1 }
    // };

    new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(1000);
          int[][] farmMap = farmSystem.getFarmMap();
          for (int y = 0; y < farmMap.length; y++) {
            for (int x = 0; x < farmMap[y].length; x++) {
              if (farmMap[y][x] != -1) {
                String update = farmSystem.grow(x, y);
                if (!update.isEmpty()) {
                  broadcastAll(update);
                }
              }
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

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

  public void spawnAnimal(String animalId, String name, int type, double x, double y, int size, String dir,
      String state) {
    AnimalState a = new AnimalState();
    a.id = animalId;
    a.name = name;
    a.type = type;
    a.x = x;
    a.y = y;
    a.size = size;
    a.direction = dir;
    a.state = state;

    animalStates.put(animalId, a);

    broadcastAll("ANIMAL ADD " + animalId + " " + name + " " + type + " " +
        x + " " + y + " " + size + " " + dir + " " + state);
  }

  public void moveAnimal(String animalId, double x, double y, String dir, String state) {
    AnimalState a = animalStates.get(animalId);
    if (a != null) {
      a.x = x;
      a.y = y;
      a.direction = dir;
      a.state = state;

      broadcastAll("ANIMAL MOVE " + animalId + " " + x + " " + y + " " + dir + " " + state);
    }
  }

  public void acceptConnections() {
    spawnAnimal("animal1", "cow", 0, 250, 350, 64, "LEFT", "idle");
    spawnAnimal("animal2", "chicken", 0, 150, 365, 32, "LEFT", "idle");
    spawnAnimal("animal3", "sheep", 0, 300, 50, 64, "LEFT", "idle");
    spawnAnimal("animal4", "pig", 0, 350, 100, 64, "LEFT", "idle");
    spawnAnimal("animal5", "fox", 0, 130, 365, 64, "LEFT", "idle");
    spawnAnimal("animal6", "cat", 0, 110, 340, 64, "LEFT", "idle");
    spawnAnimal("animal7", "dog", 0, 100, 320, 64, "LEFT", "idle");

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
          // System.out.println(playerId + ": " + msg);

          handleMessage(msg);
        }
      } catch (IOException e) {
        System.out.println(playerId + " disconnected.");
        players.remove(playerId);
        playerStates.remove(playerId);

        if (playerId.equals(animalControllerId)) {
          animalControllerId = null;

          for (String id : players.keySet()) {
            animalControllerId = id;
            break;
          }
        }

        broadcast("LEAVE " + playerId, playerId);
      } finally {
        try {
          socket.close();
        } catch (IOException ignored) {
        }
      }
    }

    private void handleMessage(String msg) {
      System.out.println(msg);
      String[] parts = msg.split(" ");
      String type = parts[0];

      switch (type) {
        case "JOIN": {
          String username = parts[1];
          int skin = Integer.parseInt(parts[2]);

          int[] coords = worldGen.getValidSpawn();

          int spawnX = coords[0] * 32 + 16;
          int spawnY = coords[1] * 32 + 16;

          PlayerState ps = new PlayerState();
          ps.x = spawnX;
          ps.y = spawnY;
          ps.direction = "DOWN";
          ps.state = "idle";
          ps.username = username;
          ps.skin = skin;
          playerStates.put(playerId, ps);

          send("JOIN_SUCCESS " + playerId + " " + spawnX + " " + spawnY);
          if (animalControllerId == null) {
            animalControllerId = playerId;
          }
          send(tileMapToString("ground", groundMap));
          send(tileMapToString("edge", edgeMap));
          send(tileMapToString("foliage", foliageMap));
          send(tileMapToString("farm", farmSystem.getFarmMap()));
          send(tileMapToString("tree", treeMap));

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
          for (AnimalState a : animalStates.values()) {
            send("ANIMAL ADD " + a.id + " " + a.name + " " + a.type + " "
                + a.x + " " + a.y + " " + a.size + " " + a.direction + " " + a.state);
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
          broadcast(msg, playerId);

          Timer timer = new Timer();
          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              if (tool.equals("HOE")) {
                if (foliageMap[y][x] != -1) {
                  foliageMap[y][x] = -1;
                  broadcastAll("UPDATE foliage -1 " + x + " " + y);
                } else if (groundMap[y][x] == 36) {
                  groundMap[y][x] = 353;
                  broadcastAll("UPDATE ground 353 " + x + " " + y);
                }
              } else if (tool.equals("WATER")) {
                if (groundMap[y][x] == 353) {
                  groundMap[y][x] = 354;
                  broadcastAll("UPDATE ground 354 " + x + " " + y);
                }
              }
              timer.cancel();
            }
          }, 650);
          break;
        }

        case "ANIMAL": {
          String action = parts[1];
          if (!playerId.equals(animalControllerId))
            return;

          if (action.equals("MOVE")) {
            String animalId = parts[2];
            double x = Double.parseDouble(parts[3]);
            double y = Double.parseDouble(parts[4]);
            String dir = parts[5];
            String state = parts[6];

            AnimalState a = animalStates.get(animalId);

            if (a != null) {
              a.x = x;
              a.y = y;
              a.direction = dir;
              a.state = state;
            }
            broadcastAll(msg);

          }
          break;

        }

        case "FARM": {
          String action = parts[1];
          if (action.equals("PLANT")) {
            String cropType = parts[2];
            int x = Integer.parseInt(parts[3]);
            int y = Integer.parseInt(parts[4]);
            if (groundMap[y][x] == 354) {
              broadcastAll(farmSystem.plant(x, y, cropType));
            }
          } else {
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            String id = parts[4];
            int[][] farmMap = farmSystem.getFarmMap();
            int val = farmMap[y][x];
            System.out.println(val);

            if ((val < 6 && val == 5) || (val >= 6 && val % 6 == 5)) {
              System.out.println("its mature");
              broadcastSelf("INVENTORY ADD " + id + " " + farmSystem.getPlantFromIndex(val) + " 2", playerId);
            }
            broadcastAll(farmSystem.harvest(x, y));
          }
          break;
        }

        case "ITEM": {
          String action = parts[1];
          if (action.equals("DROP")) {
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            int itemId = Integer.parseInt(parts[4]);
            int quantity = Integer.parseInt(parts[5]);

          }
        }
      }
    }

    public void send(String msg) {
      synchronized (out) {
        try {
          out.writeUTF(msg);
          out.flush();
        } catch (IOException ignored) {
        }
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

  private void broadcastSelf(String msg, String selfId) {
    for (PlayerConnection pc : players.values()) {
      if (pc.playerId.equals(selfId)) {
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
