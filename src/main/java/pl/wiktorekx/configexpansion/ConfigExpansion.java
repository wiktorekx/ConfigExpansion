package pl.wiktorekx.configexpansion;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigExpansion extends PlaceholderExpansion implements Configurable {
    private final Map<String, YamlConfiguration> loadedConfigurations = new HashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return "config";
    }

    @Override
    public @NotNull String getAuthor() {
        return "wiktorekx";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0-SNAPSHOT";
    }


    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("config", "config.yml");
        return defaults;
    }

    private String getValue(String storage, String path) {
        if(storage.equals("ENV")) {
            return System.getenv(path);
        } else if(storage.equals("PROPERTY")) {
            return System.getProperty(path);
        } else {
            if(!loadedConfigurations.containsKey(storage)) {
                String configPath = (String) get(storage, null);
                if(configPath != null) {
                    loadedConfigurations.put(storage, YamlConfiguration.loadConfiguration(new File(configPath)));
                }
            }
            YamlConfiguration configuration = loadedConfigurations.get(storage);
            if(configuration != null) {
                return String.valueOf(configuration.get(path));
            } else {
                return null;
            }
        }
    }

    @Override
    public @Nullable String onRequest(org.bukkit.OfflinePlayer player, @NotNull String params) {
        String[] args = params.split("_", 2);
        if(args.length > 1) {
            String value = getValue(args[0], args[1]);
            if(value != null) {
                return PlaceholderAPI.setBracketPlaceholders(player, value);
            }
        }
        return null;
    }
}
