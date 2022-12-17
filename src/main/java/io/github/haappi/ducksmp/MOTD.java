package io.github.haappi.ducksmp;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.util.Objects;

import static io.github.haappi.ducksmp.DuckSMP.miniMessage;

public class MOTD implements Listener {

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        String motd = "<bold><gold>Duck</gold><yellow>Nation</yellow><green> SMP</green></bold>";
        event.motd(miniMessage.deserialize(motd));

        Component component = miniMessage.deserialize(motd + getPlayerMOTDFromIP(event.getAddress().getHostAddress()));
        event.motd(component);

    }

    private String getPlayerMOTDFromIP(String hostAddress) {
        String encrypted = Encryption.encrypt(hostAddress);

        try (Jedis jedis = DuckSMP.getSingleton().getJedisPool().getResource()) {
            jedis.auth(DuckSMP.getSingleton().getConfig().getString("redisPassword"));
            String motd = jedis.get("motd:" + encrypted);
            return Objects.requireNonNullElse(motd, "");
        }
    }
}
