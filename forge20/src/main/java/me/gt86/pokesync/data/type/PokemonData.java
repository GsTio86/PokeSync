package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class PokemonData {

    @SerializedName("pokemon")
    public String data;

    public PokemonData(String data) {
        this.data = data;
    }
}
