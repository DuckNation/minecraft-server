package io.github.haappi.ducksmp;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoSprint extends BukkitCommand {
    public AutoSprint(String name) {
        super(name);
        setAliases(List.of("as"));
    }

    @Override
    public boolean execute(org.bukkit.command.@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (player.isSprinting()) {
                player.setSprinting(false);
            } else {
                player.setSprinting(true);
            }
            player.sendRichMessage("<aqua>AutoSprint has been " + (player.isSprinting() ? "<green>enabled" : "<red>disabled") + "<aqua>.");
        }
        return true;
    }
}
