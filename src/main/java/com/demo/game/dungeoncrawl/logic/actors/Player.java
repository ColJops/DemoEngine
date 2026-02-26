package com.demo.game.dungeoncrawl.logic.actors;

import com.demo.game.dungeoncrawl.logic.Cell;

public class Player extends Actor {
    public Player(Cell cell) {
        super(cell);
    }

    public String getTileName() {
        return "player";
    }
}