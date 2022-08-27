package io.github.haappi.ducksmp.Internals;

import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.github.haappi.ducksmp.DuckSMP.*;

public class DiscordLink implements Listener {

    private final DuckSMP plugin;
    private final JedisPool pool;
    JedisPubSub jedisPubSub = new JedisPubSub() {

        @Override
        public void onMessage(String channel, String message) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendRichMessage(message));
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            plugin.getLogger().info("Subscribed to channel " + channel);
        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {
            plugin.getLogger().info("Unsubscribed from channel " + channel);
        }

    };

    public DiscordLink() {
        pool = new JedisPool(redisHost, redisPort);

        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            int size = Bukkit.getOnlinePlayers().size();
            HashMap<String, String> doc = new HashMap<>();
            doc.put("message", String.format("**%s/%s** players online. Join at **smp.quack.tk**", size, Bukkit.getMaxPlayers()));
            ArrayList<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            doc.put("onlinePlayers", arrayToString(players));
            uploadToMongo(doc, "player_count");
        }, 20 * 15L, 20L * 60 * 10); // 10 minutes

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.auth(redisPassword);
                jedis.subscribe(jedisPubSub, "minecraft");
            }
        });
    }

    private void uploadToMongo(Player player, HashMap<String, String> document, String type) {
        document.put("bound", "clientbound");
        document.put("type", type);
        document.put("playerName", player.getName());
        document.put("playerUUID", player.getUniqueId().toString());
        borkBorb(document);
    }

    private void borkBorb(HashMap<String, String> document) {
        document.put("ack", String.valueOf(0));
        document.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        if (document.getOrDefault("message", "").length() > 512) {
            document.put("message", document.getOrDefault("message", "").substring(0, 512) + "...");
        }
        document.put("message", document.getOrDefault("message", "").replaceAll("\\*", "\\\\*"));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.auth(redisPassword);
                jedis.publish("discord", mapToJson(document).toString());
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void uploadToMongo(HashMap<String, String> document, String type) {
        document.put("bound", "clientbound");
        document.put("type", type);
        borkBorb(document);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;
        HashMap<String, String> document = new HashMap<>();
        document.put("message", PaperAdventure.asPlain(event.message(), event.getPlayer().locale()));
        uploadToMongo(event.getPlayer(), document, "chat");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        HashMap<String, String> document = new HashMap<>();
        String message;
        if (event.quitMessage() != null) {
            message = "**" + PaperAdventure.asPlain(event.quitMessage(), event.getPlayer().locale()) + "**";
        } else {
            return;
        }
        document.put("message", message);
        uploadToMongo(event.getPlayer(), document, "quit");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        HashMap<String, String> document = new HashMap<>();
        String message;
        if (event.joinMessage() != null) {
            message = "**" + PaperAdventure.asPlain(event.joinMessage(), event.getPlayer().locale()) + "**";
        } else {
            return;
        }
        document.put("message", message);
        uploadToMongo(event.getPlayer(), document, "join");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        HashMap<String, String> document = new HashMap<>();
        String message = event.getPlayer().getName() + " died.";
        if (event.deathMessage() != null) {
            message = PaperAdventure.asPlain(event.deathMessage(), event.getPlayer().locale());
        }
        document.put("message", message);
        uploadToMongo(event.getPlayer(), document, "death");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerAdvancementDoneEvent event) {
        if (event.getAdvancement().getKey().getKey().contains("recipes")) {
            return;
        }
        HashMap<String, String> document = new HashMap<>();
        String message = event.getPlayer().getName() + " received the advancement " + event.getAdvancement().getKey().getKey();
        if (event.message() != null) {
            message = PaperAdventure.asPlain(event.message(), event.getPlayer().locale());
        }
        document.put("message", message);
        uploadToMongo(event.getPlayer(), document, "advancement");
    }

    private Map<String, String> jsonToMap(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }

        return map;
    }

    private JSONObject mapToJson(Map<String, String> map) throws JSONException {
        JSONObject jObject = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jObject.put(entry.getKey(), entry.getValue());
        }
        return jObject;
    }

    private String arrayToString(ArrayList<String> array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s).append(",");
        }
        return sb.toString();
    }
}
