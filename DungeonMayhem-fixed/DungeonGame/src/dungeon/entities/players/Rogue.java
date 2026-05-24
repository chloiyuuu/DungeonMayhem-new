package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Rogue extends Player {
    public Rogue(String name) {
        super(name, 90, 70, 8, 22, 25, 2);
    }

    public void backstab(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println("Rogue uses Backstab from stealth!");
        target.takeDamage(getAttackDamage() + 30);
    }

    public void poisonDart(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(10);
        System.out.println("Rogue shoots a Poison Dart!");
        target.takeDamage(getAttackDamage() + 10);
    }

    public void shadowClone(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println("Rogue summons a Shadow Clone to attack!");
        target.takeDamage(getAttackDamage() + 20);
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { backstab(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { poisonDart(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { shadowClone(target); }
}