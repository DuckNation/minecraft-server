package io.github.haappi.ducksmp.Internals;

import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class DiscordLink implements Listener {

    private final DuckSMP plugin;

    public DiscordLink() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            int size = Bukkit.getOnlinePlayers().size();
            Document doc = new Document("message", String.format("**%s/%s** players online. Join at **smp.quack.tk**", size, Bukkit.getMaxPlayers()));
            ArrayList<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            doc.put("onlinePlayers", players);
            uploadToMongo(doc, "player_count");
        }, 20 * 15L, 20L * 60 * 10); // 10 minutes
    }

    private void uploadToMongo(Player player, Document document, String type) {
        document.put("bound", "clientbound");
        document.put("type", type);
        document.put("playerName", player.getName());
        document.put("playerUUID", player.getUniqueId().toString());
        borkBorb(document);
    }

    private void borkBorb(Document document) {
        document.put("ack", 0);
        document.put("timestamp", System.currentTimeMillis() / 1000);

        if (document.getOrDefault("message", "").toString().length() > 512) {
            document.put("message", document.getOrDefault("message", "").toString().substring(0, 512) + "...");
        }
        document.put("message", document.getOrDefault("message", "").toString().replaceAll("\\*", "\\\\*"));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages").insertOne(document));
    }

    @SuppressWarnings("SameParameterValue")
    private void uploadToMongo(Document document, String type) {
        document.put("bound", "clientbound");
        document.put("type", type);
        borkBorb(document);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;
        Document document = new Document();
        document.put("message", PaperAdventure.asPlain(event.message(), event.getPlayer().locale()));
        uploadToMongo(event.getPlayer(), document, "chat");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Document document = new Document();
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
        Document document = new Document();
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
        Document document = new Document();
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
        Document document = new Document();
        String message = event.getPlayer().getName() + " received the advancement " + event.getAdvancement().getKey().getKey();
        if (event.message() != null) {
            message = PaperAdventure.asPlain(event.message(), event.getPlayer().locale());
        }
        document.put("message", message);
        uploadToMongo(event.getPlayer(), document, "advancement");
    }
}
