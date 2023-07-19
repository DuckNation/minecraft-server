package io.github.haappi.duckvelocity.Send;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MovePlayer implements SimpleCommand {

    private final ProxyServer server;

    public MovePlayer(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length < 2) {
            invocation.source().sendMessage(Component.text("Usage: /transfer <player> <server>", NamedTextColor.RED));
            return;
        }
        Player toMove = server.getPlayer(invocation.arguments()[0]).orElse(null);
        if (toMove == null) {
            invocation.source().sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }


        Component invoker;
        if (invocation.source() instanceof Player player) {
            invoker = Component.text(player.getUsername(), NamedTextColor.YELLOW);
        } else {
            invoker = Component.text("Console", NamedTextColor.YELLOW);
        }

        Component message = invoker.append(Component.text(" is moving you to ", NamedTextColor.GRAY).append(Component.text(invocation.arguments()[1], NamedTextColor.GREEN)));
        String serverName = invocation.arguments()[1];
        RegisteredServer registeredServer = server.getServer(serverName).orElse(null);
        if (registeredServer == null) {
            invocation.source().sendMessage(Component.text("Server not found", NamedTextColor.RED));
            return;
        }
        toMove.sendMessage(message);
        server.getScheduler()
                .buildTask(DuckVelocity.getInstance(), () -> {
                    toMove.createConnectionRequest(registeredServer).fireAndForget();
                })
                .delay(3L, TimeUnit.SECONDS)
                .schedule();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if (invocation.arguments().length == 0) { // suggesting player name
            return CompletableFuture.supplyAsync(() -> server.getAllPlayers().stream()
                    .map(Player::getUsername)
                    .collect(Collectors.toList()));
        } else { // suggesting server name
            return CompletableFuture.supplyAsync(() -> server.getAllServers().stream()
                    .map(RegisteredServer::getServerInfo)
                    .map(ServerInfo::getName)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("duck.transfer");
    }
}