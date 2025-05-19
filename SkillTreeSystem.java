import java.util.ArrayList;

public class SkillTreeSystem {

    private Skill rootSkill;
    private ArrayList<Skill> skills;
    private EconomySystem economySystem;

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

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public Skill getRootSkill() {
        return rootSkill;
    }

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

    public boolean isUpgradeable(Skill s) {
        return s.isUnlocked() && s.getUpgradeCost() <= economySystem.getBalance() && !s.isMaxLevel();
    }

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

    public boolean upgradeSkill(Skill s) {
        if (s.isUnlocked() && s.getUpgradeCost() <= economySystem.getBalance() && !s.isMaxLevel()) {
            economySystem.setBalance(economySystem.getBalance() - s.getUpgradeCost());
            s.upgrade();
            return true;
        }
        return false;
    }

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
