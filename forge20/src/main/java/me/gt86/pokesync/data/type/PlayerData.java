package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class PlayerData {

    public static final String PLAYER_DATA = "playerData";

    @SerializedName(PLAYER_DATA)
    public String data;

    public PlayerData(String playerData) {
        this.data = playerData;
    }

}
