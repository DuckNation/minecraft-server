package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.utils.SingleDoor;
import io.github.haappi.duckpaper.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Doors implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleDoubleDoor(event.getClickedBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        final Block block = event.getBlock();
        handleDoubleDoor(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        int now = event.getNewCurrent();
        int old = event.getOldCurrent();
        if (now != old && (now == 0 || old == 0)) {
            handleDoubleDoor(event.getBlock());
        }
    }

    public void handleDoubleDoor(final Block block) {
        final SingleDoor first = SingleDoor.createDoorFromBlock(block);
        if (first == null) {
            return;
        }
        final SingleDoor second = first.getSecondDoor();
        if (second == null) {
            return;
        }

        // Update second door state directly after the event (delay 0)
        Utils.scheduleNextTick(() -> {
            // Make sure to include changes from last tick
            if (!first.updateCachedState() || !second.updateCachedState()) {
                return;
            }

            second.setOpen(first.isOpen());
        });
    }
}
