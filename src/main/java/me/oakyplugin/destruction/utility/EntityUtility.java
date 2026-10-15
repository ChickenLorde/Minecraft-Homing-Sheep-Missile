package me.oakyplugin.destruction.utility;

import net.kyori.adventure.chat.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class EntityUtility {

    public static boolean isTouchingBlocks(World world, Vector from, Vector to)
    {
        double minAX = Math.min(from.getX(),to.getX());
        double minAY = Math.min(from.getY(),to.getY());
        double minAZ = Math.min(from.getZ(),to.getZ());

        double maxAX = Math.max(from.getX(),to.getX());
        double maxAY = Math.max(from.getY(),to.getY());
        double maxAZ = Math.max(from.getZ(),to.getZ());

        int minX =(int)Math.floor(minAX);
        int minY =(int)Math.floor(minAY);
        int minZ =(int)Math.floor(minAZ);

        int maxX =(int)Math.ceil(maxAX);
        int maxY =(int)Math.ceil(maxAY);
        int maxZ =(int)Math.ceil(maxAZ);

        for(int x = minX; x <= maxX; x++)
            for(int y = minY; y <= maxY; y++)
                for(int z = minZ; z <= maxZ; z++)
                {
                    Block block = world.getBlockAt(x,y,z);

                    if(!block.isSolid()) continue;

                    VoxelShape shape = block.getCollisionShape();

                    Collection<BoundingBox> boxes = shape.getBoundingBoxes();

                    if(boxes.isEmpty()) continue;

                    for(BoundingBox box : boxes)
                    {
                        double minBX = box.getMinX() + x;
                        double minBY = box.getMinY() + y;
                        double minBZ = box.getMinZ() + z;

                        double maxBX = box.getMaxX() + x;
                        double maxBY = box.getMaxY() + y;
                        double maxBZ = box.getMaxZ() + z;

                        if(maxBX >= minAX && minBX <= maxAX &&
                                maxBY >= minAY && minBY <= maxAY &&
                                maxBZ >= minAZ && minBZ <= maxAZ) return true;
                    }
                }
        return false;
    }

    public static boolean isTouchingBlocks(World world, BoundingBox box)
    {

        int minX =(int)Math.floor(box.getMinX());
        int minY =(int)Math.floor(box.getMinY());
        int minZ =(int)Math.floor(box.getMinZ());

        int maxX =(int)Math.ceil(box.getMaxX());
        int maxY =(int)Math.ceil(box.getMaxY());
        int maxZ =(int)Math.ceil(box.getMaxZ());

        for(int x = minX; x <= maxX; x++)
            for(int y = minY; y <= maxY; y++)
                for(int z = minZ; z <= maxZ; z++)
                {
                    Block block = world.getBlockAt(x,y,z);

                    if(!block.isSolid()) continue;

                    if(block.getCollisionShape().overlaps(box)) return true;
                }
        return false;
    }

    public static boolean isTouchingEntities(World world, Entity executor, Set<Entity> ignoreEntities, double extraSize)
    {
        BoundingBox box = executor.getBoundingBox().clone();
        box.expand(extraSize);
        Collection<Entity> entities = world.getNearbyEntities(box);

        entities.remove(executor);
        for(Entity entity : ignoreEntities)
            entities.remove(entity);


        return !entities.isEmpty();
    }

    public static Collection<Entity> getTouchingEntities(World world, Entity executor, double extraSize)
    {
        BoundingBox box = executor.getBoundingBox().clone();
        box.expand(extraSize);
        Collection<Entity> entities = world.getNearbyEntities(box);

        entities.remove(executor);


        return entities;
    }
}
