package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.haappi.ducksmp.utils.Utils.canRunAway;

public class TPA extends BukkitCommand implements Listener {

    public static final ConcurrentHashMap<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<UUID, Long> requestExpiry = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Integer> tasks = new ConcurrentHashMap<>();
    private final DuckSMP plugin;

    public TPA(String name) {
        super(name);
        this.plugin = DuckSMP.getInstance();
        this.setDescription("Teleport to another player");
        this.setUsage("/tpa <player_name | accept | deny>");
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> requestExpiry.forEach((uuid, time) -> {
            if (System.currentTimeMillis() > time) {
                requestExpiry.remove(uuid);
                UUID target = tpaRequests.remove(uuid);
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.sendMessage(Component.text("Outgoing teleport request expired.", NamedTextColor.GRAY));
                }
                Player t = Bukkit.getPlayer(target);
                if (t != null) {
                    t.sendMessage(Component.text("Incoming teleport request expired.", NamedTextColor.GRAY));
                }
            }
        }), 0, 20);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command", NamedTextColor.RED));
            return true;
        }
        if (!canRunAway(player)) return true;
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /tpa <player_name | accept | deny>", NamedTextColor.RED));
            return true;
        }


        switch (args[0].toLowerCase()) {
            case "accept" -> handleAccept(player, args);
            case "deny" -> handleDeny(player, args);
            default -> {
                if (cooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                    sender.sendMessage(Component.text("You must wait " + (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000 + " seconds before using this command again.", NamedTextColor.RED));
                    return true;
                }
                cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 30)); // 30 seconds cooldown
                if (tpaRequests.containsKey(player.getUniqueId())) {
                    player.sendMessage(Component.text("You already have an outgoing request", NamedTextColor.RED));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                    return true;
                }
                if (target.getUniqueId() == player.getUniqueId()) {
                    player.sendMessage(Component.text("You cannot teleport to yourself", NamedTextColor.RED));
                    return true;
                }
                if (target.getWorld().getUID() != player.getWorld().getUID()) {
                    player.sendMessage(Component.text("You cannot teleport to players in different worlds", NamedTextColor.RED));
                    return true;
                }
                tpaRequests.put(player.getUniqueId(), target.getUniqueId());
                requestExpiry.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 15)); // 15 seconds
                target.sendMessage(Component.text(player.getName(), NamedTextColor.GOLD).append(Component.text(" wants to teleport to you", NamedTextColor.GREEN)));
                target.sendMessage(Component.text("Type ", NamedTextColor.GRAY).append(getAccept(player.getName())).append(Component.text(" to accept or ", NamedTextColor.GRAY).append(getDeny(player.getName())).append(Component.text(" to deny", NamedTextColor.GRAY))));
                player.sendMessage(Component.text("Request sent to ", NamedTextColor.GREEN).append(Component.text(target.getName(), NamedTextColor.GOLD)));
            }
        }
        return true;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (tasks.containsKey(event.getPlayer().getUniqueId())) {
            int taskID = tasks.get(event.getPlayer().getUniqueId());
            Bukkit.getScheduler().cancelTask(tasks.get(event.getPlayer().getUniqueId()));
            UUID uuid = getKeyFromValue(tasks, taskID);
            if (uuid != null) {
                tasks.remove(uuid);
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    target.sendMessage(Component.text("Teleport request expired. ", NamedTextColor.GRAY).append(Component.text(event.getPlayer().getName(), NamedTextColor.GOLD)).append(Component.text(" teleported!", NamedTextColor.GRAY)));
                }
            }
            event.getPlayer().sendMessage(Component.text("Teleport request expired. You teleported!", NamedTextColor.GRAY));
            requestExpiry.remove(event.getPlayer().getUniqueId());
            tpaRequests.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasExplicitlyChangedBlock()) {
            return;
        }
        if (tasks.containsKey(event.getPlayer().getUniqueId())) {
            int taskID = tasks.remove(event.getPlayer().getUniqueId());
            Bukkit.getScheduler().cancelTask(taskID);
            UUID uuid = getKeyFromValue(tasks, taskID);
            if (uuid != null) {
                tasks.remove(uuid);
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    target.sendMessage(Component.text("Teleport request expired. ", NamedTextColor.GRAY).append(Component.text(event.getPlayer().getName(), NamedTextColor.GOLD)).append(Component.text(" moved!", NamedTextColor.GRAY)));
                }
            }
            event.getPlayer().sendMessage(Component.text("Teleport request expired. You moved!", NamedTextColor.GRAY));
            requestExpiry.remove(event.getPlayer().getUniqueId());
            tpaRequests.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (tasks.containsKey(event.getPlayer().getUniqueId())) {
            int taskID = tasks.remove(event.getPlayer().getUniqueId());
            Bukkit.getScheduler().cancelTask(taskID);
            UUID uuid = getKeyFromValue(tasks, taskID);
            if (uuid != null) {
                tasks.remove(uuid);
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    target.sendMessage(Component.text("Teleport request expired. ", NamedTextColor.GRAY).append(Component.text(event.getPlayer().getName(), NamedTextColor.GOLD)).append(Component.text(" logged off!", NamedTextColor.GRAY)));
                }
            }
            requestExpiry.remove(event.getPlayer().getUniqueId());
            tpaRequests.remove(event.getPlayer().getUniqueId());
        }
    }

    private void handleAccept(Player player, String[] args) {
        if (!tpaRequests.containsValue(player.getUniqueId())) {
            player.sendMessage(Component.text("You have no teleport requests!", NamedTextColor.RED));
            return;
        }
        if (args.length == 1) {
            if (Collections.frequency(tpaRequests.values(), player.getUniqueId()) > 1) {
                player.sendMessage(Component.text("You have multiple teleport requests, please specify a username.", NamedTextColor.RED));
                return;
            }
            UUID uuid = getKeyFromValue(tpaRequests, player.getUniqueId());
            if (uuid == null) {
                player.sendMessage(Component.text("You have no teleport requests!", NamedTextColor.RED));
                return;
            }
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) {
                player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                return;
            }
            setupTPA(player, target);
        } else {
            // The target requested a specific person
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                return;
            }
            if (tpaRequests.get(target.getUniqueId()) != player.getUniqueId()) {
                player.sendMessage(Component.text(target.getName() + " hasn't sent a teleport request to you!", NamedTextColor.RED));
                return;
            }
            setupTPA(player, target);
        }
    }

    private void setupTPA(Player player, Player target) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int taskID = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                performTpa(target, player); // Managed to code it backwards & I'm too lazy to fix it.
            }, 20 * 8L).getTaskId();
            tasks.put(player.getUniqueId(), taskID);
            tasks.put(target.getUniqueId(), taskID);
        }, 20L * 2L); // 2 seconds leeway to move around.

        tpaRequests.remove(target.getUniqueId());  // Person who made the request originally
        requestExpiry.remove(target.getUniqueId()); // Person who made the request originally
        target.sendMessage(Component.text("Teleporting to " + player.getName() + ". ", NamedTextColor.GREEN).append(Component.text("Don't move for 10 seconds", NamedTextColor.RED)));
        player.sendMessage(Component.text(target.getName() + " is teleporting to you. ", NamedTextColor.GREEN).append(Component.text("Don't move for 10 seconds", NamedTextColor.RED)));
    }

    private void handleDeny(Player player, String[] args) {
        if (!tpaRequests.containsValue(player.getUniqueId())) {
            player.sendMessage(Component.text("You have no teleport requests!", NamedTextColor.RED));
            return;
        }
        if (args.length == 1) {
            if (Collections.frequency(tpaRequests.values(), player.getUniqueId()) > 1) {
                player.sendMessage(Component.text("You have multiple teleport requests, please specify a username.", NamedTextColor.RED));
                return;
            }
            UUID uuid = getKeyFromValue(tpaRequests, player.getUniqueId());
            if (uuid == null) {
                player.sendMessage(Component.text("You have no teleport requests!", NamedTextColor.RED));
                return;
            }
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) {
                player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                return;
            }
            tpaRequests.remove(target.getUniqueId());
            requestExpiry.remove(target.getUniqueId());
            target.sendMessage(Component.text(player.getName(), NamedTextColor.GOLD).append(Component.text(" has denied your teleport request", NamedTextColor.RED)));
            player.sendMessage(Component.text("Request denied", NamedTextColor.GREEN));
        } else {
            // The target requested a specific person
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                return;
            }
            if (tpaRequests.get(target.getUniqueId()) != player.getUniqueId()) {
                player.sendMessage(Component.text(target.getName() + " hasn't sent a teleport request to you!", NamedTextColor.RED));
                return;
            }
            tpaRequests.remove(target.getUniqueId());
            requestExpiry.remove(target.getUniqueId());
            target.sendMessage(Component.text(player.getName(), NamedTextColor.GOLD).append(Component.text(" has denied your teleport request", NamedTextColor.RED)));
            player.sendMessage(Component.text("Request denied", NamedTextColor.GREEN));
        }
    }

    private @Nullable UUID getKeyFromValue(Map<?, ?> hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return (UUID) o;
            }
        }
        return null;
    }

    private Component getAccept(String playerName) {
        return Component.text("/tpa accept", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/tpa accept " + playerName)).hoverEvent(HoverEvent.showText(Component.text("Accept ", NamedTextColor.GREEN).append(Component.text("the teleport request from ", NamedTextColor.GRAY).append(Component.text(playerName, NamedTextColor.GOLD)))));
    }

    private Component getDeny(String playerName) {
        return Component.text("/tpa deny", NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/tpa deny " + playerName)).hoverEvent(HoverEvent.showText(Component.text("Deny ", NamedTextColor.RED).append(Component.text("the teleport request from ", NamedTextColor.GRAY).append(Component.text(playerName, NamedTextColor.GOLD)))));
    }

    private void performTpa(Player invoker, Player target) {
        requestExpiry.remove(target.getUniqueId());
        tpaRequests.remove(target.getUniqueId());
        tasks.remove(target.getUniqueId());
        tasks.remove(invoker.getUniqueId());

        invoker.teleport(target.getLocation());
        invoker.sendMessage(Component.text("Teleported to ", NamedTextColor.GREEN).append(Component.text(target.getName(), NamedTextColor.GOLD)));
        target.sendMessage(Component.text(invoker.getName(), NamedTextColor.GREEN).append(Component.text(" has teleported to you", NamedTextColor.GRAY)));
    }

}
