package io.github.haappi.ducksmp.Cosemetics.NameTag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.setStuff;

public class BedrockMenu extends BukkitCommand { // refactor to use one command -> detect client -> correct menu

    private final HashMap<Integer, ChatFormatting> colorMapping = new HashMap<>();
    public BedrockMenu(String name) {
        super(name);
        colorMapping.put(0, ChatFormatting.BLACK);
        colorMapping.put(1, ChatFormatting.DARK_BLUE);
        colorMapping.put(2, ChatFormatting.DARK_GREEN);
        colorMapping.put(3, ChatFormatting.DARK_AQUA);
        colorMapping.put(4, ChatFormatting.DARK_RED);
        colorMapping.put(5, ChatFormatting.DARK_PURPLE);
        colorMapping.put(6, ChatFormatting.GOLD);
        colorMapping.put(7, ChatFormatting.GRAY);
        colorMapping.put(8, ChatFormatting.DARK_GRAY);
        colorMapping.put(9, ChatFormatting.BLUE);
        colorMapping.put(10, ChatFormatting.GREEN);
        colorMapping.put(11, ChatFormatting.AQUA);
        colorMapping.put(12, ChatFormatting.RED);
        colorMapping.put(13, ChatFormatting.LIGHT_PURPLE);
        colorMapping.put(14, ChatFormatting.YELLOW);
        colorMapping.put(15, ChatFormatting.WHITE);
    }

    public void form(Player player) {
        CustomForm.Builder form = CustomForm.builder()
                .title("NameTag")
                .dropdown("Select a color", Arrays.asList("Black", "Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple", "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow", "White"))
                .input("Tag Prefix", "bob", "a");

        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            Integer color = response.asDropdown();
            String prefix = "[" + response.asInput() + "] ";
            Common.teamPacket(player, String.valueOf(System.currentTimeMillis()), prefix, colorMapping.get(color));
            setStuff(player, prefix, colorMapping.get(color));
            player.sendMessage(Component.text("Your prefix is: " + prefix, NamedTextColor.nearestTo(TextColor.color(colorMapping.get(color).getColor()))));
        });
        FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                player.sendMessage(Component.text("Coming soon for Java Edition near you! :tm:", NamedTextColor.AQUA));
                return true;
            }
            form(player);
        }
        return true;
    }
}
