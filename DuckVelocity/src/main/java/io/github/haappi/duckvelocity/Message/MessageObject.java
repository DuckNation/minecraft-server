package io.github.haappi.duckvelocity.Message;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageObject {
    public static final ConcurrentHashMap<UUID, MessageObject> lastMessaged = new ConcurrentHashMap<>();

    private final Player player;
    private long lastMessageTime;

    public MessageObject(Player player) {
        this.player = player;
        this.lastMessageTime = System.currentTimeMillis(); // 10 minutes
    }

    public Player getPlayer() {
        return player;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime() {
        this.lastMessageTime = System.currentTimeMillis();
    }

    public boolean shouldDeleteFromMap() {
        return System.currentTimeMillis() - lastMessageTime > 600000; // 10 minutes
    }

    public static @Nullable Player getLastMessaged(Player source) {
        if (source == null) {
            return null;
        }
        if (lastMessaged.containsKey(source)) {
            return lastMessaged.get(source).getPlayer();
        }
        return null;
    }

    public static void updateLastMessage(Player source, Player target) {
        if (source == null || target == null) {
            return;
        }

        if (lastMessaged.containsKey(source.getUniqueId())) {
            lastMessaged.get(source.getUniqueId()).setLastMessageTime();
        } else {
            lastMessaged.put(source.getUniqueId(), new MessageObject(target));
        }

        if (lastMessaged.containsKey(target.getUniqueId())) {
            lastMessaged.get(target.getUniqueId()).setLastMessageTime();
        } else {
            lastMessaged.put(target.getUniqueId(), new MessageObject(source));
        }
    }
}
