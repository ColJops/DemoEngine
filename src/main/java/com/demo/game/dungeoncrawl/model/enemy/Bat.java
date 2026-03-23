package com.demo.game.dungeoncrawl.model.enemy;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.util.Random;

public class Bat extends Enemy implements Drawable {

    private long lastMove = 0;

    public Bat(Cell cell) {
        super(cell, 6, 2, 0); // szybki, słaby
    }

    @Override
    public String getTileName() {
        return "bat";
    }

    @Override
    public void update(long now, GameMap map) {

        if (now - lastMove < 300_000_000) return; // szybszy niż skeleton
        lastMove = now;

        int[][] dirs = {
                {0,-1},{0,1},{-1,0},{1,0}
        };

        int[] d = dirs[new Random().nextInt(dirs.length)];

        Cell next = getCell().getNeighbor(d[0], d[1]);

        if (next == null) return;

        Actor target = next.getActor();

        if (target instanceof Player) {
            attack(target);
        } else {
            move(d[0], d[1]);
        }
    }
}