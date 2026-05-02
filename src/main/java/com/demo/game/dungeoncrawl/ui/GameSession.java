package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.engine.GameEngine;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.map.GameMap;

public class GameSession {
    private int currentLevel;
    private GameMap map;
    private GameEngine engine;

    public GameSession() {
        restartGame();
    }

    public void restartGame() {
        currentLevel = 1;
        loadCurrentLevel();
    }

    public void restartLevel() {
        loadCurrentLevel();
    }

    public boolean load(SaveData data) {
        GameMap loadedMap = SaveManager.restoreMap(data);
        if (loadedMap == null) {
            return false;
        }

        currentLevel = data.level;
        setMap(loadedMap);
        return true;
    }

    public void nextLevel() {
        currentLevel++;
        GameMap newMap = MapLoader.loadMap("map" + currentLevel + ".txt");
        carryPlayerState(map.getPlayer(), newMap.getPlayer());
        setMap(newMap);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public GameMap getMap() {
        return map;
    }

    public GameEngine getEngine() {
        return engine;
    }

    private void loadCurrentLevel() {
        setMap(MapLoader.loadMap("map" + currentLevel + ".txt"));
    }

    private void setMap(GameMap map) {
        this.map = map;
        this.engine = new GameEngine(map);
    }

    private void carryPlayerState(Player oldPlayer, Player newPlayer) {
        newPlayer.getInventory().addAll(oldPlayer.getInventory());

        if (oldPlayer.getEquippedWeapon() != null) {
            newPlayer.equipWeapon(oldPlayer.getEquippedWeapon());
        }

        if (oldPlayer.getEquippedShield() != null) {
            newPlayer.equipShield(oldPlayer.getEquippedShield());
        }

        newPlayer.setKills(oldPlayer.getKills());
        newPlayer.setHp(oldPlayer.getHp());
        newPlayer.recalculateStats();
    }
}
