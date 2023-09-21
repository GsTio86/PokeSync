package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

import java.awt.*;
import java.util.Map;

public class PartyData {

    public static final String PARTY = "party";

    public static final String TEMP_MODE = "tempMode";

    public static final String TEMP_PARTY_COLOR = "tempPartyColor";
    @SerializedName(PARTY)
    public Map<Integer, String> party;

    @SerializedName(TEMP_MODE)
    public boolean tempMode;

    @SerializedName(TEMP_PARTY_COLOR)
    public ColorData colorData;

    public PartyData(Map<Integer, String> party, boolean tempMode, Color tempPartyColor) {
        this.party = party;
        this.tempMode = tempMode;
        this.colorData = new ColorData(tempPartyColor);
    }

}
