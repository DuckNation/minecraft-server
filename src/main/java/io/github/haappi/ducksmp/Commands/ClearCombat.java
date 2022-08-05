package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.haappi.ducksmp.Listeners.Combat.timers;

public class ClearCombat extends BukkitCommand {
    public ClearCombat(String name) {
        super(name);
        this.setDescription("Clears combat logging status for everyone.");
        this.setUsage("/" + name);
        this.setAliases(List.of("cc"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You must be an OP to use this command", NamedTextColor.RED));
            return true;
        }
        int length = timers.size();
        timers.clear();
        sender.sendMessage(Component.text("Cleared combat logging status for " + length + " players.", NamedTextColor.GREEN));
        return true;
    }
}
