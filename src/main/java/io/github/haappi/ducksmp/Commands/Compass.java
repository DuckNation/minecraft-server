package io.github.haappi.ducksmp.Commands;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Compass extends BukkitCommand implements Listener {

    private final DuckSMP plugin;

    private final String defaultCompass = "S · ◈ · ◈ · ◈ · SW · ◈ · ◈ · ◈ · W · ◈ · ◈ · ◈ · NW" +
            " · ◈ · ◈ · ◈ · N · ◈ · ◈ · ◈ · NE · ◈ · ◈ · ◈ · E · ◈ · ◈ · ◈ · SE" +
            " · ◈ · ◈ · ◈ · S · ◈ · ◈ · ◈ · SW · ◈ · ◈ · ◈ · W · ◈ · ◈ · ◈ · NW" +
            " · ◈ · ◈ · ◈ · N · ◈ · ◈ · ◈ · NE · ◈ · ◈ · ◈ · E · ◈ · ◈ · ◈ · SE" +
            " · ◈ · ◈ · ◈ · ";
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public Compass(String name) {
        super(name);
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Set<UUID> uuids = bossBars.keySet();
            for (UUID uuid : uuids) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    updateBossBar(player);
                } else {
                    bossBars.remove(uuid);
                }
            }
        }, 0L, 2L);
    }

    private BossBar.Color getColor(byte Byte) {
        return switch (Byte) {
            case 0 -> BossBar.Color.RED;
            case 1 -> BossBar.Color.GREEN;
            case 2 -> BossBar.Color.BLUE;
            // case 3: return BossBar.Color.PURPLE;
            case 4 -> BossBar.Color.WHITE;
            case 5 -> BossBar.Color.PINK;
            default -> BossBar.Color.PURPLE;
        };

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getPersistentDataContainer().has(new NamespacedKey(plugin, "compass_enabled"), PersistentDataType.BYTE)) {
            if (player.getPersistentDataContainer().has(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE)) {
                byte color = player.getPersistentDataContainer().get(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE);
                final BossBar bossbar = BossBar.bossBar(Component.text(player.getName()), 1f, getColor(color), BossBar.Overlay.NOTCHED_10);
                bossBars.put(player.getUniqueId(), bossbar);
                player.showBossBar(bossbar);
            }
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (bossBars.containsKey(player.getUniqueId())) {
                    player.hideBossBar(bossBars.get(player.getUniqueId()));
                    bossBars.remove(player.getUniqueId());
                    player.sendMessage(Component.text("Compass disabled.", NamedTextColor.RED));
                    player.getPersistentDataContainer().remove(new NamespacedKey(plugin, "compass_enabled"));
                } else {
                    final Component name = Component.text(player.getName());
                    final BossBar bossbar = BossBar.bossBar(name, 1f, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_10);
                    player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 3);
                    bossBars.put(player.getUniqueId(), bossbar);
                    player.showBossBar(bossbar);
                    player.sendMessage(Component.text("Compass enabled.", NamedTextColor.GREEN));
                    player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_enabled"), PersistentDataType.BYTE, (byte) 1);
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("color")) {

                    switch (args[1].toLowerCase()) {
                        case "red" -> {
                            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 0);
                            bossBars.get(player.getUniqueId()).color(getColor((byte) 0));
                            player.sendMessage(Component.text("Compass color set to ", NamedTextColor.GRAY).append(Component.text("red", NamedTextColor.RED)));
                        }
                        case "green" -> {
                            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 1);
                            bossBars.get(player.getUniqueId()).color(getColor((byte) 1));
                            player.sendMessage(Component.text("Compass color set to ", NamedTextColor.GRAY).append(Component.text("green", NamedTextColor.GREEN)));
                        }
                        case "blue" -> {
                            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 2);
                            bossBars.get(player.getUniqueId()).color(getColor((byte) 2));
                            player.sendMessage(Component.text("Compass color set to ", NamedTextColor.GRAY).append(Component.text("blue", NamedTextColor.BLUE)));
                        }
                        case "white" -> {
                            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 4);
                            bossBars.get(player.getUniqueId()).color(getColor((byte) 4));
                            player.sendMessage(Component.text("Compass color set to ", NamedTextColor.GRAY).append(Component.text("white", NamedTextColor.WHITE)));
                        }
                        case "pink" -> {
                            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 5);
                            bossBars.get(player.getUniqueId()).color(getColor((byte) 5));
                            player.sendMessage(Component.text("Compass color set to ", NamedTextColor.GRAY).append(Component.text("pink", NamedTextColor.LIGHT_PURPLE)));
                        }
                        case "purple" -> {
                            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "compass_color"), PersistentDataType.BYTE, (byte) 6);
                            bossBars.get(player.getUniqueId()).color(getColor((byte) 6));
                            player.sendMessage(Component.text("Compass color set to ", NamedTextColor.GRAY).append(Component.text("purple", NamedTextColor.DARK_PURPLE)));
                        }
                        default -> player.sendMessage(Component.text(
                                        "Invalid color.", NamedTextColor.RED)
                                .append(Component.text(" Valid colors: ", NamedTextColor.GRAY)
                                        .append(Component.text("red ", NamedTextColor.RED)
                                                .append(Component.text("green ", NamedTextColor.GREEN)
                                                        .append(Component.text("blue ", NamedTextColor.BLUE)
                                                                .append(Component.text("purple ", NamedTextColor.DARK_PURPLE)
                                                                        .append(Component.text("white ", NamedTextColor.WHITE)
                                                                                .append(Component.text("pink ", NamedTextColor.LIGHT_PURPLE)))))))));
                    }

                } else {
                    player.sendMessage(Component.text("Invalid argument. /compass [color] <color>", NamedTextColor.RED));
                }
            } else {
                player.sendMessage(Component.text("Invalid argument. /compass [color] <color>", NamedTextColor.RED));
            }

        }

        return true;
    }

    private void updateBossBar(Player player) {
        final BossBar bossbar = bossBars.get(player.getUniqueId());
        float yaw = player.getLocation().getYaw();
        int length = defaultCompass.length();
        int pos = (int) ((normalize(yaw) * (length / 720F)) + (length / 2F));
        bossbar.name(Component.text(defaultCompass.substring(pos - 25, pos + 25), NamedTextColor.GRAY));
    }

    private float normalize(float yaw) {
        while (yaw < -180.0F) {
            yaw = 360.0F;
        }
        while (yaw > 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

}
