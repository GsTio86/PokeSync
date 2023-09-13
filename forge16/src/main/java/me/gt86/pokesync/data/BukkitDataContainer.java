package me.gt86.pokesync.data;

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
import com.pixelmonmod.pixelmon.comm.packetHandlers.ServerCosmeticsUpdatePacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.UpdateClientPlayerDataPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.SetTempMode;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientInitializePCPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.daycare.SendEntireDayCarePacket;
import com.pixelmonmod.pixelmon.enums.EnumFeatureState;
import com.pixelmonmod.pixelmon.enums.EnumMegaItem;
import com.pixelmonmod.pixelmon.enums.EnumMegaItemsUnlocked;
import com.pixelmonmod.pixelmon.enums.EnumTrainerCardColor;
import me.gt86.pokesync.data.type.*;
import me.gt86.pokesync.player.BukkitUser;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BukkitDataContainer implements DataContainer {

    public static class PMPlayerData implements DataContainer.PMPlayerData {
        private PlayerData playerData;

        private PMPlayerData(@NotNull Player player) {
            CompoundNBT nbt = new CompoundNBT();
            StorageProxy.getParty(player.getUniqueId()).playerData.writeToNBT(nbt);
            this.playerData = new PlayerData(nbt.toString());
        }

        @NotNull
        public static BukkitDataContainer.PMPlayerData adapt(@NotNull Player player) {
            return new BukkitDataContainer.PMPlayerData(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            try {
                StorageProxy.getParty(player.getUniqueId()).playerData.readFromNBT(Objects.requireNonNull(JsonToNBT.parseTag(getPlayerData().data)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon playerData", e);
            }
        }

        @Override
        public PlayerData getPlayerData() {
            return playerData;
        }

        @Override
        public void setPlayerData(PlayerData playerData) {
            this.playerData = playerData;
        }
    }

    public static class Stats implements DataContainer.Stats {
        private StatsData statsData;

        private Stats(@NotNull Player player) {
            CompoundNBT nbt = new CompoundNBT();
            StorageProxy.getParty(player.getUniqueId()).stats.writeToNBT(nbt);
            this.statsData = new StatsData(nbt.toString());
        }

        @NotNull
        public static BukkitDataContainer.Stats adapt(@NotNull Player player) {
            return new BukkitDataContainer.Stats(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            try {
                StorageProxy.getParty(player.getUniqueId()).stats.readFromNBT(Objects.requireNonNull(JsonToNBT.parseTag(getStatsData().data)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon stats", e);
            }
        }

        @Override
        public StatsData getStatsData() {
            return statsData;
        }

        @Override
        public void setStatsData(StatsData statsData) {
            this.statsData = statsData;
        }
    }

    public static class Party implements DataContainer.Party {

        private PartyData partyData;

        private Party(@NotNull Player player) {
            Map<Integer, String> party = new HashMap<>();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            for (int i = 0; i < PlayerPartyStorage.MAX_PARTY; i++) {
                Pokemon pokemon = storage.get(i);
                if (pokemon != null) {
                    if (storage.inTemporaryMode()) {
                        party.put(i, pokemon.writeToNBT(new CompoundNBT()).toString());
                    }
                }
            }
            this.partyData = new PartyData(party, storage.inTemporaryMode(), storage.getTempPartyColor());
        }

        @NotNull
        public static BukkitDataContainer.Party adapt(@NotNull Player player) {
            return new BukkitDataContainer.Party(player);
        }

        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(serverPlayer.getUUID());
            Map<Integer, String> data = partyData.party;
            for (int slot : data.keySet()) {
                try {
                    CompoundNBT nbt = JsonToNBT.parseTag(data.get(slot));
                    Pokemon pokemon = PokemonFactory.create(nbt);
                    storage.set(slot, pokemon);
                } catch (CommandSyntaxException e) {
                    throw new IllegalStateException("Failed to apply pixelmon party", e);
                }
            }
            if (serverPlayer != null) {
                NetworkHelper.sendPacket(new SetTempMode(partyData.tempMode, partyData.tempPartyColor), serverPlayer);
                for (int i = 0; i < 6; i++)
                    storage.notifyListeners(new StoragePosition(-1, i), storage.get(i), EnumUpdateType.CLIENT);
            }
        }

        public PartyData getPartyData() {
            return partyData;
        }

        public void setPartyData(PartyData partyData) {
            this.partyData = partyData;
        }
    }


    public static class PC implements DataContainer.PC {

        private PCData pcData;

        private PC(@NotNull Player player) {
            Map<BoxData, PokemonData[]> data = new HashMap<>();
            PokemonData[] pokemonData = new PokemonData[30];
            PCStorage pcStorage = StorageProxy.getPCForPlayer(player.getUniqueId());
            for (int box = 0; box < pcStorage.getBoxCount(); box++) {
                PCBox pcBox = pcStorage.getBox(box);
                for (int slot = 0; slot < pokemonData.length; slot++) {
                    Pokemon pokemon = pcStorage.get(box, slot);
                    if (pokemon != null) {
                        pokemonData[slot] = new PokemonData(pokemon.writeToNBT(new CompoundNBT()).toString());
                    }
                }
                data.put(new BoxData(pcBox.boxNumber, pcBox.getName(), pcBox.getWallpaper()), pokemonData);
            }
            this.pcData = new PCData(data);
        }

        @NotNull
        public static BukkitDataContainer.PC adapt(@NotNull Player player) {
            return new BukkitDataContainer.PC(player);
        }

        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            Map<BoxData, PokemonData[]> data = pcData.pcStorage;
            PCStorage pcStorage = StorageProxy.getPCForPlayer(serverPlayer.getUUID());
            for (BoxData boxData : data.keySet()) {
                PCBox pcBox = pcStorage.getBox(boxData.box);
                pcBox.setName(boxData.name);
                pcBox.setWallpaper(boxData.wallpaper);
                PokemonData[] pokemonData = data.get(boxData);
                for (int slot = 0; slot < pokemonData.length; slot++) {
                    try {
                        CompoundNBT nbt = JsonToNBT.parseTag(pokemonData[slot].data);
                        Pokemon pokemon = PokemonFactory.create(nbt);
                        pcStorage.set(boxData.box, slot, pokemon);
                    } catch (CommandSyntaxException e) {
                        throw new IllegalStateException("Failed to apply pixelmon pc", e);
                    }
                }
            }
            if (serverPlayer != null) {
                NetworkHelper.sendPacket(serverPlayer, new ClientInitializePCPacket(pcStorage));
                pcStorage.sendContents(serverPlayer);
            }
        }

        @Override
        public PCData getPCData() {
            return pcData;
        }

        @Override
        public void setPCData(PCData pcData) {
            this.pcData = pcData;
        }
    }


    public static class Money implements DataContainer.Money {
        private MoneyData moneyData;

        private Money(@NotNull Player player) {
            Optional<? extends BankAccount> optionalAccount = BankAccountProxy.getBankAccount(player.getUniqueId());
            if (optionalAccount.isPresent()) {
                moneyData = new MoneyData(optionalAccount.get().getBalance().intValue());
            } else {
                moneyData = new MoneyData(0);
            }
        }

        @NotNull
        public static BukkitDataContainer.Money adapt(@NotNull Player player) {
            return new BukkitDataContainer.Money(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            Optional<? extends BankAccount> optionalAccount = BankAccountProxy.getBankAccount(serverPlayer.getUUID());
            if (optionalAccount.isPresent()) {
                optionalAccount.get().setBalance(moneyData.money);
                if (serverPlayer != null) {
                    BankAccountProxy.getBankAccount(serverPlayer).get().updatePlayer();
                }
            }
        }

        @Override
        public MoneyData getMoneyData() {
            return moneyData;
        }

        @Override
        public void setMoneyData(MoneyData moneyData) {
            this.moneyData = moneyData;
        }
    }


    public static class MegaItem implements DataContainer.MegaItem {
        private MegaItemData megaItemData;

        private MegaItem(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            EnumMegaItemsUnlocked megaItemsUnlocked = storage.getMegaItemsUnlocked();
            EnumMegaItem megaItem = storage.getMegaItem();
            megaItemData = new MegaItemData(megaItemsUnlocked.ordinal(), megaItem.ordinal());
        }

        @NotNull
        public static BukkitDataContainer.MegaItem adapt(@NotNull Player player) {
            return new BukkitDataContainer.MegaItem(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(serverPlayer.getUUID());
            storage.setMegaItemsUnlocked(EnumMegaItemsUnlocked.values()[megaItemData.megaItemsUnlocked]);
            storage.setMegaItem(EnumMegaItem.values()[megaItemData.megaItemString], false);
            if (serverPlayer != null) {
                if (PixelmonConfigProxy.getGeneral().isAlwaysHaveMegaRing())
                    try {
                        if (!storage.getMegaItemsUnlocked().canMega()) {
                            storage.setMegaItem(EnumMegaItem.BraceletORAS, false);
                            storage.unlockMega();
                        }
                    } catch (Exception e) {
                    }
                if (PixelmonConfigProxy.getGeneral().isAlwaysHaveDynamaxBand())
                    try {
                        if (!storage.getMegaItemsUnlocked().canDynamax()) {
                            storage.setMegaItem(EnumMegaItem.DynamaxBand, false);
                            storage.unlockDynamax();
                        }
                    } catch (Exception e) {
                    }
            }
        }


        @Override
        public MegaItemData getMegaItemData() {
            return megaItemData;
        }

        @Override
        public void setMegaItemData(MegaItemData megaItemData) {
            this.megaItemData = megaItemData;
        }
    }


    public static class Charm implements DataContainer.Charm {

        CharmData charmData;

        private Charm(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            EnumFeatureState shinyCharm = storage.getShinyCharm();
            EnumFeatureState ovalCharm = storage.getOvalCharm();
            EnumFeatureState expCharm = storage.getExpCharm();
            EnumFeatureState catchingCharm = storage.getCatchingCharm();
            EnumFeatureState markCharm = storage.getMarkCharm();
            charmData = new CharmData(shinyCharm.ordinal(), ovalCharm.ordinal(), expCharm.ordinal(), catchingCharm.ordinal(), markCharm.ordinal());
        }

        @NotNull
        public static BukkitDataContainer.Charm adapt(@NotNull Player player) {
            return new BukkitDataContainer.Charm(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            storage.setShinyCharm(EnumFeatureState.values()[charmData.shinyCharm]);
            storage.setOvalCharm(EnumFeatureState.values()[charmData.ovalCharm]);
            storage.setExpCharm(EnumFeatureState.values()[charmData.expCharm]);
            storage.setCatchingCharm(EnumFeatureState.values()[charmData.catchingCharm]);
            storage.setMarkCharm(EnumFeatureState.values()[charmData.markCharm]);
        }

        @Override
        public CharmData getCharmData() {
            return charmData;
        }

        @Override
        public void setCharmData(CharmData charmData) {
            this.charmData = charmData;
        }
    }

    public static class Lure implements DataContainer.Lure {
        LureData lureData;

        private Lure(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            ItemStack itemStack = storage.getLureStack();
            CompoundNBT nbt = new CompoundNBT();
            if (itemStack != null) {
                itemStack.save(nbt);
            }
            lureData = new LureData(nbt.toString());
        }

        @NotNull
        public static BukkitDataContainer.Lure adapt(@NotNull Player player) {
            return new BukkitDataContainer.Lure(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            if (lureData.lure.isBlank()) {
                storage.setLureStack(null);
            } else {
                try {
                    ItemStack itemStack = ItemStack.of(JsonToNBT.parseTag(lureData.lure));
                    storage.setLureStack(itemStack);
                } catch (CommandSyntaxException e) {
                    throw new IllegalStateException("Failed to apply pixelmon lure", e);
                }
            }
        }

        @Override
        public LureData getLureData() {
            return lureData;
        }

        @Override
        public void setLureData(LureData lureData) {
            this.lureData = lureData;
        }
    }

    public static class ServerCosmetic implements DataContainer.ServerCosmetic {

        ServerCosmeticData serverCosmeticData;

        private ServerCosmetic(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            BitSet set = new BitSet();
            Set<ServerCosmetics> serverCosmetics = storage.getServerCosmetics();
            for (ServerCosmetics serverCosmetic : serverCosmetics) {
                set.set(serverCosmetic.ordinal());
            }
            serverCosmeticData = new ServerCosmeticData(set.toByteArray());
        }

        @NotNull
        public static BukkitDataContainer.ServerCosmetic adapt(@NotNull Player player) {
            return new BukkitDataContainer.ServerCosmetic(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(serverPlayer.getUUID());
            BitSet set = BitSet.valueOf(serverCosmeticData.data);
            Set<ServerCosmetics> serverCosmetics = new HashSet<>();
            for (ServerCosmetics cosmetics : ServerCosmetics.values()) {
                if (set.get(cosmetics.ordinal())) {
                    serverCosmetics.add(cosmetics);
                }
            }
            storage.setServerCosmetics(serverCosmetics);
            if (serverPlayer != null) {
                NetworkHelper.sendPacket(new ServerCosmeticsUpdatePacket(storage.getServerCosmetics()), serverPlayer);
            }
        }

        @Override
        public ServerCosmeticData getServerCosmeticData() {
            return serverCosmeticData;
        }

        @Override
        public void setServerCosmeticData(ServerCosmeticData serverCosmeticData) {
            this.serverCosmeticData = serverCosmeticData;
        }
    }

    public static class Pokedex implements DataContainer.Pokedex {

        PokedexData pokedexData;

        private Pokedex(@NotNull Player player) {
            CompoundNBT dex = new CompoundNBT();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            storage.playerPokedex.writeToNBT(dex);
            pokedexData = new PokedexData(dex.toString());
        }

        @NotNull
        public static BukkitDataContainer.Pokedex adapt(@NotNull Player player) {
            return new BukkitDataContainer.Pokedex(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            try {
                storage.playerPokedex.readFromNBT(Objects.requireNonNull(JsonToNBT.parseTag(pokedexData.playerPokedex)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon pokedex", e);
            }
            storage.playerPokedex.checkForCharms();
            storage.playerPokedex.update();
        }

        @Override
        public PokedexData getPokedexData() {
            return pokedexData;
        }

        @Override
        public void setPokedexData(PokedexData pokedexData) {
            this.pokedexData = pokedexData;
        }
    }

    public static class Curry implements DataContainer.Curry {

        CurryData curryData;

        private Curry(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            curryData = new CurryData(storage.getCurryData());
        }

        @NotNull
        public static BukkitDataContainer.Curry adapt(@NotNull Player player) {
            return new BukkitDataContainer.Curry(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            int[] data = storage.getCurryData();
            int[] currys = curryData.data;
            if (data != null && currys != null && data.length >= 26 && currys.length >= 26) {
                System.arraycopy(currys, 0, data, 0, 26);
            }
        }

        @Override
        public CurryData getCurryData() {
            return curryData;
        }

        @Override
        public void setCurryData(CurryData curryData) {
            this.curryData = curryData;
        }
    }

    public static class TrainerCard implements DataContainer.TrainerCard {

        TrainerCardData trainerCardData;

        private TrainerCard(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            trainerCardData = new TrainerCardData(storage.trainerCardColor.ordinal());
        }

        @NotNull
        public static BukkitDataContainer.TrainerCard adapt(@NotNull Player player) {
            return new BukkitDataContainer.TrainerCard(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(serverPlayer.getUUID());
            storage.trainerCardColor = EnumTrainerCardColor.values()[trainerCardData.color];
            if (serverPlayer != null) {
                NetworkHelper.sendPacket(new UpdateClientPlayerDataPacket(storage.trainerCardColor), serverPlayer);
            }
        }

        @Override
        public TrainerCardData getTrainerCardData() {
            return trainerCardData;
        }

        @Override
        public void setTrainerCardData(TrainerCardData trainerCardData) {
            this.trainerCardData = trainerCardData;
        }
    }

    public static class Daycare implements DataContainer.Daycare {

        DaycareData daycareData;

        private Daycare(@NotNull Player player) {
            CompoundNBT nbt = new CompoundNBT();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            storage.getDayCare().writeToNBT(nbt);
            daycareData = new DaycareData(nbt.toString());
        }

        @NotNull
        public static BukkitDataContainer.Daycare adapt(@NotNull Player player) {
            return new BukkitDataContainer.Daycare(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            ServerPlayerEntity serverPlayer = ((BukkitUser) user).getServerPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(serverPlayer.getUUID());
            try {
                PlayerDayCare.readFromNBT(storage, JsonToNBT.parseTag(daycareData.data));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon daycare", e);
            }
            if (serverPlayer != null) {
                NetworkHelper.sendPacket(new SendEntireDayCarePacket(storage.getDayCare()), serverPlayer);
            }
        }

        @Override
        public DaycareData getDaycareData() {
            return daycareData;
        }

        @Override
        public void setDaycareData(DaycareData daycareData) {
            this.daycareData = daycareData;
        }
    }

    public static class Quest implements DataContainer.Quest {

        QuestData questData;

        private Quest(@NotNull Player player) {
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            CompoundNBT nbt = new CompoundNBT();
            storage.getQuestData().writeToNBT(nbt);
            questData = new QuestData(nbt.toString());
        }

        @NotNull
        public static BukkitDataContainer.Quest adapt(@NotNull Player player) {
            return new BukkitDataContainer.Quest(player);
        }

        @Override
        public void apply(@NotNull DataOwner user) throws IllegalStateException {
            Player player = ((BukkitUser) user).getPlayer();
            PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueId());
            try {
                storage.getQuestData().readFromNBT(Objects.requireNonNull(JsonToNBT.parseTag(questData.data)));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to apply pixelmon quest", e);
            }
        }

        @Override
        public QuestData getQuestData() {
            return questData;
        }

        @Override
        public void setQuestData(QuestData questData) {
            this.questData = questData;
        }
    }


}
