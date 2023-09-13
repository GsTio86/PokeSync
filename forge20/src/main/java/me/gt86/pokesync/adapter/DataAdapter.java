package me.gt86.pokesync.adapter;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * An adapter that adapts data to and from a portable byte array.
 */
public interface DataAdapter {

    /**
     * Converts an {@link Adaptable} to a string.
     *
     * @param data The {@link Adaptable} to adapt
     * @param <A>  The type of the {@link Adaptable}
     * @return The string
     * @throws AdaptionException If an error occurred during adaptation.
     */
    @NotNull
    default <A extends Adaptable> String toString(@NotNull A data) throws AdaptionException {
        return new String(this.toBytes(data), StandardCharsets.UTF_8);
    }

    /**
     * Converts an {@link Adaptable} to a byte array.
     *
     * @param data The {@link Adaptable} to adapt
     * @param <A>  The type of the {@link Adaptable}
     * @return The byte array
     * @throws AdaptionException If an error occurred during adaptation.
     */
    <A extends Adaptable> byte[] toBytes(@NotNull A data) throws AdaptionException;

    /**
     * Converts a string to an {@link Adaptable}.
     *
     * @param data The string to adapt.
     * @param type The class type of the {@link Adaptable} to adapt to.
     * @param <A>  The type of the {@link Adaptable}
     * @return The {@link Adaptable}
     * @throws AdaptionException If an error occurred during adaptation.
     */
    @NotNull
    default <A extends Adaptable> A fromString(@NotNull String data, @NotNull Class<A> type) throws AdaptionException {
        return this.fromBytes(data.getBytes(StandardCharsets.UTF_8), type);
    }

    /**
     * Converts a JSON string to an {@link Adaptable}.
     *
     * @param data The JSON string to adapt.
     * @param type The class type of the {@link Adaptable} to adapt to.
     * @param <A>  The type of the {@link Adaptable}
     * @return The {@link Adaptable}
     * @throws AdaptionException If an error occurred during adaptation.
     */
    @NotNull <A extends Adaptable> A fromJson(@NotNull String data, @NotNull Class<A> type) throws AdaptionException;

    /**
     * Converts an {@link Adaptable} to a JSON string.
     *
     * @param data The {@link Adaptable} to adapt
     * @param <A>  The type of the {@link Adaptable}
     * @return The JSON string
     * @throws AdaptionException If an error occurred during adaptation.
     */
    @NotNull <A extends Adaptable> String toJson(@NotNull A data) throws AdaptionException;

    /**
     * Converts a byte array to an {@link Adaptable}.
     *
     * @param data The byte array to adapt.
     * @param type The class type of the {@link Adaptable} to adapt to.
     * @param <A>  The type of the {@link Adaptable}
     * @return The {@link Adaptable}
     * @throws AdaptionException If an error occurred during adaptation.
     */
    <A extends Adaptable> A fromBytes(@NotNull byte[] data, @NotNull Class<A> type) throws AdaptionException;

    final class AdaptionException extends IllegalStateException {
        static final String FORMAT = "An exception occurred when adapting serialized/deserialized data: %s";

        AdaptionException(@NotNull String message, @NotNull Throwable cause) {
            super(String.format(FORMAT, message), cause);
        }
    }
}