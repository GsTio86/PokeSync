package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class GiftData {

    public static final String GIFT_DATA = "giftData";

    @SerializedName(GIFT_DATA)
    public String data;

    public GiftData(String giftData) {
        this.data = giftData;
    }

}
