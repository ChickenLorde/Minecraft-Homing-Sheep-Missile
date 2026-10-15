package me.oakyplugin.destruction.items.pig_cannon;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.EntityTracker;
import me.oakyplugin.destruction.utility.EntityUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;

public class PigBazookaEntity extends EntityTracker {

    private final Pig PIG;

    private final double MAX_LIFE = 100;
    private double life = 0;
    public PigBazookaEntity(Location location, double launchPower, Entity owner)
    {
        super(location, EntityType.PIG, owner);
        PIG = (Pig)getEntity();
        PIG.addScoreboardTag("cannon_pig");
        PIG.setInvulnerable(true);
        PIG.setFrictionState(TriState.FALSE);
        PIG.setAware(false);

        PIG.setVelocity(location.getDirection().clone().multiply(launchPower));

        PIG.customName(Component.text("Rocket Propelled Pig"));

        getWorld().playSound(PIG, Sound.ENTITY_PIG_AMBIENT, 0.7f, (float)Math.random() * 0.75f + 0.75f);

    }
    @SuppressWarnings("UnstableApiUsage")
    public void tick()
    {
        if(life >= MAX_LIFE) {
            end();
            return;
        }

        Particle.CAMPFIRE_COSY_SMOKE.builder()
                .location(PIG.getLocation())
                .receivers(64)
                .count(4)
                .extra(0.007)
                .offset(0.02, 0.02, 0.02)
                .spawn();

        if(getEntity() == null) return;
        BoundingBox box = PIG.getBoundingBox().clone();
        Vector from = box.getMin().subtract(new Vector(0.6, 0.6, 0.6));
        Vector to = box.getMax().add(new Vector(0.6, 0.6, 0.6));

        Collection<Entity> entities = EntityUtility.getTouchingEntities(getWorld(), PIG, 0.6);
        entities.removeIf(entity -> entity.getScoreboardTags().contains("cannon_pig") || entity.equals(getOwner()) || !(entity instanceof LivingEntity));

        boolean touchingEntity = !entities.isEmpty();

        if(touchingEntity || (life > 10 && (EntityUtility.isTouchingBlocks(getWorld(), from, to)))) {
            //Bukkit.getServer().broadcast(Component.text(PIG.toString()+" hit a block"));
            getWorld().playSound(PIG.getLocation(), Sound.ENTITY_PIG_DEATH, 4.0f, (float)Math.random() + 0.65f);
            getWorld().createExplosion(PIG.getLocation(), 6f, false, true, PIG);

            int items = (int)(Math.random() * 15) + 45;

            for(int i = 0; i < items; i++)
            {
                ItemStack itemStack = new ItemStack(Material.PORKCHOP);
                itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
                Item item = getWorld().dropItem(PIG.getLocation(), itemStack);
                item.setPickupDelay(32767);
                item.setTicksLived((int)(Math.random() * 10) + 5940);
                Vector velocity = new Vector();
                double yaw = Math.toRadians(Math.random() * 360);
                double pitch = Math.toRadians(Math.random() * 360);

                velocity.setX(Math.cos(pitch) * Math.cos(yaw));
                velocity.setZ(Math.cos(pitch) * Math.sin(yaw));
                velocity.setY(Math.sin(pitch));

                item.setVelocity(velocity.multiply(Math.random() * 1.3 + 0.2));
            }

            if(touchingEntity && (getOwner() instanceof Player p))
            {
                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1.0f);
            }

            end();
        }
        life++;
    }

    public Pig getPig() {return PIG;}
}
