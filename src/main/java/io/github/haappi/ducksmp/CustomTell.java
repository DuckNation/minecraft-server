package io.github.haappi.ducksmp;

import edu.umd.cs.findbugs.annotations.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class CustomTell extends BukkitCommand implements Listener {

    public static final HashMap<UUID, UUID> recentlyMessaged = new HashMap<>();

    public CustomTell(String name) {
        super(name);
        setAliases(Arrays.asList("w", "tell", "msg", "m"));

        Bukkit.getScheduler().runTaskTimer(DuckSMP.getInstance(), recentlyMessaged::clear, 300L, 20 * 60 * 10); // 10 minutes
    }

    public static void doTell(@NonNull CommandSender sender, @Nullable Player receiver, @NonNull String message) {
        if (receiver == null) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }
        if (sender instanceof Player player) {
            recentlyMessaged.put(receiver.getUniqueId(), player.getUniqueId());
            recentlyMessaged.put(player.getUniqueId(), receiver.getUniqueId());
        }
        receiver.sendMessage(Component.text("From ", NamedTextColor.GRAY).append(Component.text(sender.getName(), NamedTextColor.YELLOW)).append(Component.text(": " + message, NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("To ", NamedTextColor.GRAY).append(Component.text(receiver.getName(), NamedTextColor.YELLOW)).append(Component.text(": " + message, NamedTextColor.GRAY)));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /tell <player> <message>", NamedTextColor.RED));
            return true;
        }
        Player p = Bukkit.getPlayer(args[0]);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        doTell(sender, p, sb.toString());
        return true;
    }
}