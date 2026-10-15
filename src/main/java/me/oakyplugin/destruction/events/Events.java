package me.oakyplugin.destruction.events;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.oakyplugin.destruction.Main;
import me.oakyplugin.destruction.data.PlayerData;
import me.oakyplugin.destruction.items.EntityTracker;
import me.oakyplugin.destruction.items.phantom_jet.PhantomJetEntity;
import me.oakyplugin.destruction.items.sky_scrambler.SkyScramblerEntity;
import me.oakyplugin.destruction.utility.PlayerUtility;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Events implements Listener {

    private final Main main = JavaPlugin.getPlugin(Main.class);

    @EventHandler
    public void playerClickEntity(PrePlayerAttackEntityEvent event) {
            playerAttackEntity(event);
    }

    private void playerAttackEntity(PrePlayerAttackEntityEvent event)
    {

        Player p = event.getPlayer();
        Entity entity = event.getAttacked();


            EntityTracker tracker = main.getEntityManager().getTrackerFromEntity(entity);
            if (!(tracker instanceof SkyScramblerEntity skyScrambler)) return;

            skyScrambler.activate();

    }

    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event)
    {
        Player p = event.getPlayer();

        if(p.getVehicle() != null) return;

        Entity entity = event.getRightClicked();

        EntityTracker tracker = main.getEntityManager().getTrackerFromEntity(entity);

        if(tracker instanceof PhantomJetEntity phantomJet)
        {
            phantomJet.mount(p);
        }
    }

    @EventHandler
    public void playerDismount(EntityDismountEvent event)
    {
        Entity e = event.getEntity();
        if(!(e instanceof Player p)) return;

        Entity dismount = event.getDismounted();
        if(!dismount.isValid()) return;
        EntityTracker tracker = main.getEntityManager().getTrackerFromEntity(dismount);

        if(tracker == null) return;

        if(!(tracker instanceof PhantomJetEntity jet)) return;

        PlayerData d = PlayerUtility.getPlayerData(p);

        if(d != null && d.consecutiveSneaks >= 2) return;

        event.setCancelled(true);
    }
}
