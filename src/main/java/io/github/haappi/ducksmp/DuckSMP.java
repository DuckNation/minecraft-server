package io.github.haappi.ducksmp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.CreateCollectionOptions;
import io.github.haappi.ducksmp.Commands.*;
import io.github.haappi.ducksmp.Cosmetics.NameTag.Common;
import io.github.haappi.ducksmp.Cosmetics.NameTag.NameTagCommand;
import io.github.haappi.ducksmp.Cosmetics.NameTag.SetPrefix;
import io.github.haappi.ducksmp.Internals.Messages;
import io.github.haappi.ducksmp.LifeSteal.ArmorStandPlayer;
import io.github.haappi.ducksmp.LifeSteal.Signup;
import io.github.haappi.ducksmp.Listeners.*;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.haappi.ducksmp.Utils.Utils.registerNewCommand;
import static io.github.haappi.ducksmp.Utils.Utils.unRegisterBukkitCommand;

public final class DuckSMP extends JavaPlugin implements Listener {

    public static ArrayList<Integer> taskIds = new ArrayList<>();
    public static String secretKey;
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
        if (!checkMongoConfig()) {
            Bukkit.getPluginManager().disablePlugin(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(this.getName())));
            return;
        }

        this.getLogger().info(ChatColor.GREEN + "Connected to MongoDB.");
        instance = this;

        new Messages();
        new Villager();
        new TotemHandler();
        new Crystal();
        new StatHandler();
        new StringRecipe();
        new Netherite();

        new ArmorStandPlayer();
        new CustomLore();
        new AntiSpam();

        new Common();
        new GlobalDeathHandler();

        new Extra();
        new JoinLeave();

        new Combat();
//        new AntiEnd();
        new BetterTeleport();
        new FireballHandler();

        registerNewCommand(new Signup("signup"));
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

    private boolean checkMongoConfig() {
        FileConfiguration config = this.getConfig();
        if ((config.getString("mongo-uri") == null) ||
                (Objects.requireNonNull(config.getString("mongo-uri")).equalsIgnoreCase("your-mongo-uri")) ||
                (!Objects.requireNonNull(config.getString("mongo-uri")).startsWith("mongodb"))) {

            config.addDefault("mongo-uri", "your-mongo-uri");
            config.addDefault("secretKey", "this-is-not-secure-until-you-set-it");
            config.addDefault("secretKeyIP", "this-is-not-secure-until-you-set-it");
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
        secretKey = config.getString("secretKey");
        return true;
    }


}
