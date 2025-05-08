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
import java.util.function.Consumer;
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
  private Map<Integer, DroppedItem> droppedItems;

  private GameStarter client;
  private WriteToServer writer;
  private int[] lastClickedTile;
  private int[] lastClickedInventoryTile;
  private int[] lastClickedCraftingTile;
  private int[] lastClickedShopTile;
  private int[] lastClickedSkillTreeTile;

  private boolean isMapLoaded;
  private double anchorX, anchorY;
  private Inventory inventory;
  private double zoom;
  private Sprite hoveredItemSprite;
  private Item hoveredItem;
  private int previousItemSlot;
  private Boolean test;
  private boolean isCtrlPressed;
  private ArrayList<Rectangle2D> currentPath;

  private GoldCounter goldCounter;

  private CraftingGrid craftingGrid;
  private CraftingGrid shopGrid;
  private SkillTreeGrid skillTree;

  private CraftingSystem craftingSystem;
  private EconomySystem economySystem;
  private ShopSystem shopSystem;
  private SkillTreeSystem skillTreeSystem;

  private ArrayList<Recipe> recipes;
  private ArrayList<Recipe> trades;

  public GameCanvas() {
    isMapLoaded = false;
    otherPlayers = new HashMap<>();
    tileGrids = new HashMap<>();
    animals = new HashMap<>();
    droppedItems = new HashMap<>();
    inventory = new Inventory(this);

    collidableGrids = new ArrayList<>();

    recipes = new ArrayList<>();
    trades = new ArrayList<>();

    recipes.add(new Recipe(new Item(2, 1), new Item(3, 2)));
    recipes.add(new Recipe(new Item(4, 1), new Item(5, 2)));
    recipes.add(new Recipe(new Item(6, 1), new Item(7, 2)));
    recipes.add(new Recipe(new Item(8, 1), new Item(9, 2)));
    recipes.add(new Recipe(new Item(10, 1), new Item(11, 2)));
    recipes.add(new Recipe(new Item(12, 1), new Item(13, 2)));

    trades.add(new Recipe(new Item(2, 1), new Item(14, 4)));
    trades.add(new Recipe(new Item(4, 1), new Item(14, 8)));
    trades.add(new Recipe(new Item(6, 1), new Item(14, 10)));
    trades.add(new Recipe(new Item(8, 1), new Item(14, 15)));
    trades.add(new Recipe(new Item(10, 1), new Item(14, 25)));
    trades.add(new Recipe(new Item(12, 1), new Item(14, 35)));

    trades.add(new Recipe(new Item(14, 50), new Item(5, 1)));
    trades.add(new Recipe(new Item(14, 100), new Item(7, 1)));
    trades.add(new Recipe(new Item(14, 200), new Item(9, 1)));
    trades.add(new Recipe(new Item(14, 400), new Item(11, 1)));
    trades.add(new Recipe(new Item(14, 800), new Item(13, 1)));
    economySystem = new EconomySystem(this);

    ArrayList<Skill> skills = new ArrayList<>(); // NOTE: add skills bottom-up

    Skill one = new Skill("one", null, 1, 1, 1, 1, 0);
    one.unlock();
    one.upgrade();
    Skill two = new Skill("two", one, 1, 1, 1, 1, 1);
    two.unlock();
    Skill three = new Skill("three", one, 1, 1, 1, 1, 2);
    Skill four = new Skill("four", two, 1, 1, 1, 1, 3);
    Skill five = new Skill("five", two, 1, 1, 1, 1, 4);
    Skill six = new Skill("six", three, 1, 1, 1, 1, 5);
    Skill seven = new Skill("seven", three, 1, 1, 1, 1, 6);

    skills.add(one);
    skills.add(two);
    skills.add(three);
    skills.add(four);
    skills.add(five);
    skills.add(six);
    skills.add(seven);

    skillTreeSystem = new SkillTreeSystem(skills, economySystem); // TODO: make skills
    skillTree = new SkillTreeGrid(skillTreeSystem, this);

    craftingGrid = new CraftingGrid(recipes, this, false);
    shopGrid = new CraftingGrid(trades, this, true);
    craftingSystem = new CraftingSystem(recipes, inventory);
    shopSystem = new ShopSystem(trades, inventory, economySystem);

    goldCounter = new GoldCounter(this);

    zoom = 2;
    previousItemSlot = -1;
    test = false;
    currentPath = new ArrayList<>();
    SpriteFiles selectorFiles = new SpriteFiles("assets/ui/selector");
    SpriteFiles itemFiles = new SpriteFiles("assets/items");
    hoveredItemSprite = new Sprite(itemFiles.getFiles(), 32);

    lastClickedTile = new int[2];
    lastClickedInventoryTile = new int[] { 6, 1 };
    lastClickedCraftingTile = new int[2];
    lastClickedShopTile = new int[2];

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
          pickUpCollidingItems();
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
              + " " + animal.getDirection() + " "
              + animal.getAnimationState());

        }
        repaint();
      }
    });

  }

  public GameStarter getClient() {
    return client;
  }

  public void setServerOut(DataOutputStream out) {
    writer = new WriteToServer(out);
  }

  public void setTileGrid(String name, TileGrid grid) {
    tileGrids.put(name, grid);
  }

  public void initializeWorld() {
    collidableGrids.add(tileGrids.get("tree"));
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

  public void syncInventoryBulk() {
    String playerId = client.getPlayerID();

    try {
      StringBuilder sb = new StringBuilder("INVENTORY BULK " + playerId);
      for (int i = 0; i < inventory.getInventory().length; i++) {
        Item item = inventory.getItem(i);
        int itemId = (item != null) ? item.getId() : -1;
        int quantity = (item != null) ? item.getQuantity() : 0;
        sb.append(" ").append(i).append(",").append(itemId).append(",").append(quantity);
      }
      writer.send(sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void pickUpCollidingItems() {
    Rectangle2D playerHitbox = player.getHitboxAt(player.getX(), player.getY());

    ArrayList<Integer> itemsToPickup = new ArrayList<>();

    for (DroppedItem item : new ArrayList<>(droppedItems.values())) {
      Rectangle2D itemHitbox = new Rectangle2D.Double(item.getX(), item.getY(), item.getSpriteSize(),
          item.getSpriteSize());
      if (playerHitbox.intersects(itemHitbox)) {
        itemsToPickup.add(item.getDroppedItemId());
      }
    }

    for (Integer itemId : itemsToPickup) {
      pickupDroppedItem(itemId);
    }
  }

  public void dropActiveItem() {
    Item item = inventory.getActiveItem();
    if (item == null)
      return;
    int q = 1;
    if (isCtrlPressed) {
      q = item.getQuantity();
    }

    if (item.getQuantity() - q >= 0) {
      item.setQuantity(item.getQuantity() - q);
    }

    dropItem(item, q);

  }

  public void dropItem(Item item, int q) {
    String playerDirection = player.getDirection();
    double droppedItemX = player.getX() + (player.getWidth() / 2);
    double droppedItemY = player.getY() + (player.getHeight() / 2);

    switch (playerDirection) {
      case "UP":
        droppedItemY -= 40;
        droppedItemX -= 10;
        break;
      case "DOWN":
        droppedItemY += 25;
        droppedItemX -= 10;
        break;
      case "LEFT":
        droppedItemX -= 40;
        break;
      case "RIGHT":
        droppedItemX += 16;
        break;
    }

    for (DroppedItem droppedItem : droppedItems.values()) {
      if (Math.sqrt(Math.pow((player.getX() - droppedItem.getX()), 2)
          + Math.pow(player.getY() - droppedItem.getY(), 2)) < 2 * 32) {
        if (droppedItem.getItemId() == item.getId()) {

          droppedItem.setPosition(droppedItemX, droppedItemY);
          droppedItem.setQuantity(droppedItem.getQuantity() + q);

          writer.send(String.format("ITEMDROP EDIT %f %f %d %d %d", droppedItemX, droppedItemY, item.getId(),
              droppedItem.getQuantity(), droppedItem.getDroppedItemId()));
          return;
        }
      }
    }
    writer.send(String.format("ITEMDROP CREATE %f %f %d %d", droppedItemX, droppedItemY, item.getId(), q));
  }

  private void pickupDroppedItem(int droppedItemId) {
    DroppedItem item = droppedItems.get(droppedItemId);
    if (inventory.getEmptySlot() == -1) {
      return;
    }
    if (item != null) {
      droppedItems.remove(droppedItemId);
      writer.send(String.format("ITEMDROP PICKUP %s %d", client.getPlayerID(), droppedItemId));
    }
  }

  public Map<Integer, DroppedItem> getDroppedItems() {
    return droppedItems;
  }

  private void doPlayerAction(int x, int y) {
    if (!player.isDoingAction()) {
      if (inventory.getActiveItem() != null) {
        Item activeItem = inventory.getActiveItem();
        boolean itemUsed = false;
        for (String action : player.getPlayerActions().keySet()) {
          if (activeItem.getActionName() == null)
            return;
          String[] actionString = activeItem.getActionName().split(" ");
          if (actionString[0].equals(action)) {

            player.useAction(actionString[0]);
            writer.send("ACTION " + client.getPlayerID() + " "
                + actionString[0].toUpperCase() + " " + x
                + " " + y + " "
                + player.getDirection());
            if (actionString[0].equals("hoe")) {
              if (tileGrids.get("farm").getTileAt(y, x) != -1) {
                writer.send("FARM HARVEST " + x + " " + y + " " + client.getPlayerID());
              }
            }

            if (actionString[0].equals("plant")) {
              if (tileGrids.get("ground").getTileAt(y, x) == 354) {
                if (tileGrids.get("farm").getTileAt(y, x) == -1) {
                  System.out.println("FARM PLANT "
                      + actionString[1] + " " + x
                      + " " + y);
                  writer.send("FARM PLANT " + actionString[1]
                      + " " + x + " " + y);
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
          int slot = Character.getNumericValue(c) - 1;
          if (slot >= 0 && slot <= 9) {
            inventory.setActiveHotbarSlot(Character.getNumericValue(c) - 1);
          }
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
              if (hoveredItem != null) {
                dropItem(hoveredItem, hoveredItem.getQuantity());
                hoveredItem = null;
                previousItemSlot = -1;
              }
            }
            inventory.setOpen(!inventory.isOpen());
            break;
          case KeyEvent.VK_C:
            if (inventory.isOpen() && previousItemSlot != -1) {
              if (hoveredItem != null) {
                dropItem(hoveredItem, hoveredItem.getQuantity());
                hoveredItem = null;
                previousItemSlot = -1;
              }
            }
            skillTree.setOpen(!skillTree.isOpen());
          case KeyEvent.VK_CONTROL:
            isCtrlPressed = true;
            break;
          case KeyEvent.VK_Q:
            if (!inventory.isOpen()) {
              dropActiveItem();
            } else {
              int slot = inventory.getSlotFromGrid(lastClickedInventoryTile[0], lastClickedInventoryTile[1]);
              Item item = inventory.getItem(slot);

              if (slot != -1) {
                if (item != null) {
                  int q = 1;
                  if (isCtrlPressed) {
                    q = item.getQuantity();
                  }
                  dropItem(item, q);
                  inventory.removeItem(item.getId(), q);
                }
              }
            }
            syncInventoryBulk();

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
          case KeyEvent.VK_CONTROL:
            isCtrlPressed = false;
            break;
        }

        if (direction != null) {
          player.setDirectionStatus(direction, false);
        }

        Boolean isMoving = false;
        for (Boolean isActive : player.getActiveDirections().values()) {
          isMoving = isMoving || isActive;
        }

        String idleState = isPlayerInWater() ? "swim_idle" : "idle";

        if (!player.isDoingAction() && !isMoving) {
          player.setAnimationState(idleState);
          writer.send("MOVE " + client.getPlayerID() + " " + player.getX() + " "
              + player.getY() + " "
              + player.getDirection() + " " + idleState);

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
    boolean isInWater = isPlayerInWater();

    double speed = player.getSpeed();
    if (isInWater) {
      player.setSpeed(2);
    } else {
      player.setSpeed(3);
    }

    Map<String, Boolean> activeDirections = player.getActiveDirections();

    String direction = "DOWN";
    double[] currVector = { 0, 0 };

    String walkState = isInWater ? "swim" : "walk";

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
        tileGrids.get("ground").getWidth() * tileGrids.get("ground").getTileSize()
            - player.getWidth() * 22 / 32,
        player.getX() + currVector[0] * speed);
    double newY = clamp(-player.getHeight() * 6 / 32,
        tileGrids.get("ground").getHeight() * tileGrids.get("ground").getTileSize()
            - player.getHeight() * 26 / 32,
        player.getY() + currVector[1] * speed);

    Rectangle2D futureHitbox = player.getHitboxAt(newX, newY);

    if (!isColliding(futureHitbox)) {
      writer.send("MOVE " + client.getPlayerID() + " " + newX + " " + newY + " " + direction + " "
          + walkState);
      player.setDirection(direction);
      player.setAnimationState(walkState);

      player.setPosition(newX, newY);
    }

  }

  public void updateTileGrid(String name, int x, int y, int val) {
    tileGrids.get(name).setTileAt(y, x, val);
  }

  public boolean isPlayerInWater() {
    TileGrid cg = tileGrids.get("ground");
    for (int i = 0; i < cg.getHeight(); i++) {
      for (int j = 0; j < cg.getWidth(); j++) {
        if (cg.getTileAt(i, j) == 153) {
          Rectangle2D collidableHitbox = cg.getTileHitBoxAt(i, j);
          if (collidableHitbox != null) {
            if (player.getHitboxAt(player.getX(), player.getY()).intersects(collidableHitbox)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public void actionPlayer(String id, int x, int y, String dir, String action) {
    Player newPlayer = otherPlayers.get(id);
    switch (action) {
      case "HOE" -> newPlayer.useAction("hoe");
      case "WATER" -> newPlayer.useAction("water");
      case "PLANT" -> newPlayer.useAction("plant");
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

  public void addDroppedItem(double x, double y, int itemId, int quantity, int droppedItemId) {
    DroppedItem droppedItem = new DroppedItem(x, y, itemId, quantity, droppedItemId);
    droppedItems.put(droppedItemId, droppedItem);
  }

  public void updateDroppedItem(double x, double y, int itemId, int quantity, int droppedItemId) {
    DroppedItem droppedItem = droppedItems.get(droppedItemId);
    droppedItem.setPosition(x, y);
    droppedItem.setQuantity(quantity);

    droppedItems.put(droppedItemId, droppedItem);
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
        System.out.printf("%d\n", tileGrids.get("edge").getTileAt(tileY, tileX));
        lastClickedTile[0] = (int) clamp(0, tileGrids.get("ground").getWidth() - 1, tileX);
        lastClickedTile[1] = (int) clamp(0, tileGrids.get("ground").getHeight() - 1, tileY);

        if (!inventory.isOpen()) {
          doPlayerAction(lastClickedTile[0], lastClickedTile[1]);
        }
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
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
        int[] craftingTile = craftingGrid.getTileAtMouse(x, y);
        int[] shopTile = shopGrid.getTileAtMouse(x, y);

        lastClickedCraftingTile[0] = craftingTile[0];
        lastClickedCraftingTile[1] = craftingTile[1];

        lastClickedShopTile[0] = shopTile[0];
        lastClickedShopTile[1] = shopTile[1];

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

          handleCraftingGrid(lastClickedCraftingTile[0], lastClickedCraftingTile[1]);
          handleShopGrid(lastClickedShopTile[0], lastClickedShopTile[1]);

          int tileX = lastClickedInventoryTile[0];
          int tileY = lastClickedInventoryTile[1];

          if (tileX == -1) {
            if (hoveredItem != null) {
              dropItem(hoveredItem, hoveredItem.getQuantity());
              hoveredItem = null;
            }
            return;
          }

          int slot = inventory.getSlotFromGrid(tileX, tileY);

          if (hoveredItem != null) {
            Item temp = inventory.getItem(slot) != null ? inventory.getItem(slot)
                : null;
            inventory.setItem(slot, hoveredItem);
            hoveredItem = temp;
            previousItemSlot = inventory.getEmptySlot();
            if (hoveredItem != null) {
              hoveredItemSprite.setSprite(hoveredItem.getId());
              hoveredItemSprite.setPosition(x - hoveredItemSprite.getWidth()
                  * hoveredItemSprite.getHScale() / 2,
                  y - hoveredItemSprite.getHeight()
                      * hoveredItemSprite.getVScale()
                      / 2);
            }
          } else {
            hoveredItem = inventory.getItem(slot);
            if (hoveredItem != null) {
              previousItemSlot = slot;
              hoveredItemSprite.setSprite(hoveredItem.getId());
              hoveredItemSprite.setPosition(x - hoveredItemSprite.getWidth()
                  * hoveredItemSprite.getHScale() / 2,
                  y - hoveredItemSprite.getHeight()
                      * hoveredItemSprite.getVScale()
                      / 2);
            }
            inventory.setItem(slot, null);
          }
        }
        syncInventoryBulk();

      }

    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (hoveredItem != null) {
          hoveredItemSprite.setPosition(
              x - hoveredItemSprite.getWidth() * hoveredItemSprite.getHScale()
                  / 2,
              y - hoveredItemSprite.getHeight()
                  * hoveredItemSprite.getVScale() / 2);
        }
      }
    });

  }

  public void handleCraftingGrid(int tileX, int tileY) {
    if (tileY == 7) {
      if (tileX == 1) {
        craftingGrid.backwardPage();
        return;
      } else if (tileX == 3) {
        craftingGrid.forwardPage();
        return;
      }
    }

    if (tileY >= 2 && tileY <= 5) {
      if (tileX == 3) {
        int recipeRow = tileY - 2;
        int recipeIndex = craftingGrid.getCurrentPage() * 4 + recipeRow;
        if (recipeIndex >= 0 && recipeIndex < recipes.size()) {
          craftingSystem.craft(recipeIndex);
        }
      }
    }
  }

  public WriteToServer getWriter() {
    return writer;
  }

  public void handleShopGrid(int tileX, int tileY) {
    if (tileY == 7) {
      if (tileX == 1) {
        shopGrid.backwardPage();
        return;
      } else if (tileX == 3) {
        shopGrid.forwardPage();
        return;
      }
    }

    if (tileY >= 2 && tileY <= 5) {
      if (tileX == 3) {
        int tradeRow = tileY - 2;
        int tradeIndex = shopGrid.getCurrentPage() * 4 + tradeRow;
        if (tradeIndex >= 0 && tradeIndex < trades.size()) {
          shopSystem.trade(tradeIndex);
        }
      }
    }
  }

  public Inventory getInventory() {
    return inventory;
  }

  public Player getPlayer() {
    return player;
  }

  public ArrayList<Recipe> getRecipes() {
    return recipes;
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

    int tileSize = getWidth() * 32 / 800;
    invHighlight.setSize(tileSize);
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
    Set<Position> visited = new HashSet<>();
    double[] currPosition = { obj.getCenterX(), obj.getCenterY() };
    ArrayList<double[]> initialPath = new ArrayList<>();
    initialPath.add(currPosition);
    output.add(initialPath);

    while (!output.isEmpty()) {
      ArrayList<double[]> currPath = output.removeFirst();
      double[] lastVisited = currPath.get(currPath.size() - 1);

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

        ArrayList<double[]> newPath = new ArrayList<>();
        newPath.addAll(currPath);
        newPath.add(nextPosition);
        output.add(newPath);

      }
    }

    return null;
  }

  public void findPathAsync(Rectangle2D obj, double[] target, double speed,
      Consumer<ArrayList<double[]>> onResult) {
    new Thread(() -> {
      ArrayList<double[]> path = findPath(obj, target, speed);
      onResult.accept(path);
    }).start();
  }

  public EconomySystem getEconomySystem() {
    return economySystem;
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    double halfViewWidth = (getWidth() / 2.0) / zoom;
    double halfViewHeight = (getHeight() / 2.0) / zoom;

    if (isMapLoaded) {

      System.out.println(skillTree.isOpen());

      if (!test) {
        findPathAsync(player.getSpriteDimensions(), new double[] { 0, 0 },
            player.getSpeed(), path -> {
              for (double[] pos : path) {
                Rectangle2D temp = new Rectangle2D.Double(
                    pos[0] - 1,
                    pos[1] - 1,
                    2,
                    2);
                currentPath.add(temp);
              }
            });
        test = true;
      }

      anchorX = clamp(halfViewWidth,
          tileGrids.get("ground").getWidth() * tileGrids.get("ground").getTileSize()
              - halfViewWidth,
          player.getX() + player.getWidth() / 2);
      anchorY = clamp(halfViewHeight,
          tileGrids.get("ground").getHeight() * tileGrids.get("ground").getTileSize()
              - halfViewHeight,
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
      tileGrids.get("tree").draw(g2d);

      for (Rectangle2D rect : currentPath) {
        g2d.draw(rect);
      }

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

      for (DroppedItem item : droppedItems.values()) {
        item.draw(g2d);
      }

      // draw hitboxes
      // for (int i = 0; i < tileGrids.get("edge").getHeight(); i++) {
      // for (int j = 0; j < tileGrids.get("edge").getWidth(); j++) {
      // for (TileGrid collidable : collidableGrids) {
      // Rectangle2D hitbox = collidable.getTileHitBoxAt(i, j);
      // if (hitbox != null) {
      // g2d.draw(hitbox);
      // }
      // }
      // }
      // }

      // g2d.draw(player.getHitboxAt(player.getX(), player.getY()));

      g2d.setTransform(new AffineTransform());
      inventory.draw(g2d);
      craftingGrid.draw(g2d);
      shopGrid.draw(g2d);
      goldCounter.draw(g2d);
      skillTree.draw(g2d);
      // draw inventory highlight
      if (inventory.isOpen()) {
        drawInventoryHighlight(g2d);

        if (hoveredItem != null) {
          String quantityString = Integer.toString(hoveredItem.getQuantity());

          g2d.setColor(Color.BLACK);
          g2d.setFont(new Font("Arial", 1, (int) (25 * getWidth() / 800)));
          FontMetrics fm = g2d.getFontMetrics();
          int stringWidth = fm.stringWidth(quantityString);

          hoveredItemSprite.setSize(32 * getWidth() / 800);
          hoveredItemSprite.draw(g2d);

          float quantityLabelX = (float) (hoveredItemSprite.getX() + 32 * getWidth() / 800 - stringWidth);
          float quantityLabelY = (float) (hoveredItemSprite.getY() + 32 * getWidth() / 800);

          g2d.setColor(Color.BLACK);
          g2d.drawString(quantityString, quantityLabelX + 1, quantityLabelY - 1);
          g2d.drawString(quantityString, quantityLabelX + 1, quantityLabelY + 1);
          g2d.drawString(quantityString, quantityLabelX - 1, quantityLabelY - 1);
          g2d.drawString(quantityString, quantityLabelX - 1, quantityLabelY + 1);
          g2d.setColor(Color.WHITE);

          g2d.drawString(quantityString, quantityLabelX, quantityLabelY);
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
        }
      }
    }

    @Override
    public void run() {

    }
  }

}
