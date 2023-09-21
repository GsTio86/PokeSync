package me.gt86.pokesync.olddata;

import org.jetbrains.annotations.NotNull;

public interface Serializer<T extends DataContainer> {

    T deserialize(@NotNull String serialized) throws DeserializationException;

    @NotNull
    String serialize(@NotNull T element) throws SerializationException;

    final class DeserializationException extends IllegalStateException {
        DeserializationException(@NotNull String message, @NotNull Throwable cause) {
            super(message, cause);
        }
    }

    final class SerializationException extends IllegalStateException {
        SerializationException(@NotNull String message, @NotNull Throwable cause) {
            super(message, cause);
        }
    }


}
