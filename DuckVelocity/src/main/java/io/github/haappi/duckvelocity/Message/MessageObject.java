package io.github.haappi.duckvelocity.Message;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class MessageObject {
    private static final ConcurrentHashMap<Player, MessageObject> lastMessaged = new ConcurrentHashMap<>();

    private final Player player;
    private long lastMessageTime;

    public MessageObject(Player player) {
        this.player = player;
        this.lastMessageTime = System.currentTimeMillis() + 600_000L; // 10 minutes
    }

    public Player getPlayer() {
        return player;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime() {
        this.lastMessageTime = System.currentTimeMillis() + 600_000L; // 10 minutes
    }

    public static @Nullable Player getLastMessaged(Player source) {
        if (lastMessaged.containsKey(source)) {
            return lastMessaged.get(source).getPlayer();
        }
        return null;
    }
}
