package me.oakyplugin.destruction.items;

import me.oakyplugin.destruction.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class EntityTracker {

    protected final Main main = JavaPlugin.getPlugin(Main.class);
    private Entity entity;
    private final Entity owner;

    private final World world;

    private boolean isActive;

    public EntityTracker(Location location, EntityType type, Entity owner)
    {
        this.owner = owner;
        world = location.getWorld();
        this.entity = world.spawnEntity(location,type);
        entity.setPersistent(true);
        //entity.getPersistentDataContainer().set(Main.trackedEntityKey, PersistentDataType.BOOLEAN, true);
        isActive = true;

    }

    public void end()
    {
        if(entity != null) {
            entity.remove();
            entity = null;
        }
        isActive = false;
    }

    public Entity getEntity() {return entity;}
    public World getWorld() {return world;}

    public boolean getIsActive() {return isActive;}
    public Entity getOwner() {return owner;}

    public abstract void tick();

}
