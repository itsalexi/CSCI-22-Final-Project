/**
 * The SkillTreeSystem class manages the game's skill tree functionality.
 * It handles skill unlocking, upgrading, and their effects on gameplay.
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

public class SkillTreeSystem {

    private Skill rootSkill;
    private ArrayList<Skill> skills;
    private EconomySystem economySystem;

    /**
     * Creates a new SkillTreeSystem instance.
     * Initializes the skill tree with predefined skills.
     * 
     * @param s the list of skills to initialize the skill tree with
     * @param ec the EconomySystem to manage the game's economy
     */
    public SkillTreeSystem(ArrayList<Skill> s, EconomySystem ec) {
        skills = s;
        rootSkill = s.get(0);
        populateNextSkills(skills);
        economySystem = ec;
    }

    private void populateNextSkills(ArrayList<Skill> s) {
        for (int i = 0; i < s.size(); i++) {
            Skill currSkill = s.get(i);
            if (i * 2 + 1 < s.size()) {
                currSkill.addNextSkill(s.get(i * 2 + 1));
            }
            if (i * 2 + 2 < s.size()) {
                currSkill.addNextSkill(s.get(i * 2 + 2));
            }
        }
    }

    /**
     * Gets the list of all skills in the skill tree.
     * 
     * @return ArrayList of all skills
     */
    public ArrayList<Skill> getSkills() {
        return skills;
    }

    /**
     * Gets the root skill of the skill tree.
     * 
     * @return the root skill
     */
    public Skill getRootSkill() {
        return rootSkill;
    }

    /**
     * Gets skills at a specific level in the skill tree.
     * 
     * @param level the level of skills to get
     * @return ArrayList of skills at the specified level
     */
    public ArrayList<Skill> getSkillsAtLevel(int level) {
        ArrayList<Skill> output = new ArrayList<>();
        output.add(rootSkill);

        ArrayList<Skill> nextLevel;
        for (int i = 1; i < level; i++) {
            nextLevel = new ArrayList<>();
            for (Skill s : output) {
                nextLevel.addAll(s.getNextSkills());
            }
            output = new ArrayList<>();
            output.addAll(nextLevel);
        }

        return output;
    }

    /**
     * Checks if a skill is unlockable.
     * 
     * @param s the skill to check
     * @return true if the skill is unlockable, false otherwise
     */
    public boolean isUnlockable(Skill s) {
        if (s.isUnlocked()) {
            return false;
        }

        if (economySystem.getSkillPoints() < s.getUnlockCost()) {
            return false;
        }

        if (s.equals(rootSkill)) {
            return true;
        }

        for (Skill currSkill : skills) {
            if (!currSkill.isUnlocked()) {
                continue;
            }
            for (Skill unlockable : currSkill.getNextSkills()) {
                if (s.equals(unlockable)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a skill is upgradeable.
     * 
     * @param s the skill to check
     * @return true if the skill is upgradeable, false otherwise
     */
    public boolean isUpgradeable(Skill s) {
        return s.isUnlocked() && s.getUpgradeCost() <= economySystem.getBalance() && !s.isMaxLevel();
    }

    /**
     * Unlocks a skill if the player has enough skill points.
     * 
     * @param s the skill to unlock
     * @return true if the skill was successfully unlocked, false otherwise
     */
    public boolean unlockSkill(Skill s) {
        if (s.isUnlocked()) {
            return false;
        }

        if (economySystem.getSkillPoints() < s.getUnlockCost()) {
            return false;
        }

        if (s.equals(rootSkill)) {
            s.unlock();
            economySystem.setSkillPoints(economySystem.getSkillPoints() - s.getUnlockCost());
            return false;
        }

        for (Skill currSkill : skills) {
            if (!currSkill.isUnlocked()) {
                continue;
            }
            for (Skill unlockable : currSkill.getNextSkills()) {
                if (s.equals(unlockable)) {
                    s.unlock();
                    economySystem.setSkillPoints(economySystem.getSkillPoints() - s.getUnlockCost());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Upgrades a skill if the player has enough skill points.
     * 
     * @param s the skill to upgrade
     * @return true if the skill was successfully upgraded, false otherwise
     */
    public boolean upgradeSkill(Skill s) {
        if (s.isUnlocked() && s.getUpgradeCost() <= economySystem.getBalance() && !s.isMaxLevel()) {
            economySystem.setBalance(economySystem.getBalance() - s.getUpgradeCost());
            s.upgrade();
            return true;
        }
        return false;
    }

    /**
     * Finds a skill by its name.
     * 
     * @param name the name of the skill to find
     * @return the skill with the specified name, or null if not found
     */
    public Skill findSkill(String name) {
        name = name.toLowerCase();
        for (Skill s : skills) {
            if (name.equals(s.getName().toLowerCase())) {
                return s;
            }
        }
        return null;
    }
}
