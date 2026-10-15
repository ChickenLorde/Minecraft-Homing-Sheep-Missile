package me.oakyplugin.destruction.items.sky_scrambler;

import me.oakyplugin.destruction.items.EntityTracker;
import me.oakyplugin.destruction.utility.EntityUtility;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SkyScramblerSecondary extends EntityTracker {
    private final Chicken chicken;

    public SkyScramblerSecondary(Location location, Entity owner)
    {
        super(location, EntityType.CHICKEN, owner);
        chicken = (Chicken)getEntity();
        chicken.addScoreboardTag("sky_scrambler");
        chicken.setInvulnerable(true);


        chicken.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0);
        chicken.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1);
        chicken.getAttribute(Attribute.GRAVITY).setBaseValue(0.21);

        chicken.setAge(-32000);
        chicken.setAgeLock(true);


        chicken.customName(MiniMessage.miniMessage().deserialize("Chicken Bomb"));
    }
    @Override
    public void tick() {
        if(!getIsActive()) return;

        if(chicken.getVelocity().getY() > 0)
        {
            Particle.CAMPFIRE_COSY_SMOKE.builder()
                    .location(chicken.getLocation())
                    .receivers(64)
                    .count(1)
                    .extra(0.007)
                    .offset(0.02, 0.02, 0.02)
                    .spawn();
        }


        if(chicken.getTicksLived() < 2) return;

        BoundingBox box = chicken.getBoundingBox().clone();
        Vector from = box.getMin().subtract(new Vector(0.6, 0.6, 0.6));
        Vector to = box.getMax().add(new Vector(0.6, 0.6, 0.6));

        Collection<Entity> entities = EntityUtility.getTouchingEntities(getWorld(), chicken, 0.6);
        entities.removeIf(entity -> entity.getScoreboardTags().contains("sky_scrambler") || entity.equals(getOwner()) || !(entity instanceof LivingEntity));

        boolean touchingEntity = !entities.isEmpty();

        if(touchingEntity || EntityUtility.isTouchingBlocks(getWorld(), from, to))
        {
            getWorld().createExplosion(chicken, 3.0f, false, true);

            getWorld().playSound(chicken.getLocation(),Sound.ENTITY_CHICKEN_HURT, 2.0f, (float)Math.random() * 0.1f + 1.45f);

            end();
        }
    }

    public Chicken getChicken() {return chicken;}
}


