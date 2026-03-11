package com.demo.game.dungeoncrawl.engine;

import com.demo.game.dungeoncrawl.model.*;

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

        Actor enemy = nextCell.getActor();

        if (enemy != null) {

            player.attack(enemy);

            if (!enemy.isAlive()) {
                enemy.getCell().setActor(null);
                map.removeActor(enemy);
            }

        } else {
            player.move(dx, dy);
        }
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