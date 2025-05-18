import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
  private Map<String, Sound> sounds;

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
  private int mouseX, mouseY;

  private GoldCounter goldCounter;

  private CraftingGrid craftingGrid;
  private CraftingGrid shopGrid;
  private SkillTreeGrid skillTree;

  private CraftingSystem craftingSystem;
  private EconomySystem economySystem;
  private ShopSystem shopSystem;
  private SkillTreeSystem skillTreeSystem;
  private ChatSystem chatSystem;
  private FarmingSystem farmSystem;
  private LevelingSystem levelingSystem;

  private String inputText = "";

  private ArrayList<Recipe> recipes;
  private ArrayList<Recipe> trades;
  private ArrayList<Recipe> baseTrades;
  private ArrayList<Skill> skills;
  private ArrayList<Skill> baseSkills;

  private ArrayList<Sound> bgm;

  private HoverInfo hoverInfo;

  private GameAudio gameAudio;

  private double greenThumbChance;

  public GameCanvas() {
    isMapLoaded = false;
    otherPlayers = new HashMap<>();
    tileGrids = new HashMap<>();
    animals = new HashMap<>();
    droppedItems = new HashMap<>();
    inventory = new Inventory(this);
    collidableGrids = new ArrayList<>();
    chatSystem = new ChatSystem();
    farmSystem = new FarmingSystem(0);
    recipes = new ArrayList<>();
    trades = new ArrayList<>();
    sounds = new HashMap<>();
    bgm = new ArrayList<>();

    setupSounds();
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

    baseTrades = new ArrayList<>(trades);
    economySystem = new EconomySystem(this);

    skills = new ArrayList<>(); // NOTE: add skills level-order

    Skill one = new Skill("Fruit of Knowledge", 1, 1, 1, 10, 0, "test");
    Skill two = new Skill("Lightfooted", 1, 1, 1, 10, 1, "test");
    Skill three = new Skill("Merchant's Rizz", 1, 1, 1, 10, 2, "test");
    Skill four = new Skill("Cheap Tricks", 1, 1, 1, 10, 3, "test");
    Skill five = new Skill("Nature's Grasp", 1, 1, 1, 10, 4, "test");
    Skill six = new Skill("Green Thumb", 1, 1, 1, 5, 5, "test");
    Skill seven = new Skill("Seal of the Serpent", 1, 1, 1, 5, 6, "test");

    skills.add(one);
    skills.add(two);
    skills.add(three);
    skills.add(four);
    skills.add(five);
    skills.add(six);
    skills.add(seven);
    baseSkills = new ArrayList<>(skills);
    skillTreeSystem = new SkillTreeSystem(skills, economySystem); // TODO: make skills
    skillTree = new SkillTreeGrid(skillTreeSystem, this);

    craftingGrid = new CraftingGrid(recipes, this, false);
    shopGrid = new CraftingGrid(trades, this, true);
    craftingSystem = new CraftingSystem(recipes, inventory);
    shopSystem = new ShopSystem(trades, inventory, economySystem);
    levelingSystem = new LevelingSystem(economySystem, this);

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
    lastClickedSkillTreeTile = new int[2];

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
    chatSystem.setUsername(client.getPlayerID());
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
        playLocalSound("item_pickup");
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
    playLocalSound("item_drop");

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
              int tile = tileGrids.get("farm").getTileAt(y, x);
              if (tile != -1) {
                if (tile % 6 == 5) {
                  levelingSystem.addXP(farmSystem.getXPFromPlant(farmSystem.getPlantNameFromIndex(tile)));
                  playLocalSound("foliage_break");
                }
                writer.send("FARM HARVEST " + x + " " + y + " " + client.getPlayerID());
              }

              if (tileGrids.get("foliage").getTileAt(y, x) != -1) {
                levelingSystem.addXP(3);
                playLocalSound("foliage_break");
              } else {
                playLocalSound("hoe_till");
              }
            } else if (actionString[0].equals("water")) {
              playLocalSound("water_can");
            }

            boolean planted = false;
            if (actionString[0].equals("plant")) {
              if (tileGrids.get("ground").getTileAt(y, x) == 354) {
                if (tileGrids.get("farm").getTileAt(y, x) == -1) {
                  System.out.println("FARM PLANT "
                      + actionString[1] + " " + x
                      + " " + y);
                  writer.send("FARM PLANT " + actionString[1]
                      + " " + x + " " + y);
                  itemUsed = true;
                  planted = true;
                  playLocalSound("plant");
                }
              }
            }
            if (itemUsed) {
              if (planted) {
                int random = (int) (Math.random() * 100);
                if (random > 100 - greenThumbChance) {
                  return;
                }
              }
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
            if (chatSystem.isChatOpen())
              return;

            if (skillTree.isOpen()) {
              skillTree.setOpen(false);
            }

            if (inventory.isOpen() && previousItemSlot != -1) {
              if (hoveredItem != null) {
                dropItem(hoveredItem, hoveredItem.getQuantity());
                hoveredItem = null;
                previousItemSlot = -1;
              }
            }
            if (!inventory.isOpen()) {
              playLocalSound("inventory_open");
            }
            inventory.setOpen(!inventory.isOpen());
            break;
          case KeyEvent.VK_C:
            if (chatSystem.isChatOpen())
              return;

            if (inventory.isOpen() && previousItemSlot != -1) {
              if (hoveredItem != null) {
                dropItem(hoveredItem, hoveredItem.getQuantity());
                hoveredItem = null;
                previousItemSlot = -1;
              }
            }
            if (inventory.isOpen()) {
              inventory.setOpen(false);
            }

            skillTree.setOpen(!skillTree.isOpen());
          case KeyEvent.VK_CONTROL:
            isCtrlPressed = true;
            break;
          case KeyEvent.VK_T:
            if (!chatSystem.isChatOpen()) {
              chatSystem.setChatOpen(!chatSystem.isChatOpen());
              chatSystem.setCurrentInput("");
            }
            break;
          case KeyEvent.VK_ESCAPE:
            if (chatSystem.isChatOpen()) {
              chatSystem.setChatOpen(false);
              chatSystem.setCurrentInput("");
            }
            if (inventory.isOpen() && previousItemSlot != -1) {
              if (hoveredItem != null) {
                dropItem(hoveredItem, hoveredItem.getQuantity());
                hoveredItem = null;
                previousItemSlot = -1;
              }
            }
            if (inventory.isOpen()) {
              inventory.setOpen(false);
            }
            if (skillTree.isOpen()) {
              skillTree.setOpen(false);
            }
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

        if (direction != null && !chatSystem.isChatOpen()) {
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
    this.addKeyListener(new KeyAdapter() {

      @Override
      public void keyTyped(KeyEvent e) {

        if (chatSystem.isChatOpen()) {

          char c = e.getKeyChar();

          if (c == '\n') {
            if (!inputText.isBlank()) {
              if (inputText.startsWith("/")) {
                String command = inputText.substring(1);
                String[] parts = command.split(" ");
                String action = parts[0].toUpperCase();
                switch (action) {
                  case "SEND":
                    if (parts.length == 3) {
                      String playerId = parts[1];
                      int amount = Integer.parseInt(parts[2]);
                      if (playerExists(playerId) || playerId.equals(client.getPlayerID())) {
                        if (economySystem.getBalance() - amount >= 0) {
                          writer.send(String.format("ECONOMY ADD %s %d", playerId, amount));
                          economySystem.setBalance(economySystem.getBalance() - amount);
                          chatSystem.addMessage(String.format("[GAME] Sent %d coins to %s", amount, playerId));
                        } else {
                          chatSystem.addMessage("You do not have the coins to do that.");
                        }
                      } else {
                        chatSystem.addMessage(String
                            .format("[GAME] Player %s does not exist, or you are sending to yourself.", playerId));
                      }

                    } else {
                      chatSystem.addMessage("[GAME] Missing arguments.");
                    }
                    break;
                  default:
                    chatSystem.addMessage("[GAME] Invalid Command.");
                }
                System.out.println(action);
              } else {
                writer.send(String.format("CHAT (%s)", chatSystem.getMessageContent()));

                chatSystem.addMessage(chatSystem.getMessageContent());

              }
            }
            chatSystem.setChatOpen(!chatSystem.isChatOpen());
            inputText = "";
            chatSystem.setCurrentInput("");

            // control message limit
          } else if (!Character.isISOControl(c) && chatSystem.getMessageContent().length() <= 400) {
            inputText += c;
            chatSystem.setCurrentInput(inputText);
          }
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && inputText.length() > 0) {
          inputText = inputText.substring(0, inputText.length() - 1);
          chatSystem.setCurrentInput(inputText);
          repaint();
        }
      }
    });

  }

  private boolean playerExists(String playerId) {
    for (Player player : otherPlayers.values()) {
      if (player.getId().equals(playerId)) {
        return true;
      }
    }
    return false;
  }

  private double[] addVector(double x1, double y1, double x2, double y2) {
    double x = x1 + x2;
    double y = y1 + y2;
    double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    double[] result = { x / magnitude, y / magnitude };
    return result;
  }

  public ChatSystem getChatSystem() {
    return chatSystem;
  }

  public void movePlayer() {
    if (player.isDoingAction() || chatSystem.isChatOpen())
      return;
    boolean isInWater = isPlayerInWater();

    double speed = player.getBaseSpeed();
    if (isInWater) {
      player.setSpeed(player.getBaseSpeed());
    } else {
      player.setSpeed(player.getBaseSpeed() * 1.5);
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
    Player newPlayer = new Player(id, skin);
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
        lastClickedTile[0] = (int) clamp(0, tileGrids.get("ground").getWidth() - 1, tileX);
        lastClickedTile[1] = (int) clamp(0, tileGrids.get("ground").getHeight() - 1, tileY);


        int playerTileX = (int) Math.floor((player.getX() + player.getWidth() / 2) / tileSize);
        int playerTileY = (int) Math.floor((player.getY() + player.getHeight() / 2) / tileSize);

        int distance = (int) Math.sqrt(Math.pow(playerTileX - tileX, 2) + Math.pow(playerTileY - tileY, 2));

        if (distance < player.getReach()) {
          highlight.setSprite(0);
        } else {
          highlight.setSprite(2);
        }

        if (!inventory.isOpen() && !skillTree.isOpen() && distance < player.getReach()) {
          doPlayerAction(lastClickedTile[0], lastClickedTile[1]);
        }
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        double x = (e.getX() - (getWidth() / 2)) / zoom + anchorX;
        double y = (e.getY() - (getHeight() / 2)) / zoom + anchorY;

        double tileSize = 32;
        int tileX = (int) Math.floor(x / tileSize);
        int tileY = (int) Math.floor(y / tileSize);

        int playerTileX = (int) Math.floor((player.getX() + player.getWidth() / 2) / tileSize);
        int playerTileY = (int) Math.floor((player.getY() + player.getHeight() / 2) / tileSize);

        int distance = (int) Math.sqrt(Math.pow(playerTileX - tileX, 2) + Math.pow(playerTileY - tileY, 2));

        if (distance < player.getReach()) {
          highlight.setSprite(0);
        } else {
          highlight.setSprite(2);
        }

        if (isMapLoaded) {
          lastClickedTile[0] = (int) clamp(0, tileGrids.get("ground").getWidth() - 1, tileX);
          lastClickedTile[1] = (int) clamp(0, tileGrids.get("ground").getHeight() - 1, tileY);
        }
      }
    });

    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        lastClickedCraftingTile = craftingGrid.getTileAtMouse(x, y);
        lastClickedShopTile = shopGrid.getTileAtMouse(x, y);
        lastClickedSkillTreeTile = skillTree.getTileAtMouse(x, y);

        drawGridInfo(mouseX, mouseY);

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

        if (skillTree.isOpen()) {
          handleSkillTreeGrid(lastClickedSkillTreeTile[0], lastClickedSkillTreeTile[1]);
        }

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
          if (craftingSystem.craft(recipeIndex)) {
            playLocalSound("item_pickup");
          }
        }
      }
    }
  }

  private void drawGridInfo(int mouseX, int mouseY) {
    if (!inventory.isOpen())
      return;
    int tileX, tileY;

    if (skillTree.isOpen()) {
      tileX = lastClickedSkillTreeTile[0];
      tileY = lastClickedSkillTreeTile[1];
      Skill skill = skillTree.getSkillAtTile(tileX, tileY);
      if (skill != null) {
        ArrayList<TextLine> lines = new ArrayList<>();
      }
    }

    if (inventory.isOpen()) {
      CraftingGrid grid;
      ArrayList<Recipe> infos;

      for (int i = 0; i < 2; i++) {
        if (i == 0) {
          tileX = lastClickedCraftingTile[0];
          tileY = lastClickedCraftingTile[1];
          grid = craftingGrid;
          infos = recipes;
        } else {
          tileX = lastClickedShopTile[0];
          tileY = lastClickedShopTile[1];
          grid = shopGrid;
          infos = trades;
        }
        if (tileY >= 2 && tileY <= 5) {
          if (tileX == 3) {
            int recipeRow = tileY - 2;
            int recipeIndex = grid.getCurrentPage() * 4 + recipeRow;
            if (recipeIndex >= 0 && recipeIndex < infos.size()) {
              Recipe hoveredRecipe = infos.get(recipeIndex);
              if (hoveredRecipe != null) {
                ArrayList<TextLine> lines = new ArrayList<>();
                lines.add(
                    new TextLine(String.format("%s -> %s", hoveredRecipe.getItemIn().getName(),
                        hoveredRecipe.getItemOut().getName()), Color.WHITE));
                hoverInfo = new HoverInfo(lines, mouseX, mouseY);
                System.out.println(String.format("%s -> %s", hoveredRecipe.getItemIn().getName(),
                    hoveredRecipe.getItemOut().getName()));
                return;
              }
            }
          }
        }
      }
      hoverInfo = null;
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
          if (shopSystem.trade(tradeIndex)) {
            playLocalSound("purchase");
          }
        }
      }
    }
  }

  public void handleSkillTreeGrid(int tileX, int tileY) {
    Skill skill = skillTree.getSkillAtTile(tileX, tileY);
    if (skill != null) {
      if (skill.isUnlocked()) {
        skillTreeSystem.upgradeSkill(skill);
      } else {
        skillTreeSystem.unlockSkill(skill);
      }
      System.out.println(skill.getUnlockCost());
      System.out.println(economySystem.getSkillPoints());
    }
    updateSkills();
  }

  public void updateSkills() {
    // Lightfooted
    player.setBaseSpeed(2 * (1 + 0.1 * skillTreeSystem.findSkill("Lightfooted").getLevel()));

    // Merchant's Rizz & Seal of the Serpent
    for (int i = 0; i < trades.size(); i++) {
      Recipe trade = trades.get(i);
      Recipe base = baseTrades.get(i);

      if (trade.getItemIn().getId() != 14) {
        trade.setItemOut(
            new Item(14,
                (int) Math.floor((base.getItemOut().getQuantity()
                    * (1 + .02 * skillTreeSystem.findSkill("Merchant's Rizz").getLevel())))));
      } else {
        trade.setItemIn(
            new Item(14,
                (int) Math.floor((base.getItemIn().getQuantity()
                    * (1 - .02 * skillTreeSystem.findSkill("Seal of the Serpent").getLevel())))));
      }
      trades.set(i, trade);
    }
    System.out.println("level " + skillTreeSystem.findSkill("Seal of the Serpent").getLevel());

    // Cheap Tricks
    for (int i = 0; i < skills.size(); i++) {
      Skill s = skills.get(i);
      Skill base = baseSkills.get(i);
      s.setScaling(base.getScalingFactor() * (1 - .02 * skillTreeSystem.findSkill("Cheap Tricks").getLevel()));
      skills.set(i, s);
    }

    // Nature's Grasp

    // Green Thumb
    greenThumbChance = 2 * skillTreeSystem.findSkill("Green Thumb").getLevel();

    // Fruit of Knowledge
    levelingSystem.setXPBoost(1 + 0.02 * skillTreeSystem.findSkill("Fruit of Knowledge").getLevel());

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

    if (skillTree.isOpen()) {
      int x = lastClickedSkillTreeTile[0];
      int y = lastClickedSkillTreeTile[1];
      invHighlight.setPosition(x * invHighlight.getSize() + skillTree.getX(),
          y * invHighlight.getSize() + skillTree.getY());
      invHighlight.draw(g2d);
    }

    if (lastClickedInventoryTile[0] == -1 || lastClickedInventoryTile[1] == -1)
      return;

    int tileSize = getWidth() * 32 / 800;
    invHighlight.setSize(tileSize);
    int tileX = lastClickedInventoryTile[0];
    int tileY = lastClickedInventoryTile[1];

    int gridWidth = 11 * tileSize;
    int xOffset = (getWidth() - gridWidth) / 2;
    int yOffset = getHeight() - (8 * tileSize);

    invHighlight.setPosition(tileX * tileSize + xOffset, tileY * tileSize + yOffset);
    invHighlight.draw(g2d);
    Item hoveringItem = inventory.getItem(inventory.getSlotFromGrid(tileX, tileY));
    if (hoveringItem != null) {
      ArrayList<TextLine> lines = new ArrayList<>();
      lines.add(new TextLine(hoveringItem.getName(), Color.WHITE));
      lines.add(new TextLine(hoveringItem.getLore(), Color.LIGHT_GRAY));
      HoverInfo hoveredInfo = new HoverInfo(lines, mouseX, mouseY - 32);
      hoveredInfo.draw(g2d);
    }

  }

  private void drawUsername(Graphics2D g2d, Player player) {
    g2d.setFont(new Font("Minecraft", 1, 10));
    g2d.setColor(Color.WHITE);

    FontMetrics metrics = g2d.getFontMetrics();
    int usernameX = (int) (player.getX() + (player.getWidth() / 2) - metrics.stringWidth(player.getId()) / 2);
    int usernameY = (int) player.getY() + 5;
    g2d.setColor(new Color(0, 0, 0, 50));

    g2d.fillRect(usernameX - 3, usernameY - 10, metrics.stringWidth(player.getId()) + 3, metrics.getHeight());
    g2d.setColor(Color.WHITE);

    g2d.drawString(player.getId(), usernameX, usernameY);
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
        drawUsername(g2d, other);
      }

      player.draw(g2d);
      drawUsername(g2d, player);

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
      chatSystem.draw(g2d);

      // draw inventory highlight
      if (inventory.isOpen()) {
        drawInventoryHighlight(g2d);

        if (hoveredItem != null) {
          String quantityString = Integer.toString(hoveredItem.getQuantity());

          g2d.setColor(Color.BLACK);
          g2d.setFont(new Font("Minecraft", 1, (int) (25 * getWidth() / 800)));
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
        if (hoverInfo != null) {
          hoverInfo.draw(g2d);
        }
      }

      levelingSystem.draw(g2d);
      // dialogue.draw(g2d);

    }
  }

  private void setupSounds() {
    ArrayList<File> sfxList = new ArrayList<>();
    sfxList.addAll(Arrays.asList(new File("assets/sfx/").listFiles()));
    for (File f : sfxList) {
      sounds.put(f.getName().substring(0, f.getName().lastIndexOf(".")), new Sound(f));
    }

    ArrayList<File> bgmList = new ArrayList<>();
    bgmList.addAll(Arrays.asList(new File("assets/bgm/").listFiles()));
    for (File f : bgmList) {
      bgm.add(new Sound(f));
    }
    Collections.shuffle(bgm);
    gameAudio = new GameAudio(bgm);
    gameAudio.start();
  }

  public void playMusic(String soundCode) {
    Sound sound = sounds.get(soundCode);
    sound.play();
    sound.loop();
  }

  public void playSound(String soundCode, double x, double y) {
    Sound sound = sounds.get(soundCode);
    sound.play();
  }

  public void playGlobalSound(String soundCode) {
    playLocalSound(soundCode);
    writer.send("PLAY_SOUND " + soundCode + " " + player.getX() + " " + player.getY());
  }

  public void playLocalSound(String soundCode) {
    playSound(soundCode, player.getX(), player.getY());
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
