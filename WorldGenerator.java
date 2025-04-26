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
                new Boolean[] { false, true, false },
                new Boolean[] { true, false, false },
                new Boolean[] { false, false, false }
            }),
        101);

    regenerateWorld();
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
            System.out.printf("%.2f ", f);
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
      System.out.printf("\n");

    }

  }

  private void generateWaterEdges() {
    ArrayList<int[]> possibleEdges = new ArrayList<>();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (groundMap[i][j] == 153) {
          int x = i;
          int y = j;

          int[][] directions = new int[][] {
              new int[] { 0, 1 },
              new int[] { 1, 0 },
              new int[] { 0, -1 },
              new int[] { -1, 0 },
              new int[] { -1, -1 },
              new int[] { 1, 1 },
              new int[] { -1, 1 },
              new int[] { 1, -1 }
          };

          for (int[] dir : directions) {
            int currX = x + dir[0];
            int currY = y + dir[1];

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
