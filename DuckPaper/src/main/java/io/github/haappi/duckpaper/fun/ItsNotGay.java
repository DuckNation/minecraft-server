package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.utils.Utils;
import org.bukkit.block.Bed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItsNotGay implements Listener {
    // it's not gay if it's with the homies, right?

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnterBed(PlayerBedEnterEvent event) {
        event.setUseBed(Event.Result.ALLOW);
    }

    @EventHandler
    public void onPEBE(final PlayerInteractEvent event) {
        if ((event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) &&
                (event.getClickedBlock() != null) && ((event.getClickedBlock().getBlockData() instanceof Bed))) {

            Utils.runTaskLater(() -> event.getPlayer().sleep(event.getClickedBlock().getLocation(), true), 4L);
        }
    }
}
