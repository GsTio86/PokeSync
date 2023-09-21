package me.gt86.pokesync.data;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.pixelmonmod.pixelmon.api.enums.ServerCosmetics;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.storage.*;
import com.pixelmonmod.pixelmon.api.storage.breeding.PlayerDayCare;
import com.pixelmonmod.pixelmon.api.util.helpers.NetworkHelper;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.comm.data.PixelmonPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.ServerCosmeticsUpdatePacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.UpdateClientPlayerDataPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.daycare.SendEntireDayCarePacket;
import com.pixelmonmod.pixelmon.enums.EnumFeatureState;
import com.pixelmonmod.pixelmon.enums.EnumMegaItem;
import com.pixelmonmod.pixelmon.enums.EnumMegaItemsUnlocked;
import com.pixelmonmod.pixelmon.enums.EnumTrainerCardColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.william278.husksync.BukkitHuskSync;
import net.william278.husksync.adapter.Adaptable;
import net.william278.husksync.data.BukkitData;
import net.william278.husksync.user.BukkitUser;
import org.bukkit.entity.Player;

import java.util.*;

public class PixelmonData {

    public static class PC extends BukkitData implements Data.PC, Adaptable {

        @SerializedName("pc")
        private String data;

        public PC(String data) {
            this.data = data;
        }

        public PC(UUID uuid) {
            PCStorage pcStorage = StorageProxy.getPCForPlayer(uuid);
            CompoundTag nbt = new CompoundTag();
            pcStorage.writeToNBT(nbt);
            this.data = nbt.toString();
        }

