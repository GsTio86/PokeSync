package me.gt86.pokesync.event;


import me.gt86.pokesync.player.OnlineUser;

public interface PlayerEvent extends Event {

    OnlineUser getUser();

}
