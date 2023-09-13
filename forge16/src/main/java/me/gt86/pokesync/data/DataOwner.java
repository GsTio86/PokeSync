package me.gt86.pokesync.data;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.util.ThrowingConsumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * A holder of data in the form of {@link DataContainer}s, which can be synced
 */
public interface DataOwner extends MutableDataStore {

    /**
     * Get the data that is enabled for syncing in the config
     *
     * @return the data that is enabled for syncing
     * @since 3.0
     */
    @Override
    @NotNull
    default Map<DataContainer.Type, DataContainer> getData() {
        return Arrays.stream(DataContainer.Type.values())
            .filter(type -> getPlugin().getSettings().getSynchronizationFeature(type))
            .map(type -> Map.entry(type, getTypeObj(type)))
            .filter(entry -> entry.getValue().isPresent())
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue().get()), HashMap::putAll);
    }

    default Optional<? extends DataContainer> getTypeObj(DataContainer.Type type) {
        switch (type) {
            case GIFT_DATA:
                return getGift();
            case STATS:
                return getStats();
            case PARTY:
                return getParty();
            case PC:
                return getPC();
            case MONEY:
                return getMoney();
            case MEGA_ITEM:
                return getMegaItem();
            case CHARM:
                return getCharm();
            case LURE:
                return getLure();
            case SERVER_COSMETIC:
                return getServerCosmetic();
            case POKEDEX:
                return getPokedex();
            case CURRY:
                return getCurry();
            case TRAINER_CARD:
                return getTrainerCard();
            case DAYCARE:
                return getDaycare();
            case QUEST:
                return getQuest();
            default:
                return getStats();
        }
    }

    /**
     * Create a serialized data snapshot of this data owner
     *
     * @param saveCause the cause of the snapshot
     * @return the snapshot
     * @since 3.0
     */
    @NotNull
    default DataSnapshot.Packed createSnapshot(@NotNull DataSnapshot.SaveCause saveCause) {
        return DataSnapshot.create(getPlugin(), this, saveCause);
    }

    /**
     * Deserialize and apply a data snapshot to this data owner
     * <p>
     * This method will deserialize the data on the current thread, then synchronously apply it on
     * the main server thread.
     * </p>
     * The {@code runAfter} callback function will be run after the snapshot has been applied.
     *
     * @param snapshot the snapshot to apply
     * @param runAfter the function to run asynchronously after the snapshot has been applied
     * @since 3.0
     */
    default void applySnapshot(@NotNull DataSnapshot.Packed snapshot, @NotNull ThrowingConsumer<DataOwner> runAfter) {
        final PokeSync plugin = getPlugin();
        final DataSnapshot.Unpacked unpacked = snapshot.unpack(plugin);
        plugin.runSync(() -> {
            try {
                unpacked.getData().forEach((type, data) -> {
                    if (plugin.getSettings().getSynchronizationFeature(type)) {
                        data.apply(this);
                    }
                });
            } catch (Throwable e) {
                plugin.log(Level.SEVERE, "An exception occurred applying data to a user", e);
                return;
            }

            plugin.runAsync(() -> runAfter.accept(this));
        });
    }

    @Override
    default void setStats(@NotNull DataContainer.Stats stats) {
        stats.apply(this);
    }

    @Override
    default void setParty(@NotNull DataContainer.Party party) {
        party.apply(this);
    }

    @Override
    default void setPC(@NotNull DataContainer.PC pc) {
        pc.apply(this);
    }

    @Override
    default void setMoney(@NotNull DataContainer.Money money) {
        money.apply(this);
    }

    @Override
    default void setMegaItem(@NotNull DataContainer.MegaItem megaItem) {
        megaItem.apply(this);
    }

    @Override
    default void setCharm(@NotNull DataContainer.Charm charm) {
        charm.apply(this);
    }

    @Override
    default void setLure(@NotNull DataContainer.Lure lure) {
        lure.apply(this);
    }

    @Override
    default void setServerCosmetic(@NotNull DataContainer.ServerCosmetic serverCosmetic) {
        serverCosmetic.apply(this);
    }

    @Override
    default void setPokedex(@NotNull DataContainer.Pokedex pokedex) {
        pokedex.apply(this);
    }

    @Override
    default void setCurry(@NotNull DataContainer.Curry curry) {
        curry.apply(this);
    }

    @Override
    default void setTrainerCard(@NotNull DataContainer.TrainerCard trainerCard) {
        trainerCard.apply(this);
    }

    @Override
    default void setDaycare(@NotNull DataContainer.Daycare daycare) {
        daycare.apply(this);
    }

    @Override
    default void setQuest(@NotNull DataContainer.Quest quest) {
        quest.apply(this);
    }


    @NotNull
    @ApiStatus.Internal
    PokeSync getPlugin();

}
