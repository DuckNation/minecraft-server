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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static io.github.haappi.ducksmp.DuckSMP.getMongoClient;
import static io.github.haappi.ducksmp.DuckSMP.isDisabled;
import static io.github.haappi.ducksmp.utils.Utils.getCountdown;
import static io.github.haappi.ducksmp.utils.Utils.miniMessage;

public class Messages implements Listener {

    public static String commitHash = "";
    public static boolean restartNeeded = false;
    public static BukkitTask restartID;
    private final DuckSMP plugin;
    private final ConcurrentHashMap<String, String> files = new ConcurrentHashMap<>(); // FileName -> FilePath. Only for non-folders ending in .yml

    public Messages() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        insertEmptyDocumentIfNeeded();

        try {
            Path dir = Paths.get(".");

            try (Stream<Path> pathStream = Files.walk(dir)) {
                pathStream.filter(Files::isRegularFile).forEach(file -> {
                    if (file.toString().endsWith(".yml") || file.toString().endsWith(".yaml") || file.toString().endsWith(".json") || file.toString().endsWith(".properties")) {
                        String fileName = file.getFileName().toString();
                        String filePath = file.toString();
                        files.put(filePath, fileName);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(files);

        runAsyncTask();
    }

    private void runAsyncTask() {
        Document finalDoc = new Document();
        MongoCollection<Document> collection = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages");
        if (!plugin.isEnabled()) {
            return;
        }
        if (isDisabled) {
            return;
        }
        this.insertEmptyDocumentIfNeeded();
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try {
                for (Document message : collection.find(finalDoc).projection(finalDoc).cursorType(CursorType.TailableAwait)) {
                    try {
                        if (message.getString("bound").equalsIgnoreCase("serverbound")) {
                            handleMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("Error while reading messages from MongoDB");
                Bukkit.getScheduler().runTaskLater(plugin, this::runAsyncTask, 20 * 3); // Try again in 3 seconds
            }
        }, 60L);

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
            case "discord_update":
                String playerUUID = message.getString("uuid");
                String message2 = message.getString("message");
                if (playerUUID != null) {
                    Player p = Bukkit.getPlayer(playerUUID);
                    if (p != null) {
                        p.sendMessage(miniMessage.deserialize(message2));
                    }
                }
                break;
            case "release":
                downloadPluginUpdate(message.get("file", org.bson.types.Binary.class), message.getString("sha"));

                Bukkit.getLogger().info("Downloaded new release of DuckSMP! Hash: " + message.getString("sha"));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Component.text("A new version of Duck SMP is available to update. It will update when no-one is online, or after an hour.", NamedTextColor.GREEN).append(Component.text(" Hash " + Messages.commitHash, NamedTextColor.YELLOW)));
                    player.sendMessage(Component.text("Update message: ", NamedTextColor.YELLOW).append(miniMessage.deserialize(message.getString("data"))));
                });
                restartNeeded = true;
                if (Bukkit.getOnlinePlayers().isEmpty())
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart"));
                restartID = Bukkit.getScheduler().runTaskLater(plugin, () -> doCountdown("Server will restart in ", this.plugin, 10), 20 * 60 * 60L); // Restart after 1 hour
                break;
            case "critical_release":
                downloadPluginUpdate(message.get("file", org.bson.types.Binary.class), message.getString("sha"));

                Bukkit.getLogger().severe("Downloaded critical release of DuckSMP! Hash: " + message.getString("sha"));
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("An urgent version of Duck SMP has been released. Server will restart in 10 seconds.", NamedTextColor.RED).append(Component.text(" Hash " + Messages.commitHash, NamedTextColor.YELLOW))));
                restartNeeded = true;
                if (Bukkit.getOnlinePlayers().isEmpty())
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart"));
                doCountdown("Server will restart in ", this.plugin, 10);
                break;
            case "config":
                Set<String> values = getKeysByValue(files, message.getString("fileName"));
                List<String> stringsList = new ArrayList<>(values);
                if (message.getInteger("index") == 0) {
                    if (values.isEmpty()) {
                        Document doc = new Document();
                        doc.put("type", "config");
                        doc.put("bound", "clientbound");
                        doc.put("fileName", message.getString("fileName"));
                        doc.put("file", message.get("file", org.bson.types.Binary.class));
                        doc.put("returned", false);
                        doc.put("message", "A file was not found by the name of " + message.getString("fileName") + ".");
                        getMongoClient().getDatabase("duckMinecraft").getCollection("messages").insertOne(doc);
                        return;
                    }
                    if (values.size() > 1 && !(message.getInteger("index") > stringsList.size() - 1)) {
                        Document doc = new Document();
                        doc.put("type", "config");
                        doc.put("bound", "clientbound");
                        doc.put("fileName", message.getString("fileName"));
                        doc.put("file", message.get("file", org.bson.types.Binary.class));
                        doc.put("returned", values);
                        doc.put("message", "Multiple files were found by the name of " + message.getString("fileName") + ". Please pick one from the list.");
                        getMongoClient().getDatabase("duckMinecraft").getCollection("messages").insertOne(doc);
                        return;
                    }
                }


                String filePath;
                if (values.size() == 1) {
                    filePath = values.iterator().next();
                } else {
                    filePath = stringsList.get(message.getInteger("index") - 1);
                }
                boolean yes = editFileData(message.get("file", org.bson.types.Binary.class), filePath);
                if (!yes) {
                    Document doc = new Document();
                    doc.put("type", "config");
                    doc.put("bound", "clientbound");
                    doc.put("fileName", message.getString("fileName"));
                    doc.put("file", message.get("file", org.bson.types.Binary.class));
                    doc.put("returned", false);
                    doc.put("message", "Failed to edit file " + filePath + ".");
                    getMongoClient().getDatabase("duckMinecraft").getCollection("messages").insertOne(doc);
                    return;
                }
                Document doc = new Document();
                doc.put("type", "config");
                doc.put("bound", "clientbound");
                doc.put("fileName", message.getString("fileName"));
                doc.put("file", message.get("file", org.bson.types.Binary.class));
                doc.put("returned", false);
                doc.put("message", "Inserted to " + filePath + ".");
                getMongoClient().getDatabase("duckMinecraft").getCollection("messages").insertOne(doc);
                // edit the matched file based on the filePath & name

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

    private <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>(); // empty set
        for (Map.Entry<T, E> entry : map.entrySet()) { // iterate through the mappings of key | value
            if (Objects.equals(value, entry.getValue())) { // if the value is the same as the one we're looking for
                keys.add(entry.getKey()); // add the key to the set
            }
        }
        return keys; // return the set
    }

    private boolean editFileData(Binary binary, String path) {
        try {
            byte[] bytes = binary.getData();
            java.io.File file = new java.io.File(path);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void downloadPluginUpdate(Binary binary, String sha) {
        Messages.commitHash = sha;

        File folder = new File("plugins/");
        if (folder.isDirectory()) { // fix zip file closed issue. avoid replacing jar when server is restarting.
            for (File f : Objects.requireNonNull(folder.listFiles())) {
                if (f.getName().startsWith("DuckSMP-") && !f.getName().contains(Messages.commitHash)) {
                    Bukkit.getLogger().info("Deleted an older version of DuckSMP.");
                    boolean deleted = f.delete();
                    if (!deleted) {
                        Bukkit.getLogger().severe("Could not delete " + f.getName());
                    }
                }
            }
        }

        editFileData(binary, "plugins/DuckSMP-" + Messages.commitHash + ".jar");
    }

    @SuppressWarnings("SameParameterValue")
    private void doCountdown(String message, DuckSMP plugin, Integer timerLength) {
        AtomicInteger countdown = new AtomicInteger(timerLength);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown.get() == 0) {
                    Bukkit.getServer().savePlayers();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.kick(Component.text("you got boobed off"));
                    });
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
