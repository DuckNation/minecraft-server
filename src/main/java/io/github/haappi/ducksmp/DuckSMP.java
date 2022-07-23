package io.github.haappi.ducksmp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.CreateCollectionOptions;
import io.github.haappi.ducksmp.internals.Messager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class DuckSMP extends JavaPlugin {

    private static DuckSMP instance;
    public static ArrayList<Integer> taskIds = new ArrayList<>();
    private MongoClient mongoClient;

    @Override
    public void onEnable() {
        if (!checkMongoConfig()) {
            Bukkit.getPluginManager().disablePlugin(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(this.getName())));
            return;
        }

        this.getLogger().info(ChatColor.GREEN + "Connected to MongoDB.");
        instance = this;

        new Messager();
    }

    @Override
    public void onDisable() {
        for (int taskId : taskIds) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        this.mongoClient.getDatabase("duckMinecraft").getCollection("messages").drop();
        this.mongoClient.getDatabase("duckMinecraft").createCollection("messages", new CreateCollectionOptions().capped(true).sizeInBytes(1024 * 1024 * 10)); // 10 MB
    }

    public static DuckSMP getInstance() {
        return instance;
    }

    public static MongoClient getMongoClient() {
        return instance.mongoClient;
    }

    private boolean checkMongoConfig() {
        FileConfiguration config = this.getConfig();
        if ((config.getString("mongo-uri") == null) ||
                (Objects.requireNonNull(config.getString("mongo-uri")).equalsIgnoreCase("your-mongo-uri")) ||
                (!Objects.requireNonNull(config.getString("mongo-uri")).startsWith("mongodb"))) {

            config.addDefault("mongo-uri", "your-mongo-uri");
            config.addDefault("secretKey", "this-is-not-secure-until-you-set-it");
            config.options().copyDefaults(true);
            this.saveConfig();
            this.getLogger().severe("A proper Mongo URI is required to run this plugin.");
            return false;
        }
        try {
            mongoClient = MongoClients.create(Objects.requireNonNull(config.getString("mongo-uri")));
        } catch (Exception e) {
            this.getLogger().severe("Could not connect to MongoDB. Please check your Mongo URI.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
