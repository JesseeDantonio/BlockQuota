package fr.jessee.blockQuota;

import fr.jessee.blockQuota.command.BlockQuotaCommand;
import fr.jessee.blockQuota.feature.ConfigFile;
import fr.jessee.blockQuota.feature.SQLiteFile;
import fr.jessee.blockQuota.feature.SQLiteStorage;
import fr.jessee.blockQuota.runnable.ResetTask;
import fr.jessee.blockQuota.util.iface.QuotaTable;
import fr.jessee.blockQuota.util.iface.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getConsoleSender;

public final class BlockQuota extends JavaPlugin {

    private ConfigFile mainConfig;
    private ConfigFile langConfig;
    private SQLiteStorage storageBreak;
    private SQLiteStorage storagePlace;
    private static BlockQuota instance;
    private final Map<UUID, Map<Material, Integer>> quotasBreakCache = new ConcurrentHashMap<>();
    private final Map<UUID, Map<Material, Integer>> quotasPlaceCache = new ConcurrentHashMap<>();
    private ResetTask resetTask;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.saveDefaultConfig();

        mainConfig = new ConfigFile("config");
        langConfig = new ConfigFile("lang");


        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!Bukkit.getOnlineMode()) {
            Bukkit.getPluginManager().disablePlugin(this);
        }


        registerListeners(this);
        registerCommands();

        try {
            File dbFile = new File(getDataFolder(), "storage.db");
            Storage storage = new SQLiteFile(dbFile);
            storage.connect();

            storageBreak = new SQLiteStorage(storage, QuotaTable.BREAK);
            storagePlace = new SQLiteStorage(storage, QuotaTable.PLACE);
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        resetTask = new ResetTask();
        resetTask.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        resetTask.cancel();
        try {
            if (this.getStorageBreak() != null && this.storageBreak.getStorage() != null) {
                if (!this.storageBreak.getStorage().getConnection().isClosed()) {
                    this.storageBreak.getStorage().getConnection().close();
                }
            }
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private void registerCommands() {
        getCommand("bq").setExecutor(new BlockQuotaCommand());
    }

    private void registerListeners(final BlockQuota classicFaction) {
        // Scanner le package pour trouver toutes les classes implémentant Listener
        Reflections reflections = new Reflections(
                "fr.jessee.blockQuota.listener",
                Scanners.SubTypes
        );
        Set<Class<? extends Listener>> classes = reflections.getSubTypesOf(Listener.class);

        for (Class<? extends Listener> listenerClass : classes) {
            try {
                // ⚠️ Ignore les classes abstraites ou interfaces
                if (Modifier.isAbstract(listenerClass.getModifiers()) || Modifier.isInterface(listenerClass.getModifiers())) {
                    continue;
                }
                Listener listener;
                Constructor<? extends Listener> defaultConstructor;

                try {
                    defaultConstructor = listenerClass.getDeclaredConstructor(BlockQuota.class);
                    listener = defaultConstructor.newInstance(classicFaction);
                } catch (NoSuchMethodException e) {
                    defaultConstructor = listenerClass.getDeclaredConstructor();
                    listener = defaultConstructor.newInstance();
                }

                Bukkit.getPluginManager().registerEvents(listener, this);

            } catch (Exception e) {
                getConsoleSender().sendMessage(e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }

    public static BlockQuota getInstance() {
        return instance;
    }

    public Map<UUID, Map<Material, Integer>> getQuotasBreakCache() {
        return quotasBreakCache;
    }

    public Map<UUID, Map<Material, Integer>> getQuotasPlaceCache() {
        return quotasPlaceCache;
    }

    public ConfigFile getMainConfig() {
        return mainConfig;
    }

    public ConfigFile getLangConfig() {
        return langConfig;
    }

    public SQLiteStorage getStorageBreak() {
        return storageBreak;
    }

    public SQLiteStorage getStoragePlace() {
        return storagePlace;
    }

    public ResetTask getResetTask() {
        return resetTask;
    }
}
