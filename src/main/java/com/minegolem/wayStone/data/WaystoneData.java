package com.minegolem.wayStone.data;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.settings.Settings;
import com.minegolem.wayStone.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record WaystoneData(UUID player_uuid, String name, Location location) {

    public static ItemStack getWaystoneItem() {
        ItemStack waystone = new ItemStack(Material.LODESTONE);
        ItemMeta meta = waystone.getItemMeta();

        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(WayStone.INSTANCE.getWaystoneKey(), PersistentDataType.STRING, "placeable_waystone");

        meta.displayName(Settings.itemName);

        List<String> rawLore = Settings.rawLore;
        List<Component> lore = new ArrayList<>();

        rawLore.forEach(s -> lore.add(ChatUtils.serialize(s)));

        meta.lore(lore);

        waystone.setItemMeta(meta);

        return waystone;
    }
}
