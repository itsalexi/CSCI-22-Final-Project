
public class WorldGenerator {
  private int seed;
  private int width;
  private int height;
  private int[][] groundMap;
  private int[][] foliageMap;
  private int[][] edgeMap;

  public WorldGenerator(int s, int w, int h) {
    seed = s;
    width = w;
    height = h;
    groundMap = new int[h][w];
    foliageMap = new int[h][w];
    edgeMap = new int[h][w];

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
