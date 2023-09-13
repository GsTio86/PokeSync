package me.gt86.pokesync.data.type;

import com.google.gson.annotations.SerializedName;

public class BoxData {
    @SerializedName("box")
    public int box;

    @SerializedName("name")
    public String name;

    @SerializedName("wallpaper")
    public String wallpaper;

    public BoxData(int box, String name, String wallpaper) {
        this.box = box;
        this.name = name;
        this.wallpaper = wallpaper;
    }

}
