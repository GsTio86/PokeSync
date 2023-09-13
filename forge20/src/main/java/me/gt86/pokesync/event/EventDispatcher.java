package me.gt86.pokesync.event;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.DataSnapshot;
import me.gt86.pokesync.player.OnlineUser;
import me.gt86.pokesync.player.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface EventDispatcher {

    /**
     * Fire an event synchronously, then run a callback asynchronously.
     *
     * @param event    The event to fire
     * @param callback The callback to run after the event has been fired
     * @param <T>      The material of event to fire
     */
    default <T extends Event> void fireEvent(@NotNull T event, @Nullable Consumer<T> callback) {
        getPlugin().runSync(() -> {
            if (!fireIsCancelled(event) && callback != null) {
                getPlugin().runAsync(() -> callback.accept(event));
            }
        });
    }

    /**
     * Fire an event on this thread, and return whether the event was canceled.
     *
     * @param event The event to fire
     * @param <T>   The material of event to fire
     * @return Whether the event was canceled
     */
    <T extends Event> boolean fireIsCancelled(@NotNull T event);

    @NotNull
    PreSyncEvent getPreSyncEvent(@NotNull OnlineUser user, @NotNull DataSnapshot.Packed userData);

    @NotNull
    DataSaveEvent getDataSaveEvent(@NotNull User user, @NotNull DataSnapshot.Packed saveCause);

    @NotNull
    SyncCompleteEvent getSyncCompleteEvent(@NotNull OnlineUser user);

    @NotNull
    PokeSync getPlugin();
}
