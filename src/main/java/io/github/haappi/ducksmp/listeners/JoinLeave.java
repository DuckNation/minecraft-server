package io.github.haappi.ducksmp.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.chatColors;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.getFormattedPrefix;
import static io.github.haappi.ducksmp.PacketInjector.injectPlayer;
import static io.github.haappi.ducksmp.PacketInjector.removePlayer;
import static io.github.haappi.ducksmp.utils.Utils.miniMessage;

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
        if (player.getUniqueId().toString().replaceAll("-", "").equalsIgnoreCase("1ca6d48fc4f4438781f79158a209d60d")) {
            return;
        }

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

    @EventHandler
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
            event.renderer((source, sourceDisplayName, message, viewer) -> Component.text()
                    .append(
                            Component.text("[", NamedTextColor.GREEN),
                            sourceDisplayName.color(NamedTextColor.GREEN),
                            Component.text("] ", NamedTextColor.GREEN),
                            Component.text()
                                    .color(NamedTextColor.WHITE)
                                    .append(message)
                                    .build()
                    )
                    .build());
        } else { // todo make non-linked players gray
            event.renderer((source, sourceDisplayName, message, viewer) -> Component.text()
                    .append(
                            Component.text("[", NamedTextColor.YELLOW),
                            sourceDisplayName.color(NamedTextColor.YELLOW),
                            Component.text("] ", NamedTextColor.YELLOW),
                            Component.text()
                                    .color(NamedTextColor.WHITE)
                                    .append(message)
                                    .build()
                    )
                    .build());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        injectPlayer(player);
        if (player.getUniqueId().toString().replaceAll("-", "").equalsIgnoreCase("1ca6d48fc4f4438781f79158a209d60d")) {
            return;
        }
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
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        final Component component = miniMessage.deserialize("<bold><gold>Duck</gold><yellow>Nation</yellow><green> SMP</green></bold>");
        event.motd(component); // todo some sort of "welcome back message"
    }
}
