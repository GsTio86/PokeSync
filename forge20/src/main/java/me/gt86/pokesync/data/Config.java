package me.gt86.pokesync.data;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class Config {

    private static boolean debug = false;

    private static Map<PixelmonDataType, Boolean> enabledDataTypes = new HashMap<>();

    public static void loadConfig(FileConfiguration config) {
        debug = config.getBoolean("debug");
        for (PixelmonDataType dataType : PixelmonDataType.values()) {
            enabledDataTypes.put(dataType, config.getBoolean("features." + dataType.getIdentifier().getKeyValue()));
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isEnable(PixelmonDataType dataType) {
        return enabledDataTypes.get(dataType);
    }
}
