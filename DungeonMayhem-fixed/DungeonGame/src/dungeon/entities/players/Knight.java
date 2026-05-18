package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Knight extends Player {
    public Knight(String name) {
        super(name, 150, 20, 25, 10, 1);
    }

    public void spinAttack(Damageable target) {
        System.out.println("Knight uses Spin Attack!");
        target.takeDamage(getAttackDamage() + 10);
    }

    public void splashAttack(Damageable target) {
        System.out.println("Knight uses Splash Attack!");
        target.takeDamage(getAttackDamage() + 15);
    }

    public void bigSlash(Damageable target) {
        System.out.println("Knight uses Big Slash!");
        target.takeDamage(getAttackDamage() + 30);
    }

    @Override public void useSkillOne(Damageable target) { spinAttack(target); }
    @Override public void useSkillTwo(Damageable target) { splashAttack(target); }
    @Override public void useSkillThree(Damageable target) { bigSlash(target); }
}