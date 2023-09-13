package me.gt86.pokesync.commands;

import me.gt86.pokesync.PokeSync;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public abstract class Node implements Executable {

    protected static final String PERMISSION_PREFIX = "pokesync.command";

    protected final PokeSync plugin;
    private final String name;
    private final List<String> aliases;
    private boolean operatorCommand = false;

    protected Node(@NotNull String name, @NotNull List<String> aliases, @NotNull PokeSync plugin) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Command name cannot be blank");
        }
        this.name = name;
        this.aliases = aliases;
        this.plugin = plugin;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }

    @NotNull
    public String getPermission(@NotNull String... child) {
        final StringJoiner joiner = new StringJoiner(".")
            .add(PERMISSION_PREFIX)
            .add(getName());
        for (final String node : child) {
            joiner.add(node);
        }
        return joiner.toString().trim();
    }

    public boolean isOperatorCommand() {
        return operatorCommand;
    }

    public void setOperatorCommand(boolean operatorCommand) {
        this.operatorCommand = operatorCommand;
    }

    protected Optional<String> parseStringArg(@NotNull String[] args) {
        if (args.length > 0) {
            return Optional.of(args[0]);
        }
        return Optional.empty();
    }


}