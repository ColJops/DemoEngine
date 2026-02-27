package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;

import java.util.Random;

public class Skeleton extends Actor implements Drawable {
    public Skeleton(Cell cell) {
        super(cell);
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
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};
        int[] dir = dirs[new Random().nextInt(dirs.length)];
        move(dir[0], dir[1]);
    }
}