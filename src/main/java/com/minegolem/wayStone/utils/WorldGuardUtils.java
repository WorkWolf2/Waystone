package com.minegolem.wayStone.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class WorldGuardUtils {

    private static final WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();

    public static boolean canPlace(Player player, Location location) {
        if (player.getGameMode() == GameMode.CREATIVE) return true;

        try {
            LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
            com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(localPlayer.getWorld());

            if (regionManager == null) return false;

            if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
                return true;
            }

            ApplicableRegionSet regions = regionManager.getApplicableRegions(wgLocation.toVector().toBlockPoint());

            if (regions.size() == 0 || (regions.size() == 1 && regions.getRegions().iterator().next().getId().equalsIgnoreCase("__global__"))) {
                return true;
            }

            for (ProtectedRegion region : regions) {
                if (region.getId().equalsIgnoreCase("__global__")) continue;

                if (!(region.isMember(localPlayer) || region.isOwner(localPlayer))) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§eErrore nel controllo protezioni, operazione permessa.");
            return true;
        }
    }

    public static boolean isInOwnClaim(Player player, Location location) {
        try {
            LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
            com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(localPlayer.getWorld());
            if (regionManager == null) return false;

            ApplicableRegionSet regions = regionManager.getApplicableRegions(wgLocation.toVector().toBlockPoint());
            for (ProtectedRegion region : regions) {
                if (region.getId().equalsIgnoreCase("__global__")) continue;
                if (region.isMember(localPlayer) || region.isOwner(localPlayer)) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }
}

