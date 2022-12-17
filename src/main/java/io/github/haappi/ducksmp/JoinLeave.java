package io.github.haappi.ducksmp;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

import static io.github.haappi.ducksmp.PacketInjector.injectPlayer;
import static io.github.haappi.ducksmp.PacketInjector.removePlayer;

public class JoinLeave implements Listener {

    private final DuckSMP plugin;

    public JoinLeave() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
         if (event.getPlayer().isOp()) {
            event.renderer((source, sourceDisplayName, message, viewer) -> getRendering(sourceDisplayName, message, NamedTextColor.GREEN, NamedTextColor.WHITE));
        } else {
             event.renderer((source, sourceDisplayName, message, viewer) -> getRendering(sourceDisplayName, message, NamedTextColor.YELLOW, NamedTextColor.WHITE));

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
        player.setNoDamageTicks(20 * 5); // no damage for 5 seconds
    }

}