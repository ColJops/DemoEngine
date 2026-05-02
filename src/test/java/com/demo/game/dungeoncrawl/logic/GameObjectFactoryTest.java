package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameObjectFactoryTest {

    @Test
    void enemyShouldBeCreatedFromRegistry() {
        GameMap map = new GameMap(1, 1, CellType.FLOOR);

        Enemy enemy = GameObjectFactory.createEnemy("Skeleton", map.getCell(0, 0));

        assertInstanceOf(Skeleton.class, enemy);
        assertSame(enemy, map.getCell(0, 0).getActor());
    }
}
