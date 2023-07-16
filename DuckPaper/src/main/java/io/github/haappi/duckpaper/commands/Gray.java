package io.github.haappi.duckpaper.commands;

import io.github.haappi.duckpaper.chat.ChatHandler;
import io.github.haappi.duckpaper.utils.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Named;
import java.util.List;

public class Gray extends Command {

    public Gray(String name) {
        super(name, "duck.gray");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String label, String[] args) {
        ChatHandler.grayify = !ChatHandler.grayify;
        commandSender.sendMessage(ChatHandler.grayify ? Component.text("Enabled", NamedTextColor.RED) : Component.text("Disabled", NamedTextColor.RED));
        return true;
    }

    @Override
    public Component usage() {
        return Component.text()
                .append(Component.text("Usage: ", NamedTextColor.RED))
                .append(Component.text("/grayify ", NamedTextColor.YELLOW)).build();

    }
}
