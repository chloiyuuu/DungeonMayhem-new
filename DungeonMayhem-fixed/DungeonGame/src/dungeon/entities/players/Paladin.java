package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Paladin extends Player {
    public Paladin(String name) {
        super(name, 140, 80, 22, 20, 8, 1);
    }

    private void holyStrike(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println(getName() + " delivers a Holy Strike!");
        target.takeDamage(getAttackDamage() + 15);
    }

    private void consecration(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println(getName() + " consecrates the ground — Consecration!");
        target.takeDamage(getAttackDamage() + 22);
    }

    private void divineShield() throws dungeon.exceptions.InsufficientManaException {
        useMana(20);
        for (Player ally : getWholeParty()) {
            ally.setHp(Math.min(ally.getHp() + 40, ally.getMaxHp()));
        }
        System.out.println(getName() + " raises Divine Shield! (+40 HP to party)");
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { holyStrike(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { consecration(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { divineShield(); }
}
