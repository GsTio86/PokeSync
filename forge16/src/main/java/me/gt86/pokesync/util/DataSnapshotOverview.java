package me.gt86.pokesync.util;

import com.pixelmonmod.pixelmon.api.storage.PlayerStats;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.config.Locales;
import me.gt86.pokesync.data.DataContainer;
import me.gt86.pokesync.data.DataSnapshot;
import me.gt86.pokesync.player.CommandUser;
import me.gt86.pokesync.player.User;
import net.minecraft.nbt.JsonToNBT;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;

public class DataSnapshotOverview {

    private final PokeSync plugin;
    private final User dataOwner;
    private final DataSnapshot.Unpacked snapshot;

    private DataSnapshotOverview(@NotNull DataSnapshot.Unpacked snapshot, @NotNull User dataOwner,
                                 @NotNull PokeSync plugin) {
        this.snapshot = snapshot;
        this.dataOwner = dataOwner;
        this.plugin = plugin;
    }

    @NotNull
    public static DataSnapshotOverview of(@NotNull DataSnapshot.Unpacked snapshot, @NotNull User dataOwner,
                                          @NotNull PokeSync plugin) {
        return new DataSnapshotOverview(snapshot, dataOwner, plugin);
    }

    public void show(@NotNull CommandUser user) {
        // Title message, timestamp, owner and cause.
        final Locales locales = plugin.getLocales();
        locales.getLocale("data_manager_title", snapshot.getShortId(), snapshot.getId().toString(),
                dataOwner.getUsername(), dataOwner.getUuid().toString())
            .ifPresent(user::sendMessage);
        locales.getLocale("data_manager_timestamp",
                new SimpleDateFormat(locales.getRawLocale("time_format").orElse("MMM dd yyyy, HH:mm:ss.sss")).format(snapshot.getTimestamp()))
            .ifPresent(user::sendMessage);
        if (snapshot.isPinned()) {
            locales.getLocale("data_manager_pinned").ifPresent(user::sendMessage);
        }
        locales.getLocale("data_manager_cause", snapshot.getSaveCause().name().toLowerCase(Locale.ENGLISH).replaceAll("_", " "))
            .ifPresent(user::sendMessage);

        locales.getLocale("data_manager_management_buttons", dataOwner.getUsername(), snapshot.getId().toString())
            .ifPresent(user::sendMessage);
        if (user.hasPermission("pokesync.command.userdata.dump")) {
            locales.getLocale("data_manager_system_buttons", dataOwner.getUsername(), snapshot.getId().toString())
                .ifPresent(user::sendMessage);
        }
    }

}
