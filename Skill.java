/**
 * The Skill class represents a skill in the game's skill tree system.
 * It manages skill properties like level, cost, and prerequisites.
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

    /**
     * Creates a new Skill instance.
     * 
     * @param n the name of the skill
     * @param unlock the cost to unlock the skill
     * @param upgrade the initial cost to upgrade the skill
     * @param scaling the factor that increases upgrade cost with level
     * @param max the maximum level the skill can reach
     * @param icon the icon ID for the skill
     * @param d the description of the skill
     */
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

    /**
     * Gets the maximum level this skill can reach.
     * 
     * @return the maximum level
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Gets the description of this skill.
     * 
     * @return the skill description
     */
    public String getDescrption() {
        return description;
    }

    /**
     * Gets the icon ID for this skill.
     * 
     * @return the skill icon ID
     */
    public int getIcon() {
        return skillIcon;
    }

    /**
     * Gets the list of skills that can be unlocked after this skill.
     * 
     * @return the list of next available skills
     */
    public ArrayList<Skill> getNextSkills() {
        return nextSkills;
    }

    /**
     * Gets the name of this skill.
     * 
     * @return the skill name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the cost to unlock this skill.
     * 
     * @return the unlock cost
     */
    public int getUnlockCost() {
        return unlockCost;
    }

    /**
     * Gets the cost to upgrade this skill to the next level.
     * Cost increases quadratically with level.
     * 
     * @return the upgrade cost
     */
    public int getUpgradeCost() {
        return (int) (initialUpgradeCost + initialUpgradeCost * scalingFactor * level * level);
    }

    /**
     * Gets the current level of this skill.
     * 
     * @return the current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Unlocks this skill, setting its level to 1.
     */
    public void unlock() {
        level = 1;
    }

    /**
     * Checks if this skill has been unlocked.
     * 
     * @return true if the skill is unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return level > 0;
    }

    /**
     * Upgrades this skill by one level.
     */
    public void upgrade() {
        level += 1;
    }

    /**
     * Adds a skill that can be unlocked after this skill.
     * 
     * @param s the skill to add as a prerequisite
     */
    public void addNextSkill(Skill s) {
        nextSkills.add(s);
    }

    /**
     * Checks if this skill has reached its maximum level.
     * 
     * @return true if the skill is at max level, false otherwise
     */
    public boolean isMaxLevel() {
        return level == maxLevel;
    }

    /**
     * Sets the scaling factor for upgrade costs.
     * 
     * @param sf the new scaling factor
     */
    public void setScaling(double sf) {
        scalingFactor = sf;
    }

    /**
     * Gets the scaling factor for upgrade costs.
     * 
     * @return the scaling factor
     */
    public double getScalingFactor() {
        return scalingFactor;
    }

    /**
     * Sets the current level of this skill.
     * 
     * @param l the new level
     */
    public void setLevel(int l) {
        level = l;
    }

    /**
     * Compares this skill with another object for equality.
     * Two skills are equal if they have the same name.
     * 
     * @param obj the object to compare with
     * @return true if the skills are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }

        Skill other = (Skill) obj;
        return name.equals(other.getName());
    }
}
