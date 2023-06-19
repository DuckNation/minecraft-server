package io.github.haappi.duckpaper.chat;

import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.event.Listener;

import java.net.http.WebSocket;

public class Handler implements Listener {

    public Handler(DuckPaper instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);

        WebSocketClient client = new WebSocketClient();
        client.connect("wss://quack.boo/internal/api/wss/to-server?key=penis");
//        client.sendMessage("hey");
    }

}
