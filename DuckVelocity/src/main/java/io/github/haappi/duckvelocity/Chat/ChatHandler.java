package io.github.haappi.duckvelocity.Chat;

import com.velocitypowered.api.proxy.Player;
import io.github.haappi.duckvelocity.DuckVelocity;
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
            } // case "mute" -> {
//                ChannelManager.channels.get(args[0]).unsubscribePlayer(playerFromUUID(args[1]));
//            }
//            case "unmute" -> {
//                ChannelManager.createChannel(args[0]).subscribePlayer(playerFromUUID(args[1]));
//            }
        }
    }

    private static @Nullable Player playerFromUUID(String uuid) {
        return DuckVelocity.getInstance().getProxy().getPlayer(UUID.fromString(uuid)).orElse(null);
    }
}
