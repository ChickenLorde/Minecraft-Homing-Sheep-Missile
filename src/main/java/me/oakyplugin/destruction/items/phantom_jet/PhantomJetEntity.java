package me.oakyplugin.destruction.items.phantom_jet;

import me.oakyplugin.destruction.items.EntityTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Input;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PhantomJetEntity extends EntityTracker {

    private final Phantom phantom;
    private final Entity owner;

    private double accelerationMagnitude;
    private final double MAX_ACCELERATION = 0.06;

    private Vector dir;
    private Vector velocity;


    public PhantomJetEntity(Location location, Entity owner)
    {
        super(location, EntityType.PHANTOM,owner);
        this.owner = owner;

        phantom = (Phantom)getEntity();

        phantom.setSize(12);
        phantom.setAware(false);

        AttributeInstance attribute = phantom.getAttribute(Attribute.MAX_HEALTH);
        attribute.setBaseValue(60);
        attribute = phantom.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        attribute.setBaseValue(1);

        phantom.setHealth(60);
        phantom.setShouldBurnInDay(false);
        accelerationMagnitude = 0;
        velocity = new Vector(0,0,0);
        dir = new Vector(1,0,0);
    }

    @Override
    public void tick() {

        for(Entity entity : phantom.getPassengers())
            if(!(entity instanceof Player) || entity != owner)
                phantom.removePassenger(entity);

        if(!phantom.getPassengers().isEmpty())
        {
            Player p = (Player)phantom.getPassengers().getFirst();
            fly(p);
        }
        else
            accelerationMagnitude = 0;


        if(!velocity.isZero())
            dir = velocity.clone().normalize();
        phantom.setRotation(-(float)Math.toDegrees(Math.atan2(dir.getX(),dir.getZ())), (float)Math.toDegrees(Math.sin(dir.getY())));

        velocity.add(dir.clone().multiply(accelerationMagnitude));
        velocity.multiply(0.985);

        phantom.setVelocity(velocity);
    }

    private void fly(Player p)
    {
        if((int)(accelerationMagnitude /MAX_ACCELERATION*100) <= 100)
            p.sendActionBar(MiniMessage.miniMessage().deserialize("<gray>Thrust: "+(int)(accelerationMagnitude /MAX_ACCELERATION*100)+"%"));
        else
            p.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Thrust: "+(int)(accelerationMagnitude /MAX_ACCELERATION*100)+"%"));
        Input in = p.getCurrentInput();
        if(in.isJump()) {
            accelerationMagnitude += MAX_ACCELERATION * 0.05 * 0.5;
            if(accelerationMagnitude /MAX_ACCELERATION > 1.1)
                accelerationMagnitude = MAX_ACCELERATION * 1.1;
        }
        else
        {
            if(accelerationMagnitude /MAX_ACCELERATION > 1.0)
                accelerationMagnitude = MAX_ACCELERATION;
        }
        if(in.isSneak()) {
            accelerationMagnitude -= MAX_ACCELERATION * 0.05 * 0.5;
            if(accelerationMagnitude < 0)
                accelerationMagnitude = 0;
        }
        Vector right = dir.getCrossProduct(new Vector(0, 1, 0)).normalize();
        if(in.isRight() ^ in.isLeft()) {
            right.multiply(0.05);
            right.multiply(velocity.length());
            if(in.isRight())
                velocity.add(right);
            else
                velocity.subtract(right);
        }
        if(in.isForward() ^ in.isBackward()) {
            if(velocity.equals(new Vector(0,1,0)))
                right = new Vector(1,0,0);
            Vector up = right.getCrossProduct(dir).normalize();
            up.multiply(0.05);
            up.multiply(velocity.length());
            if(in.isBackward())
                velocity.add(up);
            else
                velocity.subtract(up);
        }
    }

    public void mount(Player p)
    {
        if(p != owner) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not own this"));
            return;
        }
        p.sendMessage(Component.text("Double-shift to dismount"));
        p.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Space (accelerate) Shift (decelerate) A/D (steer left/right) S/W (pitch up/down)"));
        phantom.addPassenger(p);
    }
}
