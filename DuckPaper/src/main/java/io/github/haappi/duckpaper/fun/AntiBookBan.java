package io.github.haappi.duckpaper.fun;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.nio.charset.StandardCharsets;

public class AntiBookBan implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBookEdit(PlayerEditBookEvent event) {
        for (Component page : event.getNewBookMeta().pages()) {
            if (!StandardCharsets.US_ASCII.newEncoder().canEncode(PlainTextComponentSerializer.plainText().serialize(page))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("Sorry! Books are limited to ASCII characters only.", NamedTextColor.RED));
            }
        }
    }
}
