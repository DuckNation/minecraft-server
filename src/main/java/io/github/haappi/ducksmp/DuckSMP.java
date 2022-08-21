package io.github.haappi.ducksmp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.CreateCollectionOptions;
import io.github.haappi.ducksmp.Biomes.NetherNerf;
import io.github.haappi.ducksmp.Commands.*;
import io.github.haappi.ducksmp.Cosmetics.NameTag.Common;
import io.github.haappi.ducksmp.Cosmetics.NameTag.NameTagCommand;
import io.github.haappi.ducksmp.Cosmetics.NameTag.SetPrefix;
import io.github.haappi.ducksmp.Internals.DiscordLink;
import io.github.haappi.ducksmp.Internals.Messages;
import io.github.haappi.ducksmp.Listeners.*;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.haappi.ducksmp.Utils.Utils.registerNewCommand;
import static io.github.haappi.ducksmp.Utils.Utils.unRegisterBukkitCommand;

public final class DuckSMP extends JavaPlugin implements Listener {

    public static ArrayList<Integer> taskIds = new ArrayList<>();
    public static Material secretMaterial = Material.ENDER_EYE;
    public static String secretKey;
    public static String redisHost;
    public static int redisPort;
    public static String redisPassword;
    public static boolean isDisabled = false;
    public static boolean showRestartBar = false;
    public static BossBar restartBar;
    private static DuckSMP instance;
    private MongoClient mongoClient;

    public static DuckSMP getInstance() {
        return instance;
    }

    public static MongoClient getMongoClient() {
        return instance.mongoClient;
    }

    @Override
    public void onEnable() {
        if (!checkConfig()) {
            Bukkit.getPluginManager().disablePlugin(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(this.getName())));
            return;
        }

        this.getLogger().info(ChatColor.GREEN + "Connected to MongoDB.");
        instance = this;

        new Messages();
        new Villager();
        new NetherNerf();
        new TotemHandler();
        new Crystal();
        new StatHandler();
        new StringRecipe();
        new Netherite();

        new CustomLore();
        new AntiSpam();
        new ChatFilter();

        new Common();
        new GlobalDeathHandler();

        new Extra();
        new JoinLeave();

        new Combat();
        new AntiDimension();
        new Elytra();
        new BetterTeleport();
        new FireballHandler();
        new DiscordLink();
        new EndStuff();

//        registerNewCommand(new Signup("signup"));
        registerNewCommand(new StopRestart("stoprestart"));
        registerNewCommand(new NightVision("nv"));
        registerNewCommand(new Vanish("v"));
        unRegisterBukkitCommand("tell");
        registerNewCommand(new CustomTell("tell"));
        registerNewCommand(new Reply("reply"));
        registerNewCommand(new NameTagCommand("color"));
        registerNewCommand(new ChangeMob("changeMob"));
        registerNewCommand(new SetPrefix("setprefix"));
        registerNewCommand(new Compass("compass"));
        registerNewCommand(new Home("home"));
        registerNewCommand(new Homes("homes"));
        registerNewCommand(new Flex("flex"));
        registerNewCommand(new Link("link"));
        registerNewCommand(new Unlink("unlink"));
        registerNewCommand(new ClearCombat("clearcombat"));
        registerNewCommand(new TPA("tpa"));
        unRegisterBukkitCommand("restart");
        registerNewCommand(new Restart("restart"));
        unRegisterBukkitCommand("ban");
        registerNewCommand(new Ban("ban"));
        registerNewCommand(new Unmute("unmute"));
        registerNewCommand(new Poll("poll"));
    }


    @Override
    public void onDisable() {
        isDisabled = true;
        for (int taskId : taskIds) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        this.mongoClient.getDatabase("duckMinecraft").getCollection("messages").drop();
        this.mongoClient.getDatabase("duckMinecraft").createCollection("messages", new CreateCollectionOptions().capped(true).sizeInBytes(1024 * 1024 * 10)); // 10 MB
    }

    private boolean checkConfig() {
        FileConfiguration config = this.getConfig();
        if ((config.getString("mongo-uri") == null) ||
                (Objects.requireNonNull(config.getString("mongo-uri")).equalsIgnoreCase("your-mongo-uri")) ||
                (!Objects.requireNonNull(config.getString("mongo-uri")).startsWith("mongodb"))) {

            config.addDefault("mongo-uri", "your-mongo-uri");
            config.addDefault("secretKey", "this-is-not-secure-until-you-set-it");
            config.addDefault("secretKeyIP", "this-is-not-secure-until-you-set-it");
            config.addDefault("redis-host", "localhost");
            config.addDefault("redis-port", 6379);
            config.addDefault("redis-auth", "your-redis-auth");
            config.addDefault("secret-item", "ENDER_EYE");
            config.options().copyDefaults(true);
            this.saveConfig();
            this.getLogger().severe("A proper Mongo URI is required to run this plugin.");
            return false;
        }
        mongoClient = MongoClients.create(Objects.requireNonNull(config.getString("mongo-uri")));
        secretKey = config.getString("secretKey");
        redisHost = config.getString("redis-host");
        redisPort = config.getInt("redis-port");
        redisPassword = config.getString("redis-auth");
        secretMaterial = Material.valueOf(config.getString("secret-item"));
        return true;
    }


}
