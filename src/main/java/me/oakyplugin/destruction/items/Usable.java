package me.oakyplugin.destruction.items;

import me.oakyplugin.destruction.Main;
import me.oakyplugin.destruction.utility.ItemUtility;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public abstract class Usable {

    protected final Main main = JavaPlugin.getPlugin(Main.class);

    private final String id;


    protected Usable(String id)
    {
        this.id = id.toLowerCase(Locale.ROOT);
    }

    protected void itemMeta(ItemStack item)
    {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(main.getUsableIdKey(), PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        ItemUtility.makeRightClickable(item);
    }


    public String getId()
    {
        return id;
    }

    public abstract ItemStack getItem();
    public abstract boolean use(Entity user, Location location);
}
