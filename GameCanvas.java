import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

public class GameCanvas extends JComponent {

  private Timer repaintTimer;
  private Sprite highlight;
  private Sprite invHighlight;

  private ArrayList<TileGrid> collidableGrids;
  private Player player;
  private Map<String, Player> otherPlayers;
  private Map<String, Animal> animals;
  private Map<String, TileGrid> tileGrids;
  private GameStarter client;
  private WriteToServer writer;
  private int[] lastClickedTile;
  private int[] lastClickedInventoryTile;
  private boolean isMapLoaded;
  private double anchorX, anchorY;
  private Inventory inventory;
  private double zoom;
  private Sprite hoveredItemSprite;
  private Item hoveredItem;
  private int previousItemSlot;
  private Boolean test;

  public GameCanvas() {
    isMapLoaded = false;
    otherPlayers = new HashMap<>();
    tileGrids = new HashMap<>();
    animals = new HashMap<>();
    inventory = new Inventory(this);
    collidableGrids = new ArrayList<>();
    zoom = 2;
    previousItemSlot = -1;
    test = false;

    SpriteFiles selectorFiles = new SpriteFiles("assets/ui/selector");
    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    hoveredItemSprite = new Sprite(itemFiles.getFiles(), 32);

    lastClickedTile = new int[2];
    lastClickedInventoryTile = new int[] { 6, 1 };
    highlight = new Sprite(selectorFiles.getFiles(), 32);
    invHighlight = new Sprite(selectorFiles.getFiles(), 32);
    invHighlight.setSprite(1);
    addKeyBindings();
    addMouseListener();

    repaintTimer = new Timer(1000 / 60, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.tick();

        Boolean isMoving = false;
        for (Boolean isActive : player.getActiveDirections().values()) {
          isMoving = isMoving || isActive;
        }

        if (isMoving) {
          movePlayer();
        }

        for (Player ghost : otherPlayers.values()) {
          ghost.tick();
        }

        for (Map.Entry<String, Animal> entry : new ArrayList<>(animals.entrySet())) {
          String id = entry.getKey();
          Animal animal = entry.getValue();

          animal.tick();

          animal.randomAction(GameCanvas.this);

          writer.send("ANIMAL MOVE " + id + " " + animal.getX() + " " + animal.getY()
              + " " + animal.getDirection() + " " + animal.getAnimationState());

        }
        repaint();
      }
    });

  }

  public void setServerOut(DataOutputStream out) {
    writer = new WriteToServer(out);
  }

  public void setTileGrid(String name, TileGrid grid) {
    tileGrids.put(name, grid);
  }

  public void initializeWorld() {
    collidableGrids.add(tileGrids.get("edge"));
    setMapLoaded(true);
  }

  public void setOtherPlayers(Map<String, Player> p) {
    otherPlayers = p;
  }

  public boolean isLoaded() {
    return isMapLoaded;
  }

  public boolean isColliding(Rectangle2D hitbox) {
    for (TileGrid cg : collidableGrids) {
      for (int i = 0; i < cg.getHeight(); i++) {
        for (int j = 0; j < cg.getWidth(); j++) {
          Rectangle2D collidableHitbox = cg.getTileHitBoxAt(i, j);
          if (collidableHitbox != null) {
            if (hitbox.intersects(collidableHitbox)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private void doPlayerAction(int x, int y) {
    if (!player.isDoingAction()) {
      if (inventory.getActiveItem() != null) {
        Item activeItem = inventory.getActiveItem();
        boolean itemUsed = false;
        for (String action : player.getPlayerActions().keySet()) {
          String[] actionString = activeItem.getActionName().split(" ");
          if (actionString[0].equals(action)) {

            player.useAction(actionString[0]);
            writer.send("ACTION " + client.getPlayerID() + " " + actionString[0].toUpperCase() + " " + x
                + " " + y + " "
                + player.getDirection());
            if (actionString[0].equals("hoe")) {
              if (tileGrids.get("farm").getTileAt(y, x) != -1) {
                writer.send("FARM HARVEST " + x + " " + y);
              }
            }

            if (actionString[0].equals("plant")) {
              if (tileGrids.get("ground").getTileAt(y, x) == 354) {
                if (tileGrids.get("farm").getTileAt(y, x) == -1) {
                  System.out.println("FARM PLANT " + actionString[1] + " " + x + " " + y);
                  writer.send("FARM PLANT " + actionString[1] + " " + x + " " + y);
                  itemUsed = true;
                }
              }
            }
            if (itemUsed) {
              activeItem.consume();
            }
            return;
          }
        }

      }
    }
  }

  public Map<String, Animal> getAnimals() {
    return animals;
  }

  public void addKeyBindings() {
    this.setFocusable(true);
    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if ((Character.isDigit(c))) {
          inventory.setActiveHotbarSlot(Character.getNumericValue(c) - 1);
          return;
        }
        String direction = null;
        switch (e.getKeyCode()) {
          case KeyEvent.VK_W:
            direction = "UP";
            break;
          case KeyEvent.VK_S:
            direction = "DOWN";
            break;
          case KeyEvent.VK_A:
            direction = "LEFT";
            break;
          case KeyEvent.VK_D:
            direction = "RIGHT";
            break;
          case KeyEvent.VK_E:
            if (inventory.isOpen() && previousItemSlot != -1) {
              inventory.setItem(previousItemSlot, hoveredItem);
              hoveredItem = null;
              previousItemSlot = -1;
            }
            inventory.setOpen(!inventory.isOpen());
            break;
          case KeyEvent.VK_Y:
            player.setActiveTool("hoe");
            break;
          case KeyEvent.VK_R:
            player.setActiveTool("water");
            break;
        }

        if (direction != null) {
          player.setDirectionStatus(direction, true);
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        String direction = null;
        switch (e.getKeyCode()) {
          case KeyEvent.VK_W:
            direction = "UP";
            break;
          case KeyEvent.VK_S:
            direction = "DOWN";
            break;
          case KeyEvent.VK_A:
            direction = "LEFT";
            break;
          case KeyEvent.VK_D:
            direction = "RIGHT";
            break;
        }

        if (direction != null) {
          player.setDirectionStatus(direction, false);
        }

        Boolean isMoving = false;
        for (Boolean isActive : player.getActiveDirections().values()) {
          isMoving = isMoving || isActive;
        }

        if (!player.isDoingAction() && !isMoving) {
          player.setAnimationState("idle");
          writer.send("MOVE " + client.getPlayerID() + " " + player.getX() + " " + player.getY() + " "
              + player.getDirection() + " " + "idle");

        }
      }

    });
  }

  private double[] addVector(double x1, double y1, double x2, double y2) {
    double angle = Math.atan((1 - (y1 + y2)) / -(x1 + x2));
    double[] result = { (x1 + x2), (y1 + y2) };
    return result;
  }

  public void movePlayer() {
    if (player.isDoingAction())
      return;
    double speed = player.getSpeed();
    Map<String, Boolean> activeDirections = player.getActiveDirections();

    String direction = "DOWN";
    double[] currVector = { 0, 0 };

    for (Map.Entry<String, Boolean> entry : activeDirections.entrySet()) {
      String currentDirection = entry.getKey();
      Boolean isActive = entry.getValue();

      if (isActive) {
        direction = currentDirection;
        switch (currentDirection) {
          case "UP":
            currVector = addVector(currVector[0], currVector[1], 0, -speed);
            break;
          case "DOWN":
            currVector = addVector(currVector[0], currVector[1], 0, speed);
            break;
          case "LEFT":
            currVector = addVector(currVector[0], currVector[1], -speed, 0);
            break;
          case "RIGHT":
            currVector = addVector(currVector[0], currVector[1], speed, 0);
            break;
        }
      }
    }

    double newX = clamp(-player.getWidth() * 10 / 32,
        tileGrids.get("ground").getWidth() * tileGrids.get("ground").getTileSize() - player.getWidth() * 22 / 32,
        player.getX() + currVector[0]);
    double newY = clamp(-player.getHeight() * 6 / 32,
        tileGrids.get("ground").getHeight() * tileGrids.get("ground").getTileSize() - player.getHeight() * 26 / 32,
        player.getY() + currVector[1]);

    Rectangle2D futureHitbox = player.getHitboxAt(newX, newY);

    if (!isColliding(futureHitbox)) {
      writer.send("MOVE " + client.getPlayerID() + " " + newX + " " + newY + " " + direction + " " + "walk");
      player.setDirection(direction);
      player.setAnimationState("walk");
      player.setPosition(newX, newY);
    }

  }

  public void updateTileGrid(String name, int x, int y, int val) {
    tileGrids.get(name).setTileAt(y, x, val);
  }

  public void actionPlayer(String id, int x, int y, String dir, String action) {
    Player newPlayer = otherPlayers.get(id);
    switch (action) {
      case "HOE" -> newPlayer.useAction("hoe");
      case "WATER" -> newPlayer.useAction("water");
    }
    otherPlayers.put(id, newPlayer);

  }

  public void updatePlayer(String id, double x, double y, String dir, String state) {
    Player newPlayer = otherPlayers.get(id);
    newPlayer.setPosition(x, y);

    if (!newPlayer.getDirection().equals(dir)) {
      newPlayer.setDirection(dir);
    }

    if (!newPlayer.getAnimationState().equals(state)) {
      newPlayer.setAnimationState(state);
    }
    otherPlayers.put(id, newPlayer);
  }

  public void removePlayer(String id) {
    otherPlayers.remove(id);
  }

  public void addPlayer(String id, String username, int skin, double x, double y, String dir, String state) {
    Player newPlayer = new Player(username, skin);
    newPlayer.setPosition(x, y);
    newPlayer.setDirection(dir);
    newPlayer.setAnimationState(state);

    otherPlayers.put(id, newPlayer);
  }

  public void addAnimal(String id, String animalName, int type, double x, double y, int size, String dir,
      String state) {
    Animal newAnimal = new Animal(x, y, animalName, type, size);
    newAnimal.setDirection(dir);
    newAnimal.setAnimationState(state);
    animals.put(id, newAnimal);
  }

  public void updateAnimal(String id, double x, double y, String dir, String state) {
    Animal newAnimal = animals.get(id);
    newAnimal.setPosition(x, y);

    if (!newAnimal.getDirection().equals(dir)) {
      newAnimal.setDirection(dir);
    }

    if (!newAnimal.getAnimationState().equals(state)) {
      newAnimal.setAnimationState(state);
    }
    animals.put(id, newAnimal);
  }

  private double clamp(double left, double right, double value) {
    return Math.max(left, Math.min(right, value));
  }

  public void addMouseListener() {
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        double x = (e.getX() - (getWidth() / 2)) / zoom + anchorX;
        double y = (e.getY() - (getHeight() / 2)) / zoom + anchorY;

        double tileSize = 32;
        int tileX = (int) Math.floor(x / tileSize);
        int tileY = (int) Math.floor(y / tileSize);

        lastClickedTile[0] = (int) clamp(0, tileGrids.get("ground").getWidth() - 1, tileX);
        lastClickedTile[1] = (int) clamp(0, tileGrids.get("ground").getHeight() - 1, tileY);

        // System.out.println(tileGrids.get("edge").getTileAt(lastClickedTile[1],
        // lastClickedTile[0]));

        if (!inventory.isOpen()) {
          doPlayerAction(lastClickedTile[0], lastClickedTile[1]);
        }
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        // System.out.printf("%f, %f\n", anchorX, anchorY);
        double x = (e.getX() - (getWidth() / 2)) / zoom + anchorX;
        double y = (e.getY() - (getHeight() / 2)) / zoom + anchorY;

        double tileSize = 32;
        int tileX = (int) Math.floor(x / tileSize);
        int tileY = (int) Math.floor(y / tileSize);

        lastClickedTile[0] = (int) clamp(0, tileGrids.get("ground").getWidth() - 1, tileX);
        lastClickedTile[1] = (int) clamp(0, tileGrids.get("ground").getHeight() - 1, tileY);
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        int[] coords = inventory.getTileAtMouse(x, y);
        if (coords != null) {
          int tileX = coords[0];
          int tileY = coords[1];

          boolean isValidHotbar = tileY == 6 && tileX >= 1 && tileX <= 9;
          boolean isValidInventory = tileY >= 1 && tileY <= 3 && tileX >= 1 && tileX <= 9;

          if (isValidHotbar || (inventory.isOpen() && isValidInventory)) {
            lastClickedInventoryTile[0] = tileX;
            lastClickedInventoryTile[1] = tileY;
          } else {
            lastClickedInventoryTile[0] = -1;
            lastClickedInventoryTile[1] = -1;
          }
        }
      }
    });

    this.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        int current = inventory.getActiveHotbarSlot();
        if (notches < 0) {
          current = (current + 1) % 9;
        } else {
          current = (current - 1 + 9) % 9;
        }

        inventory.setActiveHotbarSlot(current);
      }
    });

    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (inventory.isOpen()) {
          int tileX = lastClickedInventoryTile[0];
          int tileY = lastClickedInventoryTile[1];

          if (tileX == -1)
            return;

          int slot = inventory.getSlotFromGrid(tileX, tileY);

          if (hoveredItem != null) {
            Item temp = inventory.getItem(slot) != null ? inventory.getItem(slot) : null;
            inventory.setItem(slot, hoveredItem);
            hoveredItem = temp;
            previousItemSlot = inventory.getEmptySlot(); // TODO: make item drop instead
            if (hoveredItem != null) {
              hoveredItemSprite.setSprite(hoveredItem.getId());
              hoveredItemSprite.setPosition(x - hoveredItemSprite.getWidth() * hoveredItemSprite.getHScale() / 2,
                  y - hoveredItemSprite.getHeight() * hoveredItemSprite.getVScale() / 2);
            }
          } else {
            hoveredItem = inventory.getItem(slot);
            if (hoveredItem != null) {
              previousItemSlot = slot;
              hoveredItemSprite.setSprite(hoveredItem.getId());
              hoveredItemSprite.setPosition(x - hoveredItemSprite.getWidth() * hoveredItemSprite.getHScale() / 2,
                  y - hoveredItemSprite.getHeight() * hoveredItemSprite.getVScale() / 2);
            }
            inventory.setItem(slot, null);
          }
          // System.out.println(hoveredItem);
        }
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (hoveredItem != null) {
          hoveredItemSprite.setPosition(x - hoveredItemSprite.getWidth() * hoveredItemSprite.getHScale() / 2,
              y - hoveredItemSprite.getHeight() * hoveredItemSprite.getVScale() / 2);
        }
      }
    });

  }

  public Player getPlayer() {
    return player;
  }

  public void setClient(GameStarter c) {
    client = c;
    player = new Player(client.getUsername(), client.getSkin());
    repaintTimer.start();

  }

  public void setMapLoaded(boolean isLoaded) {
    isMapLoaded = isLoaded;
  }

  private void drawInventoryHighlight(Graphics2D g2d) {
    if (lastClickedInventoryTile[0] == -1 || lastClickedInventoryTile[1] == -1)
      return;

    int tileSize = 32;
    int tileX = lastClickedInventoryTile[0];
    int tileY = lastClickedInventoryTile[1];

    int gridWidth = 11 * tileSize;
    int xOffset = (getWidth() - gridWidth) / 2;
    int yOffset = getHeight() - (8 * tileSize);

    g2d.translate(xOffset, yOffset);
    invHighlight.setPosition(tileX * tileSize, tileY * tileSize);
    invHighlight.draw(g2d);
    g2d.translate(-xOffset, -yOffset);
  }

  private ArrayList<double[]> findPath(Rectangle2D obj, double[] target, double speed) {

    double tileSize = tileGrids.get("ground").getTileSize();
    Rectangle2D targetTile = new Rectangle2D.Double(
        Math.floor(target[0] / tileSize) * tileSize,
        Math.floor(target[1] / tileSize) * tileSize,
        tileSize,
        tileSize);

    ArrayList<Rectangle2D> collidableObjects = new ArrayList<>();
    for (TileGrid grid : collidableGrids) {
      for (int i = 0; i < grid.getHeight(); i++) {
        for (int j = 0; j < grid.getWidth(); j++) {
          Rectangle2D hitbox = grid.getTileHitBoxAt(i, j);
          if (hitbox != null) {
            collidableObjects.add(hitbox);
          }
        }
      }
    }

    Deque<ArrayList<double[]>> output = new ArrayDeque<>();
    Set<double[]> visited = new HashSet<>();
    double[] currPosition = { obj.getCenterX(), obj.getCenterY() };
    ArrayList<double[]> initialPath = new ArrayList<>();
    initialPath.add(currPosition);
    output.add(initialPath);
    while (!output.isEmpty()) {
      ArrayList<double[]> currPath = output.removeFirst();
      double[] lastVisited = currPath.get(currPath.size() - 1);

      if (visited.contains(lastVisited)) {
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

      visited.add(lastVisited);

      double[] up = { speed, 0 };
      double[] right = { 0, speed };
      double[] down = { -speed, 0 };
      double[] left = { 0, -speed };
      double[][] directions = { up, right, left, down };

      for (int i = 0; i < 4; i++) {

        double[] nextPosition = lastVisited;
        nextPosition[0] += directions[i][0];
        nextPosition[1] += directions[i][1];
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

        currPath.add(nextPosition);
        output.add(currPath);
        currPath.remove(currPath.size() - 1);

      }
    }

    return null;
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    double halfViewWidth = (getWidth() / 2.0) / zoom;
    double halfViewHeight = (getHeight() / 2.0) / zoom;

    if (isMapLoaded) {

      if (!test) {
        System.out.println(findPath(player.getSpriteDimensions(), new double[] { 350, 250 }, player.getSpeed()));
        test = true;
      }

      anchorX = clamp(halfViewWidth,
          tileGrids.get("ground").getWidth() * tileGrids.get("ground").getTileSize() - halfViewWidth,
          player.getX() + player.getWidth() / 2);
      anchorY = clamp(halfViewHeight,
          tileGrids.get("ground").getHeight() * tileGrids.get("ground").getTileSize() - halfViewHeight,
          player.getY() + player.getHeight() / 2);

      AffineTransform camera = new AffineTransform();
      camera.translate(getWidth() / 2, getHeight() / 2);
      camera.scale(zoom, zoom);
      camera.translate(-anchorX, -anchorY);

      g2d.setTransform(camera);
      tileGrids.get("ground").draw(g2d);
      tileGrids.get("edge").draw(g2d);
      tileGrids.get("foliage").draw(g2d);
      tileGrids.get("farm").draw(g2d);

      for (Player other : otherPlayers.values()) {
        other.draw(g2d);
      }

      player.draw(g2d);
      if (!inventory.isOpen()) {
        highlight.setPosition(lastClickedTile[0] * 32, lastClickedTile[1] * 32);
        highlight.draw(g2d);
      }

      for (Animal an : animals.values()) {
        an.draw(g2d);
      }

      // draw hitboxes
      // for (int i = 0; i < tileGrids.get("edge").getHeight(); i++) {
      // for (int j = 0; j < tileGrids.get("edge").getWidth(); j++) {
      // Rectangle2D hitbox = tileGrids.get("edge").getTileHitBoxAt(i, j);
      // if (hitbox != null) {
      // g2d.draw(hitbox);
      // }
      // }
      // }

      // g2d.draw(player.getHitboxAt(player.getX(), player.getY()));

      g2d.setTransform(new AffineTransform());
      inventory.draw(g2d);
      // draw inventory highlight
      if (inventory.isOpen()) {
        drawInventoryHighlight(g2d);

        if (hoveredItem != null) {
          hoveredItemSprite.draw(g2d);
        }
      }

      // dialogue.draw(g2d);

    }
  }

  public class WriteToServer implements Runnable {
    private DataOutputStream out;

    public WriteToServer(DataOutputStream out) {
      this.out = out;
    }

    public void send(String msg) {
      synchronized (out) {
        try {
          out.writeUTF(msg);
          out.flush();
        } catch (IOException e) {
          System.out.println("[WriteToServer] Failed to send: " + msg);
        }
      }
    }

    @Override
    public void run() {

    }
  }

}
