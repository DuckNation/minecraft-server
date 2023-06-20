package io.github.haappi.duckpaper.chat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.haappi.duckpaper.DuckPaper;
import io.github.haappi.duckpaper.utils.Config;
import io.github.haappi.duckpaper.utils.Utils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.haappi.duckpaper.DuckPaper.*;
import static io.github.haappi.duckpaper.utils.Utils.stringToByteArray;

public class ChatHandler implements Listener {
    public static final ConcurrentHashMap<UUID, String> channels = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<UUID, ArrayList<String>> allowedChannels = new ConcurrentHashMap<>();
    private final DuckPaper plugin;

    public ChatHandler(DuckPaper instance) {
        this.plugin = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);

    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void format(AsyncChatEvent event) {
        event.renderer((source, sourceDisplayName, message, viewer) ->
                Component.text()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(sourceDisplayName).color(event.getPlayer().isOp() ? NamedTextColor.GOLD : NamedTextColor.GREEN)
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(message.color(NamedTextColor.WHITE))
                .build());
    }

    @EventHandler
    public void onMessage(AsyncChatEvent event) {
        event.setCancelled(sendMessage(event.getPlayer(), formatForAPI(event.getPlayer(), event.message())));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        loadChatChannels(event.getPlayer());
    }

    private Component formatForAPI(Player player, Component msg) {
        return Component.text()
                .append(player.name().color(NamedTextColor.GREEN))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(msg).build();
    }

    private boolean sendMessage(Player player, Component message) {
        String serialized = plugin.getMiniMessage().serialize(message);

        String channel;

        if (player.getDisplayName().equals("saaddi")) {

            channel = channels.putIfAbsent(player.getUniqueId(), "okk") == null ? "okk" : channels.get(player.getUniqueId());
        } else {
            channel = channels.putIfAbsent(player.getUniqueId(), "global") == null ? "global" : channels.get(player.getUniqueId());

        }


        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Chat");
        out.writeUTF("message");
        out.writeUTF(channel);
        out.writeUTF(serialized);

        player.sendPluginMessage(plugin, PLUGIN_CHANNEL, out.toByteArray());

        return !channel.equals("global");
    }

    private void loadChatChannels(Player player) {
        Utils.scheduleNextTickAsync(() -> {
            HttpGet get = new HttpGet(Config.API_BASE_URL + "/chats/get?uuid=" + player.getUniqueId() + "&key=" + Config.API_KEY);

            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                String stringResponse = EntityUtils.toString(response.getEntity());
                if (statusCode != 200) {
                    player.sendMessage(Component.text("Failed to load your chat channels.", NamedTextColor.RED));
                    System.out.println(stringResponse);
                    return;
                }
                JSONArray jsonArray = new JSONArray(stringResponse);
                player.sendActionBar(miniMessage.deserialize("<gray>Found <green>" + jsonArray.length() + "</green> chat channels."));
                ArrayList<String> channels = allowedChannels.putIfAbsent(player.getUniqueId(), new ArrayList<>()) == null ? allowedChannels.get(player.getUniqueId()) : allowedChannels.get(player.getUniqueId());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = jsonObject.getString(key);
                        channels.add(key);

                        player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                                String.format("Chat;subscribe;%s;%s;%s", key, value, player.getUniqueId())
                        ));
                    }
                }
                System.out.println(allowedChannels.get(player.getUniqueId()));
                player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                        String.format("Chat;subscribe;global;global;%s", player.getUniqueId())
                ));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
