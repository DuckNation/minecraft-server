package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.utils.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Kiss extends Command {

    public Kiss(String name) {
        super(name, "duck.kiss");
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("ok", "penis");
    }
}
