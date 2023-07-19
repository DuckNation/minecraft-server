package io.github.haappi.duckvelocity.ServerSwitcher;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import io.github.haappi.duckvelocity.DuckVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ServerSwitcher {
    public static HashMap<UUID, ScheduledTask> shutUpStopSpamming = new HashMap<>();

    public static void handle(Player player, String server) {
        DuckVelocity.getInstance().getProxy().getServer(server).ifPresent(val -> {
            if (!pingServer(server)) {
                if (shutUpStopSpamming.containsKey(player.getUniqueId())) {
                    player.sendMessage(Component.text("You already have a connection request for a server.", NamedTextColor.RED));
                    return;
                }

                player.sendMessage(DuckVelocity.getInstance().getMiniMessage().deserialize(
                        String.format("<red>Server <gold>%s</gold> you selected is currently offline. Please wait <aqua>60</aqua> seconds as it's being loaded.",
                                server)
                ));
                HttpGet get = new HttpGet("https://quack.boo/servers/" + server);
                try {
                    DuckVelocity.httpClient.execute(get);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ScheduledTask task;

                task = DuckVelocity.getInstance().getProxy().getScheduler().buildTask(DuckVelocity.getInstance(), () -> {
                    if (pingServer(server)) {
                        player.sendMessage(Component.text("Sending you to ", NamedTextColor.AQUA).append(Component.text(server, NamedTextColor.GOLD)));
                        DuckVelocity.getInstance().getProxy().getScheduler().buildTask(DuckVelocity.getInstance(), () -> player.createConnectionRequest(val).fireAndForget()
                        ).delay(3, TimeUnit.SECONDS).schedule();
                        shutUpStopSpamming.get(player.getUniqueId()).cancel();
                        shutUpStopSpamming.remove(player.getUniqueId());
                    }
                }).repeat(5, TimeUnit.SECONDS).schedule();

                shutUpStopSpamming.put(player.getUniqueId(), task);
                return;
            }
            player.sendMessage(Component.text("Sending you to ", NamedTextColor.AQUA).append(Component.text(server, NamedTextColor.GOLD)));
            DuckVelocity.getInstance().getProxy().getScheduler().buildTask(DuckVelocity.getInstance(), () ->
                    player.createConnectionRequest(val).fireAndForget()
            ).delay(2, TimeUnit.SECONDS).schedule();
        });
    }

    public static boolean pingServer(String server) {
        try {
            Socket socket = new Socket();
            InetSocketAddress address = new InetSocketAddress(server, 25565);
            socket.connect(address, 5000);

            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
