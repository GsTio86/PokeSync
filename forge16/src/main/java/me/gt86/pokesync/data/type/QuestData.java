package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class QuestData {

    public static final String QUEST_DATA = "questData";

    @SerializedName(QUEST_DATA)
    public String data;

    public QuestData(final String questData) {
        this.data = questData;
    }

}
