package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AntiDimension implements Listener {

    private final DuckSMP plugin;

    public AntiDimension() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEnd(PlayerTeleportEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
    }
}
