import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class GameCanvas extends JComponent {

  private Timer repaintTimer;
  private Sprite testSprite;
  private ArrayList<TileGrid> collidableGrids;
  private TileGrid groundGrid;
  private TileGrid edgeGrid;
  private TileGrid foliageGrid;
  private Player player;
  private Socket socket;
  private Map<String, Player> otherPlayers;
  private GameStarter client;
  private WriteToServer writer;

  public GameCanvas() {
    otherPlayers = new HashMap<>();
    repaintTimer = new Timer(1000 / 60, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        repaint();
      }
    });
    repaintTimer.start();

    this.setPreferredSize(new Dimension(800, 600));

    SpriteFiles tileMapFiles = new SpriteFiles("assets/tilemap");

    player = new Player();

    testSprite = new Sprite(tileMapFiles.getFiles(), 32);

    // Layer: Ground
    int[][] groundMap = {
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
    int[][] edgeMap = {
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
    int[][] foliageMap = {
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

    groundGrid = new TileGrid(testSprite, groundMap);
    edgeGrid = new TileGrid(testSprite, edgeMap);
    foliageGrid = new TileGrid(testSprite, foliageMap);
    collidableGrids = new ArrayList<>();
    collidableGrids.add(edgeGrid);
    addKeyBindings();
    addMouseListener();

  }

  public void setServerOut(DataOutputStream out) {
    writer = new WriteToServer(out);
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

  private void useHoe() {
    if (!player.isHoeing()) {
      player.useHoe();
    }
  }

  public void addKeyBindings() {
    this.setFocusable(true);
    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
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
          case KeyEvent.VK_SPACE:
            useHoe();

        }

        if (direction != null) {
          movePlayer(direction);
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (!player.isHoeing()) {
          player.setAnimationState("idle");
          writer.send("MOVE " + client.getPlayerID() + " " + player.getX() + " " + player.getY() + " "
              + player.getDirection() + " " + "idle");

        }
      }
    });
  }

  public void movePlayer(String direction) {
    double speed = player.getSpeed();
    double newX = player.getX();
    double newY = player.getY();

    switch (direction) {
      case "UP":
        newY -= speed;
        break;
      case "DOWN":
        newY += speed;
        break;
      case "LEFT":
        newX -= speed;
        break;
      case "RIGHT":
        newX += speed;
        break;
    }

    Rectangle2D futureHitbox = player.getHitboxAt(newX, newY);

    if (!isColliding(futureHitbox)) {
      writer.send("MOVE " + client.getPlayerID() + " " + newX + " " + newY + " " + direction + " " + "walk");
      player.setDirection(direction);
      player.setAnimationState("walk");
      player.setPosition(newX, newY);
    }
  }

  public void updatePlayer(String id, double x, double y, String dir, String state) {
    Player newPlayer = otherPlayers.getOrDefault(id, new Player());
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

  public void addMouseListener() {
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int tileSize = 32;
        int tileX = x / tileSize;
        int tileY = y / tileSize;

        System.out.println("Clicked on tile: [" + tileX + ", " + tileY + "]");

        if (tileX < groundGrid.getWidth() && tileY < groundGrid.getHeight()) {
          System.out.println("Ground tile ID: " + groundGrid.getTileAt(tileY, tileX));
          System.out.println("Edge tile ID: " + edgeGrid.getTileAt(tileY, tileX));
          System.out.println("Foliage tile ID: " + foliageGrid.getTileAt(tileY, tileX));
        }
      }
    });

  }

  public Player getPlayer() {
    return player;
  }

  public void setClient(GameStarter c) {
    client = c;
  }

  @Override
  public void paintComponent(Graphics g) {

    Graphics2D g2d = (Graphics2D) g;

    groundGrid.draw(g2d);
    edgeGrid.draw(g2d);
    foliageGrid.draw(g2d);

    for (Player other : otherPlayers.values()) {
      other.draw(g2d);
    }
    player.draw(g2d);

    g2d.draw(player.getHitboxAt(player.getX(), player.getY()));

    for (int i = 0; i < edgeGrid.getHeight(); i++) {
      for (int j = 0; j < edgeGrid.getWidth(); j++) {
        Rectangle2D hitbox = edgeGrid.getTileHitBoxAt(i, j);
        if (hitbox != null) {
          g2d.draw(hitbox);
        }
      }
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
