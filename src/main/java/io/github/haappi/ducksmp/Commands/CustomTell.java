package io.github.haappi.ducksmp.Commands;

import com.mongodb.lang.NonNull;
import io.github.haappi.ducksmp.DuckSMP;
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

import static io.github.haappi.ducksmp.Commands.Vanish.enabledPlayers;
import static io.github.haappi.ducksmp.Listeners.AntiSpam.*;
import static io.github.haappi.ducksmp.Listeners.ChatFilter.filterMessage;

public class CustomTell extends BukkitCommand implements Listener {

    public static final HashMap<UUID, UUID> recentlyMessaged = new HashMap<>();

    public CustomTell(String name) {
        super(name);
        setAliases(Arrays.asList("w", "tell", "msg", "m"));

        Bukkit.getScheduler().runTaskTimer(DuckSMP.getInstance(), recentlyMessaged::clear, 300L, 20 * 60 * 10); // 10 minutes
    }

    public static void doTell(@NonNull CommandSender sender, @Nullable Player receiver, @NonNull String message) {
        if (receiver == null || enabledPlayers.contains(receiver.getUniqueId())) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }
        if (sender instanceof Player player) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                if (System.currentTimeMillis() - cooldowns.get(player.getUniqueId()) < 420) {
                    player.sendMessage(Component.text("Stop spamming!", NamedTextColor.RED));
                    warnings.put(player.getUniqueId(), warnings.getOrDefault(player.getUniqueId(), 1) + 1);
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis()); // screw them for spamming

                    if (warnings.get(player.getUniqueId()) > 3) {
                        mutedPlayers.add(player.getUniqueId());
                        player.sendMessage(Component.text("You have been muted for spamming.", NamedTextColor.RED));
                        Bukkit.getOnlinePlayers().forEach(_player -> _player.sendMessage(Component.text("Player " + player + " has been muted for spamming! What a loser.", NamedTextColor.RED)));
                    }
                    return;
                }
            }

            if (mutedPlayers.contains(player.getUniqueId())) {
                player.sendMessage(Component.text("You are muted.", NamedTextColor.RED));
                return;
            }

            recentlyMessaged.put(receiver.getUniqueId(), player.getUniqueId());
            recentlyMessaged.put(player.getUniqueId(), receiver.getUniqueId());
        }
        receiver.sendMessage(Component.text("From ", NamedTextColor.GRAY).append(Component.text(sender.getName(), NamedTextColor.YELLOW)).append(Component.text(": " + filterMessage(message), NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("To ", NamedTextColor.GRAY).append(Component.text(receiver.getName(), NamedTextColor.YELLOW)).append(Component.text(": " + filterMessage(message), NamedTextColor.GRAY)));
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
