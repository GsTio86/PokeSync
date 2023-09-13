package me.gt86.pokesync.util;

import java.util.function.Consumer;

/**
 * A Consumer that can throw an exception
 *
 * @param <T> The type of the element to consume
 * @since 2.0
 */
@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

    /**
     * Accepts the element, wrapping any exceptions in a {@link RuntimeException}
     *
     * @param element the input argument to accept
     * @throws RuntimeException If an exception occurs
     * @since 2.0
     */
    @Override
    default void accept(final T element) throws RuntimeException {
        try {
            acceptThrows(element);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Accepts the element, potentially throwing an exception
     *
     * @param elem The element to accept
     * @throws Throwable If an exception occurs
     * @since 2.0
     */
    void acceptThrows(T elem) throws Throwable;
}