package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;

import static io.github.haappi.ducksmp.Utils.Utils.random;

public class AntiDimension implements Listener {

    private final DuckSMP plugin;

    public AntiDimension() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (event.getReason() == PortalCreateEvent.CreateReason.FIRE) {
            int randomInt = random.nextInt(1, 10);
            if (randomInt != 1) {
                event.setCancelled(true);
                if (event.getEntity() != null) {
                    event.getEntity().sendMessage(Component.text("Damn, your lighter didn't work. Try again.", NamedTextColor.RED));
                }
            }
        }
    }

    @EventHandler
    public void onEnd(PlayerTeleportEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
        }
//        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
//            event.setCancelled(true);
//        }
    }
}
