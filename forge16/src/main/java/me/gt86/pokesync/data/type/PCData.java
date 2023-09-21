package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class PCData {
    public static final String PC_STORAGE = "pcStorage";

    @SerializedName(PC_STORAGE)
    public String data;

    public PCData(String pcData) {
        this.data = pcData;
    }

}

