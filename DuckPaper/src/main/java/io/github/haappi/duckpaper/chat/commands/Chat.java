package io.github.haappi.duckpaper.chat.commands;

import io.github.haappi.duckpaper.DuckPaper;
import io.github.haappi.duckpaper.chat.ChatHandler;
import io.github.haappi.duckpaper.utils.Command;
import io.github.haappi.duckpaper.utils.Config;
import io.github.haappi.duckpaper.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.haappi.duckpaper.DuckPaper.PLUGIN_CHANNEL;
import static io.github.haappi.duckpaper.chat.ChatHandler.currentChannel;
import static io.github.haappi.duckpaper.chat.ChatHandler.getAllowedChannels;
import static io.github.haappi.duckpaper.utils.Utils.*;

public class Chat extends Command {
    /**
     * // todo implement <block | unblock | manage | delete> properly
     */
    private final DuckPaper plugin;
    ArrayList<String> subCommnands = new ArrayList<>(Arrays.asList("create", "join", "leave", "mute", "unmute", "block", "unblock", "manage", "delete"));

    public Chat(DuckPaper plugin) {
        super("chat", "Chat related commands", "/chat <subcommand>", List.of("c"), "duck.chat");
        this.plugin = plugin;
    }


    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return subCommnands;
        }

        if (args.length == 1) {
            List<String> list = new ArrayList<>(subCommnands);
            list.addAll(getAllowedChannels(player.getUniqueId()).stream().filter(entry -> entry.toLowerCase().startsWith(args[0])).toList());
            return list;
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "leave", "mute", "unmute", "manage" -> {
                    return getAllowedChannels(player.getUniqueId()).stream().filter(entry -> entry.toLowerCase().startsWith(args[1])).collect(Collectors.toList());
                }
            }
        }
        return List.of();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(usage());
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }

        if (!subCommnands.contains(args[0])) {
            if (getAllowedChannels(player.getUniqueId()).contains(args[0]) || args[0].equals("global")) {
                if (args.length > 1) {
                    String oldChannel = currentChannel.get(player.getUniqueId());
                    currentChannel.put(player.getUniqueId(), args[0]);
                    Component message = player.name().append(Component.text(": "));
                    message = message.append(Component.text(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    ChatHandler.sendMessage(player, message);
                    currentChannel.put(player.getUniqueId(), oldChannel);
                    return true;
                } else {
                    currentChannel.put(player.getUniqueId(), args[0]);
                    player.sendMessage(Component.text("You are now talking in " + args[0], NamedTextColor.GREEN));
                }
            } else {
                player.sendMessage(Component.text("You do not have access to that channel!", NamedTextColor.RED));
                return false;
            }
        }
        String type = args[0];
        args = removeFirstElement(args);

        switch (type) {
            case "create" -> {
                return createHandler(player, args);
            }
            case "join" -> {
                return joinHandler(player, args);
            }
            case "leave" -> {
                return leaveHandler(player, args);
            }
            case "mute" -> {
                return muteHandler(player, args);
            }
            case "unmute" -> {
                return unmuteHandler(player, args);
            }
            case "manage" -> {
                return manageHandler(player, args);
            }
            case "delete" -> {
                return deleteHandler(player, args);
            }
            case "block" -> {
                return blockHandler(player, args);
            }
            case "unblock" -> {
                return unblockHandler(player, args);
            }
        }

        return false;
    }

    private boolean unblockHandler(Player player, String[] args) {
        player.sendMessage(Component.text("To be implemented...", NamedTextColor.RED));
        return false;
    }

    private boolean blockHandler(Player player, String[] args) {
        player.sendMessage(Component.text("To be implemented...", NamedTextColor.RED));
        return false;
    }

    private boolean deleteHandler(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(Component.text("This command is operator only for the time being!", NamedTextColor.RED));
            return false;
        }
        return true;
    }

    private boolean createHandler(Player player, String[] args) {
        String channelName = args[0];
        String password;
        if (args.length == 2) {
            password = args[1];
        } else {
            password = null;
        }

        Utils.scheduleNextTickAsync(() -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("uuid", player.getUniqueId());
            params.put("name", channelName);
            if (password != null) {
                params.put("password", password);
            }
            HttpPost req = new HttpPost(Config.API_BASE_URL + "/chats/create?key=" + Config.API_KEY + Utils.createQueryString(params));

            List<Object> returnType = performHttpRequest(req);

            JSONObject object = (JSONObject) returnType.get(1);

            if ((Integer) returnType.get(0) != 200) {
                player.sendMessage(plugin.getMiniMessage().deserialize(object.getString("detail")));
                return;
            }

            player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                    String.format("Chat;init;%s;%s;%s", object.get("name"), object.get("_id"), player.getUniqueId())
            ));

            Bukkit.getScheduler().runTaskLater(DuckPaper.getInstance(), () -> {
                player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                        String.format("Chat;subscribe;%s;%s;%s", object.get("name"), object.get("_id"), player.getUniqueId())
                ));

                player.sendMessage(Component.text("Successfully created channel!", NamedTextColor.GREEN));
            }, 100L);

            getAllowedChannels(player.getUniqueId()).add(object.getString("name"));
        });

        return true;
    }

    private boolean joinHandler(Player player, String[] args) {
        String channelName = args[0];
        String password = null;
        if (args.length == 2) {
            password = args[1];
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("uuid", player.getUniqueId());
        params.put("name", channelName);
        if (password != null) {
            params.put("password", password);
        }

        HttpPost req = new HttpPost(Config.API_BASE_URL + "/chats/join?key=" + Config.API_KEY + Utils.createQueryString(params));

        List<Object> returnType = performHttpRequest(req);

        JSONObject object = (JSONObject) returnType.get(1);

        if ((Integer) returnType.get(0) != 200) {
            player.sendMessage(plugin.getMiniMessage().deserialize(object.getString("detail")));
            return false;
        }

        player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                String.format("Chat;subscribe;%s;%s;%s", object.get("name"), object.get("_id"), player.getUniqueId())
        ));
        player.sendMessage(Component.text("Successfully joined channel!", NamedTextColor.GREEN));
        getAllowedChannels(player.getUniqueId()).add(object.getString("name"));

        return true;
    }

    private boolean leaveHandler(Player player, String[] args) {
        String channelName = args[0];

        HashMap<String, Object> params = new HashMap<>();
        params.put("uuid", player.getUniqueId());
        params.put("name", channelName);

        HttpDelete req = new HttpDelete(Config.API_BASE_URL + "/chats/leave?key=" + Config.API_KEY + Utils.createQueryString(params));

        List<Object> returnType = performHttpRequest(req);
        JSONObject object = (JSONObject) returnType.get(1);

        if ((Integer) returnType.get(0) != 200) {
            player.sendMessage(plugin.getMiniMessage().deserialize(object.getString("detail")));
            return false;
        }

        player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                String.format("Chat;unsubscribe;%s;%s;%s", channelName, object.get("_id"), player.getUniqueId())
        ));
        player.sendMessage(plugin.getMiniMessage().deserialize(object.getString("message")));
        getAllowedChannels(player.getUniqueId()).remove(object.getString("name"));

        return true;
    }

    private boolean muteHandler(Player player, String[] args) {
        String channelName = args[0];

        player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                String.format("Chat;mute;%s;%s", channelName, player.getUniqueId())
        ));
        player.sendMessage(Component.text("Muted: " + channelName, NamedTextColor.GREEN));

        return true;
    }

    private boolean unmuteHandler(Player player, String[] args) {
        player.sendPluginMessage(plugin, PLUGIN_CHANNEL, stringToByteArray(
                String.format("Chat;unmute;%s;%s", args[0], player.getUniqueId())
        ));

        return true;
    }

    private boolean manageHandler(Player player, String[] args) {
        player.sendMessage(Component.text("To be implemented...", NamedTextColor.RED));
        return false;
    }


    @Override
    public Component usage() {
        Component msg =
                Component.text("Usage: /chat <create|join|leave|mute|unmute|manage> [channel]", NamedTextColor.RED)
                        .append(Component.newline())
                        .append(Component.text("Create a new channel: /chat create <channel> [password]", NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Join a channel: /chat join <channel> [password]", NamedTextColor.BLUE)).append(Component.newline())
                        .append(Component.text("Leave a channel: /chat leave <channel>", NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Mute a channel: /chat mute <channel>", NamedTextColor.BLUE)).append(Component.newline())
                        .append(Component.text("Unmute a channel: /chat unmute <channel>", NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Manage a channel: /chat manage <channel>", NamedTextColor.BLUE));
        return msg;

    }
}
