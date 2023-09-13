package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class LureData {

    public static final String LURE = "Lure";

    @SerializedName(LURE)
    public String lure;

    public LureData(String lure) {
        this.lure = lure;
    }
}
