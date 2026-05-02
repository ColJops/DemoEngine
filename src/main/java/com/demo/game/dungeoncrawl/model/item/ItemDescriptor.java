package com.demo.game.dungeoncrawl.model.item;

public record ItemDescriptor(ItemType type, KeyType keyType, int bonus, String name) {

    public static ItemDescriptor healthPotion() {
        return new ItemDescriptor(ItemType.CONSUMABLE, null, 0, "Health Potion");
    }

    public static ItemDescriptor key(KeyType keyType) {
        return new ItemDescriptor(ItemType.KEY, keyType, 0, null);
    }

    public static ItemDescriptor weapon(int attackBonus, String name) {
        return new ItemDescriptor(ItemType.WEAPON, null, attackBonus, name);
    }

    public static ItemDescriptor shield(int defenseBonus, String name) {
        return new ItemDescriptor(ItemType.SHIELD, null, defenseBonus, name);
    }
}
