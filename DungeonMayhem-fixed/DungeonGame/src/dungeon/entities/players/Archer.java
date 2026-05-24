package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Archer extends Player {
    public Archer(String name) {
        super(name, 80, 50, 8, 18, 14, 10);
    }

    private void rainingArrows(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println("Archer uses Raining Arrows!");
        target.takeDamage(getAttackDamage() + 10);
    }

    private void bigArrow(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println("Archer uses Big Arrow Move!");
        target.takeDamage(getAttackDamage() + 25);
    }

    private void rapidShot(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(20);
        System.out.println("Archer uses Rapid Shot!");
        target.takeDamage(getAttackDamage() + 15);
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { rainingArrows(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { bigArrow(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { rapidShot(target); }
}