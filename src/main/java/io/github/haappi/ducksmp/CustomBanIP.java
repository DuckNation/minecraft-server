package io.github.haappi.ducksmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBanIP extends BukkitCommand implements Listener {

    public CustomBanIP(String name) {
        super(name);
        DuckSMP instance = DuckSMP.getInstance();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        sender.sendMessage(Component.text("Yeah... no.", NamedTextColor.RED));
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void beforeCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().contains("ban-ip")) {
            event.getPlayer().sendMessage(Component.text("Yeah... no.", NamedTextColor.RED));
            event.setCancelled(true);
        }
    }
}
