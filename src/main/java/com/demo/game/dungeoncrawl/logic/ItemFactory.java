package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.item.ItemDescriptor;
import com.demo.game.dungeoncrawl.model.item.ItemType;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class ItemFactory {
    private static final Map<ItemType, Function<ItemDescriptor, Item>> ITEM_REGISTRY = createItemRegistry();

    private ItemFactory() {
    }

    public static Item createItem(ItemDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }

        Function<ItemDescriptor, Item> itemFactory = ITEM_REGISTRY.get(descriptor.type());
        return itemFactory != null ? itemFactory.apply(descriptor) : null;
    }

    private static Map<ItemType, Function<ItemDescriptor, Item>> createItemRegistry() {
        Map<ItemType, Function<ItemDescriptor, Item>> itemRegistry = new HashMap<>();
        itemRegistry.put(ItemType.CONSUMABLE, descriptor -> new HealthPotion(null));
        itemRegistry.put(ItemType.KEY, descriptor -> new Key(Objects.requireNonNull(descriptor.keyType())));
        itemRegistry.put(ItemType.WEAPON, descriptor -> new Weapon(
                Objects.requireNonNull(descriptor.name()),
                descriptor.bonus()));
        itemRegistry.put(ItemType.SHIELD, descriptor -> new Shield(
                Objects.requireNonNull(descriptor.name()),
                descriptor.bonus()));
        return itemRegistry;
    }
}
