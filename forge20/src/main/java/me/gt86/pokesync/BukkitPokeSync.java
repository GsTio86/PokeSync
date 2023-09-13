package me.gt86.pokesync;

import me.gt86.pokesync.adapter.DataAdapter;
import me.gt86.pokesync.adapter.GsonAdapter;
import me.gt86.pokesync.adapter.SnappyGsonAdapter;
import me.gt86.pokesync.commands.BukkitCommand;
import me.gt86.pokesync.config.Locales;
import me.gt86.pokesync.config.Settings;
import me.gt86.pokesync.data.BukkitSerializer;
import me.gt86.pokesync.data.DataContainer;
import me.gt86.pokesync.data.Serializer;
import me.gt86.pokesync.database.Database;
import me.gt86.pokesync.database.MySqlDatabase;
import me.gt86.pokesync.event.BukkitEventDispatcher;
import me.gt86.pokesync.listener.BukkitEventListener;
import me.gt86.pokesync.listener.EventListener;
import me.gt86.pokesync.player.BukkitUser;
import me.gt86.pokesync.player.ConsoleUser;
import me.gt86.pokesync.player.OnlineUser;
import me.gt86.pokesync.redis.RedisManager;
import me.gt86.pokesync.util.BukkitTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.commands.CommandRegistration;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BukkitPokeSync extends JavaPlugin implements PokeSync, BukkitTask.Supplier, BukkitEventDispatcher {
    private static final String PLATFORM_TYPE_ID = "bukkit";

    private Database database;
    private RedisManager redisManager;
    private EventListener eventListener;
    private DataAdapter dataAdapter;
    private Map<DataContainer.Type, Serializer<? extends DataContainer>> serializers;
    private Settings settings;
    private Locales locales;
    private BukkitAudiences audiences;
    private MorePaperLib paperLib;


    @Override
    public void onEnable() {
        this.audiences = BukkitAudiences.create(this);
        this.paperLib = new MorePaperLib(this);
        this.serializers = new ConcurrentHashMap<>();

        // Load settings and locales
        initialize("plugin config & locale files", (plugin) -> this.loadConfigs());

        // Prepare data adapter
        initialize("data adapter", (plugin) -> {
            if (settings.doCompressData()) {
                dataAdapter = new SnappyGsonAdapter();
            } else {
                dataAdapter = new GsonAdapter();
            }
        });

        // Prepare serializers
        initialize("data serializers", (plugin) -> {
            serializers.put(DataContainer.Type.GIFT_DATA, new BukkitSerializer.Gift(this));
            serializers.put(DataContainer.Type.STATS, new BukkitSerializer.Stats(this));
            serializers.put(DataContainer.Type.PARTY, new BukkitSerializer.Party(this));
            serializers.put(DataContainer.Type.PC, new BukkitSerializer.PC(this));
            serializers.put(DataContainer.Type.MONEY, new BukkitSerializer.Money(this));
            serializers.put(DataContainer.Type.MEGA_ITEM, new BukkitSerializer.MegaItem(this));
            serializers.put(DataContainer.Type.CHARM, new BukkitSerializer.Charm(this));
            serializers.put(DataContainer.Type.LURE, new BukkitSerializer.Lure(this));
            serializers.put(DataContainer.Type.SERVER_COSMETIC, new BukkitSerializer.ServerCosmetic(this));
            serializers.put(DataContainer.Type.POKEDEX, new BukkitSerializer.Pokedex(this));
            serializers.put(DataContainer.Type.CURRY, new BukkitSerializer.Curry(this));
            serializers.put(DataContainer.Type.TRAINER_CARD, new BukkitSerializer.TrainerCard(this));
            serializers.put(DataContainer.Type.DAYCARE, new BukkitSerializer.Daycare(this));
            serializers.put(DataContainer.Type.QUEST, new BukkitSerializer.Quest(this));
        });


        // Initialize the database
        initialize(getSettings().getDatabaseType().getDisplayName() + " database connection", (plugin) -> {
            this.database = new MySqlDatabase(this);
            this.database.initialize();
        });

        // Prepare redis connection
        initialize("Redis server connection", (plugin) -> {
            this.redisManager = new RedisManager(this);
            this.redisManager.initialize();
        });

        // Register events
        initialize("events", (plugin) -> this.eventListener = new BukkitEventListener(this));

        // Register commands
        initialize("commands", (plugin) -> BukkitCommand.Type.registerCommands(this));
    }


    @Override
    @NotNull
    public Set<OnlineUser> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> BukkitUser.adapt(player, this))
            .collect(Collectors.toSet());
    }

    @Override
    @NotNull
    public Optional<OnlineUser> getOnlineUser(@NotNull UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(BukkitUser.adapt(player, this));
    }


    @Override
    @NotNull
    public Database getDatabase() {
        return database;
    }

    @Override
    @NotNull
    public RedisManager getRedisManager() {
        return redisManager;
    }

    @NotNull
    @Override
    public DataAdapter getDataAdapter() {
        return dataAdapter;
    }

    public Map<DataContainer.Type, Serializer<? extends DataContainer>> getSerializers() {
        return serializers;
    }


    @Override
    @NotNull
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void setSettings(@NotNull Settings settings) {
        this.settings = settings;
    }


    @Override
    @NotNull
    public Locales getLocales() {
        return locales;
    }

    @Override
    public void setLocales(@NotNull Locales locales) {
        this.locales = locales;
    }

    @Override
    public boolean isDependencyLoaded(@NotNull String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }


    @Override
    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... throwable) {
        if (throwable.length > 0) {
            getLogger().log(level, message, throwable[0]);
        } else {
            getLogger().log(level, message);
        }
    }

    @NotNull
    @Override
    public ConsoleUser getConsole() {
        return new ConsoleUser(audiences.console());
    }

    @NotNull
    @Override
    public String getMinecraftVersion() {
        return getServer().getBukkitVersion();
    }

    @NotNull
    @Override
    public String getPlatformType() {
        return PLATFORM_TYPE_ID;
    }


    @NotNull
    @Override
    public Set<UUID> getLockedPlayers() {
        return this.eventListener.getLockedPlayers();
    }

    @NotNull
    public GracefulScheduling getScheduler() {
        return paperLib.scheduling();
    }

    @NotNull
    public BukkitAudiences getAudiences() {
        return audiences;
    }

    @NotNull
    public CommandRegistration getCommandRegistrar() {
        return paperLib.commandRegistration();
    }

    @Override
    @NotNull
    public PokeSync getPlugin() {
        return this;
    }
}
