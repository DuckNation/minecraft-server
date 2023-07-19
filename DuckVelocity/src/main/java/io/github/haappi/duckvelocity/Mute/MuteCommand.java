package io.github.haappi.duckvelocity.Mute;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github.haappi.duckvelocity.DuckVelocity;
import io.github.haappi.duckvelocity.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.github.haappi.duckvelocity.DuckVelocity.customChannel;
import static io.github.haappi.duckvelocity.Utils.getPlayersFromInvocation;
import static io.github.haappi.duckvelocity.Utils.stringToByteArray;

public class MuteCommand implements SimpleCommand {
    public static final ConcurrentHashMap<UUID, Mute> mutedNoobs = new ConcurrentHashMap<>();

    public MuteCommand() {
        ProxyServer proxy = DuckVelocity.getInstance().getProxy();
        proxy.getScheduler().buildTask(DuckVelocity.getInstance(), () -> {
            ArrayList<UUID> toRemove = new ArrayList<>();
            for (Map.Entry<UUID, Mute> entry : mutedNoobs.entrySet()) {
                if (entry.getValue()


                        .getMuteTime() < System.currentTimeMillis()) {
                    toRemove.add(entry.getKey());
                }
            }
            for (UUID uuid : toRemove) {
                mutedNoobs.remove(proxy.getPlayer(uuid).get());
            }
        }).repeat(2L, TimeUnit.MINUTES).schedule();
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length < 3) {
            invocation.source().sendMessage(Component.text("Usage: /mute <player> <duration> <reason>", NamedTextColor.RED));
            return;
        }
        Player victim;
        try {
            victim = DuckVelocity.getInstance().getProxy().matchPlayer(invocation.arguments()[0]).stream().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            invocation.source().sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }

        Long time = Utils.parseTimeToMillis(invocation.arguments()[1]);

        String reason = "";

        for (int i = 2; i < invocation.arguments().length; i++) {
            reason += invocation.arguments()[i] + " ";
        }

        Mute mute = new Mute(System.currentTimeMillis() + time, reason);

        mutedNoobs.put(victim.getUniqueId(), mute);

        victim.sendMessage(Component.text("You have been muted for " + Utils.parseMillisToTime(time) + " for " + reason, NamedTextColor.RED));

        invocation.source().sendMessage(Component.text("Muted " + victim.getUsername() + " for " + Utils.parseMillisToTime(time) + " for " + reason, NamedTextColor.RED));

        ServerConnection conn = victim.getCurrentServer().isPresent() ? victim.getCurrentServer().get() : null;
        if (conn == null) {
            return;
        }
        conn.sendPluginMessage(customChannel, stringToByteArray("Mute;muted;" + victim.getUniqueId()));
    }


    @Override
    public List<String> suggest(Invocation invocation) {
        return getPlayersFromInvocation(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("duck.mute_players");
    }

}
