package me.oakyplugin.destruction.utility;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import me.oakyplugin.destruction.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemUtility {

    private static final NamespacedKey rightClickableKey = new NamespacedKey(JavaPlugin.getPlugin(Main.class),"right_clickable");
    private static final NamespacedKey unrepairableKey = new NamespacedKey(JavaPlugin.getPlugin(Main.class),"unrepairable");


@SuppressWarnings("UnstableApiUsage")
    /// Player can right-click with this item
    public static void makeRightClickable(ItemStack item)
    {
        item.editPersistentDataContainer(persistentDataContainer -> {
            persistentDataContainer.set(rightClickableKey, PersistentDataType.BOOLEAN, true);
        }
        );
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(1000000000)
                .hasConsumeParticles(false)
                .build());
        item.unsetData(DataComponentTypes.FOOD);

    }

    /// Cannot be put in anvil
    public static void makeUnrepairable(ItemStack item)
    {
        item.editPersistentDataContainer(persistentDataContainer -> {
                    persistentDataContainer.set(unrepairableKey, PersistentDataType.BOOLEAN, true);
                }
        );
    }

    public static boolean isUnrepairable(ItemStack item)
    {
        if(item.getPersistentDataContainer().get(unrepairableKey, PersistentDataType.BOOLEAN) != null)
        {
            return Boolean.TRUE.equals(item.getPersistentDataContainer().get(unrepairableKey, PersistentDataType.BOOLEAN));
        }
        return false;
    }


}
