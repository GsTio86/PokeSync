package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PCData {
    public static final String PC_STORAGE = "pcStorage";

    @SerializedName(PC_STORAGE)
    public List<BoxWithPokemon> pcStorage;

    public PCData(final List<BoxWithPokemon> pcStorage) {
        this.pcStorage = pcStorage;
    }


    public static class BoxWithPokemon {
        @SerializedName("boxData")
        public BoxData boxData;
        @SerializedName("pokemonData")
        public PokemonData[] pokemonData;

        public BoxWithPokemon(final BoxData boxData, final PokemonData[] pokemonData) {
            this.boxData = boxData;
            this.pokemonData = pokemonData;
        }
    }
}

