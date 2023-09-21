package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class TrainerCardData {

    public static final String TRAINER_CARD_COLOR = "trainerCardColor";

    @SerializedName(TRAINER_CARD_COLOR)
    public int color;

    public TrainerCardData(int color) {
        this.color = color;
    }
}
