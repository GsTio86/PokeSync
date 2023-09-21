package me.gt86.pokesync.event;


import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.olddata.DataSnapshot;
import me.gt86.pokesync.player.OnlineUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BukkitPreSyncEvent extends BukkitPlayerEvent implements PreSyncEvent, Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PokeSync plugin;
    private final DataSnapshot.Packed data;
    private boolean cancelled = false;

    protected BukkitPreSyncEvent(@NotNull OnlineUser player, @NotNull DataSnapshot.Packed data, @NotNull PokeSync plugin) {
        super(player);
        this.data = data;
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

    @Override
    @NotNull
    public DataSnapshot.Packed getData() {
        return data;
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
