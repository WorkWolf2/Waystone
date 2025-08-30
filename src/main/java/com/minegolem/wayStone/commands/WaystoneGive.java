package com.minegolem.wayStone.commands;

import com.minegolem.wayStone.data.WaystoneData;
import com.minegolem.wayStone.settings.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WaystoneGive implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("waystone.admin.give")) {
            player.sendMessage(Settings.NO_PERMISSION);
        }

        ItemStack waystone = WaystoneData.getWaystoneItem();

        player.getInventory().addItem(waystone);
        player.sendMessage(Settings.WAYSTONE_GIVEN);
        return true;
    }
}
