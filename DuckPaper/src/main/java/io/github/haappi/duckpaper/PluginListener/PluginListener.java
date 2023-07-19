package io.github.haappi.duckpaper.PluginListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static io.github.haappi.duckpaper.DuckPaper.PLUGIN_CHANNEL;
import static io.github.haappi.duckpaper.chat.ChatHandler.mutedPlayers;

public class PluginListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals(PLUGIN_CHANNEL)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String channell = in.readUTF();
        String action = in.readUTF();
        String args = "";
        try {
            args = in.readUTF();

            try {
                while (true) {
                    args += ";" + in.readUTF();
                }
            } catch (Exception ignored) {

            }
        } catch (Exception ignored) {

        }

        if (channell.equals("Mute")) {
            if (action.equals("muted")) {
                mutedPlayers.add(UUID.fromString(args));
            }
            if (action.equals("unmuted")) {
                mutedPlayers.remove(UUID.fromString(args));
            }
        }
    }
}
