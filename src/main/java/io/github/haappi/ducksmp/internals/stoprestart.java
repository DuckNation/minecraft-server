package io.github.haappi.ducksmp.internals;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.internals.Messages.restartID;
import static io.github.haappi.ducksmp.internals.Messages.restartNeeded;

public class stoprestart extends BukkitCommand {

    public stoprestart(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!restartNeeded) {
            sender.sendMessage(Component.text("There isn't a reset scheduled.", NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text("Restart scheduled!", NamedTextColor.GREEN));
            restartNeeded = false;
            restartID.cancel();
        }
        return true;
    }
}
