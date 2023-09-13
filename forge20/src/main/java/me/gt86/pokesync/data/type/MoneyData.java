package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class MoneyData {

    public static final String MONEY = "money";

    @SerializedName(MONEY)
    public int money;

    public MoneyData(final int money) {
        this.money = money;
    }
}
