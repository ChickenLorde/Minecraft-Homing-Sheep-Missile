package me.oakyplugin.destruction.items.homing_sheep;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.oakyplugin.destruction.items.EntityTracker;
import me.oakyplugin.destruction.utility.EntityUtility;
import me.oakyplugin.destruction.utility.entity_raycast.EntityRaycast;
import me.oakyplugin.destruction.utility.entity_raycast.EntityRaycastResult;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

@SuppressWarnings("SameParameterValue")
public class HomingSheepEntity  extends EntityTracker {

    private final Sheep sheep;
    private final LivingEntity target;

    private double life;

    private boolean isRed;

    private final Vector velocity;
    private double accelerationMagnitude = 0.01;

    private Vector checkPos;


    public HomingSheepEntity(Location location, Entity owner, LivingEntity target, Vector initialVelocity)
    {
        super(location, EntityType.SHEEP, owner);

        sheep = (Sheep)getEntity();

        sheep.setInvulnerable(true);
        sheep.customName(Component.text("Sheep-guided Highly Explosive Erratic Projectile"));
        sheep.setGravity(false);
        sheep.setAware(false);

        AttributeInstance speed = sheep.getAttribute(Attribute.MOVEMENT_SPEED);
        if(speed != null) speed.setBaseValue(0);

        this.target = target;
        life = 0;

        velocity = initialVelocity;
    }
    public HomingSheepEntity(Location location, Entity owner, LivingEntity target)
    {
        this(location, owner, target, new Vector(0,0,0));
    }
    @Override
    public void tick() {
        life++;

        if(target == null || !target.isValid()) {
            despawn();
            return;
        }

        if(life % 5 == 0) {
            if(life % 10 == 0) getWorld().playSound(sheep, Sound.ENTITY_SHEEP_HURT, 2.0f, (float)Math.random() * 0.2f + 1f);

            blink();
        }
        if(life % 4 == 0)
            getWorld().playSound(sheep, Sound.ENTITY_BLAZE_SHOOT, 1.0f, (float)Math.random() * 0.1f + 1f);


        sheep.setRotation(-(float)Math.toDegrees(Math.atan2(velocity.getX(),velocity.getZ())), -(float)Math.toDegrees(Math.sin(velocity.getY())));
        accelerateToTarget();



        if(life > 600 || getWorld().getNearbyEntities(sheep.getBoundingBox().clone().expand(1)).contains(target)
        || ( life > 20 && sheep.wouldCollideUsing(sheep.getBoundingBox().expand(0.05)) && velocity.lengthSquared() > 0.04))
            detonate();
    }

    private void despawn()
    {
        Particle.LARGE_SMOKE.builder()
                .location(sheep.getLocation())
                .receivers(64)
                .count(100)
                .extra(0.1)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        end();
    }

