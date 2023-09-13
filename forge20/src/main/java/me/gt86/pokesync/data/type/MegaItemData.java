package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class MegaItemData {

    public static final String MEGA_ITEM_UNLOCKED = "MegaItemsUnlocked";
    public static final String MEGA_ITEM_STRING = "MegaItemString";
    @SerializedName(MEGA_ITEM_UNLOCKED)
    public int megaItemsUnlocked;

    @SerializedName(MEGA_ITEM_STRING)
    public int megaItemString;

    public MegaItemData(int megaItemsUnlocked, int megaItemString) {
        this.megaItemsUnlocked = megaItemsUnlocked;
        this.megaItemString = megaItemString;
    }
}
