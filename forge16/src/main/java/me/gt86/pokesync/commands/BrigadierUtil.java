package me.gt86.pokesync.commands;

import me.gt86.pokesync.BukkitPokeSync;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class BrigadierUtil {

    /**
     * Uses commodore to register command completions.
     *
     * @param plugin        instance of the registering Bukkit plugin
     * @param bukkitCommand the Bukkit PluginCommand to register completions for
     * @param command       the {@link Command} to register completions for
     */
    protected static void registerCommodore(@NotNull BukkitPokeSync plugin,
                                            @NotNull org.bukkit.command.Command bukkitCommand,
                                            @NotNull Command command) {
        final InputStream commodoreFile = plugin.getResource(
            "commodore/" + bukkitCommand.getName() + ".commodore"
        );
        if (commodoreFile == null) {
            return;
        }
        try {
            CommodoreProvider.getCommodore(plugin).register(bukkitCommand,
                CommodoreFileReader.INSTANCE.parse(commodoreFile),
                player -> player.hasPermission(command.getPermission()));
        } catch (IOException e) {
            plugin.log(Level.SEVERE, String.format(
                "Failed to read command commodore completions for %s", bukkitCommand.getName()), e
            );
        }
    }

}
