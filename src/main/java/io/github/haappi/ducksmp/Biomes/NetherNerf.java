package io.github.haappi.ducksmp.Biomes;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;

public class NetherNerf implements Listener {

    private final DuckSMP plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public NetherNerf() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onChunkLoad(ChunkPopulateEvent event) {
        if (!event.getChunk().contains(Material.ANCIENT_DEBRIS.createBlockData())) {
            return;
        }
        ChunkSnapshot snapshot = event.getChunk().getChunkSnapshot();
        // debris spawns from y values of 7 to 120
        int counter = 1;

        for (int y = 7; y < 120; y = y + 8) { // each worker thread will do 8 y values at a time
            int finalY = y;
            scheduler.runTaskLaterAsynchronously(this.plugin, () -> {
                stoff(snapshot, event.getChunk(), finalY);
            }, counter * 2L - 4L * counter);
            counter++;
        }

    }

    private void stoff(ChunkSnapshot snapshot, Chunk chunk, int _y) {
        HashSet<Location> locations = new HashSet<>();
        final int chunkX = chunk.getX() << 4;
        final int chunkZ = chunk.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = _y; y < _y + 8; y++) {
                    if (snapshot.getBlockType(x, y, z) == Material.ANCIENT_DEBRIS) {
                        final Location location = new Location(chunk.getWorld(), x + chunkX, y, z + chunkZ);
                        locations.add(location);
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> location.getBlock().setType(Material.NETHERRACK), 10L);
                    }
                }
            }
        }

        for (Location location : locations) {
            for (int y = location.getBlockY() + 1; y < 120; y++) {
                if (isSafeToPlaceBlock(snapshot, location.getBlockX(), y, location.getBlockZ())) {
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> location.getBlock().setType(Material.ANCIENT_DEBRIS), 10L);
                    System.out.println("placed block at " + location.getBlockX() + " " + y + " " + location.getBlockZ());
                    break;
                }
            }
        }

    }

    private boolean isSafeToPlaceBlock(ChunkSnapshot snapshot, int newX, int y, int newZ) {
        newX = newX / 16;
        newZ = newZ / 16;
        System.out.println("newX: " + newX + " newZ: " + newZ);
        return (snapshot.getBlockType(newX, y, newZ) != Material.AIR)
                && (snapshot.getBlockType(newX, y + 1, newZ) != Material.AIR)
                && (snapshot.getBlockType(newX, y - 1, newZ) != Material.AIR)
                && (snapshot.getBlockType(newX, y, z + 1) != Material.AIR)
                && (snapshot.getBlockType(newX, y, z - 1) != Material.AIR)
                && (snapshot.getBlockType(newX + 1, y, newZ) != Material.AIR)
                && (snapshot.getBlockType(newX - 1, y, newZ) != Material.AIR)
                && (snapshot.getBlockType(newX, y, newZ) != Material.ANCIENT_DEBRIS)
                && (snapshot.getBlockType(newX, y, newZ) != Material.CHEST)
                && (snapshot.getBlockType(newX, y, newZ) != Material.GOLD_BLOCK)
                && (snapshot.getBlockType(newX, y, newZ) != Material.NETHER_PORTAL)
                && (snapshot.getBlockType(newX, y, newZ) != Material.OBSIDIAN);
    }
}
