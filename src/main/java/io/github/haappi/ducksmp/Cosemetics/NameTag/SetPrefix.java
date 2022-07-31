package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class SetPrefix extends BukkitCommand {

    public SetPrefix(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You must be an OP to use this command", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /setprefix <player> <prefix>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return true;
        }
        ArrayList<String> prefix = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        PersistentDataContainer container = target.getPersistentDataContainer();
        container.set(new org.bukkit.NamespacedKey(DuckSMP.getInstance(), "custom_prefix"), PersistentDataType.STRING, "[" + String.join(" ", prefix) + "] ");
        target.kick(Component.text("Your prefix was forced changed by an operator.", NamedTextColor.RED).append(Component.text(" Your prefix is now: " + String.join(" ", prefix), NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("Prefix changed for " + target.getName() + "!", NamedTextColor.AQUA));

        return true;
    }

}
