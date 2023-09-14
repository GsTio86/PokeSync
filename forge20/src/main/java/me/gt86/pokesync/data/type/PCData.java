package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PCData {
    public static final String PC_STORAGE = "pcStorage";

    @SerializedName(PC_STORAGE)
    public String data;

    public PCData(String pcData) {
        this.data = pcData;
    }

}

