package me.oakyplugin.destruction.items.homing_sheep;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.Usable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomingSheepItem extends Usable {

    public HomingSheepItem() {super("homingsheep");}
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);


        itemMeta(item);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text("S.H.E.E.P."));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return item;
    }

    @Override
    public boolean use(Entity user, Location location) {

        World world = location.getWorld();

        Location checkLocation = location.clone();

        List<Entity> entities = world.getEntities();

        List<LivingEntity> livingEntities = new ArrayList<>();

        for(Entity entity : entities)
            if(entity instanceof  LivingEntity livingEntity) livingEntities.add(livingEntity);

        for(int i = 0; i < 350; i++)
        {


            if(user instanceof  Player p) {
                Particle.DUST.builder()
                        .receivers(p)
                        .color(Color.RED, 0.75f)
                        .location(checkLocation)
                        .spawn();
            }
            for(LivingEntity entity : livingEntities)
            {
                if(entity.equals(user)) continue;
                if(entity.getBoundingBox().contains(checkLocation.toVector())) {
                    main.getEntityManager().setHomingSheepTarget(user, entity);
                    user.sendMessage(MiniMessage.miniMessage().deserialize("<green>S.H.E.E.P. target set to: "+entity.getName()+ " ("+entity.getType().name()+")"));
                    return true;
                }
            }
            checkLocation.add(checkLocation.getDirection().clone().multiply(0.1));
        }

        return false;
    }

    public void spawnSheep(LivingEntity user, Location location)
    {
        LivingEntity target = main.getEntityManager().getHomingSheepTarget(user);

        if(target == null || !target.isValid())
        {
            user.sendMessage(MiniMessage.miniMessage().deserialize("<red>ERROR: S.H.E.E.P. target not set"));
            return;
        }

        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f, 2.0f);


        HomingSheepEntity homingSheepEntity = new HomingSheepEntity(location, user, target, user.getLocation().getDirection().clone().normalize().multiply(0.5));
        main.getEntityManager().registerEntityTracker(homingSheepEntity);
    }

    public void holding(Player p)
    {

        Entity target = main.getEntityManager().getHomingSheepTarget(p);
        if(target == null) return;

        Location particleLocation = target.getLocation().clone();
        double shiftY = target.getBoundingBox().getHeight() * 0.5;

        particleLocation.add(0, shiftY + 1, 0);

        Particle.DUST.builder()
                .color(Color.RED, 2.0f)
                .location(particleLocation)
                .receivers(p)
                .spawn();

        p.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Target: "+ target.getName() + " ("+target.getType().name()+")"));
    }
}
