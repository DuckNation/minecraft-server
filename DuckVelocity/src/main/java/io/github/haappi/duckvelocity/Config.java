package io.github.haappi.duckvelocity;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Config {
    public static String WSS_BASE_URL;
    public static String API_KEY;
    public static String ENCRYPTION_UUID;
    public static String API_BASE_URL;

    public static void checkConfig() {
        YAMLConfigurationLoader loader;
        ConfigurationNode node;
        Path path = Path.of("plugins/DuckVelocity/config.yml");
        try {
            Files.createDirectories(Path.of("plugins/DuckVelocity/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loader = YAMLConfigurationLoader.builder()
                .setDefaultOptions(configurationOptions -> configurationOptions.withShouldCopyDefaults(true))
                .setPath(path)
                .build();

        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Config.API_BASE_URL = node.getNode("config", "base_url").getString();
        Config.WSS_BASE_URL = node.getNode("config", "wss_base_url").getString();
        Config.ENCRYPTION_UUID = node.getNode("config", "encryption").getString(UUID.randomUUID().toString());
        Config.API_KEY = node.getNode("config", "api-key").getString();

        if (Config.API_BASE_URL == null || Config.API_KEY == null) {
            try {
                node.getNode("config", "base_url").setValue("127.0.0.1/api");
                node.getNode("config", "wss_base_url").setValue("ws://127.0.0.1/api");
                node.getNode("config", "api-key").setValue(UUID.randomUUID().toString());
                loader.save(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
