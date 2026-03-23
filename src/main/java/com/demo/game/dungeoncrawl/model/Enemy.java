package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.EnemyAI;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;

public abstract class Enemy extends Actor {

    private EnemyAI ai;

    public Enemy(Cell cell, int hp, int attack, int defense) {
        super(cell, hp, attack, defense);
    }

    public void setAI(EnemyAI ai) {
        this.ai = ai;
    }

    public void update(long now, GameMap map) {
        if (ai != null) {
            ai.update(this, now, map);
        }
    }
}