package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class PokedexData {
    public static final String PLAYER_POKEDEX = "playerPokedex";

    @SerializedName(PLAYER_POKEDEX)
    public String playerPokedex;

    public PokedexData(String playerPokedex) {
        this.playerPokedex = playerPokedex;
    }
}
