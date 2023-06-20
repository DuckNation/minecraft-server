package io.github.haappi.duckpaper.NMS;

import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NMSEvents implements Listener {
    private final DuckPaper plugin;

    public NMSEvents(DuckPaper plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        PacketInjector.injectPlayer(player);
    }

    @EventHandler
    public void onLeave(PlayerJoinEvent event) {
        PacketInjector.removePlayer(event.getPlayer());
    }
}
