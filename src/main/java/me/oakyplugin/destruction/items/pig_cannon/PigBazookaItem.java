package me.oakyplugin.destruction.items.pig_cannon;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.Usable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class PigBazookaItem extends Usable {
    public PigBazookaItem() {
        super("pigcannon");
    }

    @Override
    public boolean use(Entity user, Location location) {
        //Bukkit.getServer().broadcast(Component.text("PigCannon used"));

        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f, 2.0f);
        PigBazookaEntity pigBazookaEntity = new PigBazookaEntity(location, 2, user);
        main.getEntityManager().registerEntityTracker(pigBazookaEntity);

        return true;
    }
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);


        itemMeta(item);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Pigzooka"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }
}
