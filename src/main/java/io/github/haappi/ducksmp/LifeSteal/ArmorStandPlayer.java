package io.github.haappi.ducksmp.LifeSteal;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.haappi.ducksmp.Commands.Vanish.enabledPlayers;

public class ArmorStandPlayer implements Listener {

    private final DuckSMP plugin;
    private final ConcurrentHashMap<UUID, UUID> armorMap = new ConcurrentHashMap<>();

    public ArmorStandPlayer() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::armorStandTask, 500L, 2L);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void armorStandTask() {
        armorMap.forEach((uuid, armorUuid) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                armorMap.remove(uuid);
                return;
            }
            ArmorStand stand = (ArmorStand) Bukkit.getEntity(armorUuid);

            if (stand == null) {
                armorMap.remove(uuid);
                stand = Utils.createStand(player);
                player.hideEntity(this.plugin, stand);
                armorMap.put(uuid, stand.getUniqueId());
            }

            if (stand.isDead()) {
                armorMap.remove(uuid);
                stand = Utils.createStand(player);
                player.hideEntity(this.plugin, stand);
                armorMap.put(uuid, stand.getUniqueId());
            }

            Component result;

            if (player.getPersistentDataContainer().has(new NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER)) {
                result = Component.text("LifeSteal ✔", NamedTextColor.GREEN);
            } else {
                result = Component.text("No LifeSteal ✘", NamedTextColor.RED);
            }

            stand.setCustomNameVisible(!player.isDead() && !player.isSneaking() && !player.isInvisible() && !enabledPlayers.contains(player.getUniqueId()) && (player.getGameMode() != GameMode.SPECTATOR));

            Component name = Component.text()
                    .append(result).build();
            if (!stand.name().equals(name)) {
                stand.customName(name);
            }

            if (stand.getLocation().add(0, -2.15, 0) != player.getLocation()) {
                stand.teleport(player.getLocation().add(0, 2.15, 0));
                player.hideEntity(this.plugin, stand);
            }

        });
    }


    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ArmorStand stand = Utils.createStand(event.getPlayer());
        stand.customName(Component.text("hey look you found me!!", NamedTextColor.RED));
        armorMap.put(event.getPlayer().getUniqueId(), stand.getUniqueId());
        event.getPlayer().hideEntity(DuckSMP.getInstance(), stand);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeStand(event.getPlayer().getUniqueId());
    }

    private void removeStand(UUID uuid) {
        ArmorStand entity = (ArmorStand) Bukkit.getEntity(armorMap.get(uuid));
        if (entity != null) {
            entity.remove();
        }
        armorMap.remove(uuid);
    }
}
