package me.gt86.pokesync.utils;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.Config;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class LogUtils {
    public static void debug(@NotNull String message, Throwable... throwable) {
        if (Config.isDebug()) {
            PokeSync.getInstance().getLogger().log(Level.INFO, getDebugString(message), throwable);
        }
    }

    private static @NotNull String getDebugString(@NotNull String message) {
        return String.format("[DEBUG] [%s] %s", (new SimpleDateFormat("mm:ss.SSS")).format(new Date()), message);
    }
}
