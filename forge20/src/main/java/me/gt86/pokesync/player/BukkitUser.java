package me.gt86.pokesync.player;

import de.themoep.minedown.adventure.MineDown;
import me.gt86.pokesync.BukkitPokeSync;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.data.BukkitDataOwner;
import me.gt86.pokesync.util.ForgeSpigotUtils;
import net.kyori.adventure.audience.Audience;
import net.minecraft.server.level.ServerPlayer;
import net.roxeez.advancement.display.FrameType;
import net.william278.andjam.Toast;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class BukkitUser extends OnlineUser implements BukkitDataOwner {
    private final PokeSync plugin;
    private final Player player;
    private final ServerPlayer serverPlayer;

    private BukkitUser(@NotNull Player player, @NotNull PokeSync plugin) {
        super(player.getUniqueId(), player.getName());
        this.plugin = plugin;
        this.player = player;
        this.serverPlayer = ForgeSpigotUtils.getForgePlayer(player);
    }

    private BukkitUser(@NotNull ServerPlayer player, @NotNull PokeSync plugin) {
        super(player.getUUID(), player.getName().getString());
        this.plugin = plugin;
        this.player = ForgeSpigotUtils.getSpigotPlayer(player);
        this.serverPlayer = player;
    }

    @NotNull
    public static BukkitUser adapt(@NotNull Player player, @NotNull PokeSync plugin) {
        return new BukkitUser(player, plugin);
    }

    @NotNull
    public static BukkitUser adapt(@NotNull ServerPlayer player, @NotNull PokeSync plugin) {
        return new BukkitUser(player, plugin);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isOffline() {
        return player == null || !player.isOnline();
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return ((BukkitPokeSync) plugin).getAudiences().player(player);
    }

    @Override
    public void sendToast(@NotNull MineDown title, @NotNull MineDown description,
                          @NotNull String iconMaterial, @NotNull String backgroundType) {
        try {
            final Material material = Material.matchMaterial(iconMaterial);
            Toast.builder((BukkitPokeSync) plugin)
                .setTitle(title.toComponent())
                .setDescription(description.toComponent())
                .setIcon(material != null ? material : Material.BARRIER)
                .setFrameType(FrameType.valueOf(backgroundType))
                .build()
                .show(player);
        } catch (Throwable e) {
            plugin.log(Level.WARNING, "Failed to send toast to player " + player.getName(), e);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String node) {
        return player.hasPermission(node);
    }

    @Override
    public boolean isDead() {
        return player.getHealth() <= 0;
    }

    @Override
    public boolean isLocked() {
        return plugin.getLockedPlayers().contains(player.getUniqueId());
    }

    @Override
    public boolean isNpc() {
        return player.hasMetadata("NPC");
    }

    @NotNull
    @Override
    public Player getBukkitPlayer() {
        return player;
    }

    @NotNull
    @Override
    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    @NotNull
    @Override
    public PokeSync getPlugin() {
        return plugin;
    }
}
