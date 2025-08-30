package com.minegolem.wayStone.settings;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.utils.ChatUtils;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

    private static final WayStone plugin = WayStone.INSTANCE;

    // DATABASE SETTINGS

    public static final String database_host = plugin.getConfig().getString("database.host");
    public static final String database_name = plugin.getConfig().getString("database.database");
    public static final String database_user = plugin.getConfig().getString("database.username");
    public static final String database_password = plugin.getConfig().getString("database.password");
    public static final int database_port = plugin.getConfig().getInt("database.port");

    public static final List<String> disabled_worlds = plugin.getConfig().getStringList("disabled-worlds");

    public static Map<Character, String> getCraftingMaterial() {
        Map<Character, String> craftingMaterial = new HashMap<>();

        craftingMaterial.put('A', plugin.getConfig().getString("crafting.A"));
        craftingMaterial.put('B', plugin.getConfig().getString("crafting.B"));
        craftingMaterial.put('C', plugin.getConfig().getString("crafting.C"));
        craftingMaterial.put('D', plugin.getConfig().getString("crafting.D"));
        craftingMaterial.put('E', plugin.getConfig().getString("crafting.E"));
        craftingMaterial.put('F', plugin.getConfig().getString("crafting.F"));
        craftingMaterial.put('G', plugin.getConfig().getString("crafting.G"));
        craftingMaterial.put('H', plugin.getConfig().getString("crafting.H"));
        craftingMaterial.put('I', plugin.getConfig().getString("crafting.I"));

        return craftingMaterial;
    }

    public static final List<String> shape = plugin.getConfig().getStringList("crafting.shape");

    public static final int max_limit = plugin.getConfig().getInt("max-limit");

    // ITEM
    public static Component itemName = ChatUtils.serialize(plugin.getConfig().getString("item.name"));

    public static final List<String> rawLore = plugin.getConfig().getStringList("item.lore");

    // ANIMATION

    public static final int duration = plugin.getConfig().getInt("animation-duration");

    // MESSAGES

    public static Component WORLD_DISABLED = ChatUtils.serialize(plugin.getConfig().getString("messages.world_disabled"));
    public static Component WAYSTONE_PLACED = ChatUtils.serialize(plugin.getConfig().getString("messages.waystone_placed"));
    public static Component WAYSTONE_RENAMED = ChatUtils.serialize(plugin.getConfig().getString("messages.waystone_renamed"));
    public static Component CANNOT_DESTROY_WAYSTONE = ChatUtils.serialize(plugin.getConfig().getString("messages.cannot_destroy_waystone"));
    public static Component CANNOT_RENAME_NOT_OWNED = ChatUtils.serialize(plugin.getConfig().getString("messages.cannot_rename_not_owned"));
    public static Component TOO_FAR_FROM_WAYSTONE = ChatUtils.serialize(plugin.getConfig().getString("messages.too_far_from_waystone"));
    public static Component WAYSTONE_REMOVED = ChatUtils.serialize(plugin.getConfig().getString("messages.waystone_removed"));
    public static Component LOCATION_OBSTRUCTED = ChatUtils.serialize(plugin.getConfig().getString("messages.location_obstructed"));
    public static Component TOO_CLOSE_TO_WAYSTONE = ChatUtils.serialize(plugin.getConfig().getString("messages.too_close_to_waystone"));
    public static Component NOT_ENOUGH_XP = ChatUtils.serialize(plugin.getConfig().getString("messages.not_enough_xp"));
    public static Component NO_PERMISSION = ChatUtils.serialize(plugin.getConfig().getString("messages.no_permission"));
    public static Component PLAYER_OFFLINE = ChatUtils.serialize(plugin.getConfig().getString("messages.player_offline"));
    public static Component WAYSTONE_GIVEN = ChatUtils.serialize(plugin.getConfig().getString("messages.waystone_given"));
    public static Component MAX_WAYSTONES_REACHED = ChatUtils.serialize(plugin.getConfig().getString("messages.max_waystones_reached"));

    // GUI

    public static final Component GUI_NAME = ChatUtils.serialize(plugin.getConfig().getString("gui.name"));

    public static final String WAYSTONE_NAME_FORMAT = plugin.getConfig().getString("gui.waystone.name_format");
    public static final List<String> WAYSTONE_LORE = plugin.getConfig().getStringList("gui.waystone.lore");

}
