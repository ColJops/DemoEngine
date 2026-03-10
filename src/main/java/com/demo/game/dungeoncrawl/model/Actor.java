package com.demo.game.dungeoncrawl.model;

public abstract class Actor {

    protected Cell cell;
    protected int hp;
    protected int attack;
    protected int defense;

    public Actor(Cell cell, int hp, int attack, int defense) {
        this.cell = cell;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;

        cell.setActor(this);
    }

    public Cell getCell() {
        return cell;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isAlive() {
        return hp > 0;
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