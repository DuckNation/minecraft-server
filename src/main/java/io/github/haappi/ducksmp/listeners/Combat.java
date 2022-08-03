package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Combat implements Listener {

    private final DuckSMP plugin;

    public static final ConcurrentHashMap<UUID, Long> timers = new ConcurrentHashMap<>();

    public Combat() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> timers.forEach((uuid, time) -> {
            if (System.currentTimeMillis() > time) {
                timers.remove(uuid);
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.sendMessage(Component.text("You are no longer in combat.", NamedTextColor.GRAY));
                }
            }
        }), 0, 20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (timers.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().setHealth(0);
            timers.remove(event.getPlayer().getUniqueId());
            Component logged = Component.text(event.getPlayer().getName() + " has combat logged.", NamedTextColor.RED);
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(logged));
        }
    }

    @EventHandler
    public void onSlap(EntityDamageByEntityEvent event) {
        Component component = Component.text("You're now in combat.", NamedTextColor.GRAY);
        if (event.getEntity() instanceof Player victim) {
            if (event.getDamager() instanceof Player player) {
                if (!timers.containsKey(victim.getUniqueId())) {
                    victim.sendMessage(component);
                } // Doing this resets the timer & prevents another "You're in combat" message.
                if (!timers.containsKey(player.getUniqueId())) {
                    player.sendMessage(component);
                }
                timers.put(victim.getUniqueId(), System.currentTimeMillis() + 15000); // 15 seconds
                timers.put(player.getUniqueId(), System.currentTimeMillis() + 15000); // 15 seconds
            } else {
                if (!timers.containsKey(victim.getUniqueId())) {
                    victim.sendMessage(component);
                }
                timers.put(victim.getUniqueId(), System.currentTimeMillis() + 15000); // 15 seconds
            }
        }
    }

    public static boolean canUseCommand(Player player) {
        return !timers.containsKey(player.getUniqueId());
    }

}
