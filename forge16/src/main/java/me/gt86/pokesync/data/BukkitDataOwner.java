package me.gt86.pokesync.olddata;

import net.minecraft.entity.player.ServerPlayerEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BukkitDataOwner extends DataOwner {

    @NotNull
    @Override
    default Optional<DataContainer.Gift> getGift() {
        return Optional.of(BukkitDataContainer.Gift.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Stats> getStats() {
        return Optional.of(BukkitDataContainer.Stats.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Party> getParty() {
        return Optional.of(BukkitDataContainer.Party.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.PC> getPC() {
        return Optional.of(BukkitDataContainer.PC.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Money> getMoney() {
        return Optional.of(BukkitDataContainer.Money.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.MegaItem> getMegaItem() {
        return Optional.of(BukkitDataContainer.MegaItem.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Charm> getCharm() {
        return Optional.of(BukkitDataContainer.Charm.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Lure> getLure() {
        return Optional.of(BukkitDataContainer.Lure.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.ServerCosmetic> getServerCosmetic() {
        return Optional.of(BukkitDataContainer.ServerCosmetic.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Pokedex> getPokedex() {
        return Optional.of(BukkitDataContainer.Pokedex.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Curry> getCurry() {
        return Optional.of(BukkitDataContainer.Curry.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.TrainerCard> getTrainerCard() {
        return Optional.of(BukkitDataContainer.TrainerCard.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Daycare> getDaycare() {
        return Optional.of(BukkitDataContainer.Daycare.adapt(getBukkitPlayer()));
    }

    @NotNull
    @Override
    default Optional<DataContainer.Quest> getQuest() {
        return Optional.of(BukkitDataContainer.Quest.adapt(getBukkitPlayer()));
    }

    @NotNull
    Player getBukkitPlayer();

    @NotNull ServerPlayerEntity getServerPlayer();

}
