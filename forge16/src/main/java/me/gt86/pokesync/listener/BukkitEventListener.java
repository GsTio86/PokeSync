package me.gt86.pokesync.listener;

import me.gt86.pokesync.BukkitPokeSync;
import me.gt86.pokesync.PokeSync;
import me.gt86.pokesync.player.BukkitUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BukkitEventListener extends EventListener implements BukkitJoinEventListener, BukkitQuitEventListener, Listener {
    protected final List<String> blacklistedCommands;

    public BukkitEventListener(@NotNull BukkitPokeSync pokeSync) {
        super(pokeSync);
        this.blacklistedCommands = pokeSync.getSettings().getBlacklistedCommandsWhileLocked();
        Bukkit.getServer().getPluginManager().registerEvents(this, pokeSync);
    }

    @Override
    public boolean handleEvent(@NotNull ListenerType type, @NotNull Priority priority) {
        return plugin.getSettings().getEventPriority(type).equals(priority);
    }

    @Override
    public void handlePlayerQuit(@NotNull BukkitUser bukkitUser) {
        final Player player = bukkitUser.getPlayer();
        if (!bukkitUser.isLocked() && !player.getItemOnCursor().getType().isAir()) {
            player.getWorld().dropItem(player.getLocation(), player.getItemOnCursor());
            player.setItemOnCursor(null);
        }
        super.handlePlayerQuit(bukkitUser);
    }

    @Override
    public void handlePlayerJoin(@NotNull BukkitUser bukkitUser) {
        super.handlePlayerJoin(bukkitUser);
    }


    @EventHandler(ignoreCancelled = true)
    public void onWorldSave(@NotNull WorldSaveEvent event) {
        // Handle saving player data snapshots when the world saves
        if (!plugin.getSettings().doSaveOnWorldSave()) {
            return;
        }

        plugin.runAsync(() -> super.saveOnWorldSave(event.getWorld().getPlayers()
            .stream().map(player -> BukkitUser.adapt(player, plugin))
            .collect(Collectors.toList())));
    }


    /*
     * Events to cancel if the player has not been set yet
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileLaunch(@NotNull ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player player) {
            event.setCancelled(cancelPlayerEvent(player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDropItem(@NotNull PlayerDropItemEvent event) {
        event.setCancelled(cancelPlayerEvent(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickupItem(@NotNull EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            event.setCancelled(cancelPlayerEvent(player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        event.setCancelled(cancelPlayerEvent(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        event.setCancelled(cancelPlayerEvent(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        event.setCancelled(cancelPlayerEvent(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        event.setCancelled(cancelPlayerEvent(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            event.setCancelled(cancelPlayerEvent(player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(cancelPlayerEvent(event.getWhoClicked().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraftItem(@NotNull PrepareItemCraftEvent event) {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTakeDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            event.setCancelled(cancelPlayerEvent(player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPermissionCommand(@NotNull PlayerCommandPreprocessEvent event) {
        String[] commandArgs = event.getMessage().substring(1).split(" ");
        String commandLabel = commandArgs[0].toLowerCase(Locale.ENGLISH);

        if (blacklistedCommands.contains("*") || blacklistedCommands.contains(commandLabel)) {
            event.setCancelled(cancelPlayerEvent(event.getPlayer().getUniqueId()));
        }
    }

    @NotNull
    @Override
    public PokeSync getPlugin() {
        return plugin;
    }
}
