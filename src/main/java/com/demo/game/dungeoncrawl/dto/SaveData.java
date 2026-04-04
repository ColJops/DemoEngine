package com.demo.game.dungeoncrawl.dto;

import java.util.*;

public class SaveData {
    public int level;
    public PlayerData player;
    public List<EnemyData> enemies = new ArrayList<>();
    public List<DoorData> doors = new ArrayList<>();
    public List<ItemData> items = new ArrayList<>();
}

