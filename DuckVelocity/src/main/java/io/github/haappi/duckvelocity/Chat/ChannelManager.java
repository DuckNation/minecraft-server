package io.github.haappi.duckvelocity.Chat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    public static final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();


    public static Channel createChannel(String name, String id) {
        if (channels.containsKey(name)) {
            return channels.get(name);
        }
        channels.put(name, new Channel(id, name));
        return channels.get(name);
    }

    public static void removeChannel(Channel channel) {
        Iterator<Map.Entry<String, Channel>> iterator = channels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> entry = iterator.next();
            if (entry.getValue().getId().equals(channel.getId())) {
                iterator.remove();
            }
        }
    }

    public static void sendMessageToChannel(String channel, String message) {
        channels.get(channel).sendMessage(message);

    }





}
