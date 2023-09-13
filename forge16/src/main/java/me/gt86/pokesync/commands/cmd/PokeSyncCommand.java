package me.gt86.pokesync.commands.cmd;

import de.themoep.minedown.adventure.MineDown;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.commands.Command;
import me.gt86.pokesync.commands.TabProvider;
import me.gt86.pokesync.player.CommandUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class PokeSyncCommand extends Command implements TabProvider {

    private static final Map<String, Boolean> SUB_COMMANDS = Map.of("reload", true);

    public PokeSyncCommand(@NotNull PokeSync plugin) {
        super("pokesync", List.of(), "[" + String.join("|", SUB_COMMANDS.keySet()) + "]", plugin);
        addAdditionalPermissions(SUB_COMMANDS);
    }

    @Override
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        final String action = parseStringArg(args).orElse("reload");
        if (SUB_COMMANDS.containsKey(action) && !executor.hasPermission(getPermission(action))) {
            plugin.getLocales().getLocale("error_no_permission")
                .ifPresent(executor::sendMessage);
            return;
        }

        if (action.toLowerCase(Locale.ENGLISH).equals("reload")) {
            try {
                plugin.loadConfigs();
                plugin.getLocales().getLocale("reload_complete").ifPresent(executor::sendMessage);
            } catch (Throwable e) {
                executor.sendMessage(new MineDown(
                    "[Error:](#ff3300) [Failed to reload the plugin. Check console for errors.](#ff7e5e)"
                ));
                plugin.log(Level.SEVERE, "Failed to reload the plugin", e);
            }
        } else {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                .ifPresent(executor::sendMessage);
        }
    }


    @Nullable
    @Override
    public List<String> suggest(@NotNull CommandUser user, @NotNull String[] args) {
        return switch (args.length) {
            case 0, 1 -> SUB_COMMANDS.keySet().stream().sorted().toList();
            default -> null;
        };
    }

}
