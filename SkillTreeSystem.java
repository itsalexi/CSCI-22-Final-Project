import java.util.ArrayList;
public class SkillTreeSystem {

    private Skill rootSkill;
    private ArrayList<Skill> skills;
    private EconomySystem economySystem;

    public SkillTreeSystem(ArrayList<Skill> s, EconomySystem ec) {
       skills = s;
       rootSkill = getRootSkill(skills);
       populateNextSkills(skills);
       economySystem = ec;
    }

    private void populateNextSkills(ArrayList<Skill> s) {
        for (Skill skill : s) {
            if (skill != rootSkill){
                skill.getPrevSkill().addNextSkill(skill);
            }
        }
    }

    private Skill getRootSkill(ArrayList<Skill> s) {
        for (Skill skill : s) {
            if (skill.getPrevSkill() == null) {
                return skill;
            }
        }
        return null;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public ArrayList<Skill> getSkillsAtLevel(int level) {
        ArrayList<Skill> output = new ArrayList<>();
        output.add(rootSkill);

        ArrayList<Skill> nextLevel;
        for(int i = 1; i < level; i++) {
            nextLevel = new ArrayList<>();
            for (Skill s : output) {
                nextLevel.addAll(s.getNextSkills());
            }
            output = new ArrayList<>();
            output.addAll(nextLevel);
        }

        return output;
    }

    public void unlockSkill(Skill s) {
        if (s.isUnlocked()) {
            return;
        }

        if (economySystem.getSkillPoints() < s.getUnlockCost()) {
            return;
        }

        for (Skill currSkill : skills) {
            if (!currSkill.isUnlocked()) {
                continue;
            }
            System.out.println("a");
            for (Skill unlockable : currSkill.getNextSkills()) {
                if (s.equals(unlockable)) {
                    s.unlock();
                    economySystem.setSkillPoints(economySystem.getSkillPoints() - s.getUnlockCost());
                    return;
                }
            }
        }
    }

    public void upgradeSkill(Skill s) {
        if (s.isUnlocked() && s.getUpgradeCost() <= economySystem.getBalance() && !s.isMaxLevel()) {
            economySystem.setBalance(economySystem.getBalance() - s.getUpgradeCost());
            s.upgrade();
        }
    }    
}
