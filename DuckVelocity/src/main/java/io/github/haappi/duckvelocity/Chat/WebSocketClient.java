package io.github.haappi.duckvelocity.Chat;

import io.github.haappi.duckvelocity.DuckVelocity;
import org.glassfish.tyrus.client.ClientManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static io.github.haappi.duckvelocity.Chat.SendDiscordHandler.handle;

@ClientEndpoint
public class WebSocketClient {

    public interface MessageCallback {
        void onMessageReceived(Types type, String message);
    }

    protected WebSocketContainer container;
    protected Session userSession = null;
    private final MessageCallback callback;

    public WebSocketClient(MessageCallback callback) {
        this.callback = callback;
        container = ClientManager.createClient();
    }

    public void connect(String sServer) {
        try {
            userSession = container.connectToServer(this, new URI(sServer));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            userSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        String[] message = msg.split(";", 2);
        if (message.length != 2) return;
        Types type = Types.getByValue(message[0]);
        if (type == null) {
            throw new IllegalArgumentException("Unknown type: " + message[0]);
        }
        String content = message[1];

        callback.onMessageReceived(type, content);
    }

    public void disconnect()  {
        try {
            userSession.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}