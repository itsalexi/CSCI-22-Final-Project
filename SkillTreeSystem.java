import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class SkillTreeSystem {

    private Skill rootSkill;
    private ArrayList<Skill> skills;
    private Set<Skill> unlocked;
    private EconomySystem economySystem;

    public SkillTreeSystem(ArrayList<Skill> s, EconomySystem ec) {
       skills = s;
       rootSkill = s.get(0);
       unlocked = new HashSet<>();
       unlocked.add(rootSkill);
       economySystem = ec;
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

    public int getTreeHeight() {
        return (int) (Math.log(skills.size() + 1) / Math.log(2)) - 1;
    }

    public void unlockSkill(Skill s) {
        if (unlocked.contains(s)) {
            return;
        }

        if (economySystem.getSkillPoints() < s.getUnlockCost()) {
            return;
        }

        for (Skill currSkill : unlocked) {
            System.out.println("a");
            for (Skill unlockable : currSkill.getNextSkills()) {
                if (s.equals(unlockable)) {
                    unlocked.add(s);
                    return;
                }
            }
        }
    }

    public void upgradeSkill(Skill s) {
        
    

        if (unlocked.contains(s)) {

        }
    }
    
}
