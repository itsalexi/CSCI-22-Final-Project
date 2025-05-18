import java.util.ArrayList;

public class Skill {

    private ArrayList<Skill> nextSkills;
    private String name;
    private int unlockCost;
    private int initialUpgradeCost;
    private double scalingFactor;
    private int level;
    private int maxLevel;
    private int skillIcon;
    private String description;

    public Skill(String n, int unlock, int upgrade, double scaling, int max, int icon, String d) {
        name = n;
        unlockCost = unlock;
        initialUpgradeCost = upgrade;
        scalingFactor = scaling;
        level = 0;
        maxLevel = max;
        skillIcon = icon;
        nextSkills = new ArrayList<>();
        description = d;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDescrption() {
        return description;
    }

    public int getIcon() {
        return skillIcon;
    }

    public ArrayList<Skill> getNextSkills() {
        return nextSkills;
    }

    public String getName() {
        return name;
    }

    public int getUnlockCost() {
        return unlockCost;
    }

    public int getUpgradeCost() {
        return (int) (initialUpgradeCost + initialUpgradeCost * scalingFactor * level * level);
    }

    public int getLevel() {
        return level;
    }

    public void unlock() {
        level = 1;
    }

    public boolean isUnlocked() {
        return level > 0;
    }

    public void upgrade() {
        level += 1;
    }

    public void addNextSkill(Skill s) {
        nextSkills.add(s);
    }

    public boolean isMaxLevel() {
        return level == maxLevel;
    }

    public void setScaling(double sf) {
        scalingFactor = sf;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }

        Skill other = (Skill) obj;
        return name.equals(other.getName());
    }
}
