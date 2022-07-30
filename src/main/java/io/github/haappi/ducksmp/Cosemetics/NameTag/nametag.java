package io.github.haappi.ducksmp.Cosemetics.NameTag;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.BedrockMenu.form;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.JavaMenu.menu;

public class nametag extends BukkitCommand {

    public nametag(String name) {
        super(name);
        setAliases(List.of("menu"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                menu(player, args);
            } else {
                form(player);
            }
        }
        return true;
    }
}
