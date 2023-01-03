package io.github.haappi.ducksmp;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static io.github.haappi.ducksmp.Utils.registerNewCommand;
import static io.github.haappi.ducksmp.Utils.unRegisterBukkitCommand;

public final class DuckSMP extends JavaPlugin {

    public static MiniMessage miniMessage = MiniMessage.miniMessage();
    private static DuckSMP singleton;
    private JedisPool jedisPool;
    private ArrayList<BukkitTask> tasks = new ArrayList<>();

    public void addTask(BukkitTask task) {
        tasks.add(task);
    }

    public void cancelAllTasks() {
        tasks.forEach(BukkitTask::cancel);
    }

    public static DuckSMP getInstance() {
        return singleton;
    }

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();
        config.addDefault("redisHost", "localhost");
        config.addDefault("port", 6379);
        config.addDefault("secretKeyIP", String.valueOf(UUID.randomUUID()));
        config.addDefault("redisPassword", "idkManYouSetIt");
        config.options().copyDefaults(true);
        this.saveConfig();


        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWait(Duration.ofSeconds(2));

        jedisPool = new JedisPool(jedisPoolConfig, config.getString("redisHost"), config.getInt("port"), false);
        singleton = this;
        registerNewCommand(new Compass("compass"));
        registerNewCommand(new Biome("biome"));
        registerNewCommand(new NightVision("nv"));
        unRegisterBukkitCommand("tell");
        unRegisterBukkitCommand("ban-ip");
        unRegisterBukkitCommand("minecraft:ban-ip");
        unRegisterBukkitCommand("ban");
        registerNewCommand(new Flex("flex"));
        registerNewCommand(new CustomBan("ban"));
        registerNewCommand(new CustomBanIP("ban-ip"));
        registerNewCommand(new CustomTell("tell"));
        registerNewCommand(new Reply("reply"));
        registerNewCommand(new AutoSprint("autosprint"));
        this.getServer().getPluginManager().registerEvents(new Discord(this), this);
        this.getServer().getPluginManager().registerEvents(new MOTD(), this);
        new EndBlock(this);
        new JoinLeave();
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }


    @Override
    public void onDisable() {
        if (jedisPool != null) {
            jedisPool.close();
        }
        cancelAllTasks();
    }
}
