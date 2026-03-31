package com.demo.game.dungeoncrawl.model.enemy;

import com.demo.game.dungeoncrawl.logic.PatrolAggroAI;
import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.map.Cell;

import java.util.List;

public class Scorpion extends Enemy implements Drawable {

    public Scorpion(Cell cell) {
        super(cell, 12, 5, 2);

        // patrol + aggro
        setAI(new PatrolAggroAI(
                List.of(
                        new int[]{cell.getX(), cell.getY()},
                        new int[]{cell.getX() + 2, cell.getY()}
                ),
                5
        ));
    }

    @Override
    public String getTileName() {
        return "scorpion";
    }
}