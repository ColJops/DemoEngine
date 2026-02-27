package com.demo.game.dungeoncrawl.model;

public abstract class Actor {

    protected Cell cell;

    public Actor(Cell cell) {
        this.cell = cell;
        cell.setActor(this);
    }

    public Cell getCell() {
        return cell;
    }

    public void move(int dx, int dy) {
        Cell nextCell = cell.getNeighbor(dx, dy);

        if (nextCell != null
                && nextCell.isWalkable()
                && nextCell.getActor() == null) {

            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        }
    }
}