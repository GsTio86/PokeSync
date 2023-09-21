package me.gt86.pokesync.event;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.olddata.DataSnapshot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface PreSyncEvent extends PlayerEvent {

    @NotNull
    DataSnapshot.Packed getData();

    default void editData(@NotNull Consumer<DataSnapshot.Unpacked> edit) {
        getData().edit(getPlugin(), edit);
    }

    @NotNull
    default DataSnapshot.SaveCause getSaveCause() {
        return getData().getSaveCause();
    }

    @NotNull
    @ApiStatus.Internal
    PokeSync getPlugin();

}
