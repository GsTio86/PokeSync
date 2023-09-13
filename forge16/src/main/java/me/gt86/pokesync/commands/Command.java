package me.gt86.pokesync.commands;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.player.CommandUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command extends Node {

    private final String usage;
    private final Map<String, Boolean> additionalPermissions;

    protected Command(@NotNull String name, @NotNull List<String> aliases, @NotNull String usage,
                      @NotNull PokeSync plugin) {
        super(name, aliases, plugin);
        this.usage = usage;
        this.additionalPermissions = new HashMap<>();
    }

    @Override
    public final void onExecuted(@NotNull CommandUser executor, @NotNull String[] args) {
        if (!executor.hasPermission(getPermission())) {
            plugin.getLocales().getLocale("error_no_permission")
                .ifPresent(executor::sendMessage);
            return;
        }
        plugin.runAsync(() -> this.execute(executor, args));
    }

    public abstract void execute(@NotNull CommandUser executor, @NotNull String[] args);

    @NotNull
    public final String getRawUsage() {
        return usage;
    }

    @NotNull
    public final String getUsage() {
        return "/" + getName() + " " + getRawUsage();
    }

    public final void addAdditionalPermissions(@NotNull Map<String, Boolean> permissions) {
        permissions.forEach((permission, value) -> this.additionalPermissions.put(getPermission(permission), value));
    }

    @NotNull
    public final Map<String, Boolean> getAdditionalPermissions() {
        return additionalPermissions;
    }

    @NotNull
    public String getDescription() {
        return plugin.getLocales().getRawLocale(getName() + "_command_description")
            .orElse(getUsage());
    }

    @NotNull
    public final PokeSync getPlugin() {
        return plugin;
    }

}