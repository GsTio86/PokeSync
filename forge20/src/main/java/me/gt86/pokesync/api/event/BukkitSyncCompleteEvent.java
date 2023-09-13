package me.gt86.pokesync.api.event;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.event.BukkitPlayerEvent;
import me.gt86.pokesync.event.SyncCompleteEvent;
import me.gt86.pokesync.player.OnlineUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BukkitSyncCompleteEvent extends BukkitPlayerEvent implements SyncCompleteEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public BukkitSyncCompleteEvent(@NotNull OnlineUser player, @NotNull PokeSync plugin) {
        super(player);
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
