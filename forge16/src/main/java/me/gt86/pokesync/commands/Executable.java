package me.gt86.pokesync.commands;

import me.gt86.pokesync.player.CommandUser;
import org.jetbrains.annotations.NotNull;

public interface Executable {

    void onExecuted(@NotNull CommandUser executor, @NotNull String[] args);

}
