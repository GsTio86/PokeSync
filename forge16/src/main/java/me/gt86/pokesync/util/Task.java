package me.gt86.pokesync.util;

import me.gt86.pokesync.PokeSync;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface Task extends Runnable {

    abstract class Base implements Task {

        protected final PokeSync plugin;
        protected final Runnable runnable;
        protected boolean cancelled = false;

        protected Base(@NotNull PokeSync plugin, @NotNull Runnable runnable) {
            this.plugin = plugin;
            this.runnable = runnable;
        }

        public void cancel() {
            cancelled = true;
        }

        @NotNull
        @Override
        public PokeSync getPlugin() {
            return plugin;
        }

    }

    abstract class Async extends Base {

        protected long delayTicks;

        protected Async(@NotNull PokeSync plugin, @NotNull Runnable runnable, long delayTicks) {
            super(plugin, runnable);
            this.delayTicks = delayTicks;
        }

    }

    abstract class Sync extends Base {

        protected long delayTicks;

        protected Sync(@NotNull PokeSync plugin, @NotNull Runnable runnable, long delayTicks) {
            super(plugin, runnable);
            this.delayTicks = delayTicks;
        }

    }

    abstract class Repeating extends Base {

        protected final long repeatingTicks;

        protected Repeating(@NotNull PokeSync plugin, @NotNull Runnable runnable, long repeatingTicks) {
            super(plugin, runnable);
            this.repeatingTicks = repeatingTicks;
        }

    }

    @SuppressWarnings("UnusedReturnValue")
    interface Supplier {

        @NotNull
        Task.Sync getSyncTask(@NotNull Runnable runnable, long delayTicks);

        @NotNull
        Task.Async getAsyncTask(@NotNull Runnable runnable, long delayTicks);

        @NotNull
        Task.Repeating getRepeatingTask(@NotNull Runnable runnable, long repeatingTicks);

        @NotNull
        default Task.Sync runSyncDelayed(@NotNull Runnable runnable, long delayTicks) {
            final Sync task = getSyncTask(runnable, delayTicks);
            task.run();
            return task;
        }

        default Async runAsyncDelayed(@NotNull Runnable runnable, long delayTicks) {
            final Async task = getAsyncTask(runnable, delayTicks);
            task.run();
            return task;
        }

        @NotNull
        default Task.Sync runSync(@NotNull Runnable runnable) {
            return runSyncDelayed(runnable, 0);
        }

        @NotNull
        default Task.Async runAsync(@NotNull Runnable runnable) {
            final Async task = getAsyncTask(runnable, 0);
            task.run();
            return task;
        }

        default <T> CompletableFuture<T> supplyAsync(@NotNull java.util.function.Supplier<T> supplier) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            runAsync(() -> {
                try {
                    future.complete(supplier.get());
                } catch (Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            });
            return future;
        }

        void cancelTasks();

        @NotNull
        PokeSync getPlugin();

    }

    @NotNull
    PokeSync getPlugin();

}