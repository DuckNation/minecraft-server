package io.github.haappi.duckpaper.utils;

import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class Config {
    public static String API_KEY;
    public static String ENCRYPTION_UUID;
    public static String API_BASE_URL;

    public static void loadConfig(DuckPaper instance) {
        FileConfiguration config = instance.getConfig();

        config.addDefault("api-key", UUID.randomUUID().toString());
        config.addDefault("encryption-key", UUID.randomUUID().toString());
        config.addDefault("api-base-url", "127.0.0.1/api");

        API_KEY = config.getString("api-key");
        ENCRYPTION_UUID = config.getString("encryption-key");
        API_BASE_URL = config.getString("api-base-url");

        config.options().copyDefaults(true);
        instance.saveConfig();
    }
}
