package me.oakyplugin.destruction.items.phantom_jet;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.Usable;
import me.oakyplugin.destruction.items.dolphin_rocket.DolphinRocketEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class PhantomJetItem extends Usable {
    public PhantomJetItem() { super("phantomjet"); }

    @Override
    public boolean use(Entity user, Location location) {

        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f, 2.0f);
        PhantomJetEntity phantomJetEntity = new PhantomJetEntity(location, user);
        main.getEntityManager().registerEntityTracker(phantomJetEntity);

        return true;
    }
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);


        itemMeta(item);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Phantom Jet"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }
}
