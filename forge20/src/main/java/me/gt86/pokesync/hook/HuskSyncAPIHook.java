package me.gt86.pokesync.hook;

import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.Config;
import me.gt86.pokesync.data.PixelmonDataType;
import me.gt86.pokesync.utils.LogUtils;
import net.william278.husksync.HuskSync;
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.event.BukkitDataSaveEvent;
import net.william278.husksync.event.BukkitPreSyncEvent;
import net.william278.husksync.event.BukkitSyncCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;


public class HuskSyncAPIHook implements Listener {

    private final HuskSyncAPI huskSyncAPI;
    private final boolean usePokeSyncMixins;

    public HuskSyncAPIHook() {
        this.huskSyncAPI = HuskSyncAPI.getInstance();
        this.usePokeSyncMixins = PokeSync.getInstance().isUsePokeSyncMixins();
        register();
    }

    private void register() {
        HuskSync huskSync = huskSyncAPI.getPlugin();
        for (PixelmonDataType type : PixelmonDataType.values()) {
            if (Config.isEnable(type)) {
                huskSyncAPI.registerDataSerializer(type.getIdentifier(), type.createSerializer(huskSync));
            }
        }
        Bukkit.getPluginManager().registerEvents(this, PokeSync.getInstance());
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBukkitDataSave(BukkitDataSaveEvent event) {
        event.editData(unpacked -> {
            UUID uuid = event.getUser().getUuid();
            PCStorage pc = StorageProxy.getPCForPlayerNow(uuid);
            PlayerPartyStorage party = StorageProxy.getPartyNow(uuid);
            for (PixelmonDataType type : PixelmonDataType.values()) {
                if (Config.isEnable(type)) {
                    if (type == PixelmonDataType.PC) {
                        unpacked.setData(type.getIdentifier(), type.createData(pc));
                    } else {
                        unpacked.setData(type.getIdentifier(), type.createData(party));
                    }
                    LogUtils.debug(String.format("Data saved for %s for %s", event.getUser().getUsername(), type.getIdentifier()));
                }
            }
            LogUtils.debug(String.format("PokeSync saved for data [%s]", unpacked.getId()));
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBukkitPreSync(BukkitPreSyncEvent event) {
        if (usePokeSyncMixins) {
            PokeSyncMixinsHook.callPreEvent(event.getUser().getUuid());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBukkitSyncComplete(BukkitSyncCompleteEvent event) {
        UUID uuid = event.getUser().getUuid();
        PlayerPartyStorage party = StorageProxy.getPartyNow(uuid);
        PCStorage pc = StorageProxy.getPCForPlayerNow(uuid);
        if (party != null) {
            StorageProxy.getSaveScheduler().save(party);
            LogUtils.debug(String.format("Load Party saved for %s | %d pokemon", event.getUser().getUsername(), party.countAll()));
        }
        if (pc != null) {
            StorageProxy.getSaveScheduler().save(pc);
            LogUtils.debug(String.format("Load PC saved for %s | %d pokemon", event.getUser().getUsername(), pc.countAll()));;
        }
        if (usePokeSyncMixins) {
            PokeSyncMixinsHook.callCompleteEvent(event.getUser().getUuid());
        }
    }

}
