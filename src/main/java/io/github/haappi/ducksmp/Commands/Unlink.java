package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.Commands.Link.*;

public class Unlink extends BukkitCommand {

    private final DuckSMP plugin;

    public Unlink(@NotNull String name) {
        super(name);
        this.plugin = DuckSMP.getInstance();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Document doc = getStatsFromDatabase(player.getUniqueId());
                if (doc.get("discordID") == null) {
                    setPDCLink(player, (byte) 0);
                    player.sendMessage(Component.text("You are not linked to a Discord account.", NamedTextColor.RED));
                } else {
                    setPDCLink(player, (byte) 0);
                    player.sendMessage(Component.text("You have been unlinked from your Discord account.", NamedTextColor.GREEN));
                    doc.put("discordID", null);
                    doc.put("pinCode", null);
                    saveStuffIntoDB(player.getUniqueId(), doc);
                }
            });

        }
        return true;
    }
}
