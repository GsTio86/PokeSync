package me.gt86.pokesync.util;

import me.gt86.pokesync.BukkitPokeSync;
import me.gt86.pokesync.PokeSync;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.scheduling.GracefulScheduling;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public interface BukkitTask extends Task {

    class Sync extends Task.Sync implements BukkitTask {

        private ScheduledTask task;

        protected Sync(@NotNull PokeSync plugin, @NotNull Runnable runnable, long delayTicks) {
            super(plugin, runnable, delayTicks);
        }

        @Override
        public void cancel() {
            if (task != null && !cancelled) {
                task.cancel();
            }
            super.cancel();
        }

        @Override
        public void run() {
            if (isPluginDisabled()) {
                runnable.run();
                return;
            }
            if (cancelled) {
                return;
            }

            if (delayTicks > 0) {
                this.task = getScheduler().globalRegionalScheduler().runDelayed(runnable, delayTicks);
            } else {
                this.task = getScheduler().globalRegionalScheduler().run(runnable);
            }
        }
    }

    class Async extends Task.Async implements BukkitTask {

        private ScheduledTask task;

        protected Async(@NotNull PokeSync plugin, @NotNull Runnable runnable, long delayTicks) {
            super(plugin, runnable, delayTicks);
        }

        @Override
        public void cancel() {
            if (task != null && !cancelled) {
                task.cancel();
            }
            super.cancel();
        }

        @Override
        public void run() {
            if (isPluginDisabled()) {
                runnable.run();
                return;
            }
            if (cancelled) {
                return;
            }

            if (delayTicks > 0) {
                plugin.debug("Running async task with delay of " + delayTicks + " ticks");
                this.task = getScheduler().asyncScheduler().runDelayed(
                    runnable,
                    Duration.of(delayTicks * 50L, ChronoUnit.MILLIS)
                );
            } else {
                this.task = getScheduler().asyncScheduler().run(runnable);
            }
        }
    }

    class Repeating extends Task.Repeating implements BukkitTask {

        private ScheduledTask task;

        protected Repeating(@NotNull PokeSync plugin, @NotNull Runnable runnable, long repeatingTicks) {
            super(plugin, runnable, repeatingTicks);
        }

        @Override
        public void cancel() {
            if (task != null && !cancelled) {
                task.cancel();
            }
            super.cancel();
        }

        @Override
        public void run() {
            if (isPluginDisabled()) {
                return;
            }

            if (!cancelled) {
                this.task = getScheduler().asyncScheduler().runAtFixedRate(
                    runnable, Duration.ZERO,
                    Duration.of(repeatingTicks * 50L, ChronoUnit.MILLIS)
                );
            }
        }
    }

    // Returns if the Bukkit PokeSync plugin is disabled
    default boolean isPluginDisabled() {
        return !((BukkitPokeSync) getPlugin()).isEnabled();
    }

    interface Supplier extends Task.Supplier {

        @NotNull
        @Override
        default Task.Sync getSyncTask(@NotNull Runnable runnable, long delayTicks) {
            return new Sync(getPlugin(), runnable, delayTicks);
        }

        @NotNull
        @Override
        default Task.Async getAsyncTask(@NotNull Runnable runnable, long delayTicks) {
            return new Async(getPlugin(), runnable, delayTicks);
        }

        @NotNull
        @Override
        default Task.Repeating getRepeatingTask(@NotNull Runnable runnable, long repeatingTicks) {
            return new Repeating(getPlugin(), runnable, repeatingTicks);
        }

        @Override
        default void cancelTasks() {
            ((BukkitPokeSync) getPlugin()).getScheduler().cancelGlobalTasks();
        }

    }

    @NotNull
    default GracefulScheduling getScheduler() {
        return ((BukkitPokeSync) getPlugin()).getScheduler();
    }

}