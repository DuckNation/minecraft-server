package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.Commands.Home.getHomesOfPlayer;

public class Homes extends BukkitCommand {
    public Homes(String name) {
        super(name);
        this.setDescription("List someone's homes");
        this.setUsage(String.format("/%s <player_name>", name));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You are not an operator.", NamedTextColor.RED));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /homes <player_name>", NamedTextColor.RED));
            return true;
        }
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return true;
        }
        sender.sendMessage(Component.text("Homes of " + player.getName(), NamedTextColor.YELLOW).append(getHomesOfPlayer(player)));
        return true;
    }
}
