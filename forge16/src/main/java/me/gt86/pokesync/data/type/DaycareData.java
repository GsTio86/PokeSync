package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class DaycareData {

    public static final String DAY_CARE = "dayCare";

    @SerializedName(DAY_CARE)
    public String data;

    public DaycareData(final String daycare) {
        this.data = daycare;
    }
}
