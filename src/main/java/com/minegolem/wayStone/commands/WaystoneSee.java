package com.minegolem.wayStone.commands;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.menu.WayStoneMenu;
import com.minegolem.wayStone.settings.Settings;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class WaystoneSee implements CommandExecutor {

    private final WayStone plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("waystone.admin.see")) {
            player.sendMessage(Settings.NO_PERMISSION);
        }

        if (args.length == 0) return false;

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Settings.PLAYER_OFFLINE);
            return true;
        }

        plugin.getGuiManager().openGUI(new WayStoneMenu(plugin, target), player);
        return true;
    }
}
