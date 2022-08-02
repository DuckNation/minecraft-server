package io.github.haappi.ducksmp.Cosemetics.NameTag;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.BedrockMenu.form;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.JavaMenu.menu;

public class NameTagCommand extends BukkitCommand {

    public NameTagCommand(String name) {
        super(name);
        setAliases(List.of("menu"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());

            if (discordId == null) {
                player.sendMessage(Component.text("You must be linked to a Discord account to use this command,", NamedTextColor.RED));
                player.sendMessage(Component.text("Link using /discord link", NamedTextColor.YELLOW));
                return true;
            }

            User user = DiscordUtil.getJda().getUserById(discordId);
            if (user == null) {
                player.sendMessage(Component.text("You must be linked to a Discord account to use this command,", NamedTextColor.RED));
                player.sendMessage(Component.text("Link using /discord link", NamedTextColor.YELLOW));
                return true;
            }

            if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                menu(player, args);
            } else {
                form(player);
            }
        }
        return true;
    }
}
