package com.demo.game.dungeoncrawl.model.enemy;

import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatTest {

    @Test
    void batShouldUseCombatSystemWhenAttackingPlayer() {
        GameMap map = new GameMap(3, 3, CellType.FLOOR);
        Player player = new Player(map.getCell(1, 1));
        map.setPlayer(player);
        map.addActor(player);

        Bat bat = new Bat(map.getCell(1, 2));
        map.addActor(bat);

        player.setHp(1);

        bat.update(System.nanoTime() + 1_000_000_000L, map);

        assertFalse(player.isAlive());
    }

}