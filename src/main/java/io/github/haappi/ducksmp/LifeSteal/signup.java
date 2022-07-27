package io.github.haappi.ducksmp.LifeSteal;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.utils.GUIUtils.sendOptInForm;

public class signup extends BukkitCommand {

    public signup(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            sendOptInForm(player);
        }
        return true;
    }
}
