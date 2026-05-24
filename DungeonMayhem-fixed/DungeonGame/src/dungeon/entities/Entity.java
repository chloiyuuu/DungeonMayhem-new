package dungeon.entities;

import dungeon.interfaces.Damageable;

public abstract class Entity implements Damageable {
    private String name;
    private int hp;
    private int maxHp;
    private int mana;
    private int maxMana;
    private int defense;
    private int attackDamage;
    private int speed;

    public Entity(String name, int maxHp, int maxMana, int defense, int attackDamage, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.defense = defense;
        this.attackDamage = attackDamage;
        this.speed = speed;
    }

    public void basicAttack(Damageable target) {
        System.out.println(this.name + " performs a basic attack!");
        target.takeDamage(this.attackDamage);
        if (this.maxMana > 0) {
            int restore = 10;
            setMana(this.mana + restore);
            System.out.println(this.name + " restored " + restore + " MP!");
        }
    }

    @Override
    public void takeDamage(int amount) {
        // Implement Speed check for Dodge (e.g. Speed * 1% chance, max 50%)
        int dodgeChance = Math.min(this.speed * 1, 50);
        int rand = (int)(Math.random() * 100);
        if (rand < dodgeChance) {
            System.out.println(this.name + " dodged the attack! (Speed " + this.speed + ")");
            return;
        }

        int actualDamage = Math.max(amount - this.defense, 1);
        setHp(this.hp - actualDamage);
        System.out.println(this.name + " took " + actualDamage + " damage. Remaining HP: " + this.hp);
    }

    @Override
    public boolean isDead() {
        return this.hp <= 0;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.min(Math.max(0, hp), maxHp); }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = Math.min(Math.max(0, mana), maxMana); }
    public int getMaxMana() { return maxMana; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }

    public void useMana(int amount) throws dungeon.exceptions.InsufficientManaException {
        if (this.mana >= amount) {
            this.mana -= amount;
            return;
        }
        throw new dungeon.exceptions.InsufficientManaException(this.name + " doesn't have enough mana!");
    }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }
    public int getAttackDamage() { return attackDamage; }
    public void setAttackDamage(int attackDamage) { this.attackDamage = attackDamage; }
    public int getSpeed() { return speed; }
}
