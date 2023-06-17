package io.github.haappi.duckpaper.utils;

import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class WorldTime {
    private static final HashMap<UUID, BukkitTask> currentTasks = new HashMap<>();

    // https://github.com/oddlama/vane/blob/main/vane-core/src/main/java/org/oddlama/vane/util/WorldUtil.java#L14
    public static boolean changeTime(
            final World world,
            final DuckPaper plugin,
            final long worldTime,
            final long interpolationTicks) {
        synchronized (currentTasks) {
            if (currentTasks.containsKey(world.getUID())) {
                return false;
            }

            // Calculate relative time from and to
            long rel_to = worldTime;
            long rel_from = world.getTime();
            if (rel_to <= rel_from) {
                rel_to += 24000;
            }

            // Calculate absolute values
            final long delta_ticks = rel_to - rel_from;
            final long absolute_from = world.getFullTime();

            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            BukkitTask task = scheduler.runTaskTimer(plugin,
                    new Runnable() {
                        private long elapsed = 0;

                        @Override
                        public void run() {
                            // Remove task if we finished interpolation
                            if (elapsed > interpolationTicks) {
                                synchronized (currentTasks) {
                                    currentTasks.remove(world.getUID()).cancel();
                                }
                            }

                            // Make transition smooth by applying a cosine
                            float lin_delta = (float) elapsed / interpolationTicks;
                            float delta = (1f - (float) Math.cos(Math.PI * lin_delta)) / 2f;

                            var cur_ticks = absolute_from + (long) (delta_ticks * delta);
                            world.setFullTime(cur_ticks);
                            ++elapsed;
                        }
                    }, 1, 1);

            currentTasks.put(world.getUID(), task);
        }

        return true;
    }
}
