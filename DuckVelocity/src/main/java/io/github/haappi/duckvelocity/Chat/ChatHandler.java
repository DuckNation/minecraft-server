package io.github.haappi.duckvelocity.Chat;

import io.github.haappi.duckvelocity.DuckVelocity;

import java.util.Arrays;
import java.util.UUID;

public class ChatHandler {

    public static void handle(String action, String[] args) {
        switch (action) {
            case "message" -> {
                ChannelManager.sendMessageToChannel(args[0], args[1]);
            }
            case "subscribe" -> {
                ChannelManager.createChannel(args[0], args[1]).subscribePlayer(DuckVelocity.getInstance().getProxy().getPlayer(UUID.fromString(args[2])).orElse(null));
            }
        }
    }
}
