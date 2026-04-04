package com.demo.game.dungeoncrawl.dto;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public int x, y;
    public int hp;
    public int kills;
    public String weapon;
    public String shield;
    public List<String> inventory = new ArrayList<>();
}