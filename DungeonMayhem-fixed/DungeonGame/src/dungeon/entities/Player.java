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
    private Player leader;

    public Player(String name, int maxHp, int maxMana, int defense, int attackDamage, int speed, int range) {
        super(name, maxHp, maxMana, defense, attackDamage, speed);
        this.level  = 1;
        this.range  = range;
        this.gold   = 0;
        this.team   = new ArrayList<>();
        this.leader = null;
    }

    public void setLeader(Player leader) { this.leader = leader; }
    public Player getLeader() { return leader; }

    public List<Player> getWholeParty() {
        List<Player> party = new ArrayList<>();
        Player main = (this.leader != null) ? this.leader : this;
        party.add(main);
        party.addAll(main.getTeam());
        return party;
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
        ally.setLeader(this);
        team.add(ally);
        System.out.println(ally.getName() + " the " + ally.getClass().getSimpleName() + " joined the party!");
        return true;
    }

    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }

    public void levelUp() {
        this.level++;
        setMaxHp(getMaxHp() + 20);
        setMaxMana(getMaxMana() + 10);
        setDefense(getDefense() + 3);
        setAttackDamage(getAttackDamage() + 5);
        setHp(getMaxHp());
        setMana(getMaxMana());
        System.out.println(getName() + " leveled up to " + this.level + "! Stats increased and fully restored!");
    }

    public int getLevel()         { return level; }
    public int getRange()         { return range; }
    public void setGold(int gold) { this.gold = gold; }
    public int getGold()          { return gold; }
    public List<Player> getTeam() { return team; }

    public List<String> getRecruitedClasses() {
        List<String> names = new ArrayList<>();
        for (Player p : team) names.add(p.getClass().getSimpleName());
        return names;
    }
}
