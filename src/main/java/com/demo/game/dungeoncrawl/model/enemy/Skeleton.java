package com.demo.game.dungeoncrawl.model.enemy;

import com.demo.game.dungeoncrawl.logic.ChaseAI;
import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.Enemy;

public class Skeleton extends Enemy implements Drawable {


    public Skeleton(Cell cell) {
        super(cell, 10, 4, 1);
        setAI(new ChaseAI());
    }

    @Override
    public String getTileName() {
        return "skeleton";
    }



}