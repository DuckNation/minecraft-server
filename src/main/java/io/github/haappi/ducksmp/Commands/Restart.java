package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.Utils.Utils.restartWarning;

public class Restart extends BukkitCommand {
    public Restart(String name) {
        super(name);
        this.setDescription("Restarts the server");
        this.setUsage("/" + name + " <time>");
    }

    @Override
    public boolean execute(org.bukkit.command.CommandSender sender, @NotNull String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You must be an OP to use this command", NamedTextColor.RED));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /restart <time in minutes>", NamedTextColor.YELLOW));
            return true;
        }
        try {
            int time = Integer.parseInt(args[0]);
            if (time < 5) {
                sender.sendMessage(Component.text("Time must be at least 5 minutes", NamedTextColor.RED));
                return true;
            }
            restartWarning(time, true);
            sender.sendMessage(Component.text("Restarting server in " + time + " minutes", NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Usage: /restart <time in minutes>", NamedTextColor.YELLOW));
        }
        return true;
    }
}
