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
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import io.github.haappi.duckpaper.chat.commands.Chat;
import static io.github.haappi.duckpaper.DuckPaper.*;
import static io.github.haappi.duckpaper.utils.CommandRelated.registerNewCommand;
import static io.github.haappi.duckpaper.utils.Utils.stringToByteArray;

public class ChatHandler implements Listener {
    public static final ConcurrentHashMap<UUID, String> currentChannel = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, ArrayList<String>> allowedChannels = new ConcurrentHashMap<>();
    private final DuckPaper plugin;

    public ChatHandler(DuckPaper instance) {
        this.plugin = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);

        registerNewCommand(new Chat(instance));
    }

    public static ArrayList<String> getAllowedChannels(UUID uuid) {
        if (!allowedChannels.containsKey(uuid)) {
            ArrayList<String> channels = new ArrayList<>();
            channels.add("global");
            allowedChannels.put(uuid, channels);
            return channels;
        }
        return allowedChannels.get(uuid);
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        allowedChannels.remove(event.getPlayer().getUniqueId());
    }

    private Component formatForAPI(Player player, Component msg) {
        return Component.text()
                .append(player.name().color(NamedTextColor.GREEN))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(msg).build();
    }

    public static boolean sendMessage(Player player, Component message) {
        String serialized = DuckPaper.getInstance().getMiniMessage().serialize(message);

        String channel;

        channel = currentChannel.putIfAbsent(player.getUniqueId(), "global") == null ? "global" : currentChannel.get(player.getUniqueId());

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Chat");
        out.writeUTF("message");
        out.writeUTF(channel);
        out.writeUTF(serialized);

        player.sendPluginMessage(DuckPaper.getInstance(), PLUGIN_CHANNEL, out.toByteArray());

        return !channel.equals("global");
    }

    private void loadChatChannels(Player player) {
        Utils.runTaskLater(() -> {
            HttpGet get = new HttpGet(Config.API_BASE_URL + "/chats/better-get?player_uuid=" + player.getUniqueId() + "&key=" + Config.API_KEY);

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
                ArrayList<String> channels = getAllowedChannels(player.getUniqueId());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String key = jsonObject.getString("name");
                    String value = jsonObject.getString("uuid");
                    channels.add(key);

                    player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                            String.format("Chat;subscribe;%s;%s;%s", key, value, player.getUniqueId())
                    ));
                }
                player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                        String.format("Chat;subscribe;global;global;%s", player.getUniqueId())
                ));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 3000, true);
    }

}
