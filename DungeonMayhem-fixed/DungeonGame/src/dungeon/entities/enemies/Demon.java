package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class Demon extends Enemy {
    // Demon: dark spellcaster — high magic damage but fragile defense
    public Demon() {
        super("Demon", 100, 8, 28, 16);
    }

    // Channels dark energy into a devastating blast
    public void soulDrain(Damageable target) {
        System.out.println(getName() + " casts Soul Drain, siphoning your life force!");
        target.takeDamage(getAttackDamage() + 15);
    }

    // Summons a ring of hellfire around the target
    public void hellfireRing(Damageable target) {
        System.out.println(getName() + " summons a Hellfire Ring!");
        target.takeDamage(getAttackDamage() + 20);
    }

    public String getDescription() {
        return "A dark magic user. High damage spells but low physical defense.";
    }
}
