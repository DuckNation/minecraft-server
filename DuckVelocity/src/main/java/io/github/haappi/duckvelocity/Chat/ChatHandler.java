package io.github.haappi.duckvelocity.Chat;

import com.velocitypowered.api.proxy.Player;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class ChatHandler {

    public static void handle(String action, String[] args) {
        switch (action) {
            case "message" -> {
                ChannelManager.sendMessageToChannel(args[0], args[1]);
            }
            case "subscribe" -> {
                ChannelManager.createChannel(args[0], args[1]).subscribePlayer(playerFromUUID(args[2]));
            }
            case "unsubscribe" -> {
                ChannelManager.createChannel(args[0], args[1]).unsubscribePlayer(playerFromUUID(args[2]));
                ChannelManager.createChannel("global", "global").sendMessage(Types.REMOVE_PLAYER, args[1] + ";" + args[2]);
            }
            case "init" -> {
                ChannelManager.createChannel("global", "global").sendMessage(Types.CREATE_DISCORD_CHANNEL, args[1] + ";" + args[0]);
            }
             case "mute" -> {
                ChannelManager.channels.get(args[0]).unsubscribePlayer(playerFromUUID(args[1]));
            }
            case "unmute" -> {
                Channel channel = ChannelManager.getChannel(args[0]);
                if (channel != null) {
                    channel.subscribePlayer(playerFromUUID(args[1]));
                } else {
                    playerFromUUID(args[1]).sendMessage(Component.text("There was an error trying to unmute the channel. Please rejoin the server!", NamedTextColor.RED));
                }
            }
        }
    }

    private static @Nullable Player playerFromUUID(String uuid) {
        return DuckVelocity.getInstance().getProxy().getPlayer(UUID.fromString(uuid)).orElse(null);
    }
}
