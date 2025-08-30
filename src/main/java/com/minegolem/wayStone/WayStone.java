package com.minegolem.wayStone;

import com.minegolem.wayStone.commands.WaystoneGive;
import com.minegolem.wayStone.commands.WaystoneSee;
import com.minegolem.wayStone.craftings.WayStoneCrafting;
import com.minegolem.wayStone.database.Database;
import com.minegolem.wayStone.database.MysqlDatabase;
import com.minegolem.wayStone.listeners.WaystoneListener;
import com.minegolem.wayStone.managers.menu.GUIManager;
import com.minegolem.wayStone.managers.menu.listener.GuiListener;
import com.minegolem.wayStone.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class WayStone extends JavaPlugin {

    public static WayStone INSTANCE = null;
    private Database database;
    public NamespacedKey waystoneKey = new NamespacedKey(this, "waystone");

    public final NamespacedKey waystoneCraftingKey = new NamespacedKey(this, "waystone_crafting");

    public final NamespacedKey idNSK = new NamespacedKey(this, "waystone_id");
    public final NamespacedKey uuidNSK = new NamespacedKey(this, "waystone_player_uuid");
    public final NamespacedKey nameNSK = new NamespacedKey(this, "waystone_name");

    @Getter
    public GUIManager guiManager;

    @Override
    public void onEnable() {
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "WAYSTONE LOADING... (Developed by Minegolem)");
        Logger.log(Logger.LogLevel.OUTLINE, " ");

        INSTANCE = this;

        Logger.log(Logger.LogLevel.INFO, "Loading Config");

        this.saveDefaultConfig();

        Logger.log(Logger.LogLevel.SUCCESS, "Config loaded Successfully");
        Logger.log(Logger.LogLevel.INFO, "Loading Database");

        this.database = new MysqlDatabase(this);

        this.database.initialize();

        Logger.log(Logger.LogLevel.SUCCESS, "Database initialized Successfully");
        Logger.log(Logger.LogLevel.INFO, "Loading Listeners");

        this.guiManager = new GUIManager();
        getServer().getPluginManager().registerEvents(new WaystoneListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this.guiManager), this);

        Logger.log(Logger.LogLevel.SUCCESS, "Listener loaded Successfully");
        Logger.log(Logger.LogLevel.INFO, "Loading craftings");

        Recipe recipe = WayStoneCrafting.getRecipe();

        Bukkit.addRecipe(recipe);

        Logger.log(Logger.LogLevel.SUCCESS, "Recipe loaded Successfully");
        Logger.log(Logger.LogLevel.INFO, "Loading commands");

        Objects.requireNonNull(getCommand("waystonesee")).setExecutor(new WaystoneSee(this));
        Objects.requireNonNull(getCommand("waystonegive")).setExecutor(new WaystoneGive());

        Logger.log(Logger.LogLevel.SUCCESS, "Command loaded Successfully");

        Logger.log(Logger.LogLevel.OUTLINE, " ");

        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");

    }

    @Override
    public void onDisable() {
        this.database.terminate();

        getServer().removeRecipe(this.waystoneCraftingKey);
    }
}
