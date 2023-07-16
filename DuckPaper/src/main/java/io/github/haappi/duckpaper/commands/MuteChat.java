package io.github.haappi.duckpaper.commands;

import io.github.haappi.duckpaper.chat.ChatHandler;
import io.github.haappi.duckpaper.utils.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MuteChat extends Command {

    public MuteChat(String name) {
        super(name, "duck.mute_chat");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String label, String[] args) {
        ChatHandler.muted = !ChatHandler.muted;
        commandSender.sendMessage(ChatHandler.muted ? Component.text("Muted", NamedTextColor.RED) : Component.text("Unmuted", NamedTextColor.RED));
        return true;
    }

    @Override
    public Component usage() {
        return Component.text()
                .append(Component.text("Usage: ", NamedTextColor.RED))
                .append(Component.text("/grayify ", NamedTextColor.YELLOW)).build();

    }
}
