package io.github.haappi.duckvelocity.Message;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.github.haappi.duckvelocity.Message.MessageObject.lastMessaged;
import static io.github.haappi.duckvelocity.Mute.MuteCommand.mutedNoobs;
import static io.github.haappi.duckvelocity.Utils.getPlayersFromInvocation;

public class Message implements SimpleCommand {
    public Message() {
        ProxyServer proxy = DuckVelocity.getInstance().getProxy();
        proxy.getScheduler().buildTask(DuckVelocity.getInstance(), () -> {
            ArrayList<UUID> toRemove = new ArrayList<>();
            for (Map.Entry<UUID, MessageObject> entry : lastMessaged.entrySet()) {
                if (entry.getValue().shouldDeleteFromMap()) {
                    toRemove.add(entry.getKey());
                }
            }
            for (UUID uuid : toRemove) {
                lastMessaged.remove(uuid);
            }
        }).repeat(2L, TimeUnit.MINUTES).schedule();
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length < 2) {
            invocation.source().sendMessage(Component.text("Usage: /message <player> <message>", NamedTextColor.RED));
            return;
        }
        if (invocation.source() instanceof Player player) {
            if (mutedNoobs.containsKey(player.getUniqueId())) {
                player.sendMessage(Component.text("You are muted.", NamedTextColor.RED));
                return;
            }
        }
        Player toMessage;
        try {
            toMessage = DuckVelocity.getInstance().getProxy().matchPlayer(invocation.arguments()[0]).stream().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            invocation.source().sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }

        Reply.doMessage(invocation, toMessage, String.join(" ", invocation.arguments()).replaceFirst(invocation.arguments()[0], ""));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return getPlayersFromInvocation(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("duck.message");
    }
}
