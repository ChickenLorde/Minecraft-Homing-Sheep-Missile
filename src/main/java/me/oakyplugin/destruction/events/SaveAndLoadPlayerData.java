package me.oakyplugin.destruction.events;

import me.oakyplugin.destruction.data.PlayerData;
import me.oakyplugin.destruction.utility.PlayerUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;

public class SaveAndLoadPlayerData implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event)
    {
        Bukkit.getServer().broadcast(Component.text("A player joined the server!"));
        loadPlayerData(event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event)
    {
        Bukkit.getServer().broadcast(Component.text("A player left the server!"));
        savePlayerData(event.getPlayer());

    }

    private void loadPlayerData(Player p)
    {
        PlayerData data = new PlayerData(p);
        File f = new File(PlayerUtility.getFolderPath(p) + "/stats.yml");

        if(f.exists())
        {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        }else {
        }

        PlayerUtility.setPlayerData(p.getPlayer(), data);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void savePlayerData(Player p) {
        PlayerData data = PlayerUtility.getPlayerData(p);
        File f = new File(PlayerUtility.getFolderPath(p) + "/stats.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);



        try{ cfg.save(f); } catch(IOException e) { Bukkit.getServer().getLogger().info("Could not save player data!"); }

        PlayerUtility.setPlayerData(p.getPlayer(), null);
    }
}
