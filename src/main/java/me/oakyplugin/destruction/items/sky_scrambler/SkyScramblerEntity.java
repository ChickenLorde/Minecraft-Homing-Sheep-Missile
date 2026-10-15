package me.oakyplugin.destruction.items.sky_scrambler;

import me.oakyplugin.destruction.items.EntityTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;


public class SkyScramblerEntity extends EntityTracker {

    private final Chicken chicken;

    private int stage;

    private int stageProgress;

    private boolean isActivated;

    public SkyScramblerEntity(Location location, Entity owner)
    {
        super(location, EntityType.CHICKEN, owner);
        chicken = (Chicken)getEntity();
        chicken.addScoreboardTag("sky_scrambler");
        chicken.setInvulnerable(true);


        chicken.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0);
        chicken.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1);

        stage = 0;
        isActivated = false;

        chicken.customName(MiniMessage.miniMessage().deserialize("<red>Click to arm"));
    }
    @Override
    public void tick() {
        if(!isActivated || !getIsActive()) return;
        if(stage == 0) stage0();
        else if(stage == 1) stage1();
    }

    private void stage0()
    {
        if(stageProgress >= 40)
        {
            stage = 1;
            stageProgress = 100;
            chicken.setAI(false);
            return;
        }

        AttributeInstance scale = chicken.getAttribute(Attribute.SCALE);

        if(scale != null)


            if(stageProgress % 4 == 0) {
                getWorld().playSound(chicken, Sound.ENTITY_CHICKEN_HURT, 2.0f, stageProgress * 0.0375f + 0.5f);
                scale.setBaseValue(1.0 + stageProgress * 0.075);
        }



        stageProgress++;
    }
    private void stage1()
    {
        if(stageProgress <= 0)
        {
            stageProgress = 0;

            getWorld().playSound(chicken.getLocation(), Sound.ENTITY_CHICKEN_HURT, 3.0f, 1.0f);
            getWorld().createExplosion(chicken, 4f, false, true);

            end();
            return;
        }

        Location newLoc = chicken.getLocation().clone().addRotation(30f, 0);

        chicken.teleport(newLoc);

        AttributeInstance scale = chicken.getAttribute(Attribute.SCALE);

        if(scale != null)
            scale.setBaseValue(1.0 + stageProgress * 0.03);

        spawnSecondary();
        if(stageProgress % 2 == 0) spawnSecondary();
        if(stageProgress % 10 == 0) getWorld().playSound(chicken, Sound.ENTITY_CHICKEN_HURT, 1.5f, (float)Math.random() * 0.1f + 0.95f);




        getWorld().playSound(chicken, Sound.ENTITY_CHICKEN_EGG, 1.5f, (float)Math.random() * 0.1f + 0.95f);

        stageProgress--;


    }

    private void spawnSecondary()
    {
        Location spawnLocation = chicken.getLocation().clone();
        spawnLocation.setY(chicken.getBoundingBox().getMaxY() - 0.3);

        SkyScramblerSecondary secondary = new SkyScramblerSecondary(spawnLocation, getOwner());

        main.getEntityManager().registerEntityTracker(secondary);
        Chicken spawn = secondary.getChicken();

        Vector velocity = new Vector();
        double yaw = Math.toRadians(Math.random() * 360);
        double pitch = Math.toRadians(Math.random() * 60 + 30);

        velocity.setX(Math.cos(pitch) * Math.cos(yaw));
        velocity.setZ(Math.cos(pitch) * Math.sin(yaw));
        velocity.setY(Math.sin(pitch));

        spawn.setVelocity(velocity.multiply(Math.random() * 2 + 6));
    }

    public void activate()
    {
        Bukkit.getServer().broadcast(Component.text("Sky scrambler activated"));
        isActivated = true;
        chicken.customName(Component.text("Sky Scrambler"));
    }
}
