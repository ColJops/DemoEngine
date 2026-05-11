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

    public boolean nextLevel() {
        int nextLevel = currentLevel + 1;

        GameMap newMap = MapLoader.loadMap("map" + nextLevel + ".txt");

        if (newMap == null) {
            Main.log("No next level found.");
            return false;
        }

        carryPlayerState(map.getPlayer(), newMap.getPlayer());
        currentLevel = nextLevel;
        setMap(newMap);

        return true;
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

    private boolean loadCurrentLevel() {
        GameMap loadedMap = MapLoader.loadMap("map" + currentLevel + ".txt");

        if (loadedMap == null) {
            Main.log("Could not load map" + currentLevel + ".txt");
            return false;
        }

        setMap(loadedMap);
        return true;
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
        newPlayer.setBaseAttack(oldPlayer.getBaseAttack());
        newPlayer.setBaseDefense(oldPlayer.getBaseDefense());
        newPlayer.recalculateStats();
    }
}
