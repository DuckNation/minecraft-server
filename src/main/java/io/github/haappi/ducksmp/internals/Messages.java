package io.github.haappi.ducksmp.internals;

import com.mongodb.CursorType;
import com.mongodb.client.MongoCollection;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.haappi.ducksmp.DuckSMP.taskIds;
import static io.github.haappi.ducksmp.utils.Utils.getCountdown;

public class Messages implements Listener {

    public static String commitHash = "";
    private final DuckSMP plugin;
    private final ConcurrentHashMap<String, String> files = new ConcurrentHashMap<>(); // FileName -> FilePath. Only for non-folders ending in .yml
    private boolean restartNeeded = false;

    public Messages() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        MongoCollection<Document> collection = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages");
        insertEmptyDocumentIfNeeded();
        Document finalDoc = new Document();

        try {
            Path dir = Paths.get("plugins/");
            Files.walk(dir).forEach(path -> saveFile(path.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(files);

        @NotNull BukkitTask id = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            for (Document message : collection.find(finalDoc).projection(finalDoc).cursorType(CursorType.TailableAwait)) {
                try {
                    if (message.getString("bound").equalsIgnoreCase("serverbound")) {
                        handleMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 40L);
        taskIds.add(id.getTaskId());

    }

    private void saveFile(File file) {
        if (!file.isDirectory()) {
            if (file.getName().endsWith(".yml")) {
                files.put(file.getPath(), file.getName());
            }
        } // ignore folders
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if ((Bukkit.getOnlinePlayers().size() <= 1) && (restartNeeded)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }
    }

    private void insertEmptyDocumentIfNeeded() {
        MongoCollection<Document> collection = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages");
        Document doc = new Document();
        if (collection.find(doc).first() == null) {
            doc.put("type", "ignore");
            doc.put("bound", "uranusbound");
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
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("A new version of Duck SMP is available to update. It will update when no-one is online, or after an hour.", NamedTextColor.GREEN).append(Component.text(" Hash " + Messages.commitHash, NamedTextColor.YELLOW))));
                restartNeeded = true;
                if (Bukkit.getOnlinePlayers().isEmpty())
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart"));
                break;
            case "critical_release":
                downloadPluginUpdate(message.get("file", org.bson.types.Binary.class), message.getString("sha"));

                Bukkit.getLogger().severe("Downloaded critical release of DuckSMP!");
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("An urgent version of Duck SMP has been released. Server will restart in 10 seconds.", NamedTextColor.RED).append(Component.text(" Hash " + Messages.commitHash, NamedTextColor.YELLOW))));
                restartNeeded = true;
                if (Bukkit.getOnlinePlayers().isEmpty())
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart"));
                doCountdown("Server will restart in ", this.plugin, 10);
                break;
            case "config":

                /*
                1. Download the config file
                2. Load the config file
                    - If there's multiple files with the same name in all the plugin config dirs, send a clientbound message asking to clarify which one to use. (picka fike 1-6_
                    - If there's only one file with the same name in all the plugin config dirs, load it.
                3. Send a clientbound message saying that the config file has been loaded.
                    - Clientbound error if it didn't load
                 */
                break; // handle config changes here
            default:
                Bukkit.getLogger().severe("Got type " + message.getString("type") + " but I don't know how to handle it.");
                Bukkit.getLogger().severe(message.toJson());
                break;
        }
        insertEmptyDocumentIfNeeded();
    }

    private void downloadPluginUpdate(Binary binary, String sha) {
        Messages.commitHash = sha;

        File folder = new File("plugins/");
        if (folder.isDirectory()) { // fix zip file closed issue. avoid replacing jar when server is restarting.
            for (File f : folder.listFiles()) {
                if (f.getName().startsWith("DuckSMP-") && !f.getName().contains(Messages.commitHash)) {
                    Bukkit.getLogger().info("Deleted an older version of DuckSMP.");
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
