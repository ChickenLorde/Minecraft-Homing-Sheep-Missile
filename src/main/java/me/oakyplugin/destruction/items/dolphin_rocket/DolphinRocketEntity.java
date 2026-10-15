package me.oakyplugin.destruction.items.dolphin_rocket;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.EntityTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class DolphinRocketEntity extends EntityTracker {
    private final Dolphin dolphin;

    private final Vector velocity;

    private double accelerationMagnitude;
    private double maxAccelerationMagnitude;

    public DolphinRocketEntity(Location location, Entity owner)
    {
        super(location, EntityType.DOLPHIN, owner);
        dolphin = (Dolphin)getEntity();
        dolphin.addScoreboardTag("dolphin_rocket");
        dolphin.setInvulnerable(true);
        dolphin.setFrictionState(TriState.FALSE);
        dolphin.setAware(false);
        dolphin.setGravity(false);

        dolphin.addPassenger(owner);

        velocity = new Vector(0,1,0);
        accelerationMagnitude = 0.04;
        maxAccelerationMagnitude = 0.25;

        dolphin.customName(Component.text("Dolphin Rocket"));

    }

    @Override
    public void tick() {
        //region particles
        Particle.CAMPFIRE_COSY_SMOKE.builder()
                .location(dolphin.getBoundingBox().getCenter().toLocation(getWorld()))
                .force(true)
                .count(4)
                .extra(0.005)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        Particle.FLAME.builder()
                .location(dolphin.getBoundingBox().getCenter().toLocation(getWorld()))
                .receivers(128)
                .count(12)
                .extra(0.03 + accelerationMagnitude * 0.36)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        Particle.LARGE_SMOKE.builder()
                .location(dolphin.getBoundingBox().getCenter().toLocation(getWorld()))
                .receivers(128)
                .count(12)
                .extra(0.02)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        //endregion


        Vector accelerationDir = getOwner().getLocation().getDirection().clone();
        velocity.add(accelerationDir.multiply(accelerationMagnitude));
        velocity.multiply(0.95);

        dolphin.setVelocity(velocity);

        if(accelerationMagnitude < maxAccelerationMagnitude)
            accelerationMagnitude += 0.0004;
        else
            accelerationMagnitude = maxAccelerationMagnitude;

        dolphin.setRotation(-(float)Math.toDegrees(Math.atan2(velocity.getX(),velocity.getZ())), -(float)Math.toDegrees(Math.sin(velocity.getY())));

        Collection<Entity> nearby = getWorld().getNearbyEntities(dolphin.getBoundingBox().clone().expand(1));
        boolean hitEntity = false;

        for(Entity entity : nearby)
        {
            if((entity instanceof LivingEntity living) && !living.equals(dolphin) && !living.equals(getOwner()))
            {
                hitEntity = true;
                break;
            }
        }

        if(dolphin.wouldCollideUsing(dolphin.getBoundingBox().clone().expand(0.1)) || !dolphin.getPassengers().contains(getOwner()) || hitEntity)
            detonate();

    }

    private void detonate()
    {
        getWorld().createExplosion(dolphin, 7, true, true);
        getWorld().playSound(dolphin, Sound.ENTITY_DOLPHIN_HURT, 4.0f, (float)Math.random() * 0.2f + 1f);

        int items = (int)(Math.random() * 15) + 65;

        for(int i = 0; i < items; i++)
        {
            int random = (int)(Math.random() * 3);

            ItemStack itemStack = null;
            if(random == 0)
                itemStack = new ItemStack(Material.COD);
            else if(random == 1)
                itemStack = new ItemStack(Material.SALMON);
            else
                itemStack = new ItemStack(Material.TROPICAL_FISH);
            
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
            Item item = getWorld().dropItem(dolphin.getLocation(), itemStack);
            item.setPickupDelay(32767);
            item.setTicksLived((int)(Math.random() * 10) + 5920);
            Vector velocity = new Vector();
            double yaw = Math.toRadians(Math.random() * 360);
            double pitch = Math.toRadians(Math.random() * 360);

            velocity.setX(Math.cos(pitch) * Math.cos(yaw));
            velocity.setZ(Math.cos(pitch) * Math.sin(yaw));
            velocity.setY(Math.sin(pitch));

            item.setVelocity(velocity.multiply(Math.random() * 1.2 + 0.2));
        }

        end();
    }
}
