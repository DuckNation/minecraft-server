package io.github.haappi.ducksmp.Commands;

import com.mongodb.lang.NonNull;
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

import static io.github.haappi.ducksmp.Commands.vanish.enabledPlayers;

public class tell extends BukkitCommand implements Listener {

    public static final HashMap<UUID, UUID> recentlyMessaged = new HashMap<>();

    public tell(String name) {
        super(name);
        setAliases(Arrays.asList("w", "tell", "msg", "m"));
    }

    public static void doTell(@NonNull Player sender, @Nullable Player receiver, @NonNull String message) {
        if (receiver == null || enabledPlayers.contains(receiver.getUniqueId())) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }
        receiver.sendMessage(Component.text("From ", NamedTextColor.GRAY).append(Component.text(sender.getName(), NamedTextColor.YELLOW)).append(Component.text(": " + message, NamedTextColor.GRAY)));
        recentlyMessaged.put(receiver.getUniqueId(), sender.getUniqueId());
        sender.sendMessage(Component.text("To ", NamedTextColor.GRAY).append(Component.text(receiver.getName(), NamedTextColor.YELLOW)).append(Component.text(": " + message, NamedTextColor.GRAY)));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You need to be a player to use this command.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /tell <player> <message>", NamedTextColor.RED));
            return true;
        }
        Player p = Bukkit.getPlayer(args[0]);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        doTell(player, p, sb.toString());
        return true;
    }
}
