package me.oakyplugin.destruction.utility.entity_raycast;

import me.oakyplugin.destruction.utility.EntityUtility;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class EntityRaycastResult
{

    EntityRaycastResult() {}

    private double length;
    private Block hitBlock;
    private Vector hitPos;

    protected void getHitBlock(Entity entity, Vector dir, double length, double maxLength)
    {
        Vector shift = dir.clone().multiply(length);
        for(int i = 0; i <= maxLength; i++)
        {

        }
        if(length >= maxLength) {
            this.length = maxLength;
            hitBlock = null;
            return;
        }
        hitBlock = entity.getLocation().clone().add(shift).getBlock();
        this.length = length;
    }

    public Vector getHitPosition() {return hitPos;}
    public double getHitDistance() {return length;}
    public Block getHitBlock() {return hitBlock;}

    public boolean hasHitBlock() {return hitBlock != null; }

}
