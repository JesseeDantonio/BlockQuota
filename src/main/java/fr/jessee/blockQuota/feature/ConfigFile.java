package fr.jessee.blockQuota.feature;

import fr.jessee.blockQuota.BlockQuota;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigFile {
    private final File file;
    private YamlConfiguration configuration;

    public ConfigFile(String name) {
        BlockQuota plugin = BlockQuota.getInstance();
        file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public boolean containsInSection(String section, String key) {
        org.bukkit.configuration.ConfigurationSection sec = configuration.getConfigurationSection(section);
        return sec != null && sec.contains(key);
    }

    public double getDouble(String path) {
        return configuration.getDouble(path, 0);
    }

    public int getInt(String path) {
        return configuration.getInt(path, 0);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path, false);
    }

    public String getString(String path) {
        String value = configuration.getString(path);
        return value != null ? ChatColor.translateAlternateColorCodes('&', value) : null;
    }

    public List<String> getStringList(String path) {
        if (configuration.contains(path)) {
            return configuration.getStringList(path)
                    .stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .toList();
        }
        return List.of();
    }

    public List<String> getReversedStringList(String path) {
        List<String> list = getStringList(path);
        List<String> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }
}
