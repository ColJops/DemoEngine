package com.demo.game.dungeoncrawl.combat;

import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatSystemTest {

    @Test
    void killingEnemyShouldRemoveItFromMapAndAwardKillToPlayer() {
        GameMap map = createMapWithPlayer();
        Player player = map.getPlayer();
        Skeleton skeleton = new Skeleton(map.getCell(1, 2));
        skeleton.setHp(1);
        map.addActor(skeleton);

        CombatSystem.attack(player, skeleton, map);

        assertFalse(skeleton.isAlive());
        assertNull(map.getCell(1, 2).getActor());
        assertFalse(map.getActors().contains(skeleton));
        assertEquals(1, player.getKills());
    }

    @Test
    void killingPlayerShouldNotRemovePlayerFromMapModel() {
        GameMap map = createMapWithPlayer();
        Player player = map.getPlayer();
        Skeleton skeleton = new Skeleton(map.getCell(1, 2));
        player.setHp(1);
        map.addActor(skeleton);

        CombatSystem.attack(skeleton, player, map);

        assertFalse(player.isAlive());
        assertSame(player, map.getPlayer());
        assertSame(player, map.getCell(1, 1).getActor());
        assertTrue(map.getActors().contains(player));
    }

    private GameMap createMapWithPlayer() {
        GameMap map = new GameMap(3, 3, CellType.FLOOR);
        Player player = new Player(map.getCell(1, 1));
        map.setPlayer(player);
        map.addActor(player);
        return map;
    }
}
