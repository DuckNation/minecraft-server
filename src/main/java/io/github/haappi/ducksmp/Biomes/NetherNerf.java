package io.github.haappi.ducksmp.Biomes;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;

import static io.github.haappi.ducksmp.Utils.Utils.random;

public class NetherNerf implements Listener {

    private final DuckSMP plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public NetherNerf() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        if (container.has(new NamespacedKey(plugin, "nerfed"), PersistentDataType.BYTE)) {
            return;
        }
        // set the PDC
        if (!event.getChunk().contains(Material.ANCIENT_DEBRIS.createBlockData())) {
            return;
        }
        ChunkSnapshot snapshot = event.getChunk().getChunkSnapshot();
        // debris spawns from y values of 7 to 120
        int counter = 1;

        for (int y = 7; y < 127; y = y + 7) { // each worker thread will do 8 y values at a time
            int finalY = y;
            scheduler.runTaskLaterAsynchronously(this.plugin, () -> stoff(snapshot, event.getChunk(), finalY), counter * 4L - 3L * counter);
            counter++;
        }

        container.set(new NamespacedKey(plugin, "nerfed"), PersistentDataType.BYTE, (byte) 1);
    }

    private void stoff(ChunkSnapshot snapshot, Chunk chunk, int _y) {
        HashSet<Location> locations = new HashSet<>();
//        HashMap<String, Location> locationHashMap = new HashMap<>();
        final int chunkX = chunk.getX() << 4;
        final int chunkZ = chunk.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = _y; y < _y + 8; y++) {
                    if (snapshot.getBlockType(x, y, z) == Material.ANCIENT_DEBRIS) {
                        final Location location = new Location(chunk.getWorld(), x + chunkX, y, z + chunkZ);
                        locations.add(location);
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> location.getBlock().setType(Material.NETHERRACK), 2L);
                    }
                }
            }
        }

        for (Location _location : locations) {
            int x = _location.getBlockX();
            int z = _location.getBlockZ();
            for (int y = _location.getBlockY() + random.nextInt(8, 25); y < 120; y++) {
                if (isSafeToPlaceBlock(snapshot, x, y, z)) {
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        _location.getBlock().setType(Material.ANCIENT_DEBRIS);
                        locations.remove(_location);
                    }, 10L);
                    break;
                }
            }
        }

    }

    private boolean isSafeToPlaceBlock(ChunkSnapshot snapshot, int newX, int y, int newZ) {
        int newXX = newX & 15;
        int newZZ = newZ & 15;
        System.out.println("checking " + newXX + " " + y + " " + newZZ);
        return (snapshot.getBlockType(newXX, y, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y + 1, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y - 1, newZZ) != Material.AIR)
//                && (snapshot.getBlockType(newXX, y, newZZ + 1) != Material.AIR)
//                && (snapshot.getBlockType(newXX, y, newZZ - 1) != Material.AIR)
//                && (snapshot.getBlockType(newXX + 1, y, newZZ) != Material.AIR)
//                && (snapshot.getBlockType(newXX - 1, y, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.ANCIENT_DEBRIS)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.CHEST)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.GOLD_BLOCK)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.NETHER_PORTAL)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.OBSIDIAN);
    }
}
