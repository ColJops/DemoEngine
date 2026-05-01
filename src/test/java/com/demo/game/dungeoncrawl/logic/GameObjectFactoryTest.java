package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameObjectFactoryTest {

    @Test
    void keyShouldRoundTripWithColor() {
        Key key = new Key(KeyType.RED);

        Item restored = GameObjectFactory.createItem(GameObjectFactory.itemId(key));

        assertInstanceOf(Key.class, restored);
        assertEquals(KeyType.RED, ((Key) restored).getKeyType());
    }

    @Test
    void equipmentShouldRoundTripWithStatsAndName() {
        Weapon weapon = new Weapon("Iron Sword", 2);
        Shield shield = new Shield("Wooden Shield", 1);

        Item restoredWeapon = GameObjectFactory.createItem(GameObjectFactory.itemId(weapon));
        Item restoredShield = GameObjectFactory.createItem(GameObjectFactory.itemId(shield));

        assertInstanceOf(Weapon.class, restoredWeapon);
        assertEquals("Iron Sword", restoredWeapon.getName());
        assertEquals(2, ((Weapon) restoredWeapon).getAttackBonus());

        assertInstanceOf(Shield.class, restoredShield);
        assertEquals("Wooden Shield", restoredShield.getName());
        assertEquals(1, ((Shield) restoredShield).getDefenseBonus());
    }
}
