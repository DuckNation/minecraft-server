package io.github.haappi.ducksmp;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static io.github.haappi.ducksmp.Utils.easyPublish;

public class Discord implements Listener {

    public Discord(Plugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = DuckSMP.getSingleton().getJedisPool().getResource()) {
                jedis.auth(DuckSMP.getSingleton().getConfig().getString("redisPassword"));
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        Bukkit.getOnlinePlayers().forEach(p -> p.sendRichMessage(message));
                    }
                }, "minecraft");
            }
        });


        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try (Jedis jedis = DuckSMP.getSingleton().getJedisPool().getResource()) {
                jedis.auth(DuckSMP.getSingleton().getConfig().getString("redisPassword"));
                jedis.publish("discord", "online;" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            }
        }, 20L, 20 * 60 * 10); // 10 minutes

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String message = PaperAdventure.asPlain(event.message(), event.getPlayer().locale());
        message = event.getPlayer().getName() + ": " + message;
        easyPublish("chat;" + message);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        easyPublish("join;" + event.getPlayer().getName());
        Bukkit.getScheduler().runTaskAsynchronously(DuckSMP.getSingleton(), () -> {
            try (Jedis jedis = DuckSMP.getSingleton().getJedisPool().getResource()) {
                jedis.auth(DuckSMP.getSingleton().getConfig().getString("redisPassword"));
                String encrypted = Encryption.encrypt(event.getPlayer().getAddress().getAddress().getHostAddress());
                jedis.set("motd:" + encrypted, String.format("<newline><gray>Welcome Back</gray> <aqua><bold>%s</bold></aqua><gray>", event.getPlayer().getName()));
            }
        });

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerJoinEvent event) {
        easyPublish("leave;" + event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        easyPublish("death;" + PaperAdventure.asPlain(event.deathMessage(), event.getEntity().locale()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (event.getAdvancement().getKey().getKey().contains("recipes")) {
            return;
        }
        String message = event.getPlayer().getName() + " received the advancement " + event.getAdvancement().getKey().getKey();
        if (event.message() != null) {
            message = PaperAdventure.asPlain(event.message(), event.getPlayer().locale());
        }
        easyPublish("advancement;" + message);
    }

}
