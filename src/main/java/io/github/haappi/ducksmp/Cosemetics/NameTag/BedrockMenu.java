package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.setStuff;

public class BedrockMenu extends BukkitCommand { // refactor to use one command -> detect client -> correct menu

    private final HashMap<Integer, ChatFormatting> colorMapping = new HashMap<>();

    public BedrockMenu(String name) {
        super(name);
        colorMapping.put(0, ChatFormatting.DARK_BLUE);
        colorMapping.put(1, ChatFormatting.DARK_GREEN);
        colorMapping.put(2, ChatFormatting.DARK_AQUA);
        colorMapping.put(3, ChatFormatting.DARK_RED);
        colorMapping.put(4, ChatFormatting.DARK_PURPLE);
        colorMapping.put(5, ChatFormatting.GOLD);
        colorMapping.put(6, ChatFormatting.GRAY);
        colorMapping.put(7, ChatFormatting.DARK_GRAY);
        colorMapping.put(8, ChatFormatting.BLUE);
        colorMapping.put(9, ChatFormatting.GREEN);
        colorMapping.put(10, ChatFormatting.AQUA);
        colorMapping.put(11, ChatFormatting.RED);
        colorMapping.put(12, ChatFormatting.LIGHT_PURPLE);
        colorMapping.put(13, ChatFormatting.YELLOW);
    }

    public void form(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        String thing = container.get(new NamespacedKey(DuckSMP.getInstance(), "custom_prefix"), PersistentDataType.STRING);
        if (thing == null) {
            thing = "my awesome prefix";
        } else {
            thing = thing.replace("[", "").replace("] ", "");
        }
        CustomForm.Builder form = CustomForm.builder()
                .title("Name Tag")
                .dropdown("Select a color", Arrays.asList("Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple", "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow"))
                .input("Tag Prefix", "14 characters max", thing);

        form.closedOrInvalidResultHandler(response -> {
            player.sendMessage(Component.text("Form closed or invalid", NamedTextColor.RED));
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            Integer color = response.asDropdown();
            String responseInput = response.asInput();
            if (responseInput == null) {
                player.sendMessage(Component.text("Form closed or invalid", NamedTextColor.RED));
                return;
            }
            String prefix;
            if (responseInput.length() < 13) {
                prefix = "[" + responseInput + "] ";
            } else {
                prefix = "[" + responseInput.substring(0, 13) + "] ";
            }
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
