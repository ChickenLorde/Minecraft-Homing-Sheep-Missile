package me.oakyplugin.destruction.items.sky_scrambler;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.Usable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class SkyScramblerItem extends Usable {

    public SkyScramblerItem() {super("skyscrambler");}

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);


        itemMeta(item);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Sky Scrambler"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }

    @Override
    public boolean use(Entity user, Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_CHICKEN_AMBIENT, 0.7f, 1.0f);
        SkyScramblerEntity skyScramblerEntity = new SkyScramblerEntity(location, user);
        main.getEntityManager().registerEntityTracker(skyScramblerEntity);

        return true;
    }
}
