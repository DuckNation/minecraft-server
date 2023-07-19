package io.github.haappi.duckvelocity.Mute;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

import static io.github.haappi.duckvelocity.DuckVelocity.customChannel;
import static io.github.haappi.duckvelocity.Mute.MuteCommand.mutedNoobs;
import static io.github.haappi.duckvelocity.Utils.getPlayersFromInvocation;
import static io.github.haappi.duckvelocity.Utils.stringToByteArray;

public class UnmuteCommand implements SimpleCommand {

    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        if (invocation.arguments().length < 1) {
            invocation.source().sendMessage(Component.text("Usage: /unmute <player>", NamedTextColor.RED));
            return;
        }
        Player victim;
        try {
            victim = DuckVelocity.getInstance().getProxy().matchPlayer(invocation.arguments()[0]).stream().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            invocation.source().sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }


        mutedNoobs.remove(victim.getUniqueId());

        victim.sendMessage(Component.text("You have been unmuted"));
        invocation.source().sendMessage(Component.text("Unmuted " + victim.getUsername(), NamedTextColor.RED));

        ServerConnection conn = victim.getCurrentServer().isPresent() ? victim.getCurrentServer().get() : null;
        if (conn == null) {
            return;
        }
        conn.sendPluginMessage(customChannel, stringToByteArray("Mute;unmuted;" + victim.getUniqueId()));
    }


    @Override
    public List<String> suggest(SimpleCommand.Invocation invocation) {
        return getPlayersFromInvocation(invocation);
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("duck.mute_players");
    }
}
