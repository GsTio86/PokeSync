package me.gt86.pokesync.hook;

import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.PixelmonData;
import me.gt86.pokesync.data.PixelmonSerializer;
import net.william278.husksync.HuskSync;
import net.william278.husksync.api.BukkitHuskSyncAPI;
import net.william278.husksync.data.DataSnapshot;
import net.william278.husksync.data.Identifier;
import net.william278.husksync.event.BukkitDataSaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.function.Consumer;

import static me.gt86.pokesync.PokeSync.PLUGIN_ID;

public class HuskSyncAPIHook implements Listener {

    private final PokeSync pokeSync;
    private final BukkitHuskSyncAPI huskSyncAPI;

    public static Identifier PC = Identifier.from(PLUGIN_ID, "pixelmon_pc");
    public static Identifier PARTY = Identifier.from(PLUGIN_ID, "pixelmon_party");
    public static Identifier POKEDEX = Identifier.from(PLUGIN_ID, "pixelmon_pokedex");
    public static Identifier STATS = Identifier.from(PLUGIN_ID, "pixelmon_stats");
    public static Identifier MONEY = Identifier.from(PLUGIN_ID, "pixelmon_dollars");
    public static Identifier DAYCARE = Identifier.from(PLUGIN_ID, "pixelmon_daycare");
    public static Identifier MEGA_ITEM = Identifier.from(PLUGIN_ID, "pixelmon_mega_item");
    public static Identifier CHARM = Identifier.from(PLUGIN_ID, "pixelmon_charm");
    public static Identifier GIFTDATA = Identifier.from(PLUGIN_ID, "pixelmon_gift_data");
    public static Identifier TRAINER_CARD = Identifier.from(PLUGIN_ID, "pixelmon_trainer_card");
    public static Identifier COSMETIC = Identifier.from(PLUGIN_ID, "pixelmon_server_cosmetic");
    public static Identifier LURE = Identifier.from(PLUGIN_ID, "pixelmon_lure");
    public static Identifier QUEST = Identifier.from(PLUGIN_ID, "pixelmon_quest");
    public static Identifier CURRY = Identifier.from(PLUGIN_ID, "pixelmon_curry");

    public HuskSyncAPIHook(PokeSync pokeSync) {
        this.pokeSync = pokeSync;
        this.huskSyncAPI = BukkitHuskSyncAPI.getInstance();
        register();
    }

    private void register() {
        HuskSync huskSync = huskSyncAPI.getPlugin();
        huskSyncAPI.registerDataSerializer(PC, new PixelmonSerializer.PC(huskSync));
        huskSyncAPI.registerDataSerializer(PARTY, new PixelmonSerializer.Party(huskSync));
        huskSyncAPI.registerDataSerializer(POKEDEX, new PixelmonSerializer.Pokedex(huskSync));
        huskSyncAPI.registerDataSerializer(STATS, new PixelmonSerializer.Stats(huskSync));
        huskSyncAPI.registerDataSerializer(MONEY, new PixelmonSerializer.Money(huskSync));
        huskSyncAPI.registerDataSerializer(DAYCARE, new PixelmonSerializer.Daycare(huskSync));
        huskSyncAPI.registerDataSerializer(MEGA_ITEM, new PixelmonSerializer.MegaItem(huskSync));
        huskSyncAPI.registerDataSerializer(CHARM, new PixelmonSerializer.Charm(huskSync));
        huskSyncAPI.registerDataSerializer(GIFTDATA, new PixelmonSerializer.Gift(huskSync));
        huskSyncAPI.registerDataSerializer(TRAINER_CARD, new PixelmonSerializer.TrainerCard(huskSync));
        huskSyncAPI.registerDataSerializer(COSMETIC, new PixelmonSerializer.Cosmetic(huskSync));
        huskSyncAPI.registerDataSerializer(LURE, new PixelmonSerializer.Lure(huskSync));
        huskSyncAPI.registerDataSerializer(QUEST, new PixelmonSerializer.Quest(huskSync));
        huskSyncAPI.registerDataSerializer(CURRY, new PixelmonSerializer.Curry(huskSync));

        Bukkit.getServer().getPluginManager().registerEvents(this, this.pokeSync);

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHuskSyncDataSave(BukkitDataSaveEvent event) {
        event.editData(new Consumer<DataSnapshot.Unpacked>() {
            @Override
            public void accept(DataSnapshot.Unpacked unpacked) {
                UUID uuid = event.getUser().getUuid();
                unpacked.setData(PC, new PixelmonData.PC(uuid));
                unpacked.setData(PARTY, new PixelmonData.Party(uuid));
                unpacked.setData(POKEDEX, new PixelmonData.Pokedex(uuid));
                unpacked.setData(STATS, new PixelmonData.Stats(uuid));
                unpacked.setData(MONEY, new PixelmonData.Money(uuid));
                unpacked.setData(DAYCARE, new PixelmonData.Daycare(uuid));
                unpacked.setData(MEGA_ITEM, new PixelmonData.MegaItem(uuid));
                unpacked.setData(CHARM, new PixelmonData.Charm(uuid));
                unpacked.setData(GIFTDATA, new PixelmonData.Gift(uuid));
                unpacked.setData(TRAINER_CARD, new PixelmonData.TrainerCard(uuid));
                unpacked.setData(COSMETIC, new PixelmonData.Cosmetic(uuid));
                unpacked.setData(LURE, new PixelmonData.Lure(uuid));
                unpacked.setData(QUEST, new PixelmonData.Quest(uuid));
                unpacked.setData(CURRY, new PixelmonData.Curry(uuid));
            }
        });
    }
}
