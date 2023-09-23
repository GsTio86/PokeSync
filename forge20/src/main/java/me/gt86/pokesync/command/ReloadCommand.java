package me.gt86.pokesync.command;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadCommand implements CommandExecutor {
    public ReloadCommand() {
        Objects.requireNonNull(PokeSync.getInstance().getCommand("pokesync")).setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PokeSync.getInstance().loadConfig();
        sender.sendMessage(TextUtils.parseColour(TextUtils.PREFIX + "Reloaded Config!"));
        return false;
    }


}
