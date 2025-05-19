/**
 * The FarmingSystem class manages plant growth, harvesting, and farming mechanics.
 * It handles crop cycles, growth timers, and resource generation from harvested plants.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 19, 2025
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

import java.util.HashMap;
import java.util.Map;

public class FarmingSystem {
  private int[][] farmMap;
  private Map<String, Integer> plants;
  private Map<String, Long> lastGrowthTimestamps;
  private Map<String, Integer> drops;
  private Map<String, Integer[]> dropQuantities;
  private Map<String, Integer> xpDrops;
  private Map<String, Long> plantGrowthTimes;

  /**
   * Creates a new FarmingSystem.
   * 
   * @param size farm size
   */
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
    xpDrops = new HashMap<>();
    dropQuantities = new HashMap<>();

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

    xpDrops.put("blueberry", 75);
    xpDrops.put("carrot", 25);
    xpDrops.put("onion", 40);
    xpDrops.put("potato", 15);
    xpDrops.put("strawberry", 60);
    xpDrops.put("wheat", 10);

    plantGrowthTimes.put("wheat", 10000L);
    plantGrowthTimes.put("potato", 15000L);
    plantGrowthTimes.put("carrot", 15000L);
    plantGrowthTimes.put("onion", 20000L);
    plantGrowthTimes.put("strawberry", 20000L);
    plantGrowthTimes.put("blueberry", 30000L);

    dropQuantities.put("wheat", new Integer[] { 1, 2 });
    dropQuantities.put("potato", new Integer[] { 1, 2 });
    dropQuantities.put("carrot", new Integer[] { 1, 2 });
    dropQuantities.put("onion", new Integer[] { 1, 2 });
    dropQuantities.put("strawberry", new Integer[] { 2, 3 });
    dropQuantities.put("blueberry", new Integer[] { 3, 4 });
  }

  /**
   * Gets the plant id from index.
   * 
   * @param val index
   * @return plant id
   */
  public int getPlantFromIndex(int val) {
    String[] plantNames = { "blueberry", "carrot", "onion", "potato", "strawberry", "wheat" };

    int index = (int) Math.floor(val / 6);
    if (index >= plantNames.length)
      return -1;

    return drops.get(plantNames[index]);
  }

  /**
   * Gets the plant name from index.
   * 
   * @param val index
   * @return plant name
   */
  public String getPlantNameFromIndex(int val) {
    String[] plantNames = { "blueberry", "carrot", "onion", "potato", "strawberry", "wheat" };

    int index = (int) Math.floor(val / 6);
    if (index >= plantNames.length)
      return null;

    return plantNames[index];
  }

  /**
   * Plants a seed at position.
   * 
   * @param x x position
   * @param y y position
   * @param plant plant type
   * @return update message
   */
  public String plant(int x, int y, String plant) {
    if (farmMap[y][x] != -1) {
      return "";
    }
    lastGrowthTimestamps.put(x + "," + y, System.currentTimeMillis());
    farmMap[y][x] = plants.get(plant);
    return String.format("UPDATE farm %d %d %d", plants.get(plant), x, y);
  }

  /**
   * Gets random quantity for plant.
   * 
   * @param plantString plant type
   * @return quantity
   */
  public int getRandomQuantity(String plantString) {
    Integer[] range = dropQuantities.get(plantString);

    return range[0] + (int) (Math.random() * (range[1] - range[0]));
  }

  /**
   * Gets xp from plant.
   * 
   * @param plantString plant type
   * @return xp amount
   */
  public int getXPFromPlant(String plantString) {
    int xp = xpDrops.get(plantString);
    return xp;
  }

  /**
   * Grows plant at position.
   * 
   * @param x x position
   * @param y y position
   * @return update message
   */
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

  /**
   * Harvests plant at position.
   * 
   * @param x x position
   * @param y y position
   * @return update message
   */
  public String harvest(int x, int y) {
    farmMap[y][x] = -1;
    return String.format("UPDATE farm %d %d %d", -1, x, y);
  }

  /**
   * Gets the farm map.
   * 
   * @return farm map
   */
  public int[][] getFarmMap() {
    return farmMap;
  }
}
