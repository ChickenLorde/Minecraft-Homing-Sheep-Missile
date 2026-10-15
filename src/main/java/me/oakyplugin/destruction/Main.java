package me.oakyplugin.destruction;

import me.oakyplugin.destruction.commands.GetCustomItemCommand;
import me.oakyplugin.destruction.commands.GetRegisteredTrackersCommand;
import me.oakyplugin.destruction.events.Events;
import me.oakyplugin.destruction.items.dolphin_rocket.DolphinRocketItem;
import me.oakyplugin.destruction.items.homing_sheep.HomingSheepItem;
import me.oakyplugin.destruction.items.phantom_jet.PhantomJetItem;
import me.oakyplugin.destruction.items.proximity_pufferfish.ProximityPufferfishItem;
import me.oakyplugin.destruction.items.sky_scrambler.SkyScramblerItem;
import me.oakyplugin.destruction.items.cow_bazooka.CowBazookaItem;
import me.oakyplugin.destruction.items.pig_cannon.PigBazookaItem;
import me.oakyplugin.destruction.managers.EntityManager;
import me.oakyplugin.destruction.player.PlayerTick;
import me.oakyplugin.destruction.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main extends JavaPlugin {

    private final NamespacedKey usableIdKey = new NamespacedKey(this,"usable_id");

    private final EntityManager entityManager = new EntityManager();

    //public static final NamespacedKey trackedEntityKey = new NamespacedKey(JavaPlugin.getPlugin(Main.class), "trackedEntity");
    @Override
    public void onEnable() {
        PlayerTick playerTick = new PlayerTick(this);

        registerEvents();

        installDatapack();
        registerUsables();
        registerCommands();

        getLogger().info("Plugin enabled successfully");

        new BukkitRunnable()
        {
            @Override
            public void run() {
                for(Player player : getServer().getOnlinePlayers())
                    playerTick.tick(player);
                entityManager.tick();
            }
        }.runTaskTimer(this, 0, 1);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void installDatapack()
    {
        try
        {
            File worldFolder = Bukkit.getWorld("world").getWorldFolder();
            File datapacksFolder = new File(worldFolder, "datapacks");

            if (!datapacksFolder.exists()) {
                datapacksFolder.mkdirs();
            }

            String resourcePath = "plugin_datapacks/";

            try (JarFile jar = new JarFile(getFile())) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (entry.getName().startsWith(resourcePath)) {
                        File destFile = new File(datapacksFolder,
                                entry.getName().substring(resourcePath.length()));

                        if (entry.isDirectory()) {
                            destFile.mkdirs();
                        } else {
                            destFile.getParentFile().mkdirs();

                            try (InputStream in = jar.getInputStream(entry)) {
                                Files.copy(in, destFile.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            }

            getLogger().info("Datapack copied successfully!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerUsables()
    {
        Utility.registerUsable(new PigBazookaItem());
        Utility.registerUsable(new CowBazookaItem());
        Utility.registerUsable(new SkyScramblerItem());
        Utility.registerUsable(new HomingSheepItem());
        Utility.registerUsable(new DolphinRocketItem());
        Utility.registerUsable(new ProximityPufferfishItem());
        Utility.registerUsable(new PhantomJetItem());
    }
    private void registerCommands() {
        getCommand("getcitem").setExecutor(new GetCustomItemCommand());
        getCommand("getTrackerCount").setExecutor(new GetRegisteredTrackersCommand());
    }

    private void registerEvents()
    {
     registerEvent(new Events());
    }

    private void registerEvent(Listener event)
    {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(event, this);
    }

    public NamespacedKey getUsableIdKey()
    {
        return usableIdKey;
    }

    public EntityManager getEntityManager() {return entityManager;}

}
