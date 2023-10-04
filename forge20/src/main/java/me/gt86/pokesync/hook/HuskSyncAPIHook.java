package me.gt86.pokesync.hook;

import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.Config;
import me.gt86.pokesync.data.PixelmonDataType;
import net.william278.husksync.HuskSync;
import net.william278.husksync.api.BukkitHuskSyncAPI;
import net.william278.husksync.data.DataSnapshot;
import net.william278.husksync.event.BukkitDataSaveEvent;
import net.william278.husksync.event.BukkitSyncCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.function.Consumer;


public class HuskSyncAPIHook implements Listener {

    private final PokeSync pokeSync;
    private final BukkitHuskSyncAPI huskSyncAPI;

    public HuskSyncAPIHook(PokeSync pokeSync) {
        this.pokeSync = pokeSync;
        this.huskSyncAPI = BukkitHuskSyncAPI.getInstance();
        register();
    }

    private void register() {
        HuskSync huskSync = huskSyncAPI.getPlugin();
        for (PixelmonDataType type : PixelmonDataType.values()) {
            huskSyncAPI.registerDataSerializer(type.getIdentifier(), type.createSerializer(huskSync));
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this.pokeSync);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHuskSyncDataSave(BukkitDataSaveEvent event) {
        event.editData(new Consumer<DataSnapshot.Unpacked>() {
            @Override
            public void accept(DataSnapshot.Unpacked unpacked) {
                UUID uuid = event.getUser().getUuid();
                for (PixelmonDataType type : PixelmonDataType.values()) {
                    if (Config.isEnable(type)) {
                        unpacked.setData(type.getIdentifier(), type.createData(uuid));
                    }
                }
            }
        });
    }

    // Need Test
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHuskSyncDataComplete(BukkitSyncCompleteEvent event) {
        UUID uuid = event.getUser().getUuid();
        PlayerPartyStorage party = StorageProxy.getPartyNow(uuid);
        PCStorage pc = StorageProxy.getPCForPlayerNow(uuid);
        if (party != null)
            StorageProxy.getSaveScheduler().save(party);
        if (pc != null)
            StorageProxy.getSaveScheduler().save(pc);
    }
}
