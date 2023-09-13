package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class CharmData {

    public static final String SHINY_CHARM = "ShinyCharmID";
    public static final String OVAL_CHARM = "OvalCharmID";
    public static final String EXP_CHARM = "ExpCharmID";
    public static final String CATCHING_CHARM = "CatchingCharmID";
    public static final String MARK_CHARM = "MarkCharmID";

    @SerializedName(SHINY_CHARM)
    public int shinyCharm;

    @SerializedName(OVAL_CHARM)
    public int ovalCharm;

    @SerializedName(EXP_CHARM)
    public int expCharm;

    @SerializedName(CATCHING_CHARM)
    public int catchingCharm;

    @SerializedName(MARK_CHARM)
    public int markCharm;

    public CharmData(int shinyCharm, int ovalCharm, int expCharm, int catchingCharm, int markCharm) {
        this.shinyCharm = shinyCharm;
        this.ovalCharm = ovalCharm;
        this.expCharm = expCharm;
        this.catchingCharm = catchingCharm;
        this.markCharm = markCharm;
    }
}
