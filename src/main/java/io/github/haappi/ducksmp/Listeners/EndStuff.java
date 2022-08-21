package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static io.github.haappi.ducksmp.DuckSMP.secretMaterial;

public class EndStuff implements Listener {

    private final DuckSMP plugin;

    public EndStuff() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                    if (event.getItem() != null) {
                        if (event.getItem().getType().equals(Material.ENDER_EYE)) {
                            event.setCancelled(true);
                            event.getPlayer().sendRichMessage("<red>Pfft, try again.");
                            return;
                        }
                        if (event.getItem().getType().equals(secretMaterial)) {
                            event.setCancelled(true);
                            EndPortalFrame frame = (EndPortalFrame) event.getClickedBlock().getBlockData();
                            if (frame.hasEye()) {
                                return;
                            }
                            frame.setEye(true);
                            event.getItem().setAmount(event.getItem().getAmount() - 1);
                            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.0f);
                            event.getClickedBlock().setBlockData(frame);
                            doLoop(event.getClickedBlock().getLocation());
                        }
                    }
                }

            }
        }
    }

    private void doLoop(Location location) {
        ArrayList<Location> locations = new ArrayList<>();
        for (int x = location.getBlockX() - 5; x < location.getBlockX() + 5; x++) {
            for (int z = location.getBlockZ() - 5; z < location.getBlockZ() + 5; z++) {
                Location loc = new Location(location.getWorld(), x, location.getBlockY(), z);
                Block block = loc.getBlock();
                if (block.getType() == Material.END_PORTAL_FRAME) {
                    EndPortalFrame frame = (EndPortalFrame) block.getBlockData();
                    if (!frame.hasEye()) {
                        return;
                    }
                    locations.add(loc);
                }
            }
        }
        if (locations.size() != 12) {
            return;
        }
        Location center = getCenter(locations);
        for (int newX = center.getBlockX() - 1; newX <= center.getBlockX() + 1; newX++) {
            for (int newZ = center.getBlockZ() - 1; newZ <= center.getBlockZ() + 1; newZ++) {
                Location loc = new Location(location.getWorld(), newX, location.getBlockY(), newZ);
                loc.getBlock().setType(Material.END_PORTAL);
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.0f));
            }
        }
    }

    private Location getCenter(ArrayList<Location> locations) {
        double x = 0;
        double y = 0;
        double z = 0;
        for (Location location : locations) {
            x += location.getBlockX();
            y += location.getBlockY();
            z += location.getBlockZ();
        }
        return new Location(locations.get(0).getWorld(), x / locations.size(), y / locations.size(), z / locations.size());
    }
}
