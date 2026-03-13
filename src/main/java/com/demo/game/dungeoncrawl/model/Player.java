package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;

public class Player extends Actor implements Drawable {
    public Player(Cell cell) {
        super(cell, 20, 6, 2);
    }

    @Override
    public String getTileName() {
        return "player";
    }

    public void damage(int amount) {

        health -= amount;

        if (health <= 0) {
            System.out.println("Player died");
        }
    }

}