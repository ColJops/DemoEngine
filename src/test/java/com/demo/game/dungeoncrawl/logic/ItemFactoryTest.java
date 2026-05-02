package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.ItemDescriptor;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest {

    @Test
    void itemShouldBeCreatedFromTypedDescriptor() {
        Item weapon = ItemFactory.createItem(ItemDescriptor.weapon(2, "Iron Sword"));
        Item shield = ItemFactory.createItem(ItemDescriptor.shield(1, "Wooden Shield"));
        Item key = ItemFactory.createItem(ItemDescriptor.key(KeyType.BLUE));

        assertInstanceOf(Weapon.class, weapon);
        assertEquals("Iron Sword", weapon.getName());
        assertEquals(2, ((Weapon) weapon).getAttackBonus());

        assertInstanceOf(Shield.class, shield);
        assertEquals("Wooden Shield", shield.getName());
        assertEquals(1, ((Shield) shield).getDefenseBonus());

        assertInstanceOf(Key.class, key);
        assertEquals(KeyType.BLUE, ((Key) key).getKeyType());
    }

    @Test
    void keyShouldRoundTripWithColor() {
        Key key = new Key(KeyType.RED);

        Item restored = ItemFactory.createItem(ItemParser.parse(ItemParser.itemId(key)));

        assertInstanceOf(Key.class, restored);
        assertEquals(KeyType.RED, ((Key) restored).getKeyType());
    }

    @Test
    void equipmentShouldRoundTripWithStatsAndName() {
        Weapon weapon = new Weapon("Iron Sword", 2);
        Shield shield = new Shield("Wooden Shield", 1);

        Item restoredWeapon = ItemFactory.createItem(ItemParser.parse(ItemParser.itemId(weapon)));
        Item restoredShield = ItemFactory.createItem(ItemParser.parse(ItemParser.itemId(shield)));

        assertInstanceOf(Weapon.class, restoredWeapon);
        assertEquals("Iron Sword", restoredWeapon.getName());
        assertEquals(2, ((Weapon) restoredWeapon).getAttackBonus());

        assertInstanceOf(Shield.class, restoredShield);
        assertEquals("Wooden Shield", restoredShield.getName());
        assertEquals(1, ((Shield) restoredShield).getDefenseBonus());
    }
}
