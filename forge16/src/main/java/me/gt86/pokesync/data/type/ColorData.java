package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

import java.awt.*;

public class ColorData {
    @SerializedName("r")
    public int red;
    @SerializedName("g")
    public int green;

    @SerializedName("b")
    public int blue;
    @SerializedName("a")
    public int alpha;

    public ColorData(Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getAlpha();
    }

    public Color toColor() {
        return new Color(red, green, blue, alpha);
    }
}
