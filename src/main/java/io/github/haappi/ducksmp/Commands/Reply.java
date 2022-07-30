package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.haappi.ducksmp.Commands.CustomTell.doTell;
import static io.github.haappi.ducksmp.Commands.CustomTell.recentlyMessaged;

public class Reply extends BukkitCommand {
    public Reply(String name) {
        super(name);
        setAliases(List.of("r"));
    }

    @Override
    public boolean execute(@NotNull org.bukkit.command.CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You need to be a player to use this command.", NamedTextColor.RED));
            return true;
        }
        if (!recentlyMessaged.containsKey(player.getUniqueId())) {
            sender.sendMessage(Component.text("You haven't messaged anyone recently.", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text("You need to specify a message to reply.", NamedTextColor.RED));
            return true;
        }
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        doTell(player, Bukkit.getPlayer(recentlyMessaged.get(player.getUniqueId())), message.toString());
        return true;
    }
}
