package io.github.haappi.ducksmp.Listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

import static io.github.haappi.ducksmp.Commands.Link.setPDCLink;
import static io.github.haappi.ducksmp.Cosmetics.NameTag.Common.chatColors;
import static io.github.haappi.ducksmp.Cosmetics.NameTag.Common.getFormattedPrefix;
import static io.github.haappi.ducksmp.PacketInjector.injectPlayer;
import static io.github.haappi.ducksmp.PacketInjector.removePlayer;
import static io.github.haappi.ducksmp.Utils.Encryption.encrypt;
import static io.github.haappi.ducksmp.Utils.Utils.miniMessage;

public class JoinLeave implements Listener {

    private final DuckSMP plugin;
    private final ConcurrentHashMap<String, String> IPNameMapping = new ConcurrentHashMap<>();
//    public static boolean isAllowingConnections = true;

    public JoinLeave() {
        this.plugin = DuckSMP.getInstance();
//        isAllowingConnections = false;
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        Bukkit.getScheduler().runTaskLater(this.plugin, () -> isAllowingConnections = true, 20 * 60 * 2L); // 2 minutes
    }

//    @EventHandler
//    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
//        if (isAllowingConnections) {
//            return;
//        }
//        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
//        if (Bukkit.getServer().getOperators().contains(player)) {
//            event.allow();
//        } else {
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("The server is currently in startup phase. Please try again later.", NamedTextColor.RED));
//        }
//    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayer(player);
        if (player.isOp()) {
            event.quitMessage(Component.text()
                    .append(
                            Component.text("-", NamedTextColor.DARK_RED)
                                    .append(Component.text(" " + player.getName(), NamedTextColor.GREEN))).build()
            );
        } else {
            event.quitMessage(Component.text()
                    .append(
                            Component.text("-", NamedTextColor.DARK_RED)
                                    .append(Component.text(" " + player.getName(), NamedTextColor.YELLOW))).build()
            );
        }
        if (player.getUniqueId().toString().replaceAll("-", "").equalsIgnoreCase("1ca6d48fc4f4438781f79158a209d60d")) {
            event.quitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        if (chatColors.get(event.getPlayer().getUniqueId()) != null) {
            event.renderer((source, sourceDisplayName, message, viewer) -> Component.text()
                    .append(
                            getFormattedPrefix(event.getPlayer()),
                            Component.text("[", chatColors.get(event.getPlayer().getUniqueId())),
                            sourceDisplayName.color(chatColors.get(event.getPlayer().getUniqueId())),
                            Component.text("] ", chatColors.get(event.getPlayer().getUniqueId())),
                            Component.text()
                                    .color(NamedTextColor.WHITE)
                                    .append(message)
                                    .build()
                    )
                    .build());
        } else if (event.getPlayer().isOp()) {
            event.renderer((source, sourceDisplayName, message, viewer) -> getRendering(sourceDisplayName, message, NamedTextColor.GREEN, NamedTextColor.WHITE));
        } else {
            if (event.getPlayer().getPersistentDataContainer().has(new NamespacedKey(plugin, "linked"))) {
                byte key = event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(plugin, "linked"), PersistentDataType.BYTE);
                if (key == 1) {
                    event.renderer((source, sourceDisplayName, message, viewer) -> getRendering(sourceDisplayName, message, NamedTextColor.YELLOW, NamedTextColor.WHITE));
                } else {
                    event.renderer((source, sourceDisplayName, message, viewer) -> getRendering(sourceDisplayName, message, NamedTextColor.GRAY, NamedTextColor.GRAY));
                }
            } else {
                event.renderer((source, sourceDisplayName, message, viewer) -> getRendering(sourceDisplayName, message, NamedTextColor.GRAY, NamedTextColor.GRAY));
            }

        }
    }


    private Component getRendering(Component sourceDisplayName, Component message, NamedTextColor nameColor, NamedTextColor messageColor) {
        return Component.text()
                .append(
                        Component.text("[", nameColor),
                        sourceDisplayName.color(nameColor),
                        Component.text("] ", nameColor),
                        Component.text()
                                .color(messageColor)
                                .append(message)
                                .build()
                )
                .build();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
//        if (!isAllowingConnections) {
//            player.sendMessage(Component.text("Reminder: The server is currently in startup mode.", NamedTextColor.RED));
//        }
        injectPlayer(player);
        if (player.isOp()) {
            event.joinMessage(Component.text()
                    .append(
                            Component.text("+", NamedTextColor.DARK_GREEN)
                                    .append(Component.text(" " + player.getName(), NamedTextColor.GREEN))).build()
            );
        } else {
            event.joinMessage(Component.text()
                    .append(
                            Component.text("+", NamedTextColor.DARK_GREEN)
                                    .append(Component.text(" " + player.getName(), NamedTextColor.YELLOW))).build()
            );
        }
        if (player.getUniqueId().toString().replaceAll("-", "").equalsIgnoreCase("1ca6d48fc4f4438781f79158a209d60d")) {
            event.joinMessage(null);
        }
        if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "linked"), PersistentDataType.BYTE)) {
            setPDCLink(player, (byte) 0);
        }
        player.setNoDamageTicks(20 * 3); // no damage for 3 seconds
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> saveStuffInDB(player), 20 * 5L);

        if (DuckSMP.showRestartBar) {
            player.showBossBar(DuckSMP.restartBar);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void saveStuffInDB(Player player) {
        Document doc = new Document();
        doc.put("playerIP", encrypt(player.getAddress().getAddress().getHostAddress())); // it's encrypted with the key in the config
        doc.put("playerName", player.getName().replaceAll("\\.", "")); // Doubt Bedrock players want to see the prepended '.'

        Bson filter = Filters.eq("_id", player.getUniqueId().toString().replaceAll("-", ""));

        DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("playerData")
                .updateOne(
                        filter,
                        new Document("$set", doc),
                        new UpdateOptions().upsert(true)
                );
    }

    private @NotNull String getPlayerMOTDFromIP(String ipAddress) {
        String mapping = IPNameMapping.get(ipAddress);
        if (mapping != null) {
            if (mapping.equals("")) {
//                return "";
            } else {
                return String.format("<newline><gray>Welcome Back</gray> <aqua><bold>%s</bold></aqua><gray>", IPNameMapping.get(ipAddress));
            }
        }
        Document doc = DuckSMP.getMongoClient().getDatabase("duckMinecraft")
                .getCollection("playerData")
                .find(new Document("playerIP", encrypt(ipAddress))).first(); // todo some way to somehow get a random player name from the IP provided.
        // just so bunny & fire have a chance of getting both of their names lmao
        if (doc == null) {
            IPNameMapping.put(ipAddress, "");
            return "";
        } else {
            IPNameMapping.put(ipAddress, doc.getString("playerName"));
            return String.format("<newline><gray>Welcome Back</gray> <aqua><bold>%s</bold></aqua><gray>", IPNameMapping.get(ipAddress));
        }
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        String motd = "<bold><gold>Duck</gold><yellow>Nation</yellow><green> SMP</green></bold>";
        event.motd(miniMessage.deserialize(motd));

        Component component = miniMessage.deserialize(motd + getPlayerMOTDFromIP(event.getAddress().getHostAddress()));
        event.motd(component);

    }
}
