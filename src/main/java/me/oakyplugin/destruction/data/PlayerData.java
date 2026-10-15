package me.oakyplugin.destruction.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Input;
import org.bukkit.entity.Player;


public class PlayerData {


    private final Player player;

    public double timeSneaking, timeUnsneaked;
    public int consecutiveSneaks;


    public Player getPlayer() {return player; }




    public PlayerData(Player p) {
        player = p;
    }
}
