package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.util.Random;

public class RandomMoveAI implements EnemyAI {

    private static final int[][] DIRS = {
            {0,-1},{0,1},{-1,0},{1,0}
    };

    private long lastMove = 0;

    @Override
    public void update(Enemy enemy, long now, GameMap map) {

        if (now - lastMove < 300_000_000) return;
        lastMove = now;

        int[] d = DIRS[new Random().nextInt(DIRS.length)];

        Cell next = enemy.getCell().getNeighbor(d[0], d[1]);
        if (next == null) return;

        Actor target = next.getActor();

        if (target instanceof Player) {
            enemy.attack(target);
        } else {
            enemy.move(d[0], d[1]);
        }
    }
}