package com.demo.game.dungeoncrawl.model.enemy;

import com.demo.game.dungeoncrawl.logic.RandomMoveAI;
import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.map.Cell;

public class Wasp extends Enemy implements Drawable {

    public Wasp(Cell cell) {
        super(cell, 5, 2, 0);
        setAI(new RandomMoveAI()); // losowa, nerwowa
    }

    @Override
    public String getTileName() {
        return "wasp";
    }
}
