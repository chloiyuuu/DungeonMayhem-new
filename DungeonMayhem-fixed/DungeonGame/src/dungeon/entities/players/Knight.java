package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Knight extends Player {
    public Knight(String name) {
        super(name, 150, 50, 20, 25, 10, 1);
    }

    public void spinAttack(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(10);
        System.out.println("Knight uses Spin Attack!");
        target.takeDamage(getAttackDamage() + 10);
    }

    public void splashAttack(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println("Knight uses Splash Attack!");
        target.takeDamage(getAttackDamage() + 15);
    }

    public void bigSlash(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println("Knight uses Big Slash!");
        target.takeDamage(getAttackDamage() + 30);
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { spinAttack(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { splashAttack(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { bigSlash(target); }
}