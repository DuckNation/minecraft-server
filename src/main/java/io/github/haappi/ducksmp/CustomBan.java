package io.github.haappi.ducksmp;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.print.Paper;
import java.util.Locale;

public class CustomBan extends BukkitCommand implements Listener {

        public CustomBan(String name) {
            super(name);
            DuckSMP instance = DuckSMP.getInstance();
            instance.getServer().getPluginManager().registerEvents(this, instance);
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
            if (!sender.hasPermission("ducksmp.ban")) {
                sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(Component.text("Usage: /ban <player> [reason]", NamedTextColor.RED));
                return true;
            }
            String arg = args[0];
            args[0] = "";
            String reason = args.length > 1 ? String.join(" ", args) : " Banned by an operator.";
            String message = "You have been banned by " + sender.getName() +  " for " + reason;
            Bukkit.getOfflinePlayer(arg).banPlayer(message);
            Component modern = LegacyComponentSerializer.legacy('§').deserialize(message);
            Bukkit.broadcast(Component.text(arg, NamedTextColor.YELLOW).append(Component.text(" has been banned by ", NamedTextColor.RED)).append(Component.text(sender.getName(), NamedTextColor.YELLOW)).append(Component.text(" for ", NamedTextColor.RED)).append(modern));
            return true;
        }

}
