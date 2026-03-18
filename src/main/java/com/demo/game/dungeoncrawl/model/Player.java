package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.ui.Main;

public class Player extends Actor implements Drawable {

    private int kills = 0;

    public Player(Cell cell) {
        super(cell, 20, 6, 2);
    }

    @Override
    public String getTileName() {
        return "player";
    }

    public void addKill() {
        kills++;
    }

    public int getKills() {
        return kills;
    }

}