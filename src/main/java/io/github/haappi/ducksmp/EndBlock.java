package io.github.haappi.ducksmp;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;


public class EndBlock implements Listener {

    private final DuckSMP plugin;

    public EndBlock(DuckSMP plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEndEnter(PlayerTeleportEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (System.currentTimeMillis() / 1000 < 1672090200) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("The end is currently disabled. shoo off"));
            }
        }
    }
}
