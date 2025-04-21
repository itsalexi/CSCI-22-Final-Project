
import java.util.HashMap;
import java.util.Map;

public class FarmingSystem {
  private int[][] farmMap;
  private Map<String, Integer> plants;
  private Map<String, Long> lastGrowthTimestamps;
  private Map<String, Integer> drops;

  public FarmingSystem(int size) {
    farmMap = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        farmMap[i][j] = -1;
      }
    }
    lastGrowthTimestamps = new HashMap<>();
    plants = new HashMap<>();
    drops = new HashMap<>();

    plants.put("blueberry", 0);
    plants.put("carrot", 6);
    plants.put("onion", 12);
    plants.put("potato", 18);
    plants.put("strawberry", 24);
    plants.put("wheat", 30);

    drops.put("blueberry", 10);
    drops.put("carrot", 8);
    drops.put("onion", 4);
    drops.put("potato", 6);
    drops.put("strawberry", 2);
    drops.put("wheat", 12);
  }

  public int getPlantFromIndex(int val) {
    String[] plantNames = { "blueberry", "carrot", "onion", "potato", "strawberry", "wheat" };

    int index = (int) Math.floor(val / 6);
    if (index >= plantNames.length)
      return -1;

    return drops.get(plantNames[index]);
  }

  public String plant(int x, int y, String plant) {
    if (farmMap[y][x] != -1) {
      return "";
    }
    lastGrowthTimestamps.put(x + "," + y, System.currentTimeMillis());
    farmMap[y][x] = plants.get(plant);
    return String.format("UPDATE farm %d %d %d", plants.get(plant), x, y);
  }

  public String grow(int x, int y) {
    int val = farmMap[y][x];
    if (val < 6 && val == 5 || val >= 6 && val % 6 == 5) {
      return "";
    }
    long now = System.currentTimeMillis();
    long last = lastGrowthTimestamps.getOrDefault(x + "," + y, 0L);

    if (now - last < 10)
      return "";

    farmMap[y][x] += 1;
    lastGrowthTimestamps.put(x + "," + y, now);
    return String.format("UPDATE farm %d %d %d", val + 1, x, y);
  }

  public String harvest(int x, int y) {
    farmMap[y][x] = -1;
    return String.format("UPDATE farm %d %d %d", -1, x, y);
  }

  public int[][] getFarmMap() {
    return farmMap;
  }
}
