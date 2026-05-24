package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Priest extends Player {
    public Priest(String name) {
        super(name, 110, 100, 12, 15, 11, 3);
    }

    public void holyLight(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println(getName() + " channels Holy Light!");
        target.takeDamage(getAttackDamage() + 12);
    }

    public void smite(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println(getName() + " smites the enemy!");
        target.takeDamage(getAttackDamage() + 18);
    }

    public void divineBlessing() throws dungeon.exceptions.InsufficientManaException {
        useMana(30);
        int healAmount = 25;
        int newHp = Math.min(getHp() + healAmount, getMaxHp());
        setHp(newHp);
        System.out.println(getName() + " casts Divine Blessing and restores " + healAmount + " HP!");
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { holyLight(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { smite(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { divineBlessing(); }
}