    private void blink()
    {
        isRed = !isRed;
        sheep.setColor(isRed ? DyeColor.RED : DyeColor.WHITE);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void detonate()
    {
        getWorld().playSound(sheep, Sound.ENTITY_SHEEP_DEATH, 4.0f, (float)Math.random() * 0.2f + 1f);
        getWorld().createExplosion(sheep, 6, true, true);

        int items = (int)(Math.random() * 15) + 65;

        for(int i = 0; i < items; i++)
        {
            ItemStack itemStack = new ItemStack(Material.MUTTON);
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
            Item item = getWorld().dropItem(sheep.getLocation(), itemStack);
            item.setPickupDelay(32767);
            item.setTicksLived((int)(Math.random() * 10) + 5940);
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

    private void accelerateToTarget()
    {
        final double maxLength = 50;
        if(target == null || !target.isValid() || !sheep.isValid()) return;

        //region particles
        Particle.CAMPFIRE_COSY_SMOKE.builder()
                .location(sheep.getBoundingBox().getCenter().toLocation(getWorld()))
                .force(true)
                .count(4)
                .extra(0.005)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        Particle.FLAME.builder()
                .location(sheep.getBoundingBox().getCenter().toLocation(getWorld()))
                .receivers(128)
                .count(12)
                .extra(0.04)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        Particle.SMOKE.builder()
                .location(sheep.getBoundingBox().getCenter().toLocation(getWorld()))
                .receivers(128)
                .count(12)
                .extra(0.02)
                .offset(0.02, 0.02, 0.02)
                .spawn();
        //endregion

        checkPos = sheep.getLocation().toVector().clone();

        //region position adjustment
        if(!velocity.isZero()) {
            Vector velocityDirection = velocity.clone().normalize();
            double speed = velocity.lengthSquared();

            double tuning = 1.15;
            Vector lookAhead = velocityDirection.multiply(speed).multiply(1.0 / (2.0 * accelerationMagnitude)).multiply(tuning);
            checkPos = checkPos.add(lookAhead);

            /*Vector posVector = sheep.getLocation().toVector();
            for(int i = 0; i <= posVector.distance(checkPos) * 5; i++)
            {
                Vector dir = checkPos.clone().subtract(posVector).normalize();
                Vector newCheckPos = posVector.clone().add(dir.multiply(i * 0.2 - 0.3));

                if(sheep.collidesAt(newCheckPos.toLocation(getWorld())))
                {
                    checkPos = newCheckPos;
                    break;
                }
            }*/

            //Bukkit.getLogger().info("veloDir: " + velocityDirection.toString() + " speed: " + speed + " accel: " + accelerationMagnitude + " lookAHead: " + lookAhead.toString());
        } //endregion



        if(accelerationMagnitude > 0.07) accelerationMagnitude = 0.07;
        else accelerationMagnitude += 0.0005;

        Vector forward = setForwardDirection();

        Map<Vector, Double> rays = fireRays(forward, 65, 360, maxLength);
        Vector accelerationVector = getBestVector(rays, forward, maxLength);

        /*for(Vector vector : rays.keySet())
            for(int i = 0; i < rays.get(vector) * 3; i++)
            {
                Vector pos = checkPos.clone().add(vector.clone().normalize().multiply(i * 0.333));

                Particle.DUST.builder()
                        .location(pos.toLocation(getWorld()))
                        .color(Color.RED, 0.75f)
                        .spawn();

            }
        for(int i = 0; i < 30; i++)
        {
            Vector pos = checkPos.clone().add(accelerationVector.clone().normalize().multiply(i * 0.333));

            Particle.DUST.builder()
                    .location(pos.toLocation(getWorld()))
                    .color(Color.YELLOW, 0.75f)
                    .spawn();

        }*/

        if(accelerationVector != null)
            Bukkit.getLogger().info("acceleration: "+accelerationVector);


        if(!accelerationVector.isZero())
            velocity.add(accelerationVector.multiply(accelerationMagnitude));
        final double dragCoefficient = 0.98;
        velocity.multiply(dragCoefficient);

        sheep.setVelocity(velocity.clone());
    }

    private Vector setForwardDirection()
    {
        Vector direction = new Vector();
        Vector targetLoc = target.getLocation().toVector();

        direction.setX(targetLoc.getX() - checkPos.getX());
        direction.setY(targetLoc.getY() - checkPos.getY());
        direction.setZ(targetLoc.getZ() - checkPos.getZ());
        direction = direction.normalize();

        return direction;
    }

    private Map<Vector, Double> fireRays(Vector forward, int rays, double spread, double maxLength)
    {

        Vector fwd = forward.clone().normalize();

        double x = fwd.getX();
        double y = fwd.getY();
        double z = fwd.getZ();

        double yawDeg = Math.toDegrees(Math.atan2(z, x));
        double pitchDeg = Math.toDegrees(Math.asin(y));

        Map<Vector, Double> raysLengths = new HashMap<>();

        for(int i = 0; i < rays; i++)
        {
            Vector vector = new Vector();

            double newYaw = Math.toRadians(yawDeg + Math.random() * spread - (spread * 0.5));
            double newPitch = Math.toRadians(pitchDeg + Math.random() * spread - (spread * 0.5));
            if(i == 0) {
                newYaw = Math.toRadians(yawDeg);
                newPitch = Math.toRadians(pitchDeg);
            }

            vector.setX(Math.cos(newPitch) * Math.cos(newYaw));
            vector.setZ(Math.cos(newPitch) * Math.sin(newYaw));
            vector.setY(Math.sin(newPitch));

            RayTraceResult ray = getWorld().rayTraceBlocks(checkPos.toLocation(getWorld()), vector.normalize(), maxLength);

            if(i == 0)
            {
                double length = maxLength;
                if(!(ray == null || ray.getHitBlock() == null)) length = ray.getHitPosition().clone().subtract(checkPos).length();

                if(checkPos.distance(target.getLocation().toVector()) <= length + 0.2) {
                    raysLengths.put(fwd.clone(), length);
                    return raysLengths;
                }
            }

            else if(ray == null || ray.getHitBlock() == null)
            {
                Vector rayVector = vector.clone().normalize();
                raysLengths.put(rayVector, maxLength);

            }
            else
            {
                double length = ray.getHitPosition().clone().subtract(checkPos).length();
                Vector rayVector = vector.clone().normalize();
                raysLengths.put(rayVector, length);
            }
        }

        return raysLengths;
    }

    private Vector getBestVector(Map<Vector,Double> vectors, Vector idealVector, double maxDistance)
    {
        //if(vectors.size() <= 1) return idealVector.clone().normalize();
        double bestScore = Double.NEGATIVE_INFINITY;
        Vector bestVector = null;



        for(Vector vector : vectors.keySet())
        {
            Vector idealVectorN = idealVector.clone().normalize();
            Vector vectorN = vector.clone().normalize();

            double vectorLength = vectors.get(vector);

            for (int i = 0; i <= vectorLength * 5; i++) {
                Vector shift = checkPos.clone().add(vectorN.clone().multiply(i * 0.2));
                BoundingBox sheepBox = sheep.getBoundingBox();
                BoundingBox box = new BoundingBox();
                box.expand(sheepBox.getWidthX() * 3, sheepBox.getHeight() * 3, sheepBox.getWidthZ() * 3);
                box.shift(shift);


                if (sheep.wouldCollideUsing(box)) {
                    /*
                    //region collision visualization
                    Particle.DUST.builder()
                            .location(shift.toLocation(getWorld()))
                            .color(Color.YELLOW, 3.0f)
                            .count(5)
                            .offset(0.02,0.02,0.02)
                            .spawn();//endregion*/

                    vectorLength = i * 0.2;
                    break;
                }

            }

            double alignmentScore = Math.max(0.1,vectorN.dot(idealVectorN));
            double clearanceScore = Math.min(1.0, vectors.get(vector) / maxDistance);
            double collisionScore = Math.min(1.0, Math.max(0.001,vectorLength / maxDistance));

            if(checkPos.distance(target.getLocation().toVector()) <= vectorLength + 0.2)
            {
                collisionScore = clearanceScore = 1;
                //Bukkit.getServer().broadcast(Component.text("Locked on"));
            }

            double score = Math.max(0,alignmentScore * Math.pow(clearanceScore * collisionScore, 2.5));

            if(score > bestScore)
            {
                //Bukkit.getLogger().info("Score: "+score+" align: "+alignmentScore+" clearance: "+clearanceScore+" collision: "+collisionScore);
                bestScore = score;
                bestVector = vector;
            }
        }
        return bestVector;
    }
}
