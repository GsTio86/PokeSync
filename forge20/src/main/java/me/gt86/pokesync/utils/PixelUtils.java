package me.gt86.pokesync.utils;

import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.NetworkHelper;
import com.pixelmonmod.pixelmon.comm.data.PixelmonPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class PixelUtils {

    public static PCStorage getPCStorage(UUID uuid) {
        return StorageProxy.getPCForPlayerNow(uuid);
    }

    public static PlayerPartyStorage getPartyStorage(UUID uuid) {
        PlayerPartyStorage storage = StorageProxy.getPartyNow(uuid);
        return storage;
    }

    public static ServerPlayer getPlayer(UUID uuid) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getPlayerList().getPlayer(uuid);
    }

    public static void sendPacket(UUID uuid, PixelmonPacket object) {
        ServerPlayer player = getPlayer(uuid);
        if (player != null) {
            NetworkHelper.sendPacket(player, object);
        }
    }
}
