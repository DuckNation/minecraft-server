package io.github.haappi.duckpaper.commands;

import io.github.haappi.duckpaper.utils.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Named;
import java.util.List;

public class Kiss extends Command {

    public Kiss(String name) {
        super(name, "duck.kiss");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String label, String[] args) {
        if (args.length != 1) {
            commandSender.sendMessage(usage());
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return false;
        }
        commandSender.sendMessage(Component.text("You just kissed ", NamedTextColor.GREEN).append(Component.text(args[0], NamedTextColor.AQUA)));
        player.sendMessage(Component.text("You've been kissed by ", NamedTextColor.GREEN).append(Component.text(commandSender.getName(), NamedTextColor.AQUA)).append(Component.text(". Was it with consent???", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    public Component usage() {
        return Component.text()
                .append(Component.text("Usage: ", NamedTextColor.RED))
                .append(Component.text("/kiss ", NamedTextColor.YELLOW))
                .append(Component.text("<username>", NamedTextColor.AQUA)).build();

    }
}
