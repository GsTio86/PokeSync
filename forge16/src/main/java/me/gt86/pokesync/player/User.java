package me.gt86.pokesync.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class User {

    private final UUID uuid;

    private final String username;

    public User(@NotNull UUID uuid, @NotNull String username) {
        this.username = username;
        this.uuid = uuid;
    }

    /**
     * Get the user's unique account ID
     */
    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the user's username
     */
    @NotNull
    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof User other) {
            return this.getUuid().equals(other.getUuid());
        }
        return super.equals(object);
    }
}
