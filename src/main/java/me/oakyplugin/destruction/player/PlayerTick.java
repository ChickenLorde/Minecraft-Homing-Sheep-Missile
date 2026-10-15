package me.oakyplugin.destruction.player;

import me.oakyplugin.destruction.Main;
import me.oakyplugin.destruction.data.PlayerData;
import me.oakyplugin.destruction.items.Usable;
import me.oakyplugin.destruction.items.homing_sheep.HomingSheepItem;
import me.oakyplugin.destruction.utility.PlayerUtility;
import me.oakyplugin.destruction.utility.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Input;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;


public class PlayerTick {

    private final Main main;
    public PlayerTick(Main main)
    {
        this.main = main;
    }

    public void tick(Player p)
    {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();


        PlayerData data = PlayerUtility.getPlayerData(p);


        Usable usable = Utility.getPlayerUsable(p);

        if(usable instanceof HomingSheepItem homingSheepItem)
            holdingHomingSheep(p, homingSheepItem);

        if(isPlayerRightClicking(p))
        {
            Long rightClickTime = PlayerUtility.getRightClickTick(p);

            //p.sendMessage("Right Click detected! " + PlayerUtility.getRightClickTick(p));
            Objects.requireNonNull(scoreboard.getObjective("right_click")).getScore(p.getName()).setScore(0);




            if(usable != null && rightClickTime < 1)
            {
                if(!(usable instanceof HomingSheepItem))
                    usable.use(p, p.getEyeLocation());
            }

            rightClickTime++;
            PlayerUtility.getRightClickTicks().replace(p, rightClickTime);
        }
        else
        {
            //p.sendMessage("Right Click Released!");
            PlayerUtility.getRightClickTicks().remove(p);

        }
            Input in = p.getCurrentInput();

            if(in.isSneak())
            {
                data.timeUnsneaked = 0;
                data.timeSneaking++;
            }
            else
            {
                data.timeUnsneaked++;
                data.timeSneaking = 0;
            }

            if(data.timeSneaking == 1)
                data.consecutiveSneaks++;

            if(data.timeSneaking > 3 || data.timeUnsneaked > 5) data.consecutiveSneaks = 0;

            //if(data.consecutiveSneaks == 2) p.sendMessage(Component.text("Double sneak"));

    }

    private void holdingHomingSheep(Player p, HomingSheepItem item)
    {
        if(isPlayerRightClicking(p) && PlayerUtility.getRightClickTick(p) < 1) {
            if (p.isSneaking()) item.use(p, p.getEyeLocation());
            else item.spawnSheep(p, p.getEyeLocation());
        }

        item.holding(p);

    }

    public boolean isPlayerRightClicking(Player p)
    {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective("right_click").getScore(p.getName()).getScore() > 0;
    }

    private int getRightClick(Player p)
    {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective("right_click").getScore(p.getName()).getScore();
    }




}
