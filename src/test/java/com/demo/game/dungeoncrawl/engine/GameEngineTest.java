package com.demo.game.dungeoncrawl.engine;

import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @Test
    void playerShouldAttackEnemyInsteadOfMovingIntoEnemyCell() {
        GameMap map = createMapWithPlayer();
        Player player = map.getPlayer();
        Cell enemyCell = map.getCell(1, 2);
        Skeleton skeleton = new Skeleton(enemyCell);
        map.addActor(skeleton);
        int initialEnemyHp = skeleton.getHp();

        new GameEngine(map).handlePlayerMove(0, 1);

        assertSame(map.getCell(1, 1), player.getCell());
        assertSame(skeleton, enemyCell.getActor());
        assertTrue(skeleton.getHp() < initialEnemyHp);
    }

    @Test
    void deadEnemyShouldBeRemovedAfterPlayerAttack() {
        GameMap map = createMapWithPlayer();
        Player player = map.getPlayer();
        Skeleton skeleton = new Skeleton(map.getCell(1, 2));
        skeleton.setHp(1);
        map.addActor(skeleton);

        new GameEngine(map).handlePlayerMove(0, 1);

        assertFalse(skeleton.isAlive());
        assertNull(map.getCell(1, 2).getActor());
        assertFalse(map.getActors().contains(skeleton));
        assertEquals(1, player.getKills());
    }

    @Test
    void lockedDoorShouldStayClosedWithoutRequiredKey() {
        GameMap map = createMapWithPlayer();
        Cell door = map.getCell(1, 2);
        door.setType(CellType.DOOR);
        door.setRequiredKey(KeyType.RED);

        new GameEngine(map).handlePlayerMove(0, 1);

        assertEquals(CellType.DOOR, door.getType());
        assertSame(map.getCell(1, 1), map.getPlayer().getCell());
    }

    @Test
    void lockedDoorShouldOpenAndConsumeRequiredKey() {
        GameMap map = createMapWithPlayer();
        Player player = map.getPlayer();
        player.pickUp(new Key(KeyType.RED));
        Cell door = map.getCell(1, 2);
        door.setType(CellType.DOOR);
        door.setRequiredKey(KeyType.RED);

        new GameEngine(map).handlePlayerMove(0, 1);

        assertEquals(CellType.FLOOR, door.getType());
        assertFalse(player.hasKey(KeyType.RED));
        assertSame(map.getCell(1, 1), player.getCell());
    }

    private GameMap createMapWithPlayer() {
        GameMap map = new GameMap(3, 3, CellType.FLOOR);
        Player player = new Player(map.getCell(1, 1));
        map.setPlayer(player);
        map.addActor(player);
        return map;
    }
}
