package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.DoorData;
import com.demo.game.dungeoncrawl.dto.EnemyData;
import com.demo.game.dungeoncrawl.dto.ItemData;
import com.demo.game.dungeoncrawl.dto.PlayerData;
import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.enemy.Wasp;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {

    @Test
    void restoreMapShouldRestorePlayerInventoryEquipmentEnemiesAndOpenDoors() {
        GameMap baseMap = MapLoader.loadMap("map1.txt");
        Cell initialPlayerCell = baseMap.getPlayer().getCell();
        Cell doorCell = findDoor(baseMap);

        SaveData data = new SaveData();
        data.level = 1;
        data.player = new PlayerData();
        data.player.x = initialPlayerCell.getX();
        data.player.y = initialPlayerCell.getY();
        data.player.hp = 13;
        data.player.kills = 4;
        data.player.inventory.add("Key:GOLD");
        data.player.weapon = "Weapon:2:Iron Sword";
        data.player.shield = "Shield:1:Wooden Shield";

        DoorData doorData = new DoorData();
        doorData.x = doorCell.getX();
        doorData.y = doorCell.getY();
        doorData.open = true;
        data.doors.add(doorData);

        EnemyData enemyData = new EnemyData();
        enemyData.type = "Wasp";
        enemyData.x = initialPlayerCell.getX() + 1;
        enemyData.y = initialPlayerCell.getY();
        enemyData.hp = 3;
        data.enemies.add(enemyData);

        GameMap restored = SaveManager.restoreMap(data);
        Player player = restored.getPlayer();

        assertEquals(13, player.getHp());
        assertEquals(4, player.getKills());
        assertTrue(player.hasKey(KeyType.GOLD));
        assertEquals("Iron Sword", player.getEquippedWeapon().getName());
        assertEquals("Wooden Shield", player.getEquippedShield().getName());
        assertEquals(CellType.FLOOR, restored.getCell(doorData.x, doorData.y).getType());
        assertEquals(doorCell.getRequiredKey(), restored.getCell(doorData.x, doorData.y).getRequiredKey());
        assertEquals(doorCell.isExit(), restored.getCell(doorData.x, doorData.y).isExit());
        assertTrue(restored.getCell(enemyData.x, enemyData.y).getActor() instanceof Wasp);
        assertEquals(3, restored.getCell(enemyData.x, enemyData.y).getActor().getHp());
        assertTrue(restored.getActors().stream().anyMatch(actor -> actor instanceof Enemy));
    }

    @Test
    void newSaveDataShouldStartAsLegacyUntilSavedOrMigrated() {
        SaveData data = new SaveData();

        assertEquals(0, data.version);
        assertEquals(2, SaveData.CURRENT_VERSION);
    }

    @Test
    void restoreMapShouldMigrateLegacySaveVersion() {
        GameMap baseMap = MapLoader.loadMap("map1.txt");

        SaveData data = new SaveData();
        data.level = 1;
        data.player = new PlayerData();
        data.player.x = baseMap.getPlayer().getX();
        data.player.y = baseMap.getPlayer().getY();
        data.player.hp = baseMap.getPlayer().getHp();

        SaveManager.restoreMap(data);

        assertEquals(SaveData.CURRENT_VERSION, data.version);
    }

    @Test
    void restoreMapShouldApplyDoorKeyAndExitStateFromSaveData() {
        GameMap baseMap = MapLoader.loadMap("map1.txt");
        Cell playerCell = baseMap.getPlayer().getCell();
        Cell doorCell = findDoor(baseMap);

        SaveData data = new SaveData();
        data.version = SaveData.CURRENT_VERSION;
        data.level = 1;
        data.player = new PlayerData();
        data.player.x = playerCell.getX();
        data.player.y = playerCell.getY();
        data.player.hp = baseMap.getPlayer().getHp();

        DoorData doorData = new DoorData();
        doorData.x = doorCell.getX();
        doorData.y = doorCell.getY();
        doorData.open = false;
        doorData.requiredKey = KeyType.GOLD;
        doorData.isExit = true;
        data.doors.add(doorData);

        GameMap restored = SaveManager.restoreMap(data);
        Cell restoredDoor = restored.getCell(doorData.x, doorData.y);

        assertEquals(CellType.DOOR, restoredDoor.getType());
        assertEquals(KeyType.GOLD, restoredDoor.getRequiredKey());
        assertTrue(restoredDoor.isExit());
    }

    @Test
    void restoreMapShouldInferOldKeySaveDataFromOriginalMapCell() {
        GameMap baseMap = MapLoader.loadMap("map1.txt");
        Cell keyCell = findKey(baseMap);
        Key originalKey = (Key) keyCell.getItem();

        SaveData data = new SaveData();
        data.level = 1;
        data.player = new PlayerData();
        data.player.x = baseMap.getPlayer().getX();
        data.player.y = baseMap.getPlayer().getY();
        data.player.hp = baseMap.getPlayer().getHp();

        ItemData oldItemData = new ItemData();
        oldItemData.type = "Key";
        oldItemData.x = keyCell.getX();
        oldItemData.y = keyCell.getY();
        data.items.add(oldItemData);

        GameMap restored = SaveManager.restoreMap(data);

        assertInstanceOf(Key.class, restored.getCell(keyCell.getX(), keyCell.getY()).getItem());
        Key restoredKey = (Key) restored.getCell(keyCell.getX(), keyCell.getY()).getItem();
        assertEquals(originalKey.getKeyType(), restoredKey.getKeyType());
    }

    private Cell findDoor(GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);
                if (cell.getType() == CellType.DOOR) {
                    return cell;
                }
            }
        }

        fail("Expected map to contain a door");
        return null;
    }

    private Cell findKey(GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);
                if (cell.getItem() instanceof Key) {
                    return cell;
                }
            }
        }

        fail("Expected map to contain a key");
        return null;
    }
}
