package me.gt86.pokesync.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class BukkitEvent extends Event implements me.gt86.pokesync.event.Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    protected BukkitEvent() {
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
