package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AntiSpam implements Listener {

    private final DuckSMP plugin;

    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    public static final ConcurrentLinkedQueue<UUID> mutedPlayers = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<UUID, Integer> warnings = new ConcurrentHashMap<>();

    public AntiSpam() {
        plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        if (mutedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("You are muted.", NamedTextColor.RED));
            return;
        }

        if (cooldowns.containsKey(event.getPlayer().getUniqueId())) {
            if (System.currentTimeMillis() - cooldowns.get(event.getPlayer().getUniqueId()) < 420) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("Stop spamming!", NamedTextColor.RED));
                warnings.put(event.getPlayer().getUniqueId(), warnings.getOrDefault(event.getPlayer().getUniqueId(), 1) + 1);
                cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis()); // screw them for spamming

                if (warnings.get(event.getPlayer().getUniqueId()) > 3) {
                    mutedPlayers.add(event.getPlayer().getUniqueId());
                    event.getPlayer().sendMessage(Component.text("You have been muted for spamming.", NamedTextColor.RED));
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("Player " + event.getPlayer().getName() + " has been muted for spamming! What a loser.", NamedTextColor.RED)));
                }
            }
        }
        cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());


    }
}
