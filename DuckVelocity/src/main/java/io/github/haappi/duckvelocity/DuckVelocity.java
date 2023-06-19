package io.github.haappi.duckvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.haappi.duckvelocity.Chat.SendDiscordHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

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

    private ProxyServer server;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();


    @Inject
    private Logger logger;

    @Inject
    public DuckVelocity(ProxyServer proxy) {
        this.server = proxy;
        instance = this;
        checkConfig();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new SendDiscordHandler());
    }

    public static DuckVelocity getInstance() {
        return instance;
    }

    public ProxyServer getProxy() {
        return this.server;
    }

    public MiniMessage getMiniMessage() {
        return this.miniMessage;
    }



}
