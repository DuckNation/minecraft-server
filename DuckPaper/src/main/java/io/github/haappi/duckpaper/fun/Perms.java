package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.utils.Config;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static io.github.haappi.duckpaper.DuckPaper.httpClient;

public class Perms implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        HttpGet get = new HttpGet(Config.API_BASE_URL + "/info/stats?uid=" + event.getPlayer().getUniqueId() + "&key=" + Config.API_KEY);

        try {
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());
            if (statusCode != 200) {
                return;
            }
            JSONObject obj = new JSONObject(stringResponse);
            ArrayList<String> arrayList;

            if (obj.has("permissions")) {
                arrayList = (ArrayList<String>) obj.get("permissions");
            } else {
                return;
            }

            for (String cmd : arrayList) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
