import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

  public GameCanvas() {
    isMapLoaded = false;
    otherPlayers = new HashMap<>();
    tileGrids = new HashMap<>();
    animals = new HashMap<>();

    inventory = new Inventory();
    collidableGrids = new ArrayList<>();

    SpriteFiles selectorFiles = new SpriteFiles("assets/ui/selector");
    lastClickedTile = new int[2];
    lastClickedInventoryTile = new int[]{6, 1};
    highlight = new Sprite(selectorFiles.getFiles(), 32);
    invHighlight = new Sprite(selectorFiles.getFiles(), 32);
    invHighlight.setSprite(1);
    addKeyBindings();
    addMouseListener();
    repaintTimer = new Timer(1000 / 60, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.tick();
        for (Player ghost : otherPlayers.values()) {
          ghost.tick();
        }

        anchorX = player.getX() + player.getWidth() / 2;
        anchorY = player.getY() + player.getHeight() / 2;

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
      player.useAction(player.getActiveTool());
      writer.send("ACTION " + client.getPlayerID() + " " + player.getActiveTool().toUpperCase() + " " + x
          + " " + y + " "
          + player.getDirection());
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
          movePlayer();
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
    double x = x1 + x2;
    double y = y1 + y2;
    double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    double[] result = { x / magnitude, y / magnitude };
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

    double newX = player.getX() + currVector[0] * speed;
    double newY = player.getY() + currVector[1] * speed;

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
      public void mouseClicked(MouseEvent e) {
        doPlayerAction(lastClickedTile[0], lastClickedTile[1]);
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        double x = e.getX() - (400 - anchorX);
        double y = e.getY() - (300 - anchorY);

        int tileSize = 32;
        int tileX = (int) Math.ceil(x / tileSize);
        int tileY = (int) Math.ceil(y / tileSize);

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
    int xOffset = (800 - gridWidth) / 2;
    int yOffset = 600 - (8 * tileSize) - 40;

    g2d.translate(xOffset, yOffset);
    invHighlight.setPosition(tileX * tileSize, tileY * tileSize);
    invHighlight.draw(g2d);
    g2d.translate(-xOffset, -yOffset);
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    if (isMapLoaded) {

      AffineTransform camera = new AffineTransform();
      camera.translate(400 - anchorX, 300 - anchorY);
      g2d.setTransform(camera);
      tileGrids.get("ground").draw(g2d);
      tileGrids.get("edge").draw(g2d);
      tileGrids.get("foliage").draw(g2d);
      tileGrids.get("farm").draw(g2d);

      for (Player other : otherPlayers.values()) {
        other.draw(g2d);
      }
      player.draw(g2d);
      highlight.setPosition(lastClickedTile[0] * 32, lastClickedTile[1] * 32);
      highlight.draw(g2d);

      for (Animal an : animals.values()) {
        an.draw(g2d);
      }

      AffineTransform reset = new AffineTransform();
      g2d.setTransform(reset);
      inventory.draw(g2d);
      // draw inventory highlight
      drawInventoryHighlight(g2d);

      // dialogue.draw(g2d);

      // g2d.draw(player.getHitboxAt(player.getX(), player.getY()));

    }
  }

  private class WriteToServer implements Runnable {
    private DataOutputStream out;

    public WriteToServer(DataOutputStream out) {
      this.out = out;
    }

    public void send(String msg) {
      try {
        out.writeUTF(msg);
        out.flush();
      } catch (IOException e) {
        System.out.println("[WriteToServer] Failed to send: " + msg);
      }
    }

    @Override
    public void run() {

    }
  }

}
