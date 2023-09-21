package me.gt86.pokesync.data;

import net.william278.husksync.HuskSync;
import net.william278.husksync.data.BukkitSerializer;
import net.william278.husksync.data.Serializer;
import org.jetbrains.annotations.NotNull;

public class PixelmonSerializer {

    protected final HuskSync api;

    public PixelmonSerializer(@NotNull HuskSync api) {
        this.api = api;
    }

    public static class PC extends BukkitSerializer.Json<PixelmonData.PC> implements Serializer<PixelmonData.PC> {
        public PC(@NotNull HuskSync api) {
            super(api, PixelmonData.PC.class);
        }
    }

    public static class Party extends BukkitSerializer.Json<PixelmonData.Party> implements Serializer<PixelmonData.Party> {
        public Party(@NotNull HuskSync api) {
            super(api, PixelmonData.Party.class);
        }
    }

    public static class Pokedex extends BukkitSerializer.Json<PixelmonData.Pokedex> implements Serializer<PixelmonData.Pokedex> {
        public Pokedex(@NotNull HuskSync api) {
            super(api, PixelmonData.Pokedex.class);
        }
    }

    public static class Stats extends BukkitSerializer.Json<PixelmonData.Stats> implements Serializer<PixelmonData.Stats> {
        public Stats(@NotNull HuskSync api) {
            super(api, PixelmonData.Stats.class);
        }
    }

    public static class Money extends BukkitSerializer.Json<PixelmonData.Money> implements Serializer<PixelmonData.Money> {
        public Money(@NotNull HuskSync api) {
            super(api, PixelmonData.Money.class);
        }
    }

    public static class Daycare extends BukkitSerializer.Json<PixelmonData.Daycare> implements Serializer<PixelmonData.Daycare> {
        public Daycare(@NotNull HuskSync api) {
            super(api, PixelmonData.Daycare.class);
        }
    }

    public static class MegaItem extends BukkitSerializer.Json<PixelmonData.MegaItem> implements Serializer<PixelmonData.MegaItem> {
        public MegaItem(@NotNull HuskSync api) {
            super(api, PixelmonData.MegaItem.class);
        }
    }

    public static class Charm extends BukkitSerializer.Json<PixelmonData.Charm> implements Serializer<PixelmonData.Charm> {
        public Charm(@NotNull HuskSync api) {
            super(api, PixelmonData.Charm.class);
        }
    }

    public static class Gift extends BukkitSerializer.Json<PixelmonData.Gift> implements Serializer<PixelmonData.Gift> {
        public Gift(@NotNull HuskSync api) {
            super(api, PixelmonData.Gift.class);
        }
    }

    public static class TrainerCard extends BukkitSerializer.Json<PixelmonData.TrainerCard> implements Serializer<PixelmonData.TrainerCard> {
        public TrainerCard(@NotNull HuskSync api) {
            super(api, PixelmonData.TrainerCard.class);
        }
    }

    public static class Cosmetic extends BukkitSerializer.Json<PixelmonData.Cosmetic> implements Serializer<PixelmonData.Cosmetic> {
        public Cosmetic(@NotNull HuskSync api) {
            super(api, PixelmonData.Cosmetic.class);
        }
    }

    public static class Lure extends BukkitSerializer.Json<PixelmonData.Lure> implements Serializer<PixelmonData.Lure> {
        public Lure(@NotNull HuskSync api) {
            super(api, PixelmonData.Lure.class);
        }
    }

    public static class Quest extends BukkitSerializer.Json<PixelmonData.Quest> implements Serializer<PixelmonData.Quest> {
        public Quest(@NotNull HuskSync api) {
            super(api, PixelmonData.Quest.class);
        }
    }

    public static class Curry extends BukkitSerializer.Json<PixelmonData.Curry> implements Serializer<PixelmonData.Curry> {
        public Curry(@NotNull HuskSync api) {
            super(api, PixelmonData.Curry.class);
        }
    }

}
