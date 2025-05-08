import java.util.ArrayList;

public class Skill {
   
    private ArrayList<Skill> nextSkills;
    private String name;
    private int cost;

    public Skill(String n, int c, ArrayList<Skill> ns) {
        name = n;
        cost = c;
        nextSkills = ns;
    }

    public ArrayList<Skill> getNextSkills() {
        return nextSkills;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public void addNextSkill(Skill s) {
        nextSkills.add(s);
    }
}
