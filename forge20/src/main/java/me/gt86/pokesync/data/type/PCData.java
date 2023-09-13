package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PCData {
    public static final String PC_STORAGE = "pcStorage";

    @SerializedName(PC_STORAGE)
    public Map<BoxData, PokemonData[]> pcStorage;

    public PCData(final Map<BoxData, PokemonData[]> pcStorage) {
        this.pcStorage = pcStorage;
    }
}
