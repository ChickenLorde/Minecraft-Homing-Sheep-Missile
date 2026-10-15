package me.oakyplugin.destruction.commands;

import me.oakyplugin.destruction.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GetRegisteredTrackersCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Main main = JavaPlugin.getPlugin(Main.class);

        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Registered EntityTracker count: "+main.getEntityManager().getTrackerCount()));

        return true;
    }
}
