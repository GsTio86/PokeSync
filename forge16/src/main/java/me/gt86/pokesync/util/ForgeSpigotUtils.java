package me.gt86.pokesync.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ForgeSpigotUtils {
    public static ServerPlayerEntity getForgePlayer(Player player) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player.getUniqueId());
    }

    public static Player getSpigotPlayer(PlayerEntity playerEntity) {
        if (playerEntity == null)
            return null;
        return Bukkit.getPlayer(playerEntity.getName().getString());
    }
}
