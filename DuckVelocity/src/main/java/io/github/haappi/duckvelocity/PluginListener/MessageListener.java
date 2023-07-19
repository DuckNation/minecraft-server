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
import io.github.haappi.duckvelocity.Mute.Mute;
import io.github.haappi.duckvelocity.Mute.MuteCommand;
import io.github.haappi.duckvelocity.ServerSwitcher.ServerSwitcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static io.github.haappi.duckvelocity.Utils.parseMillisToTime;

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
                        ServerSwitcher.handle(((ServerConnection) event.getSource()).getPlayer(), action);
                    }
                    case "Message" -> {
                        Player player = ((ServerConnection) event.getSource()).getPlayer();
                        player.sendMessage(DuckVelocity.getInstance().getMiniMessage().deserialize(action));
                    }
                    case "WhyMute" -> {
                        Player player = ((ServerConnection) event.getSource()).getPlayer();
                        Mute mute = MuteCommand.mutedNoobs.get(player.getUniqueId());

                        if (mute != null) {
                            player.sendMessage(Component.text("You are muted for: ", NamedTextColor.RED).append(Component.text(mute.getReason(), NamedTextColor.GOLD)));
                            player.sendMessage(Component.text("You are muted until: ", NamedTextColor.GOLD).append(Component.text(parseMillisToTime(mute.getMuteTime() - System.currentTimeMillis()), NamedTextColor.GOLD)));
                        }
                    }
                    default -> System.out.println("Unknown channel: " + channel + " args: " + args);
                }
            }

        }

    }


}