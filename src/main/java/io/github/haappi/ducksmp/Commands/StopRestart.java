package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.Internals.Messages.restartID;
import static io.github.haappi.ducksmp.Internals.Messages.restartNeeded;

public class StopRestart extends BukkitCommand {

    public StopRestart(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You need to be an operator to use this command.", NamedTextColor.RED));
            return true;
        }
        if (!restartNeeded) {
            sender.sendMessage(Component.text("There isn't a reset scheduled.", NamedTextColor.RED));
        } else {
            restartNeeded = false;
            restartID.cancel();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(Component.text("The server restart has been cancelled.", NamedTextColor.GREEN));
            }
        }
        return true;
    }
}
