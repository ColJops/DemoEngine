package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;

public enum CellType  implements Drawable {
    EMPTY("empty"),
    FLOOR("floor"),
    WALL("wall"),
    DOOR("door");

    private final String tileName;

    CellType(String tileName) {
        this.tileName = tileName;
    }

    public String getTileName() {
        return tileName;
    }

}