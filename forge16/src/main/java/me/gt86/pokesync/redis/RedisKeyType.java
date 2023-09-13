package me.gt86.pokesync.redis;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum RedisKeyType {
    CACHE(60 * 60 * 24),
    DATA_UPDATE(10),
    SERVER_SWITCH(10);

    private final int timeToLive;

    RedisKeyType(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    @NotNull
    public String getKeyPrefix() {
        return RedisManager.KEY_NAMESPACE.toLowerCase(Locale.ENGLISH) + ":" + RedisManager.clusterId.toLowerCase(Locale.ENGLISH) + ":" + name().toLowerCase(Locale.ENGLISH);
    }

    public int getTimeToLive() {
        return timeToLive;
    }

}
