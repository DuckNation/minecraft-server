package io.github.haappi.duckpaper.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Utils {
    public static BukkitTask scheduleNextTick(Runnable task) {
        return Bukkit.getScheduler().runTask(DuckPaper.getInstance(), task);
    }

    public static BukkitTask scheduleNextTickAsync(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(DuckPaper.getInstance(), task);
    }

    public static BukkitTask scheduleNextTick(Runnable task, boolean async) {
        if (async) {
            return scheduleNextTickAsync(task);
        } else {
            return scheduleNextTick(task);
        }
    }

    public static BukkitTask runTaskLater(Runnable task, long ticks) {
        return Bukkit.getScheduler().runTaskLater(DuckPaper.getInstance(), task, ticks);
    }

    public static BukkitTask runTaskLater(Runnable task, int ms) {
        return runTaskLater(task, (long) ms / 50);
    }

    public static String titleCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static byte[] stringToByteArray(String message, String delim) {
        String[] msg = message.split(delim);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String s : msg) {
            out.writeUTF(s);
        }

        return out.toByteArray();
    }

    public static byte[] stringToByteArray(String message) {
        return stringToByteArray(message, ";");
    }
}
