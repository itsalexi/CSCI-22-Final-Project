/**
 * The WorldGenerator class handles procedural generation of the game world.
 * It creates terrain, water, foliage, and trees using noise-based algorithms.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 20, 2025
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

import java.util.ArrayList;

public class WorldGenerator {
  private int seed;
  private int width;
  private int height;
  private int[][] groundMap;
  private int[][] foliageMap;
  private int[][] edgeMap;

  private int[][] treeMap;
  private ArrayList<WaterEdgeMatrix> validEdgeMatrices;
  private ArrayList<Integer> edgeIDs;

  private int[] validSpawn;

  /**
   * Represents a 3x3 matrix of water positions for edge detection.
   */
  private class WaterEdgeMatrix {

    private Boolean[][] waterPositions;

    /**
     * Creates a new WaterEdgeMatrix with specified water positions.
     * 
     * @param wp 3x3 boolean array representing water positions
     */
    public WaterEdgeMatrix(Boolean[][] wp) {
      waterPositions = wp;
    }

    /**
     * Compares this matrix with another for equality.
     * 
     * @param another the object to compare with
     * @return true if the matrices are equal
     */
    @Override
    public boolean equals(Object another) {
      if (another == this) {
        return true;
      }

      if (another == null) {
        return false;
      }

      if (another.getClass() != this.getClass()) {
        return false;
      }

      WaterEdgeMatrix other = (WaterEdgeMatrix) another;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (waterPositions[i][j] == null || other.getWaterPositions()[i][j] == null) {
            continue;
          }
          if (waterPositions[i][j] != other.getWaterPositions()[i][j]) {
            return false;
          }
        }
      }
      return true;
    }

    /**
     * Gets the water positions matrix.
     * 
     * @return the 3x3 boolean array of water positions
     */
    public Boolean[][] getWaterPositions() {
      return waterPositions;
    }
  }

  /**
   * Creates a new WorldGenerator instance and generates the world.
   * 
   * @param s the seed for world generation
   * @param w the width of the world
   * @param h the height of the world
   */
  public WorldGenerator(int s, int w, int h) {
    seed = s;
    width = w;
    height = h;
    groundMap = new int[h][w];
    foliageMap = new int[h][w];
    edgeMap = new int[h][w];
    treeMap = new int[h][w];
    validEdgeMatrices = new ArrayList<>();
    edgeIDs = new ArrayList<>();

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { null, true, null },
                { true, false, false },
                { null, false, false }
            }));
    edgeIDs.add(101);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { null, true, null },
                { false, false, true },
                { false, false, null }
            }));
    edgeIDs.add(102);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { null, false, false },
                { true, false, false },
                { null, true, null }
            }));
    edgeIDs.add(117);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { false, false, null },
                { false, false, true },
                { null, true, null }
            }));
    edgeIDs.add(118);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { false, false, false },
                { false, false, false },
                { false, false, true }
            }));
    edgeIDs.add(138);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { false, false, false },
                { false, false, false },
                { null, true, null }
            }));
    edgeIDs.add(139);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { false, false, false },
                { false, false, false },
                { true, false, false }
            }));
    edgeIDs.add(140);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { false, false, null },
                { false, false, true },
                { false, false, null }
            }));
    edgeIDs.add(147);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { null, false, false },
                { true, false, false },
                { null, false, false }
            }));
    edgeIDs.add(148);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { false, false, true },
                { false, false, false },
                { false, false, false }
            }));
    edgeIDs.add(162);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { null, true, null },
                { false, false, false },
                { false, false, false }
            }));
    edgeIDs.add(163);

    validEdgeMatrices.add(
        new WaterEdgeMatrix(
            new Boolean[][] {
                { true, false, false },
                { false, false, false },
                { false, false, false }
            }));
    edgeIDs.add(164);

    regenerateWorld();
    generateWaterEdges();
    validSpawn = findValidSpawn();

  }

  /**
   * Finds a valid spawn location for the player.
   * 
   * @return array containing coordinates of valid spawn point
   */
  private int[] findValidSpawn() {
    int[] coords = { 0, 0 };
    for (int y = height / 2; y < height; y++) {
      for (int x = width / 2; x < width; x++) {
        if (groundMap[x][y] == 36 && treeMap[y][x] == -1) {
          boolean nearTree = false;
          for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
              int checkY = y + i;
              int checkX = x + j;
              if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                if (treeMap[checkY][checkX] != -1) {
                  nearTree = true;
                  break;
                }
              }
            }
            if (nearTree)
              break;
          }
          if (nearTree) {
            continue;
          }
          coords[0] = y;
          coords[1] = x;
          return coords;
        }
      }
    }
    return null;
  }

  /**
   * Gets the valid spawn location.
   * 
   * @return array containing coordinates of valid spawn point
   */
  public int[] getValidSpawn() {
    return validSpawn;
  }

  /**
   * Regenerates the world using noise-based generation.
   */
  private void regenerateWorld() {

    NoiseGenerator groundGen = new NoiseGenerator(seed);
    NoiseGenerator foliageGen = new NoiseGenerator(seed + 1);

    double waterThreshold = 0.45;
    double foliageThreshold = 0.6;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        edgeMap[y][x] = -1;
        treeMap[y][x] = -1;
        double g = groundGen.noise(x / 0.5, y / 0.5);
        g = (g + 1) / 2;
        if (g < waterThreshold) {
          groundMap[y][x] = 153;
          foliageMap[y][x] = -1;
        } else {
          double rand = Math.random();

          groundMap[y][x] = 36;
          if (rand > 0.8) {
            treeMap[y][x] = 0;
          }

          if (g > foliageThreshold) {
            double f = foliageGen.noise(x / 0.5, y / 0.5);
            f = (f + 1) / 2;
            int grassFoliageType = (int) (Math.random() * 4);
            switch (grassFoliageType) {
              case 0 -> foliageMap[y][x] = 85;
              case 1 -> foliageMap[y][x] = 355;
              case 2 -> foliageMap[y][x] = 360;
              case 3 -> foliageMap[y][x] = 362;
            }
            if (f > 0.6 && rand > 0.65) {
              int pick = (int) (Math.random() * 12);
              switch (pick) {
                case 0 -> foliageMap[y][x] = 82;
                case 1 -> foliageMap[y][x] = 79;
                case 2 -> foliageMap[y][x] = 94;
                case 3 -> foliageMap[y][x] = 75;
                case 4 -> foliageMap[y][x] = 356;
                case 5 -> foliageMap[y][x] = 357;
                case 6 -> foliageMap[y][x] = 358;
                case 7 -> foliageMap[y][x] = 359;
                case 8 -> foliageMap[y][x] = 361;
                case 9 -> foliageMap[y][x] = 363;
                case 10 -> foliageMap[y][x] = 364;
              }
            }

          } else {
            foliageMap[y][x] = -1;

          }
        }
      }

    }

  }

  /**
   * Gets a list of valid spawn locations.
   * 
   * @return ArrayList of valid spawn coordinates
   */
  public ArrayList<int[]> getValidSpawns() {
    ArrayList<int[]> valids = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        if (groundMap[i][j] == 36) {
          valids.add(new int[] { i, j });
        }
      }
    }
    return valids;
  }

  /**
   * Generates water edges and transitions.
   */
  private void generateWaterEdges() {
    ArrayList<int[]> possibleEdges;
    ArrayList<WaterEdgeMatrix> edgeMatrices;
    while (true) {
      Boolean valid = true;
      possibleEdges = new ArrayList<>();
      edgeMatrices = new ArrayList<>();
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          // Check for existing water tiles
          if (groundMap[i][j] == 153) {
            int x = i;
            int y = j;

            for (int dx = -1; dx < 2; dx++) {
              for (int dy = -1; dy < 2; dy++) {
                if (dx == 0 && dy == 0) {
                  continue;
                }
                int currX = x + dx;
                int currY = y + dy;

                // Ensure we are not modifying an already water cell
                try {
                  if (groundMap[currX][currY] != 153) {
                    possibleEdges.add(new int[] { currX, currY });
                  }
                } catch (Exception e) {
                  continue; // Ignore out-of-bound errors
                }
              }
            }
          }
        }
      }
      for (int[] edge : possibleEdges) {
        Boolean[][] waterMatrix = new Boolean[3][3];
        int x = edge[0];
        int y = edge[1];

        // Form water matrix around the edge
        for (int i = -1; i < 2; i++) {
          for (int j = -1; j < 2; j++) {
            int currX = x + i;
            int currY = y + j;
            try {
              waterMatrix[i + 1][j + 1] = groundMap[currX][currY] == 153;
            } catch (Exception e) {
              waterMatrix[i + 1][j + 1] = false;
            }
          }
        }

        // Check if the water matrix is valid
        WaterEdgeMatrix currMatrix = new WaterEdgeMatrix(waterMatrix);
        Boolean currIsValid = false;
        for (int i = 0; i < validEdgeMatrices.size(); i++) {
          if (currMatrix.equals(validEdgeMatrices.get(i))) {
            currIsValid = true;
            edgeMatrices.add(currMatrix);
          }
        }
        if (!currIsValid) {
          valid = false;
          groundMap[x][y] = 153;
          foliageMap[x][y] = -1;
          treeMap[x][y] = -1;
        }
      }
      if (valid) {
        break;
      }
    }
    for (int i = 0; i < possibleEdges.size(); i++) {
      for (int j = 0; j < validEdgeMatrices.size(); j++) {
        if (edgeMatrices.get(i).equals(validEdgeMatrices.get(j))) {
          int x = possibleEdges.get(i)[0];
          int y = possibleEdges.get(i)[1];
          groundMap[x][y] = edgeIDs.get(j);
          edgeMap[x][y] = edgeIDs.get(j);
          foliageMap[x][y] = -1;
          treeMap[x][y] = -1;
          break;
        }
      }
    }
  }

  /**
   * Gets the ground tile map.
   * 
   * @return 2D array of ground tile IDs
   */
  public int[][] getGroundMap() {
    return groundMap;
  }

  /**
   * Gets the foliage tile map.
   * 
   * @return 2D array of foliage tile IDs
   */
  public int[][] getFoliageMap() {
    return foliageMap;
  }

  /**
   * Gets the edge tile map.
   * 
   * @return 2D array of edge tile IDs
   */
  public int[][] getEdgeMap() {
    return edgeMap;
  }

  /**
   * Gets the tree tile map.
   * 
   * @return 2D array of tree tile IDs
   */
  public int[][] getTreeMap() {
    return treeMap;
  }
}
