package me.gt86.pokesync.commands;


import me.gt86.pokesync.BukkitPokeSync;
import me.gt86.pokesync.commands.cmd.PokeSyncCommand;
import me.gt86.pokesync.commands.cmd.UserDataCommand;
import me.gt86.pokesync.player.BukkitUser;
import me.gt86.pokesync.player.CommandUser;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class BukkitCommand extends org.bukkit.command.Command {

    private final BukkitPokeSync plugin;
    private final Command command;

    public BukkitCommand(@NotNull Command command, @NotNull BukkitPokeSync plugin) {
        super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
        this.command = command;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        this.command.onExecuted(sender instanceof Player p ? BukkitUser.adapt(p, plugin) : plugin.getConsole(), args);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                    @NotNull String[] args) throws IllegalArgumentException {
        if (!(this.command instanceof TabProvider provider)) {
            return List.of();
        }
        final CommandUser user = sender instanceof Player p ? BukkitUser.adapt(p, plugin) : plugin.getConsole();
        return provider.getSuggestions(user, args);
    }

    public void register() {
        // Register with bukkit
        plugin.getCommandRegistrar().getServerCommandMap().register("pokesync", this);

        // Register permissions
        BukkitCommand.addPermission(
            plugin,
            command.getPermission(),
            command.getUsage(),
            BukkitCommand.getPermissionDefault(command.isOperatorCommand())
        );
        final List<Permission> childNodes = command.getAdditionalPermissions()
            .entrySet().stream()
            .map((entry) -> BukkitCommand.addPermission(
                plugin,
                entry.getKey(),
                "",
                BukkitCommand.getPermissionDefault(entry.getValue()))
            )
            .filter(Objects::nonNull)
            .toList();
        if (!childNodes.isEmpty()) {
            BukkitCommand.addPermission(
                plugin,
                command.getPermission("*"),
                command.getUsage(),
                PermissionDefault.FALSE,
                childNodes.toArray(new Permission[0])
            );
        }

        // Register commodore TAB completion
        if (CommodoreProvider.isSupported() && plugin.getSettings().doBrigadierTabCompletion()) {
            BrigadierUtil.registerCommodore(plugin, this, command);
        }
    }

    @Nullable
    protected static Permission addPermission(@NotNull BukkitPokeSync plugin, @NotNull String node,
                                              @NotNull String description, @NotNull PermissionDefault permissionDefault,
                                              @NotNull Permission... children) {
        final Map<String, Boolean> childNodes = Arrays.stream(children)
            .map(Permission::getName)
            .collect(HashMap::new, (map, child) -> map.put(child, true), HashMap::putAll);

        final PluginManager manager = plugin.getServer().getPluginManager();
        if (manager.getPermission(node) != null) {
            return null;
        }

        Permission permission;
        if (description.isEmpty()) {
            permission = new Permission(node, permissionDefault, childNodes);
        } else {
            permission = new Permission(node, description, permissionDefault, childNodes);
        }
        manager.addPermission(permission);

        return permission;
    }

    @NotNull
    protected static PermissionDefault getPermissionDefault(boolean isOperatorCommand) {
        return isOperatorCommand ? PermissionDefault.OP : PermissionDefault.TRUE;
    }

    /**
     * Commands available on the Bukkit PokeSync implementation
     */
    public enum Type {

        POKESYNC_COMMAND(PokeSyncCommand::new),
        USERDATA_COMMAND(UserDataCommand::new);

        public final Function<BukkitPokeSync, Command> commandSupplier;

        Type(@NotNull Function<BukkitPokeSync, Command> supplier) {
            this.commandSupplier = supplier;
        }

        @NotNull
        public Command createCommand(@NotNull BukkitPokeSync plugin) {
            return commandSupplier.apply(plugin);
        }

        public static void registerCommands(@NotNull BukkitPokeSync plugin) {
            Arrays.stream(values())
                .map((type) -> type.createCommand(plugin))
                .forEach((command) -> new BukkitCommand(command, plugin).register());
        }


    }
}
