package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;

import java.util.Random;

public class Skeleton extends Actor implements Drawable {

    private long lastMove = 0;

    public Skeleton(Cell cell) {
        super(cell, 10, 4, 1);
    }

    @Override
    public String getTileName() {
        return "skeleton";
    }

    //Prosty ruch Skeletona
    private static final int[][] DIRECTIONS = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };

    public void moveRandom() {

        int[][] dirs = {
                {0, -1}, {0, 1}, {-1, 0}, {1, 0}
        };

        int[] dir = dirs[new Random().nextInt(dirs.length)];

        Cell next = cell.getNeighbor(dir[0], dir[1]);

        if (next == null) return;

        Actor target = next.getActor();

        if (target instanceof Player) {
            attack(target);
        } else {
            move(dir[0], dir[1]);
        }
    }

    public void update(long now, GameMap map) {

        if (now - lastMove < 500_000_000) return;

        Player player = map.getPlayer();

        int bestDx = 0;
        int bestDy = 0;
        int bestDistance = Integer.MAX_VALUE;

        int[][] dirs = {
                {0, -1},
                {0, 1},
                {-1, 0},
                {1, 0}
        };

        for (int[] dir : dirs) {

            int nx = cell.getX() + dir[0];
            int ny = cell.getY() + dir[1];

            Cell next = map.getCell(nx, ny);

            if (next == null) continue;
            if (!next.isWalkable()) continue;
            //Blokada większej ilości atakujących
            if (!next.isWalkable() || next.getActor() != null) continue;

            int distance = Math.abs(player.getCell().getX() - nx)
                    + Math.abs(player.getCell().getY() - ny);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestDx = dir[0];
                bestDy = dir[1];
            }
        }

        move(bestDx, bestDy);

        lastMove = now;
    }
}