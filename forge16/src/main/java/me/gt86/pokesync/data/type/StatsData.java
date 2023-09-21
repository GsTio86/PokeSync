package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class StatsData {
    public static final String STATS = "stats";


    public static final String STARTER_PICKED = "starterPicked";

    public static final String BATTLE_ENABLED = "battleEnabled";


    @SerializedName(STATS)
    public String data;

    @SerializedName(STARTER_PICKED)
    public boolean starterPicked;

    @SerializedName(BATTLE_ENABLED)
    public boolean battleEnabled;

    public StatsData(String data, boolean starterPicked, boolean battleEnabled) {
        this.data = data;
        this.starterPicked = starterPicked;
        this.battleEnabled = battleEnabled;
    }
}
