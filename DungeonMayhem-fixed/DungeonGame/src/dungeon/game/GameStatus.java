package dungeon.game;

import dungeon.entities.Player;
import dungeon.exceptions.GameException;
import dungeon.interfaces.TransactionSystem;
import dungeon.interfaces.LevelSystem;
import dungeon.shop.Shop;

public class GameStatus {
    private Player player;
    private LevelSystem currentFloor;
    private Shop shop;
    private boolean isGameOver;
    private boolean hasWon;

    private static final int GOLD_PER_FLOOR = 50;

    public GameStatus() {
        this.shop = new Shop();
    }

    public void startGame(String playerName, String playerClass) throws dungeon.exceptions.UnknownClassException {
        this.player = dungeon.entities.CharacterFactory.createPlayer(playerClass, playerName);
        this.currentFloor = new CurrentFloor();
        this.isGameOver   = false;
        this.hasWon       = false;
        System.out.println("Game Started! " + playerName + " the "
                + playerClass + " enters the dungeon.");
    }

    public void floorCleared() {
        player.collectReward(GOLD_PER_FLOOR);
        player.levelUp();
        for (Player ally : player.getTeam()) {
            ally.levelUp();
        }
        System.out.println("Floor " + currentFloor.getFloorNumber() + " cleared!");
    }

    public void nextFloor() {
        try {
            currentFloor.incrementFloor();
            System.out.println("Descending to floor " + currentFloor.getFloorNumber() + "...");
        } catch (dungeon.exceptions.MaxFloorReachedException e) {
            System.out.println("Error transitioning floors: " + e.getMessage());
        }
    }

    public void checkVictory() {
        boolean allDead = player.isDead();
        for (Player ally : player.getTeam()) {
            if (!ally.isDead()) {
                allDead = false;
                break;
            }
        }

        if (currentFloor.getFloorNumber() >= 10 && !allDead) {
            System.out.println("Victory! The Dungeon Lord has been defeated!");
            this.hasWon    = true;
            this.isGameOver = true;
        } else if (allDead) {
            System.out.println(player.getName() + " and their party have fallen...");
            this.isGameOver = true;
        }
    }

    public Player getPlayer()             { return player; }
    public LevelSystem getCurrentFloor()  { return currentFloor; }
    public TransactionSystem getShop()                 { return shop; }
    public boolean isGameOver()           { return isGameOver; }
    public boolean hasWon()               { return hasWon; }
    public int getGoldRewardPerFloor()    { return GOLD_PER_FLOOR; }

    public void executePlayerTurn(Player p, dungeon.entities.Enemy currentEnemy, int type) throws dungeon.exceptions.InsufficientManaException {
        switch (type) {
            case 0: p.basicAttack(currentEnemy); break;
            case 1: p.useSkillOne(currentEnemy); break;
            case 2: p.useSkillTwo(currentEnemy); break;
            case 3: p.useSkillThree(currentEnemy); break;
            default: break;
        }
    }

    public void executeEnemyTurn(Player p, dungeon.entities.Enemy currentEnemy) {
        if (p.getRange() > 1 && Math.random() < (p.getRange() * 0.02)) {
            System.out.println(currentEnemy.getName() + " tries to attack, but " + p.getName() + " is out of range!");
        } else {
            System.out.println(currentEnemy.getName() + " attacks " + p.getName() + "!");
            currentEnemy.performAction(p);
        }
    }

    public boolean healParty() {
        if (player.spendGold(50)) {
            player.setHp(player.getMaxHp());
            player.setMana(player.getMaxMana());
            for (Player ally : player.getTeam()) {
                ally.setHp(ally.getMaxHp());
                ally.setMana(ally.getMaxMana());
            }
            System.out.println("✨ The party rested at the tavern and fully recovered!");
            return true;
        }
        return false;
    }

    public boolean recruitAlly(String cls, String allyName) {
        int cost = shop.getPrice(cls);
        if (player.spendGold(cost)) {
            Player ally = shop.buyCharacter(cls, allyName, cost + 1);
            if (ally != null) {
                player.recruitCharacter(ally);
                System.out.println("🤝 " + allyName + " the " + cls + " joined the party!");
                return true;
            }
        }
        return false;
    }
}
