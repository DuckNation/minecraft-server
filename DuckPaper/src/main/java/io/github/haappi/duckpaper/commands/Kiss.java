package io.github.haappi.duckpaper.commands;

import io.github.haappi.duckpaper.utils.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
