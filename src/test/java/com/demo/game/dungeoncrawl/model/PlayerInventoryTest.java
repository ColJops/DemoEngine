package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerInventoryTest {

    @Test
    void pickingUpKeyShouldAddItToInventory() {
        Player player = createPlayer();
        Key key = new Key(KeyType.BLUE);

        player.pickUp(key);

        assertTrue(player.hasKey(KeyType.BLUE));
        assertTrue(player.getInventory().contains(key));
    }

    @Test
    void useKeyShouldRemoveMatchingKeyOnly() {
        Player player = createPlayer();
        Key blueKey = new Key(KeyType.BLUE);
        Key redKey = new Key(KeyType.RED);
        player.pickUp(blueKey);
        player.pickUp(redKey);

        player.useKey(KeyType.BLUE);

        assertFalse(player.hasKey(KeyType.BLUE));
        assertTrue(player.hasKey(KeyType.RED));
        assertFalse(player.getInventory().contains(blueKey));
        assertTrue(player.getInventory().contains(redKey));
    }

    @Test
    void healthPotionShouldHealPlayerWithoutExceedingActorDamageRules() {
        Player player = createPlayer();
        player.takeDamage(7);

        new HealthPotion(player.getCell()).use(player);

        assertEquals(18, player.getHp());
    }

    @Test
    void firstWeaponPickupShouldEquipWeaponAndIncreaseAttackStat() {
        Player player = createPlayer();
        Weapon sword = new Weapon("Iron Sword", 2);

        player.pickUp(sword);

        assertSame(sword, player.getEquippedWeapon());
        assertFalse(player.getInventory().contains(sword));
        assertEquals(4, player.getAttack());
    }

    @Test
    void firstShieldPickupShouldEquipShieldAndIncreaseDefenseStat() {
        Player player = createPlayer();
        Shield shield = new Shield("Wooden Shield", 1);

        player.pickUp(shield);

        assertSame(shield, player.getEquippedShield());
        assertFalse(player.getInventory().contains(shield));
        assertEquals(2, player.getDefense());
    }

    @Test
    void secondWeaponPickupShouldStackAttackBonus() {
        Player player = createPlayer();
        player.pickUp(new Weapon("Iron Sword", 2));

        player.pickUp(new Weapon("Steel Sword", 3));

        assertEquals(7, player.getAttack());
        assertEquals("Iron Sword", player.getEquippedWeapon().getName());
    }

    private Player createPlayer() {
        GameMap map = new GameMap(3, 3, CellType.FLOOR);
        return new Player(map.getCell(1, 1));
    }
}
