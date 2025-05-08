import java.util.ArrayList;
public class SkillTreeSystem {

    private Skill rootSkill;
    private ArrayList<Skill> skills;

    public SkillTreeSystem(ArrayList<Skill> s) {
       skills = s;
       rootSkill = s.get(0);
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
    
}