        @SuppressWarnings("unused")
        private PC() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PCStorage pcStorage = StorageProxy.getPCForPlayer(player.getUniqueId());
            try {
                CompoundTag nbt = TagParser.parseTag(data);
                pcStorage.readFromNBT(nbt);
                for (int i = 0; i < PixelmonConfigProxy.getStorage().getComputerBoxes(); i++) {
                    for (int j = 0; j < PCBox.POKEMON_PER_BOX; j++)
                        pcStorage.notifyListeners(new StoragePosition(i, j), pcStorage.getBox(i).get(j), EnumUpdateType.CLIENT);
                }
            } catch (CommandSyntaxException e) {
                throw new IllegalStateException("Failed to apply pixelmon pc", e);
            }
        }
    }

    public static class Party extends BukkitData implements Data.Party, Adaptable {

        @SerializedName("party")
        public Map<Integer, String> party;

        public Party(Map<Integer, String> party) {
            this.party = party;
        }

        public Party(UUID uuid) {
            Map<Integer, String> party = new HashMap<>();
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            for (int i = 0; i < PlayerPartyStorage.MAX_PARTY; i++) {
                Pokemon pokemon = storage.get(i);
                if (pokemon != null) {
                    party.put(i, pokemon.writeToNBT(new CompoundTag()).toString());
                }
            }
            this.party = party;
        }

        @SuppressWarnings("unused")
        private Party() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            Map<Integer, String> data = party;
            for (int i = 0; i < PartyStorage.MAX_PARTY; i++) {
                if (data.containsKey(i)) {
                    try {
                        CompoundTag nbt = TagParser.parseTag(data.get(i));
                        Pokemon pokemon = PokemonFactory.create(nbt);
                        storage.set(i, pokemon);
                    } catch (CommandSyntaxException e) {
                        throw new IllegalStateException("Failed to apply pixelmon party", e);
                    }
                } else {
                    storage.set(i, null);
                }
                storage.notifyListeners(new StoragePosition(-1, i), storage.get(i), EnumUpdateType.CLIENT);
            }
        }
    }

    public static class Pokedex extends BukkitData implements Data.Pokedex, Adaptable {

        @SerializedName("dex")
        private String dex;

        public Pokedex(String dex) {
            this.dex = dex;
        }

        public Pokedex(UUID uuid) {
            CompoundTag dex = new CompoundTag();
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            storage.playerPokedex.writeToNBT(dex);
            this.dex = dex.toString();
        }

        @SuppressWarnings("unused")
        private Pokedex() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            try {
                storage.playerPokedex.readFromNBT(Objects.requireNonNull(TagParser.parseTag(dex)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon pokedex", e);
            }
            storage.playerPokedex.checkForCharms();
            storage.playerPokedex.update();
        }
    }

    public static class Stats extends BukkitData implements Data.Stats, Adaptable {

        @SerializedName("stats")
        private String stats;

        @SerializedName("starter_picked")
        private boolean starterPicked;

        @SerializedName("battle_enabled")
        private boolean battleEnabled;

        public Stats(String stats, boolean starterPicked, boolean battleEnabled) {
            this.stats = stats;
            this.starterPicked = starterPicked;
            this.battleEnabled = battleEnabled;
        }

        public Stats(UUID uuid) {
            CompoundTag nbt = new CompoundTag();
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            storage.stats.writeToNBT(nbt);
            this.stats = nbt.toString();
            this.starterPicked = storage.starterPicked;
            this.battleEnabled = storage.battleEnabled;
        }

        @SuppressWarnings("unused")
        private Stats() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            try {
                PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
                storage.stats.readFromNBT(Objects.requireNonNull(TagParser.parseTag(stats)));
                storage.starterPicked = starterPicked;
                storage.battleEnabled = battleEnabled;
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon stats", e);
            }
        }
    }

    public static class Money extends BukkitData implements Data.Money, Adaptable {

        @SerializedName("money")
        private int money;

        public Money(int money) {
            this.money = money;
        }

        public Money(UUID uuid) {
            Optional<? extends BankAccount> optionalAccount = BankAccountProxy.getBankAccount(uuid);
            if (optionalAccount.isPresent()) {
                this.money = optionalAccount.get().getBalance().intValue();
            } else {
                this.money = 0;
            }
        }

        @SuppressWarnings("unused")
        private Money() {

        }


        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            Optional<? extends BankAccount> optionalAccount = BankAccountProxy.getBankAccount(player.getUniqueId());
            if (optionalAccount.isPresent()) {
                optionalAccount.get().setBalance(money);
                BankAccountProxy.getBankAccount(player.getUniqueId()).get().updatePlayer();
            }
        }
    }

    public static class Daycare extends BukkitData implements Data.Daycare, Adaptable {

        @SerializedName("daycare")
        private String daycare;

        public Daycare(String daycare) {
            this.daycare = daycare;
        }

        public Daycare(UUID uuid) {
            CompoundTag nbt = new CompoundTag();
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            storage.getDayCare().writeToNBT(nbt);
            this.daycare = nbt.toString();
        }

        @SuppressWarnings("unused")
        private Daycare() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            try {
                PlayerDayCare dayCare = PlayerDayCare.readFromNBT(storage, TagParser.parseTag(daycare));
                ObfuscationReflectionHelper.setPrivateValue(PlayerPartyStorage.class, storage, dayCare, "dayCare");
                sendPacket(player.getUniqueId(), new SendEntireDayCarePacket(dayCare));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon daycare", e);
            }
        }
    }

    public static class MegaItem extends BukkitData implements Data.MegaItem, Adaptable {

        @SerializedName("mega_items_unlocked")
        private int megaItemsUnlocked;

        @SerializedName("mega_item_string")
        private int megaItemString;

        public MegaItem(int megaItemsUnlocked, int megaItemString) {
            this.megaItemsUnlocked = megaItemsUnlocked;
            this.megaItemString = megaItemString;
        }

        public MegaItem(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            this.megaItemsUnlocked = storage.getMegaItemsUnlocked().ordinal();
            this.megaItemString = storage.getMegaItem().ordinal();
        }

        @SuppressWarnings("unused")
        private MegaItem() {

        }


        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            storage.setMegaItemsUnlocked(EnumMegaItemsUnlocked.values()[megaItemsUnlocked]);
            storage.setMegaItem(EnumMegaItem.values()[megaItemString], false);

            if (player != null) {
                if (PixelmonConfigProxy.getGeneral().isAlwaysHaveMegaRing())
                    try {
                        if (!storage.getMegaItemsUnlocked().canMega()) {
                            storage.setMegaItem(EnumMegaItem.BraceletORAS, false);
                            storage.unlockMega(true);
                        }
                    } catch (Exception e) {
                    }
                if (PixelmonConfigProxy.getGeneral().isAlwaysHaveDynamaxBand())
                    try {
                        if (!storage.getMegaItemsUnlocked().canDynamax()) {
                            storage.setMegaItem(EnumMegaItem.DynamaxBand, false);
                            storage.unlockDynamax(true);
                        }
                    } catch (Exception e) {
                    }
            }
        }
    }

    public static class Charm extends BukkitData implements Data.Charm, Adaptable {

        @SerializedName("shiny_charm")
        private int shinyCharm;
        @SerializedName("oval_charm")
        private int ovalCharm;

        @SerializedName("exp_charm")
        private int expCharm;

        @SerializedName("catching_charm")
        private int catchingCharm;

        @SerializedName("mark_charm")
        private int markCharm;

        public Charm(int shinyCharm, int ovalCharm, int expCharm, int catchingCharm, int markCharm) {
            this.shinyCharm = shinyCharm;
            this.ovalCharm = ovalCharm;
            this.expCharm = expCharm;
            this.catchingCharm = catchingCharm;
            this.markCharm = markCharm;
        }

        public Charm(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            this.shinyCharm = storage.getShinyCharm().ordinal();
            this.ovalCharm = storage.getOvalCharm().ordinal();
            this.expCharm = storage.getExpCharm().ordinal();
            this.catchingCharm = storage.getCatchingCharm().ordinal();
            this.markCharm = storage.getMarkCharm().ordinal();
        }

        @SuppressWarnings("unused")
        private Charm() {

        }


        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            storage.setShinyCharm(EnumFeatureState.values()[shinyCharm]);
            storage.setOvalCharm(EnumFeatureState.values()[ovalCharm]);
            storage.setExpCharm(EnumFeatureState.values()[expCharm]);
            storage.setCatchingCharm(EnumFeatureState.values()[catchingCharm]);
            storage.setMarkCharm(EnumFeatureState.values()[markCharm]);
        }
    }

    public static class Gift extends BukkitData implements Data.Gift, Adaptable {

        @SerializedName("gift")
        private String gift;

        public Gift(String gift) {
            this.gift = gift;
        }

        public Gift(UUID uuid) {
            CompoundTag nbt = new CompoundTag();
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            storage.playerData.writeToNBT(nbt);
            this.gift = nbt.toString();
        }

        @SuppressWarnings("unused")
        private Gift() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            try {
                storage.playerData.readFromNBT(Objects.requireNonNull(TagParser.parseTag(gift)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon playerData", e);
            }
        }
    }

    public static class TrainerCard extends BukkitData implements Data.TrainerCard, Adaptable {

        @SerializedName("trainer_card_color")
        private int color;

        public TrainerCard(int color) {
            this.color = color;
        }

        public TrainerCard(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            this.color = storage.trainerCardColor.ordinal();
        }

        @SuppressWarnings("unused")
        private TrainerCard() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            storage.trainerCardColor = EnumTrainerCardColor.values()[color];
            sendPacket(player.getUniqueId(), new UpdateClientPlayerDataPacket(storage.trainerCardColor));
        }
    }

    public static class Cosmetic extends BukkitData implements Data.Cosmetic, Adaptable {

        @SerializedName("cosmetic")
        public byte[] data;

        public Cosmetic(byte[] data) {
            this.data = data;
        }

        public Cosmetic(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            BitSet set = new BitSet();
            Set<ServerCosmetics> serverCosmetics = storage.getServerCosmetics();
            for (ServerCosmetics serverCosmetic : serverCosmetics) {
                set.set(serverCosmetic.ordinal());
            }
            this.data = set.toByteArray();
        }

        @SuppressWarnings("unused")
        private Cosmetic() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            BitSet set = BitSet.valueOf(data);
            Set<ServerCosmetics> serverCosmetics = new HashSet<>();
            for (ServerCosmetics cosmetics : ServerCosmetics.values()) {
                if (set.get(cosmetics.ordinal())) {
                    serverCosmetics.add(cosmetics);
                }
            }
            storage.setServerCosmetics(serverCosmetics);
            sendPacket(player.getUniqueId(), new ServerCosmeticsUpdatePacket(storage.getServerCosmetics()));
        }
    }

    public static class Lure extends BukkitData implements Data.Lure, Adaptable {

        @SerializedName("lure")
        private String data;

        public Lure(String data) {
            this.data = data;
        }

        public Lure(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            ItemStack itemStack = storage.getLureStack();
            CompoundTag nbt = new CompoundTag();
            if (itemStack != null) {
                itemStack.save(nbt);
            }
            this.data = nbt.toString();
        }

        @SuppressWarnings("unused")
        private Lure() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            if (data.isBlank()) {
                storage.setLureStack(null);
            } else {
                try {
                    ItemStack itemStack = ItemStack.of(TagParser.parseTag(data));
                    storage.setLureStack(itemStack);
                } catch (CommandSyntaxException e) {
                    throw new IllegalStateException("Failed to apply pixelmon lure", e);
                }
            }
        }
    }

    public static class Quest extends BukkitData implements Data.Quest, Adaptable {

        @SerializedName("quest")
        private String data;

        public Quest(String data) {
            this.data = data;
        }

        public Quest(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            CompoundTag nbt = new CompoundTag();
            storage.getQuestData().writeToNBT(nbt);
            this.data = nbt.toString();
        }

        @SuppressWarnings("unused")
        private Quest() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            try {
                storage.getQuestData().readFromNBT(Objects.requireNonNull(TagParser.parseTag(data)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon quest", e);
            }
        }
    }

    public static class Curry extends BukkitData implements Data.Curry, Adaptable {

        @SerializedName("curry")
        private int[] data;

        public Curry(int[] data) {
            this.data = data;
        }

        public Curry(UUID uuid) {
            PlayerPartyStorage storage = StorageProxy.getParty(uuid);
            this.data = storage.getCurryData();
        }

        @SuppressWarnings("unused")
        private Curry() {

        }

        @Override
        public void apply(BukkitUser user, BukkitHuskSync plugin) throws IllegalStateException {
            Player player = user.getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            int[] currys = data;
            ObfuscationReflectionHelper.setPrivateValue(PlayerPartyStorage.class, storage, currys, "curryData");
        }
    }

    public static void sendPacket(UUID uuid, PixelmonPacket object) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server.getPlayerList().getPlayer(uuid) != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            NetworkHelper.sendPacket(player, object);
        }
    }
}
