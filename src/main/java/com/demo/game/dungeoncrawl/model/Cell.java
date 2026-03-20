package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;

public class Cell implements Drawable {
    private CellType type;
    private Actor actor;
    private GameMap gameMap;
    private int x, y;
    private Item item;
    private KeyType requiredKey;
    private boolean isExit;

    Cell(GameMap gameMap, int x, int y, CellType type) {
        this.gameMap = gameMap;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public boolean isWalkable() {
        return type == CellType.FLOOR;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public Cell getNeighbor(int dx, int dy) {

        int newX = x + dx;
        int newY = y + dy;

        if (newX < 0 || newY < 0 ||
                newX >= gameMap.getWidth() ||
                newY >= gameMap.getHeight()) {
            return null;
        }

        return gameMap.getCell(newX, newY);
    }

    @Override
    public String getTileName() {

        if (type == CellType.DOOR) {

            // 🔥 brak klucza = fallback (debug)
            if (requiredKey == null) {
                return "door";
            }

            return switch (requiredKey) {
                case BLUE -> "door_blue";
                case RED -> "door_red";
                case GOLD -> "door_gold";
            };
        }

        return type.getTileName();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public KeyType getRequiredKey() {
        return requiredKey;
    }

    public void setRequiredKey(KeyType key) {
        this.requiredKey = key;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }
}