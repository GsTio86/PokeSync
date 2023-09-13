package me.gt86.pokesync;

import me.gt86.pokesync.adapter.DataAdapter;
import me.gt86.pokesync.config.Locales;
import me.gt86.pokesync.config.Settings;
import me.gt86.pokesync.data.DataContainer;
import me.gt86.pokesync.data.Serializer;
import me.gt86.pokesync.database.Database;
import me.gt86.pokesync.event.EventDispatcher;
import me.gt86.pokesync.player.ConsoleUser;
import me.gt86.pokesync.player.OnlineUser;
import me.gt86.pokesync.redis.RedisManager;
import me.gt86.pokesync.util.Task;
import me.gt86.pokesync.util.ThrowingConsumer;
import net.william278.annotaml.Annotaml;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

public interface PokeSync extends Task.Supplier, EventDispatcher {

    int SPIGOT_RESOURCE_ID = 97144;

    /**
     * Returns a set of online players.
     *
     * @return a set of online players as {@link OnlineUser}
     */
    @NotNull
    Set<OnlineUser> getOnlineUsers();

    /**
     * Returns an online user by UUID if they exist
     *
     * @param uuid the UUID of the user to get
     * @return an online user as {@link OnlineUser}
     */
    @NotNull
    Optional<OnlineUser> getOnlineUser(@NotNull UUID uuid);

    /**
     * Returns the database implementation
     *
     * @return the {@link Database} implementation
     */
    @NotNull
    Database getDatabase();

    /**
     * Returns the redis manager implementation
     *
     * @return the {@link RedisManager} implementation
     */

    @NotNull
    RedisManager getRedisManager();

    @NotNull
    DataAdapter getDataAdapter();

    /**
     * Returns the data serializer for the given {@link DataContainer.Type}
     */
    @NotNull <T extends DataContainer> Map<DataContainer.Type, Serializer<T>> getSerializers();


    /**
     * Initialize a faucet of the plugin.
     *
     * @param name   the name of the faucet
     * @param runner a runnable for initializing the faucet
     */
    default void initialize(@NotNull String name, @NotNull ThrowingConsumer<PokeSync> runner) {
        log(Level.INFO, "Initializing " + name + "...");
        try {
            runner.accept(this);
        } catch (Throwable e) {
            throw new FailedToLoadException("Failed to initialize " + name, e);
        }
        log(Level.INFO, "Successfully initialized " + name);
    }

    /**
     * Returns the plugin {@link Settings}
     *
     * @return the {@link Settings}
     */
    @NotNull
    Settings getSettings();

    void setSettings(@NotNull Settings settings);

    /**
     * Returns the plugin {@link Locales}
     *
     * @return the {@link Locales}
     */
    @NotNull
    Locales getLocales();

    void setLocales(@NotNull Locales locales);

    /**
     * Returns if a dependency is loaded
     *
     * @param name the name of the dependency
     * @return {@code true} if the dependency is loaded, {@code false} otherwise
     */
    boolean isDependencyLoaded(@NotNull String name);

    /**
     * Get a resource as an {@link InputStream} from the plugin jar
     *
     * @param name the path to the resource
     * @return the {@link InputStream} of the resource
     */
    InputStream getResource(@NotNull String name);

    /**
     * Returns the plugin data folder
     *
     * @return the plugin data folder as a {@link File}
     */
    @NotNull
    File getDataFolder();

    /**
     * Log a message to the console
     *
     * @param level     the level of the message
     * @param message   the message to log
     * @param throwable a throwable to log
     */
    void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... throwable);

    /**
     * Send a debug message to the console, if debug logging is enabled
     *
     * @param message   the message to log
     * @param throwable a throwable to log
     */
    default void debug(@NotNull String message, @NotNull Throwable... throwable) {
        if (getSettings().doDebugLogging()) {
            log(Level.INFO, String.format("[DEBUG] %s", message), throwable);
        }
    }

    /**
     * Get the console user
     *
     * @return the {@link ConsoleUser}
     */
    @NotNull
    ConsoleUser getConsole();

    /**
     * Returns the platform type
     *
     * @return the platform type
     */
    @NotNull
    String getPlatformType();

    /**
     * Reloads the {@link Settings} and {@link Locales} from their respective config files.
     */
    default void loadConfigs() {
        try {
            // Load settings
            setSettings(Annotaml.create(new File(getDataFolder(), "config.yml"), Settings.class).get());

            // Load locales from language preset default
            final Locales languagePresets = Annotaml.create(
                Locales.class,
                Objects.requireNonNull(getResource(String.format("locales/%s.yml", getSettings().getLanguage())))
            ).get();
            setLocales(Annotaml.create(new File(
                getDataFolder(),
                String.format("messages_%s.yml", getSettings().getLanguage())
            ), languagePresets).get());
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new FailedToLoadException("Failed to load config or message files", e);
        }
    }


    @NotNull
    Set<UUID> getLockedPlayers();

    String getMinecraftVersion();

    /**
     * An exception indicating the plugin has been accessed before it has been registered.
     */
    final class FailedToLoadException extends IllegalStateException {

        private static final String FORMAT = """
            PokeSync has failed to load! The plugin will not be enabled and no data will be synchronised.
            Please make sure the plugin has been setup correctly:

            1) Make sure you've entered your MySQL or MariaDB database details correctly in config.yml
            2) Make sure your Redis server details are also correct in config.yml
            3) Check the error below for more details

            Caused by: %s""";

        FailedToLoadException(@NotNull String message, @NotNull Throwable cause) {
            super(String.format(FORMAT, message), cause);
        }

    }

}
