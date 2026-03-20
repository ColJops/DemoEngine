package com.demo.game.dungeoncrawl.engine;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.ui.Main;

import java.util.ArrayList;

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

            player.attack(enemy);

            if (!enemy.isAlive()) {
                enemy.getCell().setActor(null);
                map.removeActor(enemy);
                Main.log("You killed a Skeleton!");

                if (enemy instanceof Skeleton) {
                    player.addKill();
                }
            }

            return;
        }

        // 2. DRZWI
        // 2. DRZWI (tylko jeśli to drzwi!)
        if (nextCell.getType() == CellType.DOOR) {

            KeyType required = nextCell.getRequiredKey();

            if (required == null) {
                Main.log("This door is broken (no key assigned).");
                return;
            }

            if (player.hasKey(required)) {

                player.useKey(required);
                nextCell.setType(CellType.FLOOR);

                Main.log("You opened the " + required + " door!");

                if (Main.instance != null) {
                    Main.instance.nextLevel();
                }

            } else {
                Main.log("You need a " + required + " key.");
            }

            return;
        }

        // 3. RUCH
        player.move(dx, dy);
    }

    private void nextTurn() {
        for (Actor actor : new ArrayList<>(map.getActors())) {
            if (actor instanceof Skeleton && actor.isAlive()) {
                ((Skeleton) actor).moveRandom();
            }
        }
    }

    public void update(long now) {

        for (Actor actor : map.getActors()) {

            if (actor instanceof Skeleton) {
                ((Skeleton) actor).update(now, map);
            }

        }
    }
}