package com.demo.game.dungeoncrawl.model.enemy;

import com.demo.game.dungeoncrawl.logic.ChaseAI;
import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.map.Cell;

public class Spider extends Enemy implements Drawable {

    public Spider(Cell cell) {
        super(cell, 6, 3, 0); // HP, ATK, DEF
        setAI(new ChaseAI()); // agresywny
    }

    @Override
    public String getTileName() {
        return "spider";
    }
}