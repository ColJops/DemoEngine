package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    @TempDir
    Path tempDir;

    @Test
    void saveLoadRestoreRestartLevelAndRestartGameShouldKeepExpectedStateBoundaries() {
        Path savePath = tempDir.resolve("save.json");
        GameSession session = new GameSession();
        session.nextLevel();

        Player savedPlayer = session.getMap().getPlayer();
        savedPlayer.setHp(7);
        savedPlayer.setKills(5);
        savedPlayer.getInventory().add(new Key(KeyType.GOLD));

        SaveManager.save(session.getMap(), session.getCurrentLevel(), savePath);
        SaveData loadedData = SaveManager.load(savePath);

        GameSession restoredSession = new GameSession();
        assertTrue(restoredSession.load(loadedData));

        Player restoredPlayer = restoredSession.getMap().getPlayer();
        assertEquals(SaveData.CURRENT_VERSION, loadedData.version);
        assertEquals(2, restoredSession.getCurrentLevel());
        assertEquals(7, restoredPlayer.getHp());
        assertEquals(5, restoredPlayer.getKills());
        assertTrue(restoredPlayer.hasKey(KeyType.GOLD));

        restoredSession.restartLevel();

        Player restartedLevelPlayer = restoredSession.getMap().getPlayer();
        assertEquals(2, restoredSession.getCurrentLevel());
        assertEquals(20, restartedLevelPlayer.getHp());
        assertEquals(0, restartedLevelPlayer.getKills());
        assertFalse(restartedLevelPlayer.hasKey(KeyType.GOLD));

        restoredSession.restartGame();

        GameMap restartedGameMap = restoredSession.getMap();
        Player restartedGamePlayer = restartedGameMap.getPlayer();
        assertEquals(1, restoredSession.getCurrentLevel());
        assertEquals(30, restartedGameMap.getWidth());
        assertEquals(20, restartedGameMap.getHeight());
        assertEquals(20, restartedGamePlayer.getHp());
        assertEquals(0, restartedGamePlayer.getKills());
    }

    @Test
    void nextLevelShouldKeepStackedBaseStats() {
        GameSession session = new GameSession();
        Player player = session.getMap().getPlayer();
        player.pickUp(new Weapon("Iron Sword", 2));
        player.pickUp(new Weapon("Steel Sword", 3));
        int expectedAttack = player.getAttack();
        int expectedBaseAttack = player.getBaseAttack();

        assertTrue(session.nextLevel());

        Player nextLevelPlayer = session.getMap().getPlayer();
        assertEquals(2, session.getCurrentLevel());
        assertEquals(expectedAttack, nextLevelPlayer.getAttack());
        assertEquals(expectedBaseAttack, nextLevelPlayer.getBaseAttack());
        assertEquals("Iron Sword", nextLevelPlayer.getEquippedWeapon().getName());
    }

    @Test
    void nextLevelShouldReturnFalseAndKeepCurrentMapWhenThereIsNoNextMap() {
        GameSession session = new GameSession();

        while (session.nextLevel()) {
            assertNotNull(session.getMap());
        }

        assertEquals(11, session.getCurrentLevel());
        assertNotNull(session.getMap());
        assertNotNull(session.getEngine());
    }
}
