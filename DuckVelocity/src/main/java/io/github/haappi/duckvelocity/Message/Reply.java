package io.github.haappi.duckvelocity.Message;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Reply implements SimpleCommand {
    public static void doMessage(Invocation invocation, Player toMessage, String strMessage) {
        Component invoker;
        if (invocation.source() instanceof Player player) {
            invoker = Component.text(player.getUsername(), NamedTextColor.YELLOW);
        } else {
            invoker = Component.text("Console", NamedTextColor.YELLOW);
        }

        Component message = invoker.append(Component.text(" -> ", NamedTextColor.GRAY).append(Component.text(toMessage.getUsername(), NamedTextColor.YELLOW).append(Component.text(": ", NamedTextColor.GRAY)))).append(Component.text(strMessage, NamedTextColor.GREEN));


        toMessage.sendMessage(message);
        invocation.source().sendMessage(message);
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length < 1) {
            invocation.source().sendMessage(Component.text("Usage: /reply <message>", NamedTextColor.RED));
            return;
        }
        Player toMessage = Message.lastMessaged.get((Player) invocation.source());
        if (toMessage == null) {
            invocation.source().sendMessage(Component.text("No one's messaged you recently.", NamedTextColor.RED));
            return;
        }

        doMessage(invocation, toMessage, String.join(" ", invocation.arguments()));

        if (invocation.source() instanceof Player) {
            Message.lastMessaged.put((Player) invocation.source(), toMessage);
            Message.lastMessaged.put(toMessage, (Player) invocation.source());
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("duck.message");
    }
}
