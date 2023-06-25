package io.github.haappi.duckpaper.commands;

import io.github.haappi.duckpaper.DuckPaper;
import io.github.haappi.duckpaper.utils.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.haappi.duckpaper.utils.PlayerNMS.applyNightVision;
import static io.github.haappi.duckpaper.utils.PlayerNMS.removeNightVision;

public class NightVision extends Command {
    private final ArrayList<String> tabComplete = new ArrayList<>();

    public NightVision(DuckPaper plugin) {
        super("nightvision", "Night vision related commands", "/nv", List.of("nv"), "duck.night_vision");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        return this.tabComplete;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Component.text("This may only be used by players!", NamedTextColor.RED));
            return false;
        }
        if (args.length == 0) {
            applyNightVision(player);
        } else {
            switch (args[0].toLowerCase()) {
                case "on", "enable" -> applyNightVision(player);
                case "off", "disable" -> removeNightVision(player);
            }
        }
        return true;
    }

    @Override
    public Component usage() {
        return Component.text("/nv [on | off]", NamedTextColor.RED);
    }
}
