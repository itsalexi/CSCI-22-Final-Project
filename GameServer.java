import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

  private ServerSocket ss;
  private int numPlayers;
  private int maxPlayers;
  private int numItemsDropped;

  private Map<String, PlayerConnection> players;
  private int[][] groundMap;
  private int[][] edgeMap;
  private int[][] foliageMap;
  private int[][] treeMap;
  private FarmingSystem farmSystem;

  private static class PlayerState {
    double x, y;
    String direction, state, username;
    int skin, balance, skillPoints;
  }

  private static class AnimalState {
    double x, y;
    String id, name, direction, state;
    int type, size;
  }

  private static class DroppedItemState {
    double x, y;
    int id, itemId, quantity;
  }

  private static Map<String, PlayerState> playerStates;
  private static Map<String, AnimalState> animalStates;
  private static Map<Integer, DroppedItemState> droppedItemStates;
  private Map<String, PlayerState> savedPlayerStates = new HashMap<>();
  private Map<String, Item[]> savedInventories = new HashMap<>();

  private String animalControllerId = null;

  private WorldGenerator worldGen;

  public GameServer() {
    numItemsDropped = 0;
    numPlayers = 0;
    maxPlayers = 15;
    players = new HashMap<>();
    playerStates = new HashMap<>();
    animalStates = new HashMap<>();
    droppedItemStates = new HashMap<>();
    Random rand = new Random();

    farmSystem = new FarmingSystem(100);
    worldGen = new WorldGenerator(rand.nextInt(), 100, 100);
    groundMap = worldGen.getGroundMap();
    edgeMap = worldGen.getEdgeMap();
    foliageMap = worldGen.getFoliageMap();
    treeMap = worldGen.getTreeMap();

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

  private void savePlayerToMemory(String playerId) {
    PlayerState ps = playerStates.get(playerId);
    if (ps != null) {
      savedPlayerStates.put(playerId, ps);
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

          handleMessage(msg);
        }
      } catch (IOException e) {
        System.out.println(playerId + " disconnected due to IOException.");
      } finally {
        try {
          if (playerId != null) {
            savePlayerToMemory(playerId);
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
            broadcastAll("CHAT (" + playerId + " has left the game!)");

          }

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
          System.out.println(msg);
          String username = parts[1];
          int skin = Integer.parseInt(parts[2]);

          if (players.containsKey(username)) {
            send("JOIN_FAILED Username already connected.");
            return;
          }

          players.remove(playerId);
          playerId = username;
          players.put(playerId, this);

          PlayerState ps;
          if (savedPlayerStates.containsKey(username)) {
            ps = savedPlayerStates.get(username);
          } else {
            int[] coords = worldGen.getValidSpawn();
            ps = new PlayerState();
            ps.x = coords[0] * 32;
            ps.y = coords[1] * 32;
            ps.direction = "DOWN";
            ps.state = "idle";
            ps.balance = 200; // TODO: change to 0
            ps.skillPoints = 20;
          }

          ps.username = username;
          ps.skin = skin;

          playerStates.put(playerId, ps);

          send("JOIN_SUCCESS " + playerId + " " + ps.x + " " + ps.y + " " + ps.balance + " " + ps.skillPoints);

          if (animalControllerId == null) {
            animalControllerId = playerId;
          }

          send(tileMapToString("ground", groundMap));
          send(tileMapToString("edge", edgeMap));
          send(tileMapToString("foliage", foliageMap));
          send(tileMapToString("farm", farmSystem.getFarmMap()));
          send(tileMapToString("tree", treeMap));

          for (DroppedItemState i : droppedItemStates.values()) {
            send("ITEMDROP CREATE " + i.x + " " + i.y + " " + i.itemId + " " + i.quantity + " " + i.id);
          }

          Item[] inv = new Item[36];
          inv[0] = new Item(1, 1);
          inv[1] = new Item(0, 1);
          inv[2] = new Item(3, 1);
          inv[3] = new Item(5, 1);
          inv[4] = new Item(7, 1);
          inv[5] = new Item(9, 1);
          inv[6] = new Item(11, 1);
          inv[7] = new Item(13, 1);

          if (savedInventories.containsKey(playerId)) {
            System.out.println("test");
            inv = savedInventories.get(playerId);
          }
          StringBuilder sb = new StringBuilder("INVENTORY LOAD " + playerId);
          for (int i = 0; i < inv.length; i++) {
            Item item = inv[i];
            int itemId = (item != null) ? item.getId() : -1;
            int quantity = (item != null) ? item.getQuantity() : 0;
            sb.append(" ").append(i).append(",").append(itemId).append(",").append(quantity);
          }
          send(sb.toString());

          send("TILEMAP DONE");

          for (Map.Entry<String, PlayerState> entry : playerStates.entrySet()) {
            String otherId = entry.getKey();
            if (!otherId.equals(playerId)) {
              PlayerState other = entry.getValue();
              send("JOIN_ANNOUNCE " + otherId + " " + other.username + " " + other.skin + " "
                  + other.x + " " + other.y + " " + other.direction + " " + other.state);
            }
          }

          for (AnimalState a : animalStates.values()) {
            send("ANIMAL ADD " + a.id + " " + a.name + " " + a.type + " "
                + a.x + " " + a.y + " " + a.size + " " + a.direction + " " + a.state);
          }

          broadcast("JOIN_ANNOUNCE " + playerId + " " + username + " " + skin + " "
              + ps.x + " " + ps.y + " " + ps.direction + " " + ps.state, playerId);
          broadcastAll("CHAT (" + playerId + " has joined the game!)");
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
                  int rand = (int) (Math.random() * 100);

                  if (rand > 80) {
                    int itemId = 3;
                    int quantity = 1;
                    int droppedItemId = numItemsDropped++;

                    double dropX = (x * 32) + 8;
                    double dropY = (y * 32) + 8;

                    DroppedItemState dis = new DroppedItemState();
                    dis.x = dropX;
                    dis.y = dropY;
                    dis.itemId = itemId;
                    dis.quantity = quantity;
                    dis.id = droppedItemId;

                    droppedItemStates.put(droppedItemId, dis);
                    broadcastAll(
                        String.format("ITEMDROP CREATE %f %f %d %d %d", dropX, dropY, itemId, quantity,
                            droppedItemId));
                  }

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

        case "ECONOMY": {
          String action = parts[1];
          String id = parts[2];
          int newBalance = Integer.parseInt(parts[3]);

          if (action.equals("SET")) {
            PlayerState player = playerStates.get(id);
            player.balance = newBalance;
          } else if (action.equals("ADD")) {
            PlayerState player = playerStates.get(id);
            PlayerConnection ps = players.get(id);
            player.balance = player.balance + newBalance;
            ps.send(msg);
          }
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

        case "INVENTORY": {
          String action = parts[1];
          if (action.equals("BULK")) {
            String id = parts[2];
            Item[] inventory = new Item[36];
            for (int i = 3; i < parts.length; i++) {
              String[] entry = parts[i].split(",");
              int slot = Integer.parseInt(entry[0]);
              int itemId = Integer.parseInt(entry[1]);
              int quantity = Integer.parseInt(entry[2]);
              if (itemId != -1 && quantity > 0) {
                inventory[slot] = new Item(itemId, quantity);
              }
            }
            savedInventories.put(id, inventory);
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

              int itemId = farmSystem.getPlantFromIndex(val);
              int quantity = farmSystem.getRandomQuantity(farmSystem.getPlantNameFromIndex(val));
              int droppedItemId = numItemsDropped++;

              double dropX = (x * 32) + 8;
              double dropY = (y * 32) + 8;

              DroppedItemState dis = new DroppedItemState();
              dis.x = dropX;
              dis.y = dropY;
              dis.itemId = itemId;
              dis.quantity = quantity;
              dis.id = droppedItemId;

              droppedItemStates.put(droppedItemId, dis);
              broadcastAll(
                  String.format("ITEMDROP CREATE %f %f %d %d %d", dropX, dropY, itemId, quantity, droppedItemId));
            }
            broadcastAll(farmSystem.harvest(x, y));
          }
          break;
        }

        case "ITEMDROP": {
          String action = parts[1];
          if (action.equals("CREATE")) {
            double x = Double.parseDouble(parts[2]);
            double y = Double.parseDouble(parts[3]);
            int itemId = Integer.parseInt(parts[4]);
            int quantity = Integer.parseInt(parts[5]);
            int droppedItemId = numItemsDropped++;

            DroppedItemState dis = new DroppedItemState();
            dis.x = x;
            dis.y = y;
            dis.itemId = itemId;
            dis.quantity = quantity;
            dis.id = droppedItemId;

            droppedItemStates.put(droppedItemId, dis);
            broadcastAll(String.format("ITEMDROP CREATE %f %f %d %d %d", x, y, itemId, quantity, droppedItemId));
          } else if (action.equals("EDIT")) {
            double x = Double.parseDouble(parts[2]);
            double y = Double.parseDouble(parts[3]);
            int itemId = Integer.parseInt(parts[4]);
            int quantity = Integer.parseInt(parts[5]);
            int droppedItemId = Integer.parseInt(parts[6]);

            DroppedItemState dis = new DroppedItemState();
            dis.x = x;
            dis.y = y;
            dis.itemId = itemId;
            dis.quantity = quantity;
            dis.id = droppedItemId;
            droppedItemStates.put(droppedItemId, dis);
            broadcast(String.format("ITEMDROP EDIT %f %f %d %d %d", x, y, itemId,
                quantity, droppedItemId), playerId);
          } else if (action.equals("PICKUP")) {
            String playerPickupId = parts[2];
            int droppedItemId = Integer.parseInt(parts[3]);
            DroppedItemState dis = droppedItemStates.get(droppedItemId);
            if (dis != null) {
              System.out.println("test1");
              broadcast(String.format("ITEMDROP PICKUP %s %d", playerPickupId, droppedItemId), playerId);
              send(String.format("INVENTORY ADD %s %d %d", playerPickupId, dis.itemId, dis.quantity));
              droppedItemStates.remove(droppedItemId);

            }
          }
          break;
        }

        case "PLAY_SOUND": {
          broadcast(msg, playerId);
          break;
        }
        case "CHAT": {
          broadcast(msg, playerId);
          break;
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
      if (msg.contains("CHAT")) {
        System.out.println(pc.playerId);
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
