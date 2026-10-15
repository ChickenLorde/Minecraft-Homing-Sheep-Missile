package me.oakyplugin.destruction.managers;

import me.oakyplugin.destruction.Main;
import me.oakyplugin.destruction.items.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EntityManager {

    private final Set<EntityTracker> entityTrackers = new HashSet<>();

    private final Set<EntityTracker> pendingEntityTrackers = new HashSet<>();

    private final Map<Entity, UUID> homingSheepTargets = new HashMap<>();


    public void tick()
    {
        Collection<EntityTracker> toRemove = new HashSet<>();
        for (EntityTracker entityTracker : entityTrackers) {
            entityTracker.tick();
            if (!entityTracker.getIsActive() || !entityTracker.getEntity().isValid()) toRemove.add(entityTracker);
        }

        entityTrackers.removeAll(toRemove);

        if(!pendingEntityTrackers.isEmpty())
        {
            entityTrackers.addAll(pendingEntityTrackers);
            pendingEntityTrackers.clear();
        }
    }
    //region Entity Trackers
    public void registerEntityTracker(EntityTracker entityTracker)
    {
        pendingEntityTrackers.add(entityTracker);
    }

    public EntityTracker getTrackerFromEntity(Entity entity)
    {
        if(entity == null) return null;
        for(EntityTracker tracker : entityTrackers)
        {
            if(tracker == null || tracker.getEntity() == null) continue;
            if(tracker.getEntity().equals(entity)) return tracker;
        }
        return null;
    }

    public int getTrackerCount() {return entityTrackers.size();}
    //endregion

    public void setHomingSheepTarget(Entity entity, Entity target)
    {
        if(!homingSheepTargets.containsKey(entity)) homingSheepTargets.put(entity, target.getUniqueId());
        else homingSheepTargets.replace(entity, target.getUniqueId());
    }

    public LivingEntity getHomingSheepTarget(Entity entity)
    {
        if(!homingSheepTargets.containsKey(entity)) return null;

        LivingEntity target = (LivingEntity) Bukkit.getEntity(homingSheepTargets.get(entity));

        if(target == null || !target.isValid()) return null;

        return target;
    }

}
