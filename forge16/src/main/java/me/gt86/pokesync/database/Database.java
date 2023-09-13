package me.gt86.pokesync.database;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.config.Settings;
import me.gt86.pokesync.data.DataSnapshot;
import me.gt86.pokesync.data.DataSnapshot.SaveCause;
import me.gt86.pokesync.player.User;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * An abstract representation of the plugin database, storing player data.
 * <p>
 * Implemented by different database platforms - MySQL, SQLite, etc. - as configured by the administrator.
 */
public abstract class Database {

    protected final PokeSync plugin;

    protected Database(@NotNull PokeSync plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads SQL table creation schema statements from a resource file as a string array
     *
     * @param schemaFileName database script resource file to load from
     * @return Array of string-formatted table creation schema statements
     * @throws IOException if the resource could not be read
     */
    @SuppressWarnings("SameParameterValue")
    @NotNull
    protected final String[] getSchemaStatements(@NotNull String schemaFileName) throws IOException {
        return formatStatementTables(new String(Objects.requireNonNull(plugin.getResource(schemaFileName))
            .readAllBytes(), StandardCharsets.UTF_8)).split(";");
    }

    /**
     * Format all table name placeholder strings in a SQL statement
     *
     * @param sql the SQL statement with un-formatted table name placeholders
     * @return the formatted statement, with table placeholders replaced with the correct names
     */
    @NotNull
    protected final String formatStatementTables(@NotNull String sql) {
        return sql.replaceAll("%users_table%", plugin.getSettings().getTableName(Settings.TableName.USERS))
            .replaceAll("%user_data_table%", plugin.getSettings().getTableName(Settings.TableName.USER_DATA));
    }

    /**
     * Initialize the database and ensure tables are present; create tables if they do not exist.
     *
     * @throws IllegalStateException if the database could not be initialized
     */
    public abstract void initialize() throws IllegalStateException;

    /**
     * Ensure a {@link User} has an entry in the database and that their username is up-to-date
     *
     * @param user The {@link User} to ensure
     */
    public abstract void ensureUser(@NotNull User user);

    /**
     * Get a player by their Minecraft account {@link UUID}
     *
     * @param uuid Minecraft account {@link UUID} of the {@link User} to get
     * @return A future returning an optional with the {@link User} present if they exist
     */
    public abstract Optional<User> getUser(@NotNull UUID uuid);

    /**
     * Get a user by their username (<i>case-insensitive</i>)
     *
     * @param username Username of the {@link User} to get (<i>case-insensitive</i>)
     * @return A future returning an optional with the {@link User} present if they exist
     */
    public abstract Optional<User> getUserByName(@NotNull String username);

    /**
     * Get the current uniquely versioned user data for a given user, if it exists.
     *
     * @param user the user to get data for
     * @return an optional containing the {@link DataSnapshot}, if it exists, or an empty optional if it does not
     */
    public abstract Optional<DataSnapshot.Packed> getCurrentUserData(@NotNull User user);

    /**
     * Get all {@link DataSnapshot} entries for a user from the database.
     *
     * @param user The user to get data for
     * @return A future returning a list of a user's {@link DataSnapshot} entries
     */
    @NotNull
    public abstract List<DataSnapshot.Packed> getDataSnapshots(@NotNull User user);

    /**
     * Gets a specific {@link DataSnapshot} entry for a user from the database, by its UUID.
     *
     * @param user        The user to get data for
     * @param versionUuid The UUID of the {@link DataSnapshot} entry to get
     * @return A future returning an optional containing the {@link DataSnapshot}, if it exists, or an empty optional if it does not
     */
    public abstract Optional<DataSnapshot.Packed> getDataSnapshot(@NotNull User user, @NotNull UUID versionUuid);

    /**
     * <b>(Internal)</b> Prune user data for a given user to the maximum value as configured.
     *
     * @param user The user to prune data for
     * @implNote Data snapshots marked as {@code pinned} are exempt from rotation
     */
    protected abstract void rotateUserData(@NotNull User user);

    /**
     * Deletes a specific {@link DataSnapshot} entry for a user from the database, by its UUID.
     *
     * @param user        The user to get data for
     * @param versionUuid The UUID of the {@link DataSnapshot} entry to delete
     */
    public abstract boolean deleteUserData(@NotNull User user, @NotNull UUID versionUuid);

    /**
     * Save user data to the database
     * </p>
     * This will remove the oldest data for the user if the amount of data exceeds the limit as configured
     *
     * @param user     The user to add data for
     * @param snapshot The {@link DataSnapshot} to set.
     *                 The implementation should version it with a random UUID and the current timestamp during insertion.
     * @see me.gt86.pokesync.data.DataOwner#createSnapshot(SaveCause)
     */
    public void setUserData(@NotNull User user, @NotNull DataSnapshot.Packed snapshot) {
        if (snapshot.getSaveCause() != SaveCause.SERVER_SHUTDOWN) {
            plugin.fireEvent(
                plugin.getDataSaveEvent(user, snapshot),
                (event) -> this.saveDataSnapshot(user, snapshot)
            );
            return;
        }

        this.saveDataSnapshot(user, snapshot);
    }

    /**
     * <b>Internal</b> - Create user data in the database
     *
     * @param user The user to add data for
     * @param data The {@link DataSnapshot} to set.
     */
    @ApiStatus.Internal
    protected abstract void saveDataSnapshot(@NotNull User user, @NotNull DataSnapshot.Packed data);

    /**
     * Update a saved {@link DataSnapshot} by given version UUID
     *
     * @param user        The user whose data snapshot
     * @param versionUuid The UUID of the user's {@link DataSnapshot} entry
     * @param snapshot    The {@link DataSnapshot} to update
     */
    protected abstract void updateUserData(@NotNull User user, @NotNull UUID versionUuid,
                                           @NotNull DataSnapshot.Packed snapshot);

    /**
     * Unpin a saved {@link DataSnapshot} by given version UUID, setting it's {@code pinned} state to {@code false}.
     *
     * @param user        The user to unpin the data for
     * @param versionUuid The UUID of the user's {@link DataSnapshot} entry to unpin
     * @see DataSnapshot#isPinned()
     */
    public final void unpinUserData(@NotNull User user, @NotNull UUID versionUuid) {
        this.getDataSnapshot(user, versionUuid).ifPresent(data -> {
            data.edit(plugin, (snapshot) -> snapshot.setPinned(false));
            this.updateUserData(user, versionUuid, data);
        });
    }

    /**
     * Pin a saved {@link DataSnapshot} by given version UUID, setting it's {@code pinned} state to {@code true}.
     *
     * @param user        The user to pin the data for
     * @param versionUuid The UUID of the user's {@link DataSnapshot} entry to pin
     */
    public final void pinUserData(@NotNull User user, @NotNull UUID versionUuid) {
        this.getDataSnapshot(user, versionUuid).ifPresent(data -> {
            data.edit(plugin, (snapshot) -> snapshot.setPinned(true));
            this.updateUserData(user, versionUuid, data);
        });
    }

    /**
     * Close the database connection
     */
    public abstract void terminate();

    /**
     * Identifies types of databases
     */
    public enum Type {
        MYSQL("MySQL", "mysql"),
        MARIADB("MariaDB", "mariadb");

        private final String displayName;
        private final String protocol;

        Type(@NotNull String displayName, @NotNull String protocol) {
            this.displayName = displayName;
            this.protocol = protocol;
        }

        @NotNull
        public String getDisplayName() {
            return displayName;
        }

        @NotNull
        public String getProtocol() {
            return protocol;
        }
    }

}
