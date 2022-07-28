package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class vanish extends BukkitCommand implements Listener {
    public static final ArrayList<UUID> enabledPlayers = new ArrayList<>();

    public vanish(String name) {
        super(name);
        Bukkit.getPluginManager().registerEvents(this, DuckSMP.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (UUID uuid : enabledPlayers) {
            event.getPlayer().hidePlayer(DuckSMP.getInstance(), Bukkit.getPlayer(uuid));
        }
    }

    @EventHandler
    public void onQuit(PlayerJoinEvent event) {
        enabledPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        if (enabledPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("You can't send messages while being invisible", NamedTextColor.RED));
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(PlayerCommandPreprocessEvent event) {
//        override the default /tell /w commands
    }

    @Override
    public boolean execute(@NotNull org.bukkit.command.CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                player.sendMessage(Component.text("You need to be an operator to use this command.", NamedTextColor.RED));
                return true;
            }
            if (enabledPlayers.contains(player.getUniqueId())) {
                enabledPlayers.remove(player.getUniqueId());
                player.sendMessage(Component.text("You are no longer invisible.", NamedTextColor.GREEN));
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.showPlayer(DuckSMP.getInstance(), player);
                    other.sendMessage(Component.text()
                            .append(
                                    Component.text("+", NamedTextColor.DARK_GREEN)
                                            .append(Component.text(" " + player.getName(), NamedTextColor.GREEN))).build());

                }
            } else {
                enabledPlayers.add(player.getUniqueId());
                player.sendMessage(Component.text("You are now invisible.", NamedTextColor.GREEN));
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.hidePlayer(DuckSMP.getInstance(), player);
                    other.sendMessage(Component.text()
                            .append(
                                    Component.text("-", NamedTextColor.DARK_RED)
                                            .append(Component.text(" " + player.getName(), NamedTextColor.GREEN))).build());
                }
            }
        }
        return true;
    }
}
