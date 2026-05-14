package com.demo.game.dungeoncrawl.engine;

import com.demo.game.dungeoncrawl.combat.CombatSystem;
import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import com.demo.game.dungeoncrawl.ui.Main;

public class GameEngine {

    private final GameMap map;
    private boolean gameOver = false;

    public GameEngine(GameMap map) {
        this.map = map;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void handlePlayerMove(int dx, int dy) {
        if (gameOver) {
            return;
        }

        Player player = map.getPlayer();

        if (player == null || player.getCell() == null) {
            return;
        }

        Cell nextCell = player.getCell().getNeighbor(dx, dy);

        if (nextCell == null) {
            return;
        }

        // 1. ATAK GRACZA
        Actor target = nextCell.getActor();

        if (target instanceof Enemy enemy) {
            CombatResult result = CombatSystem.attack(player, enemy, map);

            if (result == CombatResult.PLAYER_KILLED) {
                gameOver = true;
                Main.gameOver();
            }

            return;
        }

        // 2. DRZWI
        if (nextCell.getType() == CellType.DOOR) {
            handleDoor(player, nextCell);
            return;
        }

        // 3. RUCH
        player.move(dx, dy);
    }

    private void handleDoor(Player player, Cell doorCell) {
        KeyType required = doorCell.getRequiredKey();

        if (required == null) {
            Main.log("This door is broken (no key assigned).");
            return;
        }

        if (!player.hasKey(required)) {
            Main.log("You need a " + required + " key.");
            return;
        }

        /*
         * Exit door:
         * Najpierw sprawdzamy, czy istnieje kolejny poziom / czy gra ma się zakończyć.
         * Nie zużywamy klucza i nie zmieniamy drzwi na FLOOR przed tą decyzją.
         */
        if (doorCell.isExit()) {
            if (Main.instance != null && !Main.instance.hasNextLevel()) {
                Main.instance.completeGame();
                return;
            }

            player.useKey(required);
            openDoor(doorCell);

            Main.log("You opened the exit!");

            if (Main.instance != null) {
                Main.instance.nextLevel();
            }

            return;
        }

        // Zwykłe drzwi
        player.useKey(required);
        openDoor(doorCell);

        Main.log("You unlocked a door.");
    }

    private void openDoor(Cell doorCell) {
        doorCell.setType(CellType.FLOOR);
        doorCell.setRequiredKey(null);
        doorCell.setExit(false);
    }

    public void update(long now) {
        if (gameOver) {
            return;
        }

        Player player = map.getPlayer();

        if (player == null || !player.isAlive()) {
            triggerGameOver();
            return;
        }

        for (Actor actor : new java.util.ArrayList<>(map.getActors())) {
            if (actor instanceof Enemy enemy && enemy.isAlive()) {
                enemy.update(now, map);

                if (!player.isAlive()) {
                    triggerGameOver();
                    return;
                }
            }
        }
    }

    private void triggerGameOver() {
        if (gameOver) {
            return;
        }

        gameOver = true;
        Main.gameOver();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}