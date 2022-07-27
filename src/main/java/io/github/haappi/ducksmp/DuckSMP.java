package io.github.haappi.ducksmp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.CreateCollectionOptions;
import io.github.haappi.ducksmp.LifeSteal.Listeners;
import io.github.haappi.ducksmp.LifeSteal.signup;
import io.github.haappi.ducksmp.internals.Messages;
import io.github.haappi.ducksmp.listeners.Villager;
import io.github.haappi.ducksmp.listeners.crystal;
import io.github.haappi.ducksmp.utils.CustomHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.haappi.ducksmp.utils.Utils.registerNewCommand;

public final class DuckSMP extends JavaPlugin implements Listener {

    public static ArrayList<Integer> taskIds = new ArrayList<>();
    private static DuckSMP instance;
    private MongoClient mongoClient;
    private boolean hasListenerLoaded = false;

    public static DuckSMP getInstance() {
        return instance;
    }

    public static MongoClient getMongoClient() {
        return instance.mongoClient;
    }

    @Override
    public void onEnable() {
        if (!checkMongoConfig()) {
            Bukkit.getPluginManager().disablePlugin(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(this.getName())));
            return;
        }

        this.getLogger().info(ChatColor.GREEN + "Connected to MongoDB.");
        instance = this;

        new Messages();
        new Villager();
//        new totem(); // enable in like 2 days
        new crystal();

        Bukkit.getPluginManager().registerEvents(this, this);

        registerNewCommand(new signup("signup"));

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CustomHolder) {
            if (event.getCurrentItem() == null) {
                return;
            }
            event.setCancelled(true);

            switch (event.getCurrentItem().getType()) {
                case GREEN_TERRACOTTA: // no
                    event.getWhoClicked().sendMessage(Component.text("Alright, you didn't join LifeSteal. Guess you live for another day", NamedTextColor.RED));
                    break;
                case RED_TERRACOTTA: // yes
                    event.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(DuckSMP.getInstance(), "claimed_hearts"), PersistentDataType.INTEGER, 0);
                    event.getWhoClicked().sendMessage(Component.text("You have joined LifeSteal! Now make sure you don't drop to zero hearts.", NamedTextColor.GREEN));
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("" + event.getWhoClicked().getName() + " has joined LifeSteal!", NamedTextColor.GREEN)));
                    break;
                default:
                    return;
            }
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> event.getWhoClicked().closeInventory());
        }

    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!hasListenerLoaded) {
            new Listeners();
            hasListenerLoaded = true;
        }
    }

    @Override
    public void onDisable() {
        for (int taskId : taskIds) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        this.mongoClient.getDatabase("duckMinecraft").getCollection("messages").drop();
        this.mongoClient.getDatabase("duckMinecraft").createCollection("messages", new CreateCollectionOptions().capped(true).sizeInBytes(1024 * 1024 * 10)); // 10 MB
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
