
import java.util.HashMap;
import java.util.Map;

public class FarmingSystem {
  private int[][] farmMap;
  private Map<String, Integer> plants;
  private Map<String, Long> lastGrowthTimestamps;
  private Map<String, Integer> drops;
  private Map<String, Long> plantGrowthTimes;

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
    plantGrowthTimes = new HashMap<>();

    plants.put("blueberry", 0);
    plants.put("carrot", 6);
    plants.put("onion", 12);
    plants.put("potato", 18);
    plants.put("strawberry", 24);
    plants.put("wheat", 30);

    drops.put("blueberry", 12);
    drops.put("carrot", 6);
    drops.put("onion", 8);
    drops.put("potato", 4);
    drops.put("strawberry", 10);
    drops.put("wheat", 2);
    plantGrowthTimes.put("wheat", 10000L);
    plantGrowthTimes.put("potato", 15000L);
    plantGrowthTimes.put("carrot", 15000L);
    plantGrowthTimes.put("onion", 20000L);
    plantGrowthTimes.put("strawberry", 20000L);
    plantGrowthTimes.put("blueberry", 30000L);
  }

  public int getPlantFromIndex(int val) {
    String[] plantNames = { "blueberry", "carrot", "onion", "potato", "strawberry", "wheat" };

    int index = (int) Math.floor(val / 6);
    if (index >= plantNames.length)
      return -1;

    return drops.get(plantNames[index]);
  }

  public String getPlantNameFromIndex(int val) {
    String[] plantNames = { "blueberry", "carrot", "onion", "potato", "strawberry", "wheat" };

    int index = (int) Math.floor(val / 6);
    if (index >= plantNames.length)
      return null;

    return plantNames[index];
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
    long last = lastGrowthTimestamps.getOrDefault(x + "," + y, 0L);
    long current = System.currentTimeMillis();
    long growthTime = plantGrowthTimes.get(getPlantNameFromIndex(val));

    if (current - last < growthTime)
      return "";

    farmMap[y][x] += 1;
    lastGrowthTimestamps.put(x + "," + y, current);

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
