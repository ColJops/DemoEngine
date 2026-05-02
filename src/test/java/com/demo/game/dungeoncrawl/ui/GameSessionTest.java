package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
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
}
