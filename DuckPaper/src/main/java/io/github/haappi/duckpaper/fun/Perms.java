package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.utils.Config;
import io.github.haappi.duckpaper.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static io.github.haappi.duckpaper.DuckPaper.httpClient;

public class Perms implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", event.getPlayer().getUniqueId().toString());
        params.put("delete", true);

        HttpGet get = new HttpGet(Config.API_BASE_URL + "/info/permissions?" + Utils.createQueryString(params));

        try {
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());
            if (statusCode != 200) {
                return;
            }
            JSONArray obj = new JSONArray(stringResponse);
            ArrayList<String> data = new ArrayList<>();

            for (int i = 0; i < obj.length(); i++) {
                data.add(obj.getString(i));
            }

            for (String cmd : data) {
                event.getPlayer().sendMessage(cmd);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            if (data.size() > 1) event.getPlayer().sendMessage("added the above perms, contact haappi if you didnt receive them with a screenshot.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
