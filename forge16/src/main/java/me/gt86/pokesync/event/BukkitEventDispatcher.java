package me.gt86.pokesync.event;

import me.gt86.pokesync.api.event.BukkitDataSaveEvent;
import me.gt86.pokesync.api.event.BukkitSyncCompleteEvent;
import me.gt86.pokesync.data.DataSnapshot;
import me.gt86.pokesync.player.OnlineUser;
import me.gt86.pokesync.player.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public interface BukkitEventDispatcher extends EventDispatcher {

    @Override
    default <T extends Event> boolean fireIsCancelled(@NotNull T event) {
        Bukkit.getPluginManager().callEvent((org.bukkit.event.Event) event);
        return event instanceof Cancellable cancellable && cancellable.isCancelled();
    }

    @NotNull
    @Override
    default PreSyncEvent getPreSyncEvent(@NotNull OnlineUser user, @NotNull DataSnapshot.Packed data) {
        return new BukkitPreSyncEvent(user, data, getPlugin());
    }

    @NotNull
    @Override
    default DataSaveEvent getDataSaveEvent(@NotNull User user, @NotNull DataSnapshot.Packed data) {
        return new BukkitDataSaveEvent(user, data, getPlugin());
    }

    @NotNull
    @Override
    default SyncCompleteEvent getSyncCompleteEvent(@NotNull OnlineUser user) {
        return new BukkitSyncCompleteEvent(user, getPlugin());
    }

}