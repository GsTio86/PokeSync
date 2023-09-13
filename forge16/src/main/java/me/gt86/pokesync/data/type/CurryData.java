package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class CurryData {
    public static final String CURRY_DATA = "curryData";

    @SerializedName(CURRY_DATA)
    public int[] data;

    public CurryData(int[] curryData) {
        this.data = curryData;
    }
}
