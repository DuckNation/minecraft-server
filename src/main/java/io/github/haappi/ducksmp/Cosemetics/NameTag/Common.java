package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.utils.Utils;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.util.Tuple;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Common implements Listener {

    public static final HashMap<UUID, Tuple<ClientboundSetPlayerTeamPacket, ClientboundSetPlayerTeamPacket>> packetsToSend = new HashMap<>();
    public static final ConcurrentHashMap<UUID, NamedTextColor> chatColors = new ConcurrentHashMap<>();
    public static final Map<Integer, ChatFormatting> colorMapping = Map.ofEntries(
            Map.entry(0, ChatFormatting.DARK_BLUE),
            Map.entry(1, ChatFormatting.DARK_GREEN),
            Map.entry(2, ChatFormatting.DARK_AQUA),
            Map.entry(3, ChatFormatting.DARK_RED),
            Map.entry(4, ChatFormatting.DARK_PURPLE),
            Map.entry(5, ChatFormatting.GOLD),
            Map.entry(6, ChatFormatting.GRAY),
            Map.entry(7, ChatFormatting.DARK_GRAY),
            Map.entry(8, ChatFormatting.BLUE),
            Map.entry(9, ChatFormatting.GREEN),
            Map.entry(10, ChatFormatting.AQUA),
            Map.entry(11, ChatFormatting.RED),
            Map.entry(12, ChatFormatting.LIGHT_PURPLE),
            Map.entry(13, ChatFormatting.YELLOW)
    );

    private final DuckSMP plugin;


    public Common() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static Component getFormattedPrefix(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Component prefix;

        String prefixString = container.get(new NamespacedKey(DuckSMP.getInstance(), "custom_prefix"), PersistentDataType.STRING);

        if (prefixString != null) {
            prefix = Component.text(prefixString, chatColors.get(player.getUniqueId()));
        } else {
            prefix = Component.text(" ", NamedTextColor.WHITE);
        }
        return prefix;
    }

    @SuppressWarnings("ConstantConditions")
    public static void setStuff(Player player, final String prefix, ChatFormatting color) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        String _prefix = prefix.replaceAll("§0", "")
                .replaceAll("§F", "")
                .replaceAll("§f", "")
                .replaceAll("§l", "")
                .replaceAll("§m", "")
                .replaceAll("§n", "")
                .replaceAll("§o", "")
                .replaceAll("§L", "")
                .replaceAll("§M", "")
                .replaceAll("§N", "")
                .replaceAll("§O", "")
                .replaceAll("§k", "")
                .replaceAll("§K", "");
        if (prefix.length() > 16) {
            _prefix = prefix.substring(0, 16) + "] ";
        }
        container.set(new NamespacedKey(DuckSMP.getInstance(), "custom_prefix"), PersistentDataType.STRING, _prefix);
        container.set(new NamespacedKey(DuckSMP.getInstance(), "custom_color"), PersistentDataType.INTEGER, color.getId());

        chatColors.put(player.getUniqueId(), NamedTextColor.nearestTo(TextColor.color(color.getColor())));

        Utils.sendTeamPackets();

    }

    public static void teamPacket(Player player, String teamName, String prefix, ChatFormatting color) {
        CraftScoreboard scoreboard = ((CraftScoreboardManager) Bukkit.getScoreboardManager()).getMainScoreboard();
        PlayerTeam team = new PlayerTeam(scoreboard.getHandle(), teamName);
        team.setDeathMessageVisibility(PlayerTeam.Visibility.ALWAYS);
        team.setCollisionRule(Team.CollisionRule.ALWAYS);
        team.setNameTagVisibility(Team.Visibility.ALWAYS);
        team.setPlayerPrefix(PaperAdventure.asVanilla(Component.text(prefix)));
        team.setColor(color);

        packetsToSend.put(player.getUniqueId(), new Tuple<>(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true), ClientboundSetPlayerTeamPacket.createPlayerPacket(team, player.getName(), ClientboundSetPlayerTeamPacket.Action.ADD)));
    }

    @EventHandler
    @SuppressWarnings("ConstantConditions")
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Player player = event.getPlayer();
            PersistentDataContainer container = player.getPersistentDataContainer();
            String prefix = "";
            boolean shouldDoTheirs = true;
            if (container.has(new NamespacedKey(plugin, "custom_prefix"), PersistentDataType.STRING)) {
                prefix = container.get(new NamespacedKey(plugin, "custom_prefix"), PersistentDataType.STRING);
            } else {
                shouldDoTheirs = false;
            }

            ChatFormatting color = ChatFormatting.WHITE;
            Integer customColor = container.get(new NamespacedKey(plugin, "custom_color"), PersistentDataType.INTEGER);
            if (customColor != null) {
                color = ChatFormatting.getById(customColor);
            } else {
                shouldDoTheirs = false;
            }
            if (shouldDoTheirs) {
                chatColors.put(player.getUniqueId(), NamedTextColor.nearestTo(TextColor.color(color.getColor())));
                teamPacket(event.getPlayer(), String.valueOf(System.currentTimeMillis()), prefix, color);
            }

            Utils.sendTeamPackets();
        }, 20 * 3L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        packetsToSend.remove(event.getPlayer().getUniqueId());
    }
}
