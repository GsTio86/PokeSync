package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class StatsData {
    public static final String STATS = "stats";

    @SerializedName(STATS)
    public String data;

    public StatsData(String stats) {
        this.data = stats;
    }
}
