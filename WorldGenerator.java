import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorldGenerator {
  private int seed;
  private int width;
  private int height;
  private int[][] groundMap;
  private int[][] foliageMap;
  private int[][] edgeMap;
  private Map<WaterEdgeMatrix, Integer> validEdgeMatrices;

  public WorldGenerator(int s, int w, int h) {
    seed = s;
    width = w;
    height = h;
    groundMap = new int[h][w];
    foliageMap = new int[h][w];
    edgeMap = new int[h][w];
    validEdgeMatrices = new HashMap<>();

    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { null, true, null },
        { true, false, false },
        { null, false, false }
      }),
    101);
    
    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { null, true, null },
        { true, false, true },
        { false, false, null }
      }),
    102);
    
    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { null, false, false },
        { true, false, false },
        { null, true, null }
      }),
    117);
    
    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { false, false, null },
        { false, false, true },
        { null, true, null }
      }),
    118);
    
    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { false, false, false },
        { false, false, false },
        { false, false, true }
      }),
    138);
    
    validEdgeMatrices.put(
      new WaterEdgeMatrix(
        new Boolean[][] {
          { false, false, false },
          { false, false, false },
          { null, true, null }
        }),
    139);
    
    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { false, false, false },
        { false, false, false },
        { true, false, false }
      }),
    140);

    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { false, false, null },
        { false, false, true },
        { false, false, null }
      }),
    147);

    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { null, false, false },
        { true, false, false },
        { null, false, false }
      }),
    148);

    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { false, false, true },
        { false, false, false },
        { false, false, false }
      }),
    162);

    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { null, true, null },
        { false, false, false },
        { false, false, false }
      }),
    163);

    validEdgeMatrices.put(
    new WaterEdgeMatrix(
      new Boolean[][] {
        { true, false, false },
        { false, false, false },
        { false, false, false }
      }),
    164);
    
    regenerateWorld();
    // generateWaterEdges();
  }

  private void regenerateWorld() {

    NoiseGenerator groundGen = new NoiseGenerator(seed);
    NoiseGenerator foliageGen = new NoiseGenerator(seed + 1);

    double waterThreshold = 0.45;
    double foliageThreshold = 0.6;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        edgeMap[y][x] = -1;
        double g = groundGen.noise(x / 0.5, y / 0.5);
        g = (g + 1) / 2;
        if (g < waterThreshold) {
          groundMap[y][x] = 153;
          foliageMap[y][x] = -1;
        } else {

          groundMap[y][x] = 36;
          if (g > foliageThreshold) {
            double rand = Math.random();

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
              int pick = (int) (Math.random() * 11);
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

  private void generateWaterEdges() {
    Boolean valid = false;
    ArrayList<int[]> possibleEdges = new ArrayList<>();
    ArrayList<WaterEdgeMatrix> edgeMatrices = new ArrayList<>();
    while (!valid) {
      System.out.println("ANOTHA ONE");
      valid = true;
      possibleEdges = new ArrayList<>();
      edgeMatrices = new ArrayList<>();
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
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
                try {
                  if (groundMap[currX][currY] != 153) {
                    possibleEdges.add(new int[] { currX, currY });
                  }
                } catch (Exception e) {
                  continue;
                }
              }
            }
          }
        }
      }
      for (int[] edge : possibleEdges) {
        Boolean [][] waterMatrix = new Boolean[3][3];
        int x = edge[0];
        int y = edge[1];
        for(int i = -1; i < 2; i++) {
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
        WaterEdgeMatrix currMatrix = new WaterEdgeMatrix(waterMatrix);
        if (!validEdgeMatrices.containsKey(currMatrix)) {
          valid = false;
          groundMap[x][y] = 153;
          foliageMap[x][y] = -1;
        } else {
          edgeMatrices.add(currMatrix);
        }
      }
    }
    for (int i = 0; i < possibleEdges.size(); i++) {
      int x = possibleEdges.get(i)[0];
      int y = possibleEdges.get(i)[1];
      groundMap[x][y] = validEdgeMatrices.get(edgeMatrices.get(i));
      foliageMap[x][y] = -1;
    }
  }

  public int[][] getGroundMap() {
    return groundMap;
  }

  public int[][] getFoliageMap() {
    return foliageMap;
  }

  public int[][] getEdgeMap() {
    return edgeMap;
  }
}
