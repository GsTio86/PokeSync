package me.gt86.pokesync.api.event;


import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.DataSnapshot;
import me.gt86.pokesync.event.BukkitEvent;
import me.gt86.pokesync.event.DataSaveEvent;
import me.gt86.pokesync.player.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BukkitDataSaveEvent extends BukkitEvent implements DataSaveEvent, Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PokeSync plugin;
    private final DataSnapshot.Packed snapshot;
    private final User user;
    private boolean cancelled = false;

    public BukkitDataSaveEvent(@NotNull User user, @NotNull DataSnapshot.Packed snapshot, @NotNull PokeSync plugin) {
        this.user = user;
        this.snapshot = snapshot;
        this.plugin = plugin;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public User getUser() {
        return user;
    }

    @Override
    @NotNull
    public DataSnapshot.Packed getData() {
        return snapshot;
    }

    @NotNull
    @Override
    public PokeSync getPlugin() {
        return plugin;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
