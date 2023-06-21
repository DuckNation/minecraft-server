package io.github.haappi.duckvelocity.Chat;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.haappi.duckvelocity.Config;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.github.haappi.duckvelocity.DuckVelocity.httpClient;

@SuppressWarnings("unused")
public class SendDiscordHandler {
    private final ProxyServer server;
    private final MiniMessage mm = DuckVelocity.getInstance().getMiniMessage();
    private final WebSocketClient client;

    public SendDiscordHandler() {
        this.server = DuckVelocity.getInstance().getProxy();

        client = new WebSocketClient((type, message) -> {
            System.out.println(message);
        });

        server.getScheduler()
                .buildTask(DuckVelocity.getInstance(), () -> {
                    client.connect(Config.WSS_BASE_URL + "/wss/to-server?key=" + Config.API_KEY);
                }).schedule();
        server.getScheduler()
                .buildTask(DuckVelocity.getInstance(), () -> client.sendMessage(getPlayers()))
                .repeat(10, TimeUnit.MINUTES)
                .delay(5L, TimeUnit.SECONDS)
                .schedule();
    }

    private String getPlayers() {
        List<String> onlinePlayerNames = server.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
        String players = String.join(", ", onlinePlayerNames);
        return String.format("%s/%s players online. Join at **smp.quack.boo**\n\nOnline Players: %s", server.getPlayerCount(), server.getConfiguration().getShowMaxPlayers(), players);
    }

    private String getServerName(RegisteredServer server) {
        return server == null ? "unknown" : server.getServerInfo().getName();
    }

    private String getServerName(ServerConnection server) {
        return server == null ? "unknown" : server.getServerInfo().getName();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPlayerJoin(ServerConnectedEvent event) {
        String serverName = getServerName(event.getServer());
        write(String.format("**%s** connected to **%s**", event.getPlayer().getUsername(), serverName), Types.PLAYER_JOIN);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPlayerLeave(DisconnectEvent event) {
        write(String.format("**%s** disconnected", event.getPlayer().getUsername()), Types.PLAYER_LEAVE);

        for (Map.Entry<String, Channel> channels : ChannelManager.channels.entrySet()) {
            channels.getValue().unsubscribePlayer(event.getPlayer());
        }
    }

    private void write(String message, Types type) {
        client.sendMessage(type + ";" + message);
    }

    private void write(Types type, String message) {
        client.sendMessage(type + ";" + message);
    }

    public static void handle(@Nullable Types type, String message) {
        if (type == null) return;
        DuckVelocity instance = DuckVelocity.getInstance();
//        switch (type) {
//            case PLAYER_CHAT -> {
//                Component deserialized = instance.getMiniMessage().deserialize(message);
//                instance.getProxy().getAllPlayers().forEach(p -> p.sendMessage(deserialized));
//            }
//        }
    }


}
