package io.github.haappi.duckvelocity.Message;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static io.github.haappi.duckvelocity.Message.MessageObject.getLastMessaged;
import static io.github.haappi.duckvelocity.Message.MessageObject.updateLastMessage;
import static io.github.haappi.duckvelocity.Mute.MuteCommand.mutedNoobs;

public class Reply implements SimpleCommand {
    public Reply() {

    }

    public static void doMessage(Invocation invocation, Player toMessage, String strMessage) {
        Component invoker;
        if (invocation.source() instanceof Player player) {
            invoker = Component.text(player.getUsername(), NamedTextColor.YELLOW);
        } else {
            invoker = Component.text("Console", NamedTextColor.YELLOW);
        }

        Component message = invoker.append(Component.text(" -> ", NamedTextColor.GRAY).append(Component.text(toMessage.getUsername(), NamedTextColor.YELLOW).append(Component.text(":", NamedTextColor.GRAY)))).append(Component.text(strMessage, NamedTextColor.LIGHT_PURPLE));

        toMessage.sendMessage(message);
        invocation.source().sendMessage(message);

        if (invocation instanceof Player player) {
            updateLastMessage(player, toMessage);
        }
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length < 1) {
            invocation.source().sendMessage(Component.text("Usage: /reply <message>", NamedTextColor.RED));
            return;
        }
        if (invocation.source() instanceof Player player) {
            if (mutedNoobs.containsKey(player.getUniqueId())) {
                player.sendMessage(Component.text("You are muted.", NamedTextColor.RED));
                return;
            }
        } else {
            invocation.source().sendMessage(Component.text("You can't really use this command...", NamedTextColor.RED));
        }
        Player player = invocation.source() != null ? (Player) invocation.source() : null;
        Player toMessage = getLastMessaged(player);
        if (toMessage == null) {
            invocation.source().sendMessage(Component.text("No one has messaged you recently.", NamedTextColor.RED));
            return;
        }

        doMessage(invocation, toMessage, String.join(" ", invocation.arguments()));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("duck.message");
    }
}
