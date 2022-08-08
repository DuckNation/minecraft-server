package io.github.haappi.ducksmp.LifeSteal;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static io.github.haappi.ducksmp.Utils.GUIUtils.sendOptInForm;

public class Signup extends BukkitCommand {

    public Signup(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (player.getPersistentDataContainer().get(new org.bukkit.NamespacedKey(DuckSMP.getInstance(), "claimed_hearts"), PersistentDataType.INTEGER) == null) {
                sendOptInForm(player);
            } else {
                player.sendMessage(Component.text("But, you're already in LifeSteal...", NamedTextColor.RED));
            }
        }
        return true;
    }
}
