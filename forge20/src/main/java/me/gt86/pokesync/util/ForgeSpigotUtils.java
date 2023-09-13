package me.gt86.pokesync.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ForgeSpigotUtils {
    public static ServerPlayer getForgePlayer(Player player) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player.getUniqueId());
    }

    public static Player getSpigotPlayer(net.minecraft.world.entity.player.Player player) {
        if (player == null)
            return null;
        return Bukkit.getPlayer(player.getName().getString());
    }
}
