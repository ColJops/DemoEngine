package com.demo.game.dungeoncrawl.engine;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import com.demo.game.dungeoncrawl.ui.Main;
import com.demo.game.dungeoncrawl.combat.CombatSystem;

public class GameEngine {

    private final GameMap map;

    public GameEngine(GameMap map) {
        this.map = map;
    }

    public void handlePlayerMove(int dx, int dy) {

        Player player = map.getPlayer();
        Cell nextCell = player.getCell().getNeighbor(dx, dy);

        if (nextCell == null) return;

        // 1. ATAK (priorytet)
        Actor enemy = nextCell.getActor();
        if (enemy != null) {

            CombatSystem.attack(player, enemy, map);
            return;

        }

        // 2. DRZWI

        if (nextCell.getType() == CellType.DOOR) {

            KeyType required = nextCell.getRequiredKey();

            if (required == null) {
                Main.log("This door is broken (no key assigned).");
                return;
            }

            if (player.hasKey(required)) {

                player.useKey(required);
                nextCell.setType(CellType.FLOOR);

                if (nextCell.isExit()) {
                    Main.log("You opened the exit!");
                } else {
                    Main.log("You unlocked a door.");
                }

                if (nextCell.isExit()) {
                    if (Main.instance != null) {
                        Main.instance.nextLevel();
                    }
                }

            } else {
                Main.log("You need a " + required + " key.");
            }

            return;
        }

        // 3. RUCH
        player.move(dx, dy);
    }

    public void update(long now) {

        for (Actor actor : new java.util.ArrayList<>(map.getActors())) {
            if (actor instanceof Enemy enemy && enemy.isAlive()) {
                enemy.update(now, map);
            }
        }
    }
}