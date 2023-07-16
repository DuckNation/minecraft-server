package io.github.haappi.duckvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github.haappi.duckvelocity.Chat.Channel;
import io.github.haappi.duckvelocity.Chat.ChannelManager;
import io.github.haappi.duckvelocity.Chat.SendDiscordHandler;
import io.github.haappi.duckvelocity.Message.Message;
import io.github.haappi.duckvelocity.Message.Reply;
import io.github.haappi.duckvelocity.Mute.Mute;
import io.github.haappi.duckvelocity.Mute.MuteCommand;
import io.github.haappi.duckvelocity.Mute.UnmuteCommand;
import io.github.haappi.duckvelocity.PluginListener.MessageListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

import static io.github.haappi.duckvelocity.Chat.ChannelManager.createChannel;
import static io.github.haappi.duckvelocity.Config.checkConfig;
import static io.github.haappi.duckvelocity.Mute.MuteCommand.mutedNoobs;
import static io.github.haappi.duckvelocity.Utils.stringToByteArray;

@Plugin(
        id = "duck-velocity",
        name = "DuckVelocity",
        version = BuildConstants.VERSION,
        authors = {"haappi"}
)
public class DuckVelocity {
    public static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static DuckVelocity instance;
    public static final ChannelIdentifier customChannel =
            MinecraftChannelIdentifier.from("duck:messenger");
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private ProxyServer proxy;
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
        proxy.getEventManager().register(this, new SendDiscordHandler());

        proxy.getChannelRegistrar().register(customChannel);
        proxy.getEventManager().register(this, new MessageListener(customChannel));

        proxy.getEventManager().register(this, ServerPreConnectEvent.class, e -> {
            for (Channel channels : ChannelManager.channels.values()) {
                channels.unsubscribePlayer(e.getPlayer());
            }
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
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public MiniMessage getMiniMessage() {
        return this.miniMessage;
    }


}
