package io.github.haappi.ducksmp;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Biome extends BukkitCommand {
    public Biome(String name) {
        super(name);
    }

    @Override
    public boolean execute(org.bukkit.command.@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage(player.getLocation().getBlock().getBiome().toString());
        }
        return true;
    }
}
