package io.github.haappi.duckvelocity.Chat;

import com.velocitypowered.api.proxy.Player;
import io.github.haappi.duckvelocity.Config;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel {
    private final String id;
    private final String name;
    private Audience subscribedPlayers;
    private WebSocketClient client;

    public Channel(String id, String name) {
        this.id = id;
        this.name = name;
        this.subscribedPlayers = Audience.empty();
        this.client = new WebSocketClient((type, message) -> {
            switch (type) {
                case PLAYER_CHAT -> {
                    String msg = message.replace("Discord", this.name);
                    this.subscribedPlayers.sendMessage(DuckVelocity.getInstance().getMiniMessage().deserialize(msg));
                }
            }
        });
        this.client.connect(Config.WSS_BASE_URL + "/wss/" + id + "?key=" + Config.API_KEY);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Audience getSubscribedPlayers() {
        return subscribedPlayers;
    }

    public Audience subscribePlayer(@Nullable Player player) {
        if (player == null) {
            return subscribedPlayers;
        }
//        AtomicBoolean inChat = new AtomicBoolean(false);
//        subscribedPlayers.forEachAudience(aud -> {
//            if (Objects.equals(String.valueOf(aud.get(Identity.UUID).orElse(null)), player.getUniqueId().toString())) {
//                inChat.set(true);
//            }
//        });
//        if (inChat.get()) {
//            return subscribedPlayers;
//        }
        subscribedPlayers = Audience.audience(subscribedPlayers, player);
        if (!this.name.equals("global")) {
            if (player.hasPermission("duck.silent_join")) {
                return subscribedPlayers;
            }
            subscribedPlayers.sendMessage(Component.text(player.getUsername() + " has connected to the channel: " + this.name, NamedTextColor.GREEN));
        }
        return subscribedPlayers;
    }

    public Audience unsubscribePlayer(Player player) {
        List<Audience> subscribed = new ArrayList<>();
        subscribedPlayers.forEachAudience(audience -> {
            if (!Objects.equals(String.valueOf(audience.get(Identity.UUID).orElse(null)), player.getUniqueId().toString())) {
                if (!subscribed.contains(audience)) {
                    subscribed.add(audience);
                }
            }
        });

        if (subscribed.isEmpty()) {
            subscribedPlayers = Audience.empty();
            client.disconnect();
            ChannelManager.removeChannel(this);
        } else {
            if (!this.name.equals("global")) {
                if (player.hasPermission("duck.silent_join")) {
                    return subscribedPlayers;
                }
            }
        }
        return subscribedPlayers;
    }

    public void sendMessage(String message) {
        MiniMessage mm = DuckVelocity.getInstance().getMiniMessage();

        client.sendMessage("chat;" + PlainTextComponentSerializer.plainText().serialize(mm.deserialize(message)));
        System.out.println(this.name + " " + message);

        message = String.format("<gray><i>(%s)</i></gray> ", this.name) + message.replaceAll("gold", "yellow").replaceAll("green", "yellow");


        if (!this.name.equals("global")) {
            subscribedPlayers.sendMessage(Component.text().append(mm.deserialize(message)));
        }
    }

    public void sendMessage(Types type, String message) {
        client.sendMessage(type.toString() + ";" + message);
    }
}
