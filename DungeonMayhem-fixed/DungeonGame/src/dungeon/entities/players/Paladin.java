package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Paladin extends Player {
    // Paladin: tank-support hybrid — high defense, moderate damage, can shield allies
    public Paladin(String name) {
        super(name, 140, 22, 20, 8, 1);
    }

    public void holyStrike(Damageable target) {
        System.out.println(getName() + " delivers a Holy Strike!");
        target.takeDamage(getAttackDamage() + 15);
    }

    public void consecration(Damageable target) {
        System.out.println(getName() + " consecrates the ground — Consecration!");
        target.takeDamage(getAttackDamage() + 22);
    }

    public void divineShield() {
        // Temporarily boosts own HP as a shield simulation
        int shieldHp = Math.min(getHp() + 30, getMaxHp());
        setHp(shieldHp);
        System.out.println(getName() + " raises Divine Shield! (+30 HP restored)");
    }

    @Override public void useSkillOne(Damageable target)   { holyStrike(target); }
    @Override public void useSkillTwo(Damageable target)   { consecration(target); }
    @Override public void useSkillThree(Damageable target) { divineShield(); }
}
