package com.minegolem.wayStone.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.data.WaystoneData;
import com.minegolem.wayStone.menu.WayStoneMenu;
import com.minegolem.wayStone.settings.Settings;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class WaystoneListener implements Listener {

    private final WayStone plugin;

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(plugin.getWaystoneKey(), PersistentDataType.STRING)) return;

        List<String> disabled_worlds = Settings.disabled_worlds;

        Player player = event.getPlayer();

        if (disabled_worlds.contains(event.getPlayer().getWorld().getName())) {
            player.sendMessage(Component.text("Non puoi piazzare una waystone in questo mondo!"));

            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        plugin.getDatabase().getWaystonesByPlayerUUID(player.getUniqueId()).thenAccept(waystones -> {
            if (waystones.size() >= Settings.max_limit) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(Settings.MAX_WAYSTONES_REACHED);
                });

                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                WaystoneData waystoneData = new WaystoneData(
                        player.getUniqueId(),
                        "Waystone",
                        event.getBlock().getLocation()
                );

                plugin.getDatabase().setWaystone(waystoneData);

                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().remove(item);
                }

                CustomBlockData customBlockData = new CustomBlockData(event.getBlock(), plugin);
                customBlockData.set(WayStone.INSTANCE.uuidNSK, PersistentDataType.STRING, player.getUniqueId().toString());
                customBlockData.set(WayStone.INSTANCE.nameNSK, PersistentDataType.STRING, "null");

                event.getBlock().setType(Material.LODESTONE);

                player.sendMessage(Settings.WAYSTONE_PLACED);
            });
        });
    }


    @EventHandler
    public void onRename(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getClickedBlock() == null) return;

        if (item == null) return;
        if (item.getType() != Material.NAME_TAG) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        ItemMeta meta = item.getItemMeta();

        Component nameTagName = meta.displayName();

        CustomBlockData customBlockData = new CustomBlockData(event.getClickedBlock(), plugin);

        if (!customBlockData.has(WayStone.INSTANCE.nameNSK)) return;

        if (nameTagName == null) return;

        UUID waystoneOwnerUUID = UUID.fromString(Objects.requireNonNull(customBlockData.get(WayStone.INSTANCE.uuidNSK, PersistentDataType.STRING)));

        if (!waystoneOwnerUUID.equals(player.getUniqueId())) {
            player.sendMessage(Settings.CANNOT_RENAME_NOT_OWNED);
            return;
        }

        customBlockData.set(WayStone.INSTANCE.nameNSK, PersistentDataType.STRING, nameTagName.examinableName());
        plugin.getDatabase().updateWaystoneNameByLocation(new WaystoneData(player.getUniqueId(), PlainTextComponentSerializer.plainText().serialize(nameTagName), event.getClickedBlock().getLocation()));
        
        player.getInventory().removeItem(item);

        player.sendMessage(Settings.WAYSTONE_RENAMED);
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.NAME_TAG) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        CustomBlockData customBlockData = new CustomBlockData(Objects.requireNonNull(event.getClickedBlock()), plugin);

        if (!customBlockData.has(WayStone.INSTANCE.nameNSK)) return;

        plugin.getGuiManager().openGUI(new WayStoneMenu(plugin), player);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        CustomBlockData customBlockData = new CustomBlockData(event.getBlock(), plugin);

        if (!customBlockData.has(WayStone.INSTANCE.nameNSK)) return;

        event.getPlayer().sendMessage(Settings.CANNOT_DESTROY_WAYSTONE);

        event.setCancelled(true);
    }
}
