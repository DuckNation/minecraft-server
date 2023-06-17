package io.github.haappi.duckpaper.fun;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;

import java.util.HashMap;
import java.util.UUID;

public class AntiRaidFarm implements Listener {
    private final HashMap<UUID, Long> lastTime = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRaid(RaidTriggerEvent event) {
        Player player = event.getPlayer();
        if (lastTime.getOrDefault(player.getUniqueId(), (long) -1) > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You recently started a raid! Please wait a moment before attempting another.", NamedTextColor.RED));
        } else {
            lastTime.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * 60 * 5); // 5 minutes
        }
    }
}
