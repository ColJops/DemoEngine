package com.demo.game.dungeoncrawl.engine;

import com.demo.game.dungeoncrawl.model.GameMap;
import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Skeleton;

public class GameEngine {

    private final GameMap map;

    public GameEngine(GameMap map) {
        this.map = map;
    }

    public void handlePlayerMove(int dx, int dy) {
        map.getPlayer().move(dx, dy);
        nextTurn();
    }

    private void nextTurn() {
        for (Actor actor : map.getActors()) {
            if (actor instanceof Skeleton) {
                ((Skeleton) actor).moveRandom();
            }
        }
    }
}