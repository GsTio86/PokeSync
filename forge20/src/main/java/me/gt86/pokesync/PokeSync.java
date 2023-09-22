package me.gt86.pokesync;

import me.gt86.pokesync.command.ReloadCommand;
import me.gt86.pokesync.hook.HuskSyncAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PokeSync extends JavaPlugin {

    public static final String PLUGIN_ID = "pokesync";

    private FileConfiguration config = getConfig();

    private HuskSyncAPIHook huskSyncAPIHook;

    private static PokeSync instance;

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        initHooks();
        initCommand();
    }


    private void initConfig() {
        saveDefaultConfig();
        this.config = getConfig();
    }

    public void reloadConfig() {
        saveDefaultConfig();
        reloadConfig();
        this.config = getConfig();
    }

    private void initHooks() {
        if (Bukkit.getPluginManager().getPlugin("HuskSync") != null) {
            this.huskSyncAPIHook = new HuskSyncAPIHook(this);
            getLogger().info("PokeSync has been enabled!");
        } else {
            getLogger().warning("HuskSync is not installed, PokeSync will not work!");
        }
    }

    private void initCommand() {
        new ReloadCommand();
    }

    public static PokeSync getInstance() {
        return instance;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

}
