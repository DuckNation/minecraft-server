package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.listeners.totem;
import io.github.haappi.ducksmp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.haappi.ducksmp.listeners.totem.mobs;

public class changeMob extends BukkitCommand {

    public changeMob(String name) {
        super(name);
        setAliases(List.of("cm"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You need to be an OP to use this command.");
            return true;
        }
        totem.randomMob = mobs.get(Utils.random.nextInt(mobs.size()));
        sender.sendMessage("Random mob set to " + totem.randomMob.name());
        return true;
    }
}
