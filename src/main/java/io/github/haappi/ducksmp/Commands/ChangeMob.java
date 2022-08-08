package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.Listeners.TotemHandler;
import io.github.haappi.ducksmp.Utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.haappi.ducksmp.Listeners.TotemHandler.mobs;

public class ChangeMob extends BukkitCommand {

    public ChangeMob(String name) {
        super(name);
        setAliases(List.of("cm"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You need to be an OP to use this command.");
            return true;
        }
        TotemHandler.randomMob = mobs.get(Utils.random.nextInt(mobs.size()));
        sender.sendMessage("Random mob set to " + TotemHandler.randomMob.name());
        return true;
    }
}
