package me.oakyplugin.destruction.items.cow_bazooka;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.Main;
import me.oakyplugin.destruction.items.EntityTracker;
import me.oakyplugin.destruction.utility.EntityUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class CowBazookaEntity extends EntityTracker {
    private final Cow cow;

    private final double MAX_LIFE = 100;
    private double life = 0;
    private final boolean isSecondary;
    private final Main main = JavaPlugin.getPlugin(Main.class);
    public CowBazookaEntity(Location location, Vector launchDirection, double launchPower, Entity owner, boolean isSecondary)
    {
        super(location, EntityType.COW, owner);
        this.isSecondary = isSecondary;
        cow = (Cow)getEntity();
        cow.addScoreboardTag("cowbazooka");
        cow.setInvulnerable(true);
        cow.setFrictionState(TriState.FALSE);
        cow.setAware(false);

        cow.setVelocity(launchDirection.multiply(launchPower));

        String name = "";

        if(!isSecondary) {
            name = "Moozooka Shell";
            getWorld().playSound(cow, Sound.ENTITY_COW_AMBIENT, 0.7f, (float) Math.random() * 0.75f + 0.75f);
        }
        else
        {
            name = "Moozooka Fragment";
            cow.setAge(Short.MIN_VALUE);
            cow.setAgeLock(true);
        }

        cow.customName(Component.text(name));

    }
    public CowBazookaEntity(Location location,Vector launchDirection, double launchPower, Entity owner)
    {
        this(location, launchDirection, launchPower, owner, false);
    }
    @SuppressWarnings("UnstableApiUsage")
    public void tick()
    {
        if(life >= MAX_LIFE) {
            end();
            return;
        }

        Particle.CAMPFIRE_COSY_SMOKE.builder()
                .location(cow.getLocation())
                .receivers(64)
                .count(4)
                .extra(0.007)
                .offset(0.02, 0.02, 0.02)
                .spawn();

        if(getEntity() == null) return;
        BoundingBox box = cow.getBoundingBox().clone();
        Vector from = box.getMin().subtract(new Vector(0.6, 0.6, 0.6));
        Vector to = box.getMax().add(new Vector(0.6, 0.6, 0.6));

        Collection<Entity> entities = EntityUtility.getTouchingEntities(getWorld(), cow, 0.6);
        entities.removeIf(entity -> entity.getScoreboardTags().contains("cowbazooka") || entity.equals(getOwner()) || !(entity instanceof LivingEntity));

        boolean touchingEntity = !entities.isEmpty();

        if((touchingEntity && (!isSecondary || life > 4)) || (life > 10 && (EntityUtility.isTouchingBlocks(getWorld(), from, to)))) {
            //Bukkit.getServer().broadcast(Component.text(PIG.toString()+" hit a block"));
            getWorld().playSound(cow.getLocation(), Sound.ENTITY_COW_DEATH, 4.0f, (float)Math.random() + 0.65f);

            float explosionPower = isSecondary ? 3f : 4.5f;

            getWorld().createExplosion(cow.getLocation(), explosionPower, false, true, cow);

            int items = (int)(Math.random() * 15) + 45;
            if(isSecondary) items = (int)(Math.random() * 5) + 15;

            for(int i = 0; i < items; i++)
            {
                ItemStack itemStack = new ItemStack(Material.BEEF);
                itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
                Item item = getWorld().dropItem(cow.getLocation(), itemStack);
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

            if(!isSecondary) {
                spawnFragments();
            }

            if(touchingEntity && (getOwner() instanceof Player p))
            {
                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1.0f);
            }

            end();
        }
        life++;
    }

    private void spawnFragments()
    {
        for(int i = 0; i < 24; i++)
        {
            Vector velocity = new Vector();
            double yaw = Math.toRadians(Math.random() * 360);
            double pitch = Math.toRadians(Math.random() * 360);

            velocity.setX(Math.cos(pitch) * Math.cos(yaw));
            velocity.setZ(Math.cos(pitch) * Math.sin(yaw));
            velocity.setY(Math.sin(pitch));
            velocity.normalize();

            double launchPower =  Math.random() * 1.3 + 0.7;
            CowBazookaEntity fragment = new CowBazookaEntity(cow.getLocation(), velocity,launchPower, getOwner(), true);
            main.getEntityManager().registerEntityTracker(fragment);
        }
    }

    public Cow getCow() {return cow;}
}
