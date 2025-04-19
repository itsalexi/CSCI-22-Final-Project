
public class WorldGenerator {

  public static void main(String[] args) {
    int[][] grid = new int[25][25];
    NoiseGenerator noiseGen = new NoiseGenerator(125125);

    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < 25; j++) {
        double val = noiseGen.noise(i, j);
        if (val > 0.4) {
          grid[i][j] = 1;
          System.out.printf("1 ", val);

        } else {
          grid[i][j] = 0;
          System.out.printf("0 ", val);
        }
      }
      System.out.printf("\n");
    }
  }
}
