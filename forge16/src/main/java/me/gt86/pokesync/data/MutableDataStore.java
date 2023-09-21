package me.gt86.pokesync.olddata;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public interface MutableDataStore {

    @NotNull
    Map<DataContainer.Type, DataContainer> getData();

    @NotNull
    default Optional<DataContainer.Gift> getGift() {
        return Optional.ofNullable((DataContainer.Gift) getData().get(DataContainer.Type.GIFT_DATA));
    }

    default void setGift(@NotNull DataContainer.Gift gift) {
        getData().put(DataContainer.Type.GIFT_DATA, gift);
    }

    @NotNull
    default Optional<DataContainer.Stats> getStats() {
        return Optional.ofNullable((DataContainer.Stats) getData().get(DataContainer.Type.STATS));
    }

    default void setStats(@NotNull DataContainer.Stats stats) {
        getData().put(DataContainer.Type.STATS, stats);
    }

    @NotNull
    default Optional<DataContainer.Party> getParty() {
        return Optional.ofNullable((DataContainer.Party) getData().get(DataContainer.Type.PARTY));
    }

    default void setParty(@NotNull DataContainer.Party party) {
        getData().put(DataContainer.Type.PARTY, party);
    }

    @NotNull
    default Optional<DataContainer.PC> getPC() {
        return Optional.ofNullable((DataContainer.PC) getData().get(DataContainer.Type.PC));
    }

    default void setPC(@NotNull DataContainer.PC pc) {
        getData().put(DataContainer.Type.PC, pc);
    }

    @NotNull
    default Optional<DataContainer.Money> getMoney() {
        return Optional.ofNullable((DataContainer.Money) getData().get(DataContainer.Type.MONEY));
    }

    default void setMoney(@NotNull DataContainer.Money money) {
        getData().put(DataContainer.Type.MONEY, money);
    }

    @NotNull
    default Optional<DataContainer.MegaItem> getMegaItem() {
        return Optional.ofNullable((DataContainer.MegaItem) getData().get(DataContainer.Type.MEGA_ITEM));
    }

    default void setMegaItem(@NotNull DataContainer.MegaItem megaItem) {
        getData().put(DataContainer.Type.MEGA_ITEM, megaItem);
    }

    @NotNull
    default Optional<DataContainer.Charm> getCharm() {
        return Optional.ofNullable((DataContainer.Charm) getData().get(DataContainer.Type.CHARM));
    }

    default void setCharm(@NotNull DataContainer.Charm charm) {
        getData().put(DataContainer.Type.CHARM, charm);
    }

    @NotNull
    default Optional<DataContainer.Lure> getLure() {
        return Optional.ofNullable((DataContainer.Lure) getData().get(DataContainer.Type.LURE));
    }

    default void setLure(@NotNull DataContainer.Lure lure) {
        getData().put(DataContainer.Type.LURE, lure);
    }

    @NotNull
    default Optional<DataContainer.ServerCosmetic> getServerCosmetic() {
        return Optional.ofNullable((DataContainer.ServerCosmetic) getData().get(DataContainer.Type.SERVER_COSMETIC));
    }

    default void setServerCosmetic(@NotNull DataContainer.ServerCosmetic serverCosmetic) {
        getData().put(DataContainer.Type.SERVER_COSMETIC, serverCosmetic);
    }

    @NotNull
    default Optional<DataContainer.Pokedex> getPokedex() {
        return Optional.ofNullable((DataContainer.Pokedex) getData().get(DataContainer.Type.POKEDEX));
    }

    default void setPokedex(@NotNull DataContainer.Pokedex pokedex) {
        getData().put(DataContainer.Type.POKEDEX, pokedex);
    }

    @NotNull
    default Optional<DataContainer.Curry> getCurry() {
        return Optional.ofNullable((DataContainer.Curry) getData().get(DataContainer.Type.CURRY));
    }

    default void setCurry(@NotNull DataContainer.Curry curry) {
        getData().put(DataContainer.Type.CURRY, curry);
    }

    @NotNull
    default Optional<DataContainer.TrainerCard> getTrainerCard() {
        return Optional.ofNullable((DataContainer.TrainerCard) getData().get(DataContainer.Type.TRAINER_CARD));
    }

    default void setTrainerCard(@NotNull DataContainer.TrainerCard trainerCard) {
        getData().put(DataContainer.Type.TRAINER_CARD, trainerCard);
    }

    @NotNull
    default Optional<DataContainer.Daycare> getDaycare() {
        return Optional.ofNullable((DataContainer.Daycare) getData().get(DataContainer.Type.DAYCARE));
    }

    default void setDaycare(@NotNull DataContainer.Daycare daycare) {
        getData().put(DataContainer.Type.DAYCARE, daycare);
    }

    @NotNull
    default Optional<DataContainer.Quest> getQuest() {
        return Optional.ofNullable((DataContainer.Quest) getData().get(DataContainer.Type.QUEST));
    }

    default void setQuest(@NotNull DataContainer.Quest quest) {
        getData().put(DataContainer.Type.QUEST, quest);
    }


}
