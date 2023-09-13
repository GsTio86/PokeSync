package me.gt86.pokesync.player;

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.DataOwner;
import me.gt86.pokesync.data.DataSnapshot;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a logged-in {@link User}
 */
public abstract class OnlineUser extends User implements CommandUser, DataOwner {

    public OnlineUser(@NotNull UUID uuid, @NotNull String username) {
        super(uuid, username);
    }

    /**
     * Indicates if the player has gone offline
     *
     * @return {@code true} if the player has left the server; {@code false} otherwise
     */
    public abstract boolean isOffline();

    /**
     * Get the player's adventure {@link Audience}
     *
     * @return the player's {@link Audience}
     */
    @NotNull
    public abstract Audience getAudience();

    /**
     * Send a message to this player
     *
     * @param component the {@link Component} message to send
     */
    public void sendMessage(@NotNull Component component) {
        getAudience().sendMessage(component);
    }

    /**
     * Dispatch a MineDown-formatted message to this player
     *
     * @param mineDown the parsed {@link MineDown} to send
     */
    public void sendMessage(@NotNull MineDown mineDown) {
        sendMessage(mineDown
            .disable(MineDownParser.Option.SIMPLE_FORMATTING)
            .replace().toComponent());
    }

    /**
     * Dispatch a MineDown-formatted action bar message to this player
     *
     * @param mineDown the parsed {@link MineDown} to send
     */
    public void sendActionBar(@NotNull MineDown mineDown) {
        getAudience().sendActionBar(mineDown
            .disable(MineDownParser.Option.SIMPLE_FORMATTING)
            .replace().toComponent());
    }

    /**
     * Dispatch a toast message to this player
     *
     * @param title          the title of the toast
     * @param description    the description of the toast
     * @param iconMaterial   the namespace-keyed material to use as an hasIcon of the toast
     * @param backgroundType the background ("ToastType") of the toast
     */
    public abstract void sendToast(@NotNull MineDown title, @NotNull MineDown description,
                                   @NotNull String iconMaterial, @NotNull String backgroundType);

    /**
     * Returns if the player has the permission node
     *
     * @param node The permission node string
     * @return {@code true} if the player has permission node; {@code false} otherwise
     */
    public abstract boolean hasPermission(@NotNull String node);


    /**
     * Returns true if the player is dead
     *
     * @return true if the player is dead
     */
    public abstract boolean isDead();

    /**
     * Set a player's status from a {@link DataSnapshot}
     *
     * @param snapshot The {@link DataSnapshot} to set the player's status from
     */
    public void applySnapshot(@NotNull DataSnapshot.Packed snapshot) {
        getPlugin().fireEvent(getPlugin().getPreSyncEvent(this, snapshot), (event) -> {
            if (!isOffline()) {
                DataOwner.super.applySnapshot(
                    event.getData(), (owner) -> completeSync(true, getPlugin())
                );
            }
        });
    }


    /**
     * Handle a player's synchronization completion
     *
     * @param succeeded Whether the synchronization succeeded
     * @param plugin    The plugin instance
     */
    public void completeSync(boolean succeeded, @NotNull PokeSync plugin) {
        if (succeeded) {
            switch (plugin.getSettings().getNotificationDisplaySlot()) {
                case CHAT -> plugin.getLocales().getLocale("synchronisation_complete")
                    .ifPresent(this::sendMessage);
                case ACTION_BAR -> plugin.getLocales().getLocale("synchronisation_complete")
                    .ifPresent(this::sendActionBar);
                case TOAST -> plugin.getLocales().getLocale("synchronisation_complete")
                    .ifPresent(locale -> this.sendToast(locale, new MineDown(""),
                        "minecraft:bell", "TASK"));
            }
            plugin.fireEvent(plugin.getSyncCompleteEvent(this), (event) -> plugin.getLockedPlayers().remove(getUuid()));
        } else {
            plugin.getLocales().getLocale("synchronisation_failed")
                .ifPresent(this::sendMessage);
        }
        plugin.getDatabase().ensureUser(this);
    }

    /**
     * Get if the player is locked
     *
     * @return the player's locked status
     */
    public abstract boolean isLocked();

    /**
     * Get if the player is a NPC
     *
     * @return if the player is a NPC with metadata
     */
    public abstract boolean isNpc();
}
