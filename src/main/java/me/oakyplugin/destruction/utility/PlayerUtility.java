package me.oakyplugin.destruction.utility;

import me.oakyplugin.destruction.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtility {
    private static Map<UUID, PlayerData> playerData = new HashMap<>();
    private static final HashMap<Player, Long> rightClickTicks = new HashMap<>();

    public static PlayerData getPlayerData(Player player)
    {
        if(!playerData.containsKey(player.getUniqueId()))
        {
            PlayerData data = new PlayerData(player);
            playerData.put(player.getUniqueId(), data);
            return data;
        }
        return playerData.get(player.getUniqueId());
    }

    public static void setPlayerData(Player player, PlayerData data)
    {
        if(data == null) playerData.remove(player.getUniqueId());
        else playerData.put(player.getUniqueId(), data);
    }

    public static String getFolderPath(Player player)
    {
        return Bukkit.getPluginsFolder().getAbsolutePath() + "/oaky_destruction_plugin/playerData/"+player.getUniqueId();
    }

    public static HashMap<Player, Long> getRightClickTicks()
    {
        return rightClickTicks;
    }

    public static Long getRightClickTick(Player p)
    {
        if(!rightClickTicks.containsKey(p))
        {
            rightClickTicks.put(p, 0L);
        }
        return rightClickTicks.get(p);
    }
}
