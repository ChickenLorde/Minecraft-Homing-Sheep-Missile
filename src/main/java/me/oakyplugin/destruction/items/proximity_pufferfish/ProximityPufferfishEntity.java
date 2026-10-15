package me.oakyplugin.destruction.items.proximity_pufferfish;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.EntityTracker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;

public class ProximityPufferfishEntity extends EntityTracker {
    private double fuse;
    private final double maxFuse = 35;

    private final PufferFish pufferfish;

    private boolean isPlaced;

    public ProximityPufferfishEntity(Location location, Entity owner)
    {
        super(location, EntityType.PUFFERFISH, owner);

        pufferfish = (PufferFish)getEntity();
        pufferfish.setAware(false);
        pufferfish.setInvulnerable(true);
        pufferfish.setPuffState(0);
        pufferfish.setPersistent(true);
        pufferfish.setRemoveWhenFarAway(false);
        pufferfish.setGravity(false);
        pufferfish.setAI(false);

        isPlaced = false;
    }

    @Override
    public void tick() {

        //pufferfish.setVelocity(new Vector(0,0,0));

        if(fuse > 24) {
            if(pufferfish.getPuffState() != 2) getWorld().playSound(pufferfish, Sound.ENTITY_PUFFER_FISH_BLOW_UP,1.5f, 1.5f);
            pufferfish.setPuffState(2);
        }
        else if(fuse > 12) {
            if(pufferfish.getPuffState() != 1) getWorld().playSound(pufferfish, Sound.ENTITY_PUFFER_FISH_BLOW_UP,1.25f, 1.25f);
            pufferfish.setPuffState(1);
        }
        else {
            if(pufferfish.getPuffState() != 0) getWorld().playSound(pufferfish, Sound.ENTITY_PUFFER_FISH_BLOW_UP,1.0f, 1.0f);

            pufferfish.setPuffState(0);
        }





        Collection<Entity> entities = getWorld().getNearbyEntities(pufferfish.getLocation(), 6, 6 ,6);

        for(Entity entity : entities)
        {
            if(!(entity instanceof LivingEntity livingEntity) || !entity.isValid() || entity.isDead()) continue;

            if(!(livingEntity == getOwner() || livingEntity == pufferfish || main.getEntityManager().getTrackerFromEntity(livingEntity) instanceof ProximityPufferfishEntity))
            {
                inflate();
                return;
            }
        }
        fuse-= 2;
        if(fuse < 0) fuse = 0;
    }

    private void inflate()
    {
        fuse++;
        if(fuse > maxFuse) detonate();
    }

    private void detonate()
    {
        getWorld().createExplosion(pufferfish, 7, true, true);
        getWorld().playSound(pufferfish, Sound.ENTITY_PUFFER_FISH_DEATH, 4.0f, (float)Math.random() * 0.2f + 1f);

        int items = (int)(Math.random() * 15) + 65;

        for(int i = 0; i < items; i++)
        {
            int random = (int)(Math.random() * 3);

            ItemStack itemStack = new ItemStack(Material.PUFFERFISH);

            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
            Item item = getWorld().dropItem(pufferfish.getLocation(), itemStack);
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

    private void place()
    {

    }
}
