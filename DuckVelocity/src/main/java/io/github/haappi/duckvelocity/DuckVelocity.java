package io.github.haappi.duckvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.haappi.duckvelocity.Chat.Channel;
import io.github.haappi.duckvelocity.Chat.ChannelManager;
import io.github.haappi.duckvelocity.Chat.SendDiscordHandler;
import io.github.haappi.duckvelocity.Message.Message;
import io.github.haappi.duckvelocity.Message.Reply;
import io.github.haappi.duckvelocity.Mute.Mute;
import io.github.haappi.duckvelocity.Mute.MuteCommand;
import io.github.haappi.duckvelocity.Mute.UnmuteCommand;
import io.github.haappi.duckvelocity.PluginListener.MessageListener;
import io.github.haappi.duckvelocity.Send.MoveAll;
import io.github.haappi.duckvelocity.Send.MovePlayer;
import io.github.haappi.duckvelocity.ServerSwitcher.ServerSwitcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

import static io.github.haappi.duckvelocity.Chat.ChannelManager.createChannel;
import static io.github.haappi.duckvelocity.Config.checkConfig;
import static io.github.haappi.duckvelocity.Mute.MuteCommand.mutedNoobs;
import static io.github.haappi.duckvelocity.ServerSwitcher.ServerSwitcher.handle;
import static io.github.haappi.duckvelocity.ServerSwitcher.ServerSwitcher.pingServer;
import static io.github.haappi.duckvelocity.Utils.stringToByteArray;

@Plugin(
        id = "duck-velocity",
        name = "DuckVelocity",
        version = BuildConstants.VERSION,
        authors = {"haappi"}
)
public class DuckVelocity {
    public static final CloseableHttpClient httpClient = HttpClients.createDefault();
    public static final ChannelIdentifier customChannel =
            MinecraftChannelIdentifier.from("duck:messenger");
    private static DuckVelocity instance;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private RegisteredServer hubServer;
    private final ProxyServer proxy;
    @Inject
    private Logger logger;

    @Inject
    public DuckVelocity(ProxyServer proxy) {
        this.proxy = proxy;
        instance = this;
        checkConfig();
    }

    public static synchronized DuckVelocity getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        hubServer = proxy.getServer("lobby").orElseThrow(() -> new RuntimeException("Hub server not found!"));

        proxy.getEventManager().register(this, new SendDiscordHandler());

        proxy.getChannelRegistrar().register(customChannel);
        proxy.getEventManager().register(this, new MessageListener(customChannel));

        proxy.getEventManager().register(this, ServerPreConnectEvent.class, e -> {
            for (Channel channels : ChannelManager.channels.values()) {
                channels.unsubscribePlayer(e.getPlayer());
            }
        });

        proxy.getEventManager().register(this, PlayerChooseInitialServerEvent.class, e -> {
            e.getInitialServer().ifPresent(server -> {
                if (!pingServer(server.getServerInfo().getName())) {
                    e.setInitialServer(hubServer);
                    handle(e.getPlayer(), server.getServerInfo().getName());
                }
            });
        });

        proxy.getEventManager().register(this, DisconnectEvent.class, e -> {
            ServerSwitcher.shutUpStopSpamming.compute(e.getPlayer().getUniqueId(), (key, value) -> {
                if (value != null) {
                    value.cancel();
                }
                return null;
            });

            ServerSwitcher.shutUpStopSpamming.remove(e.getPlayer().getUniqueId());

        });

        proxy.getEventManager().register(this, ServerPostConnectEvent.class, e -> {
            e.getPlayer().getCurrentServer().ifPresent(serverConnection -> {
                for (Map.Entry<UUID, Mute> entry : mutedNoobs.entrySet()) {
                    if (entry.getValue().getMuteTime() < System.currentTimeMillis()) {
                        mutedNoobs.remove(entry.getKey());
                        serverConnection.sendPluginMessage(customChannel, stringToByteArray("Mute;unmuted;" + e.getPlayer().getUniqueId()));
                    }
                }

                if (mutedNoobs.containsKey(e.getPlayer().getUniqueId())) {
                    serverConnection.sendPluginMessage(customChannel, stringToByteArray("Mute;muted;" + e.getPlayer().getUniqueId()));
                }
            });


        });

        createChannel("global", "global");
        CommandManager commandManager = proxy.getCommandManager();

        commandManager.register("message", new Message(), "msg", "tell", "whisper", "w", "m");
        commandManager.register("reply", new Reply(), "r");
        commandManager.register("verify", new Verify());
        commandManager.register("mute", new MuteCommand());
        commandManager.register("unmute", new UnmuteCommand());

        commandManager.register(commandManager.metaBuilder("transfer")
                .plugin(this)
                .build(), new MovePlayer(proxy));
        commandManager.register(commandManager.metaBuilder("moveall")
                .plugin(this)
                .build(), new MoveAll(proxy));
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public MiniMessage getMiniMessage() {
        return this.miniMessage;
    }


}
