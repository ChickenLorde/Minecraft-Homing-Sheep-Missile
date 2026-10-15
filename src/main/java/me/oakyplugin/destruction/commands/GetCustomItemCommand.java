package me.oakyplugin.destruction.commands;

import me.oakyplugin.destruction.items.Usable;
import me.oakyplugin.destruction.utility.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetCustomItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player p) || args.length < 1)
        {
            Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize("<red>Invalid args"));
            return false;
        }

        String id = args[0];

        Usable usable = Utility.getUsable(id);
        if(usable == null)
        {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usable not found"));

            return false;
        }

        p.getInventory().addItem(usable.getItem());
        p.sendMessage("Gave new item");
        return true;
    }
}
