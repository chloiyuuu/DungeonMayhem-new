package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Priest extends Player {
    // Priest: supportive healer — low attack but can restore HP mid-battle
    public Priest(String name) {
        super(name, 110, 12, 15, 11, 3);
    }

    public void holyLight(Damageable target) {
        System.out.println(getName() + " channels Holy Light!");
        target.takeDamage(getAttackDamage() + 12);
    }

    public void smite(Damageable target) {
        System.out.println(getName() + " smites the enemy!");
        target.takeDamage(getAttackDamage() + 18);
    }

    // Heals self instead of damaging target (special mechanic)
    public void divineBlessing() {
        int healAmount = 25;
        int newHp = Math.min(getHp() + healAmount, getMaxHp());
        setHp(newHp);
        System.out.println(getName() + " casts Divine Blessing and restores " + healAmount + " HP!");
    }

    @Override public void useSkillOne(Damageable target)   { holyLight(target); }
    @Override public void useSkillTwo(Damageable target)   { smite(target); }
    @Override public void useSkillThree(Damageable target) { divineBlessing(); } // heals self
}
