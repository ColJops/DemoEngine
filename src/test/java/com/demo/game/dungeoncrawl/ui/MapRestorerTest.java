package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.PlayerData;
import com.demo.game.dungeoncrawl.dto.SaveData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapRestorerTest {

    @Test
    void restoreMapShouldReturnNullWhenMapFileDoesNotExist() {
        SaveData data = new SaveData();
        data.version = SaveData.CURRENT_VERSION;
        data.level = 999;
        data.player = new PlayerData();

        assertNull(SaveManager.restoreMap(data));
    }

}