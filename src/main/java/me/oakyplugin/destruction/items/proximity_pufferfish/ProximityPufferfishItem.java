package me.oakyplugin.destruction.items.proximity_pufferfish;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.Usable;
import me.oakyplugin.destruction.items.pig_cannon.PigBazookaEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class ProximityPufferfishItem extends Usable {
    public ProximityPufferfishItem()
    { super("proximitypufferfish"); }
    @Override
    public boolean use(Entity user, Location location) {
        //Bukkit.getServer().broadcast(Component.text("PigCannon used"));

        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f, 2.0f);

        RayTraceResult res = user.getWorld().rayTraceBlocks(location,location.getDirection(),30);

        Location checkLoc = location.clone();
        for(int i = 0; i < 300; i++)
        {
            checkLoc.add(location.getDirection().clone().multiply(0.1));
            if(user.getWorld().getBlockAt(checkLoc).isSolid()) {
                checkLoc.subtract(location.getDirection().clone().multiply(0.25));
                break;
            }
        }

        ProximityPufferfishEntity proximityPufferfishEntity = new ProximityPufferfishEntity(checkLoc, user);
        main.getEntityManager().registerEntityTracker(proximityPufferfishEntity);

        return true;
    }
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);


        itemMeta(item);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Proximity Pufferfish"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }
}
