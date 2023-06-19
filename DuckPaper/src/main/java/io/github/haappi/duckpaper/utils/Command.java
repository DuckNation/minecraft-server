package io.github.haappi.duckpaper.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Command extends BukkitCommand implements TabCompleter {

    private final @Nullable String permission;

    protected Command(String name, String description, String usage, List<String> aliases, @Nullable String permission) {
        super(name, description, usage, aliases);
        this.permission = permission;
    }

    protected Command(String name) {
        this(name, null);
    }

    protected Command(String name, String permission) {
        super(name);
        this.permission = permission;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return false;
        }
        return onCommand(sender, label, args);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }
        if (permission != null && !player.hasPermission(permission)) {
            return null;
        }
        return onTabComplete(player, command, alias, args);
    }

    public abstract boolean onCommand(CommandSender commandSender, String label, String[] args);

    public abstract Component usage();

    public List<String> onTabComplete(Player player, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
