package me.oakyplugin.destruction.utility.entity_raycast;

import me.oakyplugin.destruction.utility.EntityUtility;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class EntityRaycast {

    public static EntityRaycastResult raycastBlocks(Entity entity, Vector dir, double length)
    {
        EntityRaycastResult res = new EntityRaycastResult();

        res.getHitBlock(entity, dir, 0, length);

        return res;
    }






}
