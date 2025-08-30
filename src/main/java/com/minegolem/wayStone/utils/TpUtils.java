package com.minegolem.wayStone.utils;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.settings.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TpUtils {

    private static final HashMap<UUID, Boolean> animationStatus = new HashMap<>();

    public static boolean animationStatusHas(UUID id) {
        return animationStatus.containsKey(id);
    }

    public static boolean isSafeLocation(Location location) {
        try {
            Block feet = location.getBlock();

            if (feet.getType().isSolid() && feet.getLocation().add(0, 1, 0).getBlock().getType().isSolid()) {
                return false;
            }

            Block head = feet.getRelative(BlockFace.UP);

            return !head.getType().isSolid();

        } catch (Exception err) {
            Logger.log(Logger.LogLevel.ERROR, "Error while checking safe location", err);
        }
        return false;
    }

    public static boolean isSafeLocationAlsoUnder(Location location) {
        try {
            var feet = location.getBlock();
            if (feet.getType().isSolid() && feet.getLocation().add(0, 1, 0).getBlock().getType().isSolid()) {
                return false;
            }
            Block head = feet.getRelative(BlockFace.UP);
            if (head.getType().isSolid()) {
                return false;
            }

            Block ground = feet.getRelative(BlockFace.DOWN);

            return ground.getType().isSolid();
        } catch (Exception err) {
            Logger.log(Logger.LogLevel.ERROR, "Error while checking safe location", err);
        }
        return false;
    }

    public static void playAnimation(Player player, Location location, WayStone plugin, boolean safeLoc) {
        UUID id = player.getUniqueId();

        if (animationStatus.containsKey(id)) {
            return;
        }

        animationStatus.put(id, true);

        Bukkit.getScheduler().runTask( plugin, ()-> {
            player.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRIGGER, 8.0F, 0.5F);
        });

        Timer timer = new Timer();

        int duration = Settings.duration;
        int repetitions = 10;

        timer.schedule(new TimerTask() {

            private int count = 0;
            private double circleHeight = 0;
            private final double multiplier = 2 / ((double) repetitions * (double) duration);

            @Override
            public void run() {
                count++;

                circleHeight = circleHeight + multiplier;

                if (count >= (duration * repetitions)) {
                    timer.cancel();

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        teleportPlayer(player, location, plugin, safeLoc);
                    });


                    return;
                }

                player.getWorld().spawnParticle(Particle.DOLPHIN, player.getEyeLocation().add(0, 20, 0), 200, 0, 20, 0);

                for (int degree = 0; degree < 360; degree++) {
                    double radians = Math.toRadians(degree);
                    double x = Math.cos(radians) * 1.5;
                    double z = Math.sin(radians) * 1.5;
                    Location loc = player.getLocation();
                    loc.add(x, circleHeight - 0.5, z);
                    player.getWorld().spawnParticle(Particle.PORTAL, loc, 0, 0, 0, 0);
                }
            }
        }, 0, (1000 / repetitions));// wait 0 ms before doing the action and do it every x repetitions per second
    }

    private static void teleportPlayer(Player player, Location loc, WayStone plugin, boolean safeLoc) {
        UUID id = player.getUniqueId();

        animationStatus.remove(id);

        player.getWorld().playSound(loc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 8.0F, 0.5F);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (safeLoc) {
                    player.teleport(loc);
                } else {
                    player.sendMessage(Settings.LOCATION_OBSTRUCTED);
                }

            }
        }.runTask(plugin);

    }

}