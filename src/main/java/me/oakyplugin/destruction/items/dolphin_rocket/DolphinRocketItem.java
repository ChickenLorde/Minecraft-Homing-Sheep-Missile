package me.oakyplugin.destruction.items.dolphin_rocket;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.Usable;
import me.oakyplugin.destruction.items.pig_cannon.PigBazookaEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class DolphinRocketItem extends Usable {
    public DolphinRocketItem() { super("dolphinrocket"); }

    @Override
    public boolean use(Entity user, Location location) {

        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f, 2.0f);
        DolphinRocketEntity dolphinRocketEntity = new DolphinRocketEntity(location, user);
        main.getEntityManager().registerEntityTracker(dolphinRocketEntity);

        return true;
    }
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);


        itemMeta(item);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Dolphin Rocket"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }
}
