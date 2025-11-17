package com.minegolem.wayStone.menu;

import com.jeff_media.customblockdata.CustomBlockData;
import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.data.WaystoneData;
import com.minegolem.wayStone.managers.menu.InventoryButton;
import com.minegolem.wayStone.managers.menu.InventoryGUI;
import com.minegolem.wayStone.settings.Settings;
import com.minegolem.wayStone.utils.ChatUtils;
import com.minegolem.wayStone.utils.Experience;
import com.minegolem.wayStone.utils.Logger;
import com.minegolem.wayStone.utils.TpUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WayStoneMenu extends InventoryGUI {

    private final WayStone plugin;
    private final Player targetPlayer;
    private final boolean isAdminView;

    public WayStoneMenu(WayStone plugin) {
        this(plugin, null);
    }

    public WayStoneMenu(WayStone plugin, Player target) {
        this.plugin = plugin;
        this.targetPlayer = target;
        this.isAdminView = target != null;
    }

    @Override
    protected Inventory createInventory() {
        return WayStone.INSTANCE.getServer().createInventory(null, 6*9, Settings.GUI_NAME);
    }

    @Override
    public void decorate(Player player) {
        Player dataOwner = isAdminView ? targetPlayer : player;

        plugin.getDatabase().getWaystonesByPlayerUUID(dataOwner.getUniqueId()).thenAccept(waystones -> {

            Bukkit.getScheduler().runTask(plugin, () -> {

                List<Integer> decorationSlots = new ArrayList<>();

                for (int i = 0; i <= 8; i++) {
                    if (i == 4) {
                        continue;
                    }
                    decorationSlots.add(i);
                }
                for (int i = 45; i <= 53; i++) decorationSlots.add(i);

                decorationSlots.add(9);
                decorationSlots.add(17);
                decorationSlots.add(18);
                decorationSlots.add(26);
                decorationSlots.add(27);
                decorationSlots.add(35);
                decorationSlots.add(36);
                decorationSlots.add(44);

                decorationSlots.forEach(slot -> {
                    this.addButton(slot, this.decorations());
                });

                this.addButton(4, this.playerHead(player));

                List<Integer> freeSlots = new ArrayList<>();
                for (int slot = 0; slot <= 53; slot++) {
                    if (slot == 4) {
                        continue;
                    }
                    if (!decorationSlots.contains(slot)) {
                        freeSlots.add(slot);
                    }
                }

                Iterator<Integer> slotIterator = freeSlots.iterator();

                waystones.forEach(waystone -> {
                    if (slotIterator.hasNext()) {
                        int slot = slotIterator.next();
                        this.addButton(slot, this.wayStonesButton(waystone));
                    } else {
                        Logger.log(Logger.LogLevel.ERROR, "Not enough slots to display all Waystones!");
                    }
                });

                super.decorate(player);
            });
        });
    }

    private InventoryButton wayStonesButton(WaystoneData data) {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = new ItemStack(Material.LODESTONE);
                    ItemMeta meta = item.getItemMeta();

                    assert Settings.WAYSTONE_NAME_FORMAT != null;
                    meta.displayName(ChatUtils.serialize(
                            String.format(Settings.WAYSTONE_NAME_FORMAT, data.name())
                    ));

                    List<Component> lore = new ArrayList<>();

                    for (String line : Settings.WAYSTONE_LORE) {
                        String formatted = String.format(
                                line,
                                (int) data.location().getX(),
                                (int) data.location().getY(),
                                (int) data.location().getZ(),
                                data.location().getWorld().getName()
                        );
                        lore.add(ChatUtils.serialize(formatted));
                    }

                    meta.lore(lore);

                    item.setItemMeta(meta);

                    return item;
                })
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    Location waystoneLocation = data.location();

                    ItemStack item = event.getCurrentItem();

                    switch (event.getClick()) {
                        case SHIFT_RIGHT -> {

                            if (item != null) {
                                player.getInventory().remove(item);
                            }

                            if (!isAtLeastBlocksAway(player, waystoneLocation, 3.0)) {
                                player.updateInventory();
                                player.sendMessage(Settings.TOO_FAR_FROM_WAYSTONE);

                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    player.closeInventory();
                                }, 4L);

                                return;
                            }

                            CustomBlockData waystoneData = new CustomBlockData(waystoneLocation.getBlock(), plugin);

                            waystoneData.clear();
                            waystoneLocation.getBlock().setType(Material.AIR);
                            plugin.getDatabase().deleteWaystone(data);

                            player.updateInventory();
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                player.closeInventory();
                            }, 4L);

                            player.sendMessage(Settings.WAYSTONE_REMOVED);
                        }
                        case LEFT -> {
                            player.closeInventory();

                            if (!isAtLeastBlocksAway(player, waystoneLocation, 6.0)) {
                                if (hasEnoughExpForDistance(player, waystoneLocation)) {
                                    Experience.changeExp(player, -getRequiredXP(player, waystoneLocation));
                                    TpUtils.playAnimation(player, waystoneLocation.add(.5f, 1f, .5f), plugin, TpUtils.isSafeLocationAlsoUnder(waystoneLocation));
                                } else {
                                    player.sendMessage(Settings.NOT_ENOUGH_XP);
                                }
                            } else {
                                player.sendMessage(Settings.TOO_CLOSE_TO_WAYSTONE);
                            }
                        }
                    }
                });
    }

    private InventoryButton decorations() {
        return new InventoryButton()
                .creator(player -> {
                    return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                })
                .consumer(event -> {
                });
    }

    private InventoryButton playerHead(Player playerOwner) {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();

                    if (meta != null) {
                        meta.setOwningPlayer(player);
                        meta.displayName(ChatUtils.serialize("<!i><bold>").append(player.displayName()));
                        skull.setItemMeta(meta);
                    }

                    return skull;
                })
                .consumer(event -> {

                });
    }

    private boolean isAtLeastBlocksAway(Player player, Location target, double blocks) {
        Location playerLocation = player.getLocation();

        double distance = playerLocation.distance(target);

        return distance <= blocks;
    }

    private static boolean hasEnoughExpForDistance(Player player, Location target) {
        double distance = player.getLocation().distance(target);

        int requiredLevels = (int) Math.ceil(distance / 1000.0);

        int requiredTotalExp = Experience.getExpFromLevel(requiredLevels);

        int playerTotalExp = Experience.getExp(player);

        return playerTotalExp >= requiredTotalExp;
    }

    private static int getRequiredXP(Player player, Location target) {
        double distance = player.getLocation().distance(target);

        int requiredLevels = (int) Math.ceil(distance / 1000.0);

        return Experience.getExpFromLevel(requiredLevels);
    }


}
