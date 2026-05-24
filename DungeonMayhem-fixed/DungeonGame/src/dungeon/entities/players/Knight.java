package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Knight extends Player {
    public Knight(String name) {
        super(name, 110, 40, 16, 18, 8, 1);
    }

    private void spinAttack(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(10);
        System.out.println("Knight uses Spin Attack!");
        target.takeDamage(getAttackDamage() + 10);
    }

    private void splashAttack(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println("Knight uses Splash Attack!");
        target.takeDamage(getAttackDamage() + 15);
    }

    private void bigSlash(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println("Knight uses Big Slash!");
        target.takeDamage(getAttackDamage() + 30);
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { spinAttack(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { splashAttack(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { bigSlash(target); }
}