package io.github.haappi.ducksmp.utils;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class GUIUtils {

    public static void sendOptInForm(UUID uuid) {
        FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
        SimpleForm.Builder form = SimpleForm.builder()
                .title("Feedback form")
                .content("We're asking for feedback, are you willing to enter some feedback to improve our server?")
                .button("Yes") // id = 0
                .button("No"); // id = 1;
        form.closedOrInvalidResultHandler(response -> {
            // no response was given
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            if (response.clickedButtonId() == 0) {
                Bukkit.getPlayer(uuid).sendMessage("Thank you for your feedback!");
            } else {
                Bukkit.getPlayer(uuid).sendMessage("We're sorry to hear that, we'll try to improve our server!");
            }
        });

        player.sendForm(form);
    }

public static void sendOptInJava(UUID uuid) {

}

}
