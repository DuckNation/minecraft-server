package io.github.haappi.ducksmp.utils;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    public static String sFormat(Integer number) {
        if (number == 1) {
            return "";
        } else {
            return "s";
        }
    }
    public static TextComponent getCountdown(Integer countdown) {
        NamedTextColor color = switch (countdown) {
            case 0, 1, 2, 3 -> NamedTextColor.RED;
            case 4, 5, 6 -> NamedTextColor.YELLOW;
            default -> NamedTextColor.GREEN;
        };

        return Component.text(countdown + " second" + sFormat(countdown), color);
    }
}
