package com.demo.game.dungeoncrawl.model.map;

import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final int width;
    private final int height;
    private Cell[][] cells;
    private List<Actor> actors = new ArrayList<>(); //Przechowujemy listę aktorów
    private BiomeType biome;

    private Player player;

    public GameMap(int width, int height, CellType defaultCellType) {
        this.width = width;
        this.height = height;
        cells = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(this, x, y, defaultCellType);
            }
        }
    }

    public boolean canMove(int x, int y) {
        return getCell(x, y).isWalkable();
    }

    public Cell getCell(int column, int row) {
        return cells[row][column];
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    // Sekcja "Aktorów"
    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void removeActor(Actor actor) {
        actors.remove(actor);
    }

    public BiomeType getBiome() {
        return biome;
    }

    public void setBiome(BiomeType biome) {
        this.biome = biome;
    }
}