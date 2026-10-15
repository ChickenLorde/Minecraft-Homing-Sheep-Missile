package me.oakyplugin.destruction.utility;

import me.oakyplugin.destruction.Main;
import me.oakyplugin.destruction.items.Usable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Utility {

    private static final Main main = JavaPlugin.getPlugin(Main.class);
    private static final Map<String, Usable> usableById = new HashMap<>();

    public static void registerUsable(Usable usable)
    {
        usableById.put(usable.getId(), usable);
    }

    public static Usable getPlayerUsable(Player p)
    {
        ItemStack item = p.getInventory().getItemInMainHand();

        if(item.getType().isAir() || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        String usableId = meta.getPersistentDataContainer().get(main.getUsableIdKey(), PersistentDataType.STRING);

        if(usableId == null || usableId.isEmpty()) return null;

        return getUsable(usableId);
    }

    public static Usable getUsable(String id)
    {
        return usableById.get(id);
    }
}
