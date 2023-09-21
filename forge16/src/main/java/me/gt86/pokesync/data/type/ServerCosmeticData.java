package me.gt86.pokesync.olddata.type;

import com.google.gson.annotations.SerializedName;

public class ServerCosmeticData {
    private static final String SERVER_COSMETICS = "ServCosm";

    @SerializedName(SERVER_COSMETICS)
    public byte[] data;

    public ServerCosmeticData(final byte[] data) {
        this.data = data;
    }
}
