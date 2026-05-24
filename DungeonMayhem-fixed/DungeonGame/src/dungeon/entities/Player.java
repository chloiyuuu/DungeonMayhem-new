package dungeon.entities;

import dungeon.interfaces.SkillCaster;
import dungeon.interfaces.Rewardable;
import java.util.ArrayList;
import java.util.List;

public abstract class Player extends Entity implements SkillCaster, Rewardable {
    private int level;
    private int range;
    private int gold;
    private List<Player> team;

    public Player(String name, int maxHp, int maxMana, int defense, int attackDamage, int speed, int range) {
        super(name, maxHp, maxMana, defense, attackDamage, speed);
        this.level  = 1;
        this.range  = range;
        this.gold   = 0;
        this.team   = new ArrayList<>();
    }

    @Override
    public void collectReward(int goldAmount) {
        this.gold += goldAmount;
        System.out.println(getName() + " collected " + goldAmount + " gold! Total: " + this.gold);
    }

    public boolean recruitCharacter(Player ally) {
        if (team.size() >= 4) {
            System.out.println("Party is full!");
            return false;
        }
        team.add(ally);
        System.out.println(ally.getName() + " the " + ally.getClass().getSimpleName() + " joined the party!");
        return true;
    }

    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }

    public int getLevel()         { return level; }
    public int getRange()         { return range; }
    public int getGold()          { return gold; }
    public List<Player> getTeam() { return team; }

    public List<String> getRecruitedClasses() {
        List<String> names = new ArrayList<>();
        for (Player p : team) names.add(p.getClass().getSimpleName());
        return names;
    }
}
