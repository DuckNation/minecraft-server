package io.github.haappi.ducksmp.Cosmetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static io.github.haappi.ducksmp.Cosmetics.NameTag.Common.colorMapping;
import static io.github.haappi.ducksmp.Cosmetics.NameTag.Common.setStuff;

public class BedrockMenu {

    public static void form(@NotNull Player player) {
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
                prefix = "[" + responseInput + "] ";
            }
            Common.teamPacket(player, String.valueOf(System.currentTimeMillis()), prefix, colorMapping.get(color));
            setStuff(player, prefix, colorMapping.get(color));
            player.sendMessage(Component.text("Your prefix is: " + prefix, NamedTextColor.nearestTo(TextColor.color(colorMapping.get(color).getColor()))));
        });
        FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
    }
}
