package me.gt86.pokesync.data;

import me.gt86.pokesync.PokeSync;
import net.william278.husksync.HuskSync;
import net.william278.husksync.data.BukkitData;
import net.william278.husksync.data.Identifier;
import net.william278.husksync.data.Serializer;

import java.util.UUID;
import java.util.function.Function;

public enum PixelmonDataType {

    PC(Identifier.from(PokeSync.PLUGIN_ID, "pc"), PixelmonSerializer.PC::new, PixelmonData.PC::new),
    PARTY(Identifier.from(PokeSync.PLUGIN_ID, "party"), PixelmonSerializer.Party::new, PixelmonData.Party::new),
    POKEDEX(Identifier.from(PokeSync.PLUGIN_ID, "pokedex"), PixelmonSerializer.Pokedex::new, PixelmonData.Pokedex::new),
    STATS(Identifier.from(PokeSync.PLUGIN_ID, "stats"), PixelmonSerializer.Stats::new, PixelmonData.Stats::new),
    MONEY(Identifier.from(PokeSync.PLUGIN_ID, "dollars"), PixelmonSerializer.Money::new, PixelmonData.Money::new),
    DAYCARE(Identifier.from(PokeSync.PLUGIN_ID, "daycare"), PixelmonSerializer.Daycare::new, PixelmonData.Daycare::new),
    MEGA_ITEM(Identifier.from(PokeSync.PLUGIN_ID, "mega_item"), PixelmonSerializer.MegaItem::new, PixelmonData.MegaItem::new),
    CHARM(Identifier.from(PokeSync.PLUGIN_ID, "charm"), PixelmonSerializer.Charm::new, PixelmonData.Charm::new),
    GIFTDATA(Identifier.from(PokeSync.PLUGIN_ID, "gift_data"), PixelmonSerializer.Gift::new, PixelmonData.Gift::new),
    TRAINER_CARD(Identifier.from(PokeSync.PLUGIN_ID, "trainer_card"), PixelmonSerializer.TrainerCard::new, PixelmonData.TrainerCard::new),

    COSMETIC(Identifier.from(PokeSync.PLUGIN_ID, "cosmetic"), PixelmonSerializer.Cosmetic::new, PixelmonData.Cosmetic::new),

    LURE(Identifier.from(PokeSync.PLUGIN_ID, "lure"), PixelmonSerializer.Lure::new, PixelmonData.Lure::new),

    QUEST(Identifier.from(PokeSync.PLUGIN_ID, "quest"), PixelmonSerializer.Quest::new, PixelmonData.Quest::new),

    CURRY(Identifier.from(PokeSync.PLUGIN_ID, "curry"), PixelmonSerializer.Curry::new, PixelmonData.Curry::new);

    private final Identifier identifier;

    private final Function<HuskSync, ? extends Serializer> serializerFunction;

    private final Function<UUID, ? extends BukkitData> constructor;

    PixelmonDataType(Identifier identifier, Function<HuskSync, ? extends Serializer> serializer, Function<UUID, ? extends BukkitData> constructor) {
        this.identifier = identifier;
        this.serializerFunction = serializer;
        this.constructor = constructor;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Serializer createSerializer(HuskSync api) {
        return serializerFunction.apply(api);
    }

    public BukkitData createData(UUID uuid) {
        return constructor.apply(uuid);
    }

    public boolean isEnable() {
        return PokeSync.getInstance().getConfig().getBoolean("features." + identifier.getKeyValue());
    }
}
