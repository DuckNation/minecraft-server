package io.github.haappi.duckvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github.haappi.duckvelocity.Chat.SendDiscordHandler;
import io.github.haappi.duckvelocity.Message.Message;
import io.github.haappi.duckvelocity.Message.Reply;
import io.github.haappi.duckvelocity.PluginListener.MessageListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import static io.github.haappi.duckvelocity.Chat.ChannelManager.createChannel;
import static io.github.haappi.duckvelocity.Config.checkConfig;

@Plugin(
        id = "duck-velocity",
        name = "DuckVelocity",
        version = BuildConstants.VERSION,
        authors = {"haappi"}
)
public class DuckVelocity {
    public static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static DuckVelocity instance;
    private final ChannelIdentifier customChannel =
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

        createChannel("global", "global");
        CommandManager commandManager = proxy.getCommandManager();

        commandManager.register("message", new Message(), "msg", "tell", "whisper", "w", "m");
        commandManager.register("reply", new Reply(), "r");
        commandManager.register("verify", new Verify());
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public MiniMessage getMiniMessage() {
        return this.miniMessage;
    }


}
