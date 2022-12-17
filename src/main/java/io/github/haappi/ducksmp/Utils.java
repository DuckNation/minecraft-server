package io.github.haappi.ducksmp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;

public class Utils {
    public static void easyPublish(Plugin plugin, Jedis instance, String channel, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            instance.publish(channel, message);
        });
    }

    public static void easyPublish(Jedis instance, String message) {
        easyPublish(DuckSMP.getSingleton(), instance, "discord", message);
    }

    public static void easyPublish(String message) {
        Bukkit.getScheduler().runTaskAsynchronously(DuckSMP.getSingleton(), () -> {
            try (Jedis jedis = DuckSMP.getSingleton().getJedisPool().getResource()) {
                jedis.auth(DuckSMP.getSingleton().getConfig().getString("redisPassword"));
                jedis.publish("discord", message);
            }
        });
    }
}
