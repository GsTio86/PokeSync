package me.gt86.pokesync.redis;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.adapter.Adaptable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RedisMessage implements Adaptable {

    public UUID targetUserUuid;
    public byte[] data;

    public RedisMessage(@NotNull UUID targetUserUuid, byte[] message) {
        this.targetUserUuid = targetUserUuid;
        this.data = message;
    }

    @SuppressWarnings("unused")
    public RedisMessage() {
    }

    public void dispatch(@NotNull PokeSync plugin, @NotNull RedisMessageType type) {
        plugin.runAsync(() -> plugin.getRedisManager().sendMessage(
            type.getMessageChannel(), new GsonBuilder().create().toJson(this)
        ));
    }

    @NotNull
    public static RedisMessage fromJson(@NotNull String json) throws JsonSyntaxException {
        return new GsonBuilder().create().fromJson(json, RedisMessage.class);
    }

}