package io.github.haappi.ducksmp.internals;

import com.mongodb.CursorType;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static io.github.haappi.ducksmp.DuckSMP.taskIds;
import static io.github.haappi.ducksmp.utils.Utils.getCountdown;

public class Messager implements Listener {

    private final DuckSMP plugin;
    private boolean restartNeeded = false;

    public Messager() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        MongoCollection<Document> collection = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages");
        insertEmptyDocumentIfNeeded();
        Document finalDoc = new Document();;
        @NotNull BukkitTask id = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            for (Document message : collection.find(finalDoc).projection(finalDoc).cursorType(CursorType.TailableAwait)) {
                handleMessage(message);
            }
        }, 40L);
        taskIds.add(id.getTaskId());
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if ((Bukkit.getOnlinePlayers().size() == 0) && (restartNeeded)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }
    }

    private void insertEmptyDocumentIfNeeded() {
        MongoCollection<Document> collection = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages");
        Document doc = new Document();
        if (collection.find(doc).first() == null) {
            doc.put("type", "ignore");
            collection.insertOne(doc);
        }
    }

    private void handleMessage(Document message) {
        switch (message.getString("type")) {
            case "console":
                Bukkit.getLogger().info(message.getString("message"));
                break;
            case "ignore":
                return;
            case "release":
                downloadPluginUpdate(message.get("file", org.bson.types.Binary.class), message.getString("sha"));

                Bukkit.getLogger().info("Downloaded new release of DuckSMP!");
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("A new version of Duck SMP is available to update. It will update when no-one is online, or after an hour.", NamedTextColor.GREEN)));
                restartNeeded = true;
                if (Bukkit.getOnlinePlayers().isEmpty()) Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart"));
                break;
            case "critical_release":
                downloadPluginUpdate(message.get("file", org.bson.types.Binary.class), message.getString("sha"));

                Bukkit.getLogger().severe("Downloaded critical release of DuckSMP!");
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("An urgent version of Duck SMP has been released. Server will restart in 10 seconds.", NamedTextColor.RED)));
                restartNeeded = true;
                if (Bukkit.getOnlinePlayers().isEmpty()) Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart"));
                doCountdown("Server will restart in ", this.plugin, 10);
                break;
            default:
                Bukkit.getLogger().severe("Got type " + message.getString("type") + " but I don't know how to handle it.");
                Bukkit.getLogger().severe(message.toJson());
                break;
        }
        insertEmptyDocumentIfNeeded();
    }

    private void downloadPluginUpdate(Binary binary, String sha) {
        File folder = new File("plugins/");
        if (folder.isDirectory()) {
            for (File f : folder.listFiles()) {
                if (f.getName().startsWith("DuckSMP-")) {
                    Bukkit.getLogger().info("Deleted commit hash: " + f.getName().split("-")[1].split("-")[0]);
                    f.delete();
                }
            }
        }
        try {
            byte[] bytes = binary.getData();
            java.io.File file = new java.io.File("plugins/DuckSMP-" + sha + ".jar");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doCountdown(String message, DuckSMP plugin, Integer timerLength) {
        AtomicInteger countdown = new AtomicInteger(timerLength);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown.get() == 0) {
                    Bukkit.getServer().savePlayers();
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    cancel();
                }
                Component actionBar = Component.text(message, NamedTextColor.AQUA).append(getCountdown(countdown.get()));
                Component subTitle = Component.text("");
                for (Player p : Bukkit.getOnlinePlayers()) {
//                    p.sendActionBar(actionBar);
                    p.showTitle(Title.title(actionBar, subTitle));
                    p.sendMessage(actionBar);
                }
                countdown.getAndDecrement();
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
