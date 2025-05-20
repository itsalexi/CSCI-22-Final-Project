/**
 * The GameServer class manages the multiplayer game server functionality.
 * It handles player connections, game state synchronization, and world management.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 19, 2025
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

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

public class GameServer {

  private ServerSocket ss;
  private int numPlayers;
  private int maxPlayers;
  private int numItemsDropped;
  private int numMovingAnimals;

  private Map<String, PlayerConnection> players;
  private int[][] groundMap;
  private int[][] edgeMap;
  private int[][] foliageMap;
  private int[][] treeMap;
  private FarmingSystem farmSystem;

  /**
   * Represents the state of a player in the game.
   */
  private static class PlayerState {
    double x, y, currXP;
    String direction, state, username;
    int skin, balance, skillPoints, level;
  }

  /**
   * Represents the state of an animal in the game.
   */
  private static class AnimalState {
    double x, y;
    String id, name, direction, state;
    int type, size;
  }

  /**
   * Represents the state of a dropped item in the game.
   */
  private static class DroppedItemState {
    double x, y;
    int id, itemId, quantity;
  }

  private static Map<String, PlayerState> playerStates;
  private static Map<String, AnimalState> animalStates;
  private static Map<String, Deque<double[]>> animalPaths;
  private static Map<Integer, DroppedItemState> droppedItemStates;

  private Map<String, PlayerState> savedPlayerStates = new HashMap<>();
  private Map<String, Item[]> savedInventories = new HashMap<>();
  private Map<String, ArrayList<Skill>> savedSkillTrees = new HashMap<>();

  private ArrayList<Skill> baseSkills;
  private ArrayList<int[]> validSpawns;

  private String animalControllerId = null;

  private WorldGenerator worldGen;

  /**
   * Initializes the game server with default settings and world generation.
   */
  public GameServer() {
    numItemsDropped = 0;
    numPlayers = 0;
    maxPlayers = 15;
    numMovingAnimals = 0;
    players = new HashMap<>();
    playerStates = new HashMap<>();
    animalStates = new HashMap<>();
    animalPaths = new HashMap<>();
    droppedItemStates = new HashMap<>();
    baseSkills = new ArrayList<>();
    Random rand = new Random();

    farmSystem = new FarmingSystem(100);
    worldGen = new WorldGenerator(rand.nextInt(), 100, 100);
    groundMap = worldGen.getGroundMap();
    edgeMap = worldGen.getEdgeMap();
    foliageMap = worldGen.getFoliageMap();
    treeMap = worldGen.getTreeMap();
    validSpawns = worldGen.getValidSpawns();

    Skill one = new Skill("Fruit of Knowledge", 1, 1, 1, 10, 0,
        "\"Getting smarter one harvest at a time.\"\nYou're so smart you probably have no friends!");
    Skill two = new Skill("Lightfooted", 1, 1, 1, 10, 1,
        "\"I swear my feet aren't touching the ground.\"\n No one's gonna catch you slipping");
    Skill three = new Skill("Merchant's Rizz", 1, 1, 1, 10, 2,
        "\"Unspoken rizz or an HR complaint?\"\nAre they paying more to buy or just to make you shut up. Either way, you win ");
    Skill four = new Skill("Cheap Tricks", 1, 1, 1, 10, 3,
        "\"Why pay full price when you can just gas light the skill tree?\"\n Too broke to upgrade huh?");
    Skill five = new Skill("Nature's Grasp", 1, 1, 1, 10, 4,
        "\"Why are my fingers so long??\"\nInteract with things from faraway. Kinda weird though.");
    Skill six = new Skill("Green Thumb", 1, 1, 1, 5, 5,
        "\"Plants just can't get enough of you.\"\nEither way, you're their favorite weirdo.");
    Skill seven = new Skill("Seal of the Serpent", 1, 1, 1, 5, 6,
        "\"One bite never hurt anyone...\"\nEverything's cheaper, and it only took a bite. What could go wrong?");

    baseSkills.add(one);
    baseSkills.add(two);
    baseSkills.add(three);
    baseSkills.add(four);
    baseSkills.add(five);
    baseSkills.add(six);
    baseSkills.add(seven);

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

  /**
   * Saves a player's state to memory.
   * 
   * @param playerId the ID of the player to save
   */
  private void savePlayerToMemory(String playerId) {
    PlayerState ps = playerStates.get(playerId);
    if (ps != null) {
      savedPlayerStates.put(playerId, ps);
    }
  }

  /**
   * Converts a tile map to a string representation.
   * 
   * @param name the name of the map
   * @param map the 2D array representing the map
   * @return string representation of the map
   */
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
  
  /**
   * Finds a path between two points avoiding obstacles.
   * 
   * @param obj the object to find path for
   * @param target the target position
   * @param speed the movement speed
   * @return deque of positions representing the path
   */
  private Deque<double[]> findPath(Rectangle2D obj, double[] target, double speed) {

    double tileSize = 32;
    Rectangle2D targetTile = new Rectangle2D.Double(
        Math.floor(target[0] / tileSize) * tileSize,
        Math.floor(target[1] / tileSize) * tileSize,
        tileSize,
        tileSize);

    ArrayList<Rectangle2D> collidableObjects = new ArrayList<>();
    ArrayList<int[][]> collidableMaps = new ArrayList<>();
    collidableMaps.add(edgeMap);
    collidableMaps.add(treeMap);
    for (int[][] map : collidableMaps) {
      for (int i=0; i < map.length; i++) {
        for (int j=0; j < map[0].length; j++) {
          if (map[i][j] != -1) {
            collidableObjects.add(new Rectangle2D.Double(
              j * tileSize,
              i * tileSize,
              tileSize,
              tileSize
            ));
            if (targetTile.intersects(collidableObjects.getLast())) {
              return new ArrayDeque<>();
            }
          }
        }
      }
    }
    for (int i=0; i < groundMap.length; i++) {
      for (int j=0; j< groundMap.length; j++) {
        if (groundMap[i][j] == 36) {
          Rectangle2D waterTile = new Rectangle2D.Double(
            j * tileSize,
            i * tileSize,
            tileSize,
            tileSize
          );
          if (targetTile.intersects(waterTile)) {
            return new ArrayDeque<>();
          }
        }
      }
    }

    Deque<Deque<double[]>> output = new ArrayDeque<>();
    Set<Position> visited = new HashSet<>();
    double[] currPosition = { obj.getCenterX(), obj.getCenterY() };
    Deque<double[]> initialPath = new ArrayDeque<>();
    initialPath.add(currPosition);
    output.add(initialPath);

    while (!output.isEmpty()) {
      Deque<double[]> currPath = output.removeFirst();
      double[] lastVisited = currPath.peekLast();

      Position lastVisitedPosition = new Position(lastVisited);
      if (visited.contains(lastVisitedPosition)) {
        continue;
      }

      Rectangle2D currHitbox = new Rectangle2D.Double(
          lastVisited[0] - obj.getWidth() / 2,
          lastVisited[1] - obj.getHeight() / 2,
          obj.getWidth(),
          obj.getHeight());
      if (currHitbox.intersects(targetTile)) {
        return currPath;
      }

      visited.add(lastVisitedPosition);

      double[] up = { speed, 0 };
      double[] right = { 0, speed };
      double[] down = { -speed, 0 };
      double[] left = { 0, -speed };
      double[][] directions = { up, right, left, down };

      for (int i = 0; i < 4; i++) {
        double[] nextPosition = new double[2];
        nextPosition[0] = lastVisited[0] + directions[i][0];
        nextPosition[1] = lastVisited[1] + directions[i][1];
        Rectangle2D nextHitbox = new Rectangle2D.Double(
            nextPosition[0] - obj.getWidth() / 2,
            nextPosition[1] - obj.getHeight() / 2,
            obj.getWidth(),
            obj.getHeight());

        Boolean collides = false;
        for (Rectangle2D collidable : collidableObjects) {
          if (collidable.intersects(nextHitbox)) {
            collides = true;
            break;
          }
        }

        if (collides) {
          continue;
        }

        Deque<double[]> newPath = new ArrayDeque<>();
        newPath.addAll(currPath);
        newPath.add(nextPosition);
        output.add(newPath);

      }
    }

    return new ArrayDeque<>();
  }

  /**
   * Asynchronously finds a path between two points.
   * 
   * @param obj the object to find path for
   * @param target the target position
   * @param speed the movement speed
   * @param onResult callback for when path is found
   */
  private void findPathAsync(Rectangle2D obj, double[] target, double speed,
      Consumer<Deque<double[]>> onResult) {
    new Thread(() -> {
      Deque<double[]> path = findPath(obj, target, speed);
      onResult.accept(path);
    }).start();
  }

  /**
   * Clamps a value between a minimum and maximum.
   * 
   * @param left minimum value
   * @param right maximum value
   * @param value value to clamp
   * @return clamped value
   */
  private double clamp(double left, double right, double value) {
    return Math.max(left, Math.min(right, value));
  }

  /**
   * Calculates distance between two points.
   * 
   * @param x1 first point x
   * @param y1 first point y
   * @param x2 second point x
   * @param y2 second point y
   * @return distance between points
   */
  private double distance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
  }

  /**
   * Updates an animal's position based on its path.
   * 
   * @param id the animal's ID
   */
  private void tickAnimal(String id) {
    AnimalState a = animalStates.get(id);
    Deque<double[]> path = animalPaths.get(id);

    if (path.size() != 0) {
      System.out.println("BRO IT'S WALKING");
      double[] newPos = path.peekFirst();
      String dir = a.direction;
      String state = "WALK";
      if (a.x < newPos[0]) {
        dir = "RIGHT";
      } else if (a.x > newPos[0]) {
        dir = "LEFT";
      } else if (a.y < newPos[1]) {
        dir = "UP";
      } else if (a.y > newPos[1]) {
        dir = "DOWN";
      } else {
        state = "IDLE";
      }

      moveAnimal(id, newPos[0], newPos[1], dir, state);
      path.removeFirst();
      if (path.size() == 0) {
        numMovingAnimals--;
      }
    }
  }

  /**
   * Makes an animal move randomly.
   * 
   * @param id the animal's ID
   */
  private void randomMoveAnimal(String id) {

    if (numMovingAnimals > 4) {
      return;
    }

    AnimalState a = animalStates.get(id);
    double tileSize = 32;
    double chanceToMove = 5;
    double roll = Math.random() * 100;
    if (roll < 100 - chanceToMove) {
      System.out.println(roll + " " + chanceToMove);
      return;
    }

    System.out.println("should move " + id);

    double movementRange = 5 * tileSize;
    double x = a.x + (2 * movementRange * Math.random()) - movementRange;
    double y = a.y + (2 * movementRange * Math.random()) - movementRange;
    double[] targetPos = {clamp(0, groundMap[0].length * tileSize - 1, x), clamp(0, groundMap.length * tileSize - 1, y)};
    Rectangle2D hitbox = new Rectangle2D.Double(a.x, a.y, a.size, a.size);
    numMovingAnimals++;
    findPathAsync(hitbox, targetPos,
      1, newPath -> {
        if (newPath.size() > 0) { 
          animalPaths.put(id, newPath);
        } else {
          System.out.println("no path");
          numMovingAnimals--;
        }
      });
  }

  /**
   * Spawns a new animal in the game world.
   * 
   * @param animalId the animal's ID
   * @param name the animal's name
   * @param type the animal's type
   * @param x x position
   * @param y y position
   * @param size the animal's size
   * @param dir the animal's direction
   * @param state the animal's state
   */
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
    animalPaths.put(animalId, new ArrayDeque<>());

    broadcastAll("ANIMAL ADD " + animalId + " " + name + " " + type + " " +
        x + " " + y + " " + size + " " + dir + " " + state);
  }

  /**
   * Moves an animal to a new position.
   * 
   * @param animalId the animal's ID
   * @param x new x position
   * @param y new y position
   * @param dir new direction
   * @param state new state
   */
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

  /**
   * Spawns a random animal in a valid location.
   */
  public void spawnRandomAnimal() {
    String animalId = "animal" + animalStates.size();
    String[] animalTypes = { "cow", "chicken", "sheep", "pig", "fox", "cat", "dog" };
    String animalType = animalTypes[(int) (Math.random() * animalTypes.length)];

    int[] pos = validSpawns.get((int) (Math.random() * validSpawns.size()));
    spawnAnimal(animalId, animalType, 0, pos[1] * 32, pos[0] * 32, animalType.equals("chicken") ? 32 : 64, "LEFT",
        "IDLE");
  }

  /**
   * Accepts and manages player connections.
   */
  public void acceptConnections() {
    for (int i = 0; i < 100; i++) {
      spawnRandomAnimal();
    }
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

  /**
   * Handles individual player connections and message processing.
   */
  private class PlayerConnection implements Runnable {
    private String playerId;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Creates a new player connection.
     * 
     * @param id player ID
     * @param s socket connection
     * @param inS input stream
     * @param outS output stream
     */
    public PlayerConnection(String id, Socket s, DataInputStream inS, DataOutputStream outS) {
      playerId = id;
      socket = s;
      in = inS;
      out = outS;
    }

    /**
     * Main connection loop handling messages.
     */
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

    /**
     * Processes incoming messages from the player.
     * 
     * @param msg the message to process
     */
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
            ps.balance = 0;
            ps.skillPoints = 0;
            ps.level = 0;
            ps.currXP = 0;
          }

          ps.username = username;
          ps.skin = skin;

          playerStates.put(playerId, ps);

          send("JOIN_SUCCESS " + playerId + " " + ps.x + " " + ps.y + " " + ps.balance + " " + ps.skillPoints + " "
              + ps.level + " " + ps.currXP);

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

          if (savedInventories.containsKey(playerId)) {
            inv = savedInventories.get(playerId);
          }

          StringBuilder sb;
          sb = new StringBuilder("INVENTORY LOAD " + playerId);
          for (int i = 0; i < inv.length; i++) {
            Item item = inv[i];
            int itemId = (item != null) ? item.getId() : -1;
            int quantity = (item != null) ? item.getQuantity() : 0;
            sb.append(" ").append(i).append(",").append(itemId).append(",").append(quantity);
          }
          send(sb.toString());

          ArrayList<Skill> playersSkills = new ArrayList<>(baseSkills);


          if (savedSkillTrees.containsKey(playerId)) {
            playersSkills = savedSkillTrees.get(playerId);
          } else {
            savedSkillTrees.put(playerId, playersSkills);
          }

          sb = new StringBuilder("SKILLTREE LOAD ");
          for (Skill s : playersSkills) {
            sb.append(s.getLevel()).append(",");
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
        case "SKILLTREE":
          String action = parts[1];
          if (action.equals("SET")) {
            ArrayList<Skill> currSkills = savedSkillTrees.get(parts[2]);
            Skill modify = currSkills.get(Integer.parseInt(parts[3]));
            modify.setLevel(Integer.parseInt(parts[4]));
            savedSkillTrees.put(playerId, currSkills);
          }
          break;
        case "LEVELS":
          action = parts[1];
          type = parts[2];
          if (action.equals("SET")) {
            PlayerState ps = savedPlayerStates.get(parts[3]);
            if (ps != null) {
              switch (type) {
                case "XP":
                  ps.currXP = Double.parseDouble(parts[4]);
                  break;
                case "LVL":
                  ps.level = Integer.parseInt(parts[4]);
                  break;
              }
              savedPlayerStates.put(playerId, ps);
            }
          }
          break;
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

    /**
     * Sends a message to the player.
     * 
     * @param msg the message to send
     */
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

  /**
   * Broadcasts a message to all players except the sender.
   * 
   * @param msg the message to broadcast
   * @param senderId the ID of the sending player
   */
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

  /**
   * Broadcasts a message to all players.
   * 
   * @param msg the message to broadcast
   */
  private void broadcastAll(String msg) {
    for (PlayerConnection pc : players.values()) {
      pc.send(msg);
    }
  }

  /**
   * Main method to start the game server.
   * 
   * @param args command line arguments
   */
  public static void main(String[] args) {
    GameServer gs = new GameServer();
    gs.acceptConnections();
  }
}
