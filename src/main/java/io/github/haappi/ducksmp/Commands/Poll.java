package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.DuckSMP.secretKey;

public class Poll extends BukkitCommand {

    public Poll(String name) {
        super(name);

    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /poll <key> <answer>");
            return true;
        }
        if (!args[0].equals(secretKey)) {
            sender.sendMessage(Component.text("Pfft, you thought!", NamedTextColor.RED));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You can't do that, silly!", NamedTextColor.RED));
            return true;
        }
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (args[1].equals("1")) {
            container.set(new NamespacedKey(DuckSMP.getInstance(), "netherite_question"), PersistentDataType.INTEGER, 1);
        } else {
            container.set(new NamespacedKey(DuckSMP.getInstance(), "netherite_question"), PersistentDataType.INTEGER, 0);
        }
        player.sendMessage(Component.text("Thanks for your input!", NamedTextColor.GREEN));
        return true;
    }
}
