package me.gt86.pokesync.data;

import me.gt86.pokesync.data.type.*;
import me.gt86.pokesync.player.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/**
 * A piece of data, held by an {@link DataOwner}
 */
public interface DataContainer {

    /**
     * Apply (set) this data container to the given {@link OnlineUser}
     *
     * @param user the user to apply this element to
     */
    void apply(@NotNull DataOwner user) throws IllegalStateException;

    /**
     * Enumeration of types of {@link DataContainer}s
     */
    enum Type {
        GIFT_DATA(true),
        STATS(true),
        PARTY(true),
        PC(true),
        MONEY(true),
        MEGA_ITEM(true),
        CHARM(true),
        LURE(true),
        SERVER_COSMETIC(true),
        POKEDEX(true),
        CURRY(true),
        TRAINER_CARD(true),
        DAYCARE(true),
        QUEST(true);

        private final boolean defaultSetting;

        Type(boolean defaultSetting) {
            this.defaultSetting = defaultSetting;
        }

        @NotNull
        private Map.Entry<String, Boolean> toEntry() {
            return Map.entry(name().toLowerCase(Locale.ENGLISH), defaultSetting);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        public static Map<String, Boolean> getDefaults() {
            return Map.ofEntries(Arrays.stream(values())
                .map(Type::toEntry)
                .toArray(Map.Entry[]::new));
        }

        public boolean getDefault() {
            return defaultSetting;
        }

    }

    interface Gift extends DataContainer {

        GiftData getGiftData();

        void setGiftData(GiftData playerDataData);
    }

    interface Stats extends DataContainer {

        StatsData getStatsData();

        void setStatsData(StatsData statsData);
    }


    interface Party extends DataContainer {

        PartyData getPartyData();

        void setPartyData(PartyData partyData);
    }

    interface PC extends DataContainer {

        PCData getPCData();

        void setPCData(PCData pcData);
    }

    interface Money extends DataContainer {

        MoneyData getMoneyData();

        void setMoneyData(MoneyData moneyData);
    }

    interface MegaItem extends DataContainer {
        MegaItemData getMegaItemData();

        void setMegaItemData(MegaItemData megaItemData);
    }

    interface Charm extends DataContainer {
        CharmData getCharmData();

        void setCharmData(CharmData charmData);
    }

    interface Lure extends DataContainer {
        LureData getLureData();

        void setLureData(LureData lureData);
    }

    interface ServerCosmetic extends DataContainer {

        ServerCosmeticData getServerCosmeticData();

        void setServerCosmeticData(ServerCosmeticData serverCosmeticData);

    }

    interface Pokedex extends DataContainer {
        PokedexData getPokedexData();

        void setPokedexData(PokedexData pokedexData);
    }

    interface Curry extends DataContainer {
        CurryData getCurryData();

        void setCurryData(CurryData curryData);
    }

    interface TrainerCard extends DataContainer {
        TrainerCardData getTrainerCardData();

        void setTrainerCardData(TrainerCardData trainerCardData);

    }

    interface Daycare extends DataContainer {
        DaycareData getDaycareData();

        void setDaycareData(DaycareData daycareData);
    }

    interface Quest extends DataContainer {
        QuestData getQuestData();

        void setQuestData(QuestData questData);
    }


}
