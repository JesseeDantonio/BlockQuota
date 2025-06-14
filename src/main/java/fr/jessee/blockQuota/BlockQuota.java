package fr.jessee.blockQuota;

import fr.jessee.blockQuota.command.BlockQuotaCommand;
import fr.jessee.blockQuota.feature.ConfigFile;
import fr.jessee.blockQuota.feature.SQLiteFile;
import fr.jessee.blockQuota.feature.SQLiteStorage;
import fr.jessee.blockQuota.runnable.ResetTask;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getLogger;

public final class BlockQuota extends JavaPlugin {

    private ConfigFile mainConfig;
    private ConfigFile langConfig;
    private Storage storage;
    private SQLiteStorage sqLiteStorage;
    private static BlockQuota instance;
    private final Map<UUID, Map<Material, Integer>> stats = new HashMap<>();
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


        registerListeners(this);
        registerCommands();

        try {
            File dbFile = new File(getDataFolder(), "storage.db");
            storage = new SQLiteFile(dbFile);
            storage.connect();
            sqLiteStorage = new SQLiteStorage(storage);
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
        try {
            if (this.getStorage().getConnection() != null && !this.getStorage().getConnection().isClosed()) {
                this.getStorage().getConnection().close();
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

    public Map<UUID, Map<Material, Integer>> getStats() {
        return stats;
    }

    public ConfigFile getMainConfig() {
        return mainConfig;
    }

    public ConfigFile getLangConfig() {
        return langConfig;
    }

    public Storage getStorage() {
        return storage;
    }

    public SQLiteStorage getSqLiteStorage() {
        return sqLiteStorage;
    }

    public ResetTask getResetTask() {
        return resetTask;
    }
}
