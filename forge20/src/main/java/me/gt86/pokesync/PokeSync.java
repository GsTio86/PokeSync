package me.gt86.pokesync;

import me.gt86.pokesync.hook.HuskSyncAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PokeSync extends JavaPlugin {

    public static final String PLUGIN_ID = "pokesync";
    public HuskSyncAPIHook huskSyncAPIHook;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("HuskSync") != null) {
            this.huskSyncAPIHook = new HuskSyncAPIHook(this);
        }
    }

}
