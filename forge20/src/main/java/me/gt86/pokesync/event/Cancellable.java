package me.gt86.pokesync.event;

@SuppressWarnings("unused")
public interface Cancellable extends Event {

    default boolean isCancelled() {
        return false;
    }

    void setCancelled(boolean cancelled);

}
