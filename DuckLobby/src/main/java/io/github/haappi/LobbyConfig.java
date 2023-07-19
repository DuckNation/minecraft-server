package io.github.haappi;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;


public class LobbyConfig {
    private final Properties config = new Properties();

    private final String serverBrand;
    private final String serverAddress;
    private final int serverPort;
    private final int viewDistance;
    private final String velocitySecret;
    private final String worldPath;
    private final double spawnX;
    private final double spawnY;
    private final double spawnZ;
    private final float spawnYaw;
    private final float spawnPitch;

    public LobbyConfig(String fileName) {
        this.saveDefaultConfig(fileName);

        try (FileReader in = new FileReader(fileName)) {
            config.load(in);
            serverBrand = config.getProperty("server-brand", "Lobby");
            serverAddress = config.getProperty("server-address", "127.0.0.1");
            serverPort = Integer.parseInt(config.getProperty("server-port", "25565"));
            viewDistance = Integer.parseInt(config.getProperty("view-distance", "8"));
            velocitySecret = config.getProperty("velocity-secret", "secret");
            worldPath = config.getProperty("world-path", "world");

            spawnX = Double.parseDouble(config.getProperty("spawn-x", "0.0"));
            spawnY = Double.parseDouble(config.getProperty("spawn-y", "0.0"));
            spawnZ = Double.parseDouble(config.getProperty("spawn-z", "0.0"));
            spawnYaw = Float.parseFloat(config.getProperty("spawn-yaw", "0.0"));
            spawnPitch = Float.parseFloat(config.getProperty("spawn-pitch", "0.0"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void saveDefaultConfig(String filename) {
        File something = new File(LobbyConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
        File defaultConfig = new File(something, filename);
        if (!defaultConfig.exists()) {
            try {
                // make the file if not exists
                Files.createFile(new File(something, filename).toPath());
            } catch (Exception e) {

            }
        }
    }

    public String getServerBrand() {
        return serverBrand;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public String getVelocitySecret() {
        return velocitySecret;
    }

    public String getWorldPath() {
        return worldPath;
    }

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }

    public double getSpawnZ() {
        return spawnZ;
    }

    public float getSpawnYaw() {
        return spawnYaw;
    }

    public float getSpawnPitch() {
        return spawnPitch;
    }
}