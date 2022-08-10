package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.join;

public class Ban extends BukkitCommand {
    public Ban(@NotNull String name) {
        super(name);
        this.setDescription("Bans a player");
        this.setUsage("/" + name + " <player> [reason]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You must be an OP to use this command", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /ban <player> [reason]", NamedTextColor.YELLOW));
            return true;
        }
        String player = args[0];
        String reason = "Banned by an operator.";
        List<String> _reason = new java.util.ArrayList<>(Arrays.stream(args).toList());
        _reason.remove(0);
        if (args.length > 1) {
            reason = join(" ", _reason);
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        offlinePlayer.banPlayer(reason);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Component.text(offlinePlayer.getName() + " has been banned for " + reason, NamedTextColor.RED));
        }
        sender.sendMessage(Component.text("Banned " + offlinePlayer.getName() + " for " + reason, NamedTextColor.GREEN));
        return true;
    }
}
