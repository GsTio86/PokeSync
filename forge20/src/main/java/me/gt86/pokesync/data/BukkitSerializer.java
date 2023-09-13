package me.gt86.pokesync.data;


import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.adapter.Adaptable;
import org.jetbrains.annotations.NotNull;


public class BukkitSerializer {
    protected final PokeSync plugin;

    public BukkitSerializer(@NotNull PokeSync plugin) {
        this.plugin = plugin;
    }

    public static class Gift extends Json<BukkitDataContainer.Gift> implements Serializer<BukkitDataContainer.Gift> {
        public Gift(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Gift.class);
        }
    }

    public static class Stats extends Json<BukkitDataContainer.Stats> implements Serializer<BukkitDataContainer.Stats> {
        public Stats(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Stats.class);
        }
    }

    public static class Party extends Json<BukkitDataContainer.Party> implements Serializer<BukkitDataContainer.Party> {
        public Party(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Party.class);
        }
    }

    public static class PC extends Json<BukkitDataContainer.PC> implements Serializer<BukkitDataContainer.PC> {
        public PC(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.PC.class);
        }
    }

    public static class Money extends Json<BukkitDataContainer.Money> implements Serializer<BukkitDataContainer.Money> {
        public Money(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Money.class);
        }
    }

    public static class MegaItem extends Json<BukkitDataContainer.MegaItem> implements Serializer<BukkitDataContainer.MegaItem> {
        public MegaItem(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.MegaItem.class);
        }
    }

    public static class Charm extends Json<BukkitDataContainer.Charm> implements Serializer<BukkitDataContainer.Charm> {
        public Charm(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Charm.class);
        }
    }

    public static class Lure extends Json<BukkitDataContainer.Lure> implements Serializer<BukkitDataContainer.Lure> {
        public Lure(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Lure.class);
        }
    }

    public static class ServerCosmetic extends Json<BukkitDataContainer.ServerCosmetic> implements Serializer<BukkitDataContainer.ServerCosmetic> {
        public ServerCosmetic(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.ServerCosmetic.class);
        }
    }

    public static class Pokedex extends Json<BukkitDataContainer.Pokedex> implements Serializer<BukkitDataContainer.Pokedex> {
        public Pokedex(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Pokedex.class);
        }
    }

    public static class Curry extends Json<BukkitDataContainer.Curry> implements Serializer<BukkitDataContainer.Curry> {
        public Curry(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Curry.class);
        }
    }

    public static class TrainerCard extends Json<BukkitDataContainer.TrainerCard> implements Serializer<BukkitDataContainer.TrainerCard> {
        public TrainerCard(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.TrainerCard.class);
        }
    }

    public static class Daycare extends Json<BukkitDataContainer.Daycare> implements Serializer<BukkitDataContainer.Daycare> {
        public Daycare(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Daycare.class);
        }
    }

    public static class Quest extends Json<BukkitDataContainer.Quest> implements Serializer<BukkitDataContainer.Quest> {
        public Quest(@NotNull PokeSync plugin) {
            super(plugin, BukkitDataContainer.Quest.class);
        }
    }

    public static abstract class Json<T extends DataContainer & Adaptable> extends BukkitSerializer implements Serializer<T> {

        private final Class<T> type;

        protected Json(@NotNull PokeSync plugin, Class<T> type) {
            super(plugin);
            this.type = type;
        }

        @Override
        public T deserialize(@NotNull String serialized) throws DeserializationException {
            return plugin.getDataAdapter().fromJson(serialized, type);
        }

        @NotNull
        @Override
        public String serialize(@NotNull T element) throws SerializationException {
            return plugin.getDataAdapter().toJson(element);
        }

    }
}
