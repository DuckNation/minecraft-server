package io.github.haappi.duckvelocity.PluginListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import io.github.haappi.duckvelocity.Chat.ChatHandler;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.TimeUnit;

public class MessageListener {

    private final ChannelIdentifier identifier;

    public MessageListener(ChannelIdentifier identifier) {
        this.identifier = identifier;
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (event.getIdentifier().equals(identifier)) {
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            if (event.getSource() instanceof Player player) {
                player.disconnect(Component.text("You attempted to send a plugin message?", NamedTextColor.RED));
            }

            if (event.getSource() instanceof ServerConnection) {
                ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
                String channel = in.readUTF();
                String action = in.readUTF();
                String args = "";
                try {
                    args = in.readUTF();

                    try {
                        while (true) {
                            args += ";" + in.readUTF();
                        }
                    } catch (Exception ignored) {

                    }
                } catch (Exception ignored) {

                }


                switch (channel) {
                    case "Chat" -> ChatHandler.handle(action, args.split(";"));
                    case "Server" -> {
                        Player player = ((ServerConnection) event.getSource()).getPlayer();
                        DuckVelocity.getInstance().getProxy().getServer(action).ifPresent(val -> {
                                player.sendMessage(Component.text("Sending you to ", NamedTextColor.AQUA).append(Component.text(action, NamedTextColor.GOLD)));
                            DuckVelocity.getInstance().getProxy().getScheduler().buildTask(DuckVelocity.getInstance(), () -> player.createConnectionRequest(val).fireAndForget()).delay(2, TimeUnit.SECONDS).schedule();

                    });
                    }
                    case "Message" -> {
                        Player player = ((ServerConnection) event.getSource()).getPlayer();
                        player.sendMessage(DuckVelocity.getInstance().getMiniMessage().deserialize(action));
                    }
                    default -> System.out.println("Unknown channel: " + channel + " args: " + args);
                }
            }

        }

    }


}