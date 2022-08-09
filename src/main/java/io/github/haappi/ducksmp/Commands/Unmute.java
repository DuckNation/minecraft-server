package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.Listeners.AntiSpam.mutedPlayers;

public class Unmute extends BukkitCommand {

    public Unmute(String name) {
        super(name);
        this.description = "Unmutes a player";
        this.usageMessage = "/unmute <player>";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You do not have permission to use this command", NamedTextColor.RED));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /unmute <player>", NamedTextColor.RED));
            return true;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (!mutedPlayers.contains(player.getUniqueId())) {
            sender.sendMessage(Component.text("Player is not muted", NamedTextColor.RED));
            return true;
        }
        mutedPlayers.remove(player.getUniqueId());
        sender.sendMessage(Component.text("Player unmuted", NamedTextColor.GREEN));
        return true;
    }
}
