package io.github.haappi.duckvelocity.Message;

import com.velocitypowered.api.proxy.Player;
import io.github.haappi.duckvelocity.DuckVelocity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageObject {
    public static final ConcurrentHashMap<UUID, MessageObject> lastMessaged = new ConcurrentHashMap<>();

    private final UUID player;
    private long lastMessageTime;

    public MessageObject(UUID player) {
        this.player = player;
        this.lastMessageTime = System.currentTimeMillis(); // 10 minutes
    }

    public static @Nullable Player getLastMessaged(Player source) {
        if (source == null) {
            return null;
        }
        if (lastMessaged.containsKey(source.getUniqueId())) {
            return DuckVelocity.getInstance().getProxy().getPlayer(lastMessaged.get(source.getUniqueId()).getPlayer()).orElse(null);
        }
        return null;
    }

    public static void updateLastMessage(Player source, Player target) {
        if (source == null || target == null) {
            return;
        }
        
        lastMessaged.put(source.getUniqueId(), new MessageObject(target.getUniqueId()));
        lastMessaged.put(target.getUniqueId(), new MessageObject(source.getUniqueId()));
    }

    public UUID getPlayer() {
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

    public String toString() {
        return "MessageObject{player=" + this.player + ", lastMessageTime=" + this.lastMessageTime + "}";
    }
}
