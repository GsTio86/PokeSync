package me.gt86.pokesync.event;

import me.gt86.pokesync.player.OnlineUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class BukkitPlayerEvent extends BukkitEvent implements PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    protected final OnlineUser player;

    protected BukkitPlayerEvent(@NotNull OnlineUser player) {
        this.player = player;
    }

    @Override
    @NotNull
    public OnlineUser getUser() {
        return player;
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
