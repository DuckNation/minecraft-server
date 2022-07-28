package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
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
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
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

    private final DuckSMP plugin;
    public static final HashMap<UUID, Tuple<ClientboundSetPlayerTeamPacket, ClientboundSetPlayerTeamPacket>> packetsToSend = new HashMap<>();
    public static final ConcurrentHashMap<UUID, NamedTextColor> chatColors = new ConcurrentHashMap<>();


    public Common() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Player player = event.getPlayer();
            PersistentDataContainer container = player.getPersistentDataContainer();
            String prefix;
            if (container.has(new NamespacedKey(plugin, "custom_prefix"), PersistentDataType.STRING)) {
                prefix = container.get(new NamespacedKey(plugin, "custom_prefix"), PersistentDataType.STRING);
            } else {
                prefix = "rank ";
            }

            ChatFormatting color;
            Integer customColor = container.get(new NamespacedKey(plugin, "custom_color"), PersistentDataType.INTEGER);
            if (customColor != null) {
                color = ChatFormatting.getById(customColor);
            } else {
                color = ChatFormatting.WHITE;
            }
            chatColors.put(player.getUniqueId(), NamedTextColor.nearestTo(TextColor.fromHexString(color.toString())));

            teamPacket(event.getPlayer(), String.valueOf(System.currentTimeMillis()), prefix, color);
            for (Map.Entry<UUID, Tuple<ClientboundSetPlayerTeamPacket, ClientboundSetPlayerTeamPacket>> entry : packetsToSend.entrySet()) {
                ((CraftPlayer) event.getPlayer()).getHandle().connection.send(entry.getValue().getA());
                ((CraftPlayer) event.getPlayer()).getHandle().connection.send(entry.getValue().getB());
            }
        }, 20 * 3L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        packetsToSend.remove(event.getPlayer().getUniqueId());
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
}
