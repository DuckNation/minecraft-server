package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class StatHandler implements Listener {

    private final DuckSMP plugin;

    public StatHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemMeta itemMeta = event.getPlayer().getActiveItem().getItemMeta();
        if (itemMeta == null) {
            return;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (!pdc.has(new NamespacedKey(plugin, "broken_blocks"), PersistentDataType.INTEGER)) {
            pdc.set(new NamespacedKey(plugin, "broken_blocks"), PersistentDataType.INTEGER, 1);
        } else {
            pdc.set(new NamespacedKey(plugin, "broken_blocks"), PersistentDataType.INTEGER, pdc.get(new NamespacedKey(plugin, "broken_blocks"), PersistentDataType.INTEGER) + 1);
        }

        event.getPlayer().getActiveItem().setItemMeta(itemMeta);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        if (event.getDamager() instanceof Player player) {
            ItemMeta itemMeta = player.getActiveItem().getItemMeta();
            if (itemMeta == null) {
                return;
            }
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            if (!pdc.has(new NamespacedKey(plugin, "damage_dealt"), PersistentDataType.DOUBLE)) {
                pdc.set(new NamespacedKey(plugin, "damage_dealt"), PersistentDataType.DOUBLE, event.getDamage());
            } else {
                pdc.set(new NamespacedKey(plugin, "damage_dealt"), PersistentDataType.DOUBLE, pdc.get(new NamespacedKey(plugin, "damage_dealt"), PersistentDataType.DOUBLE) + event.getDamage());
            }
            player.getActiveItem().setItemMeta(itemMeta);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getDamager() instanceof Player player) {
            ItemMeta itemMeta = player.getActiveItem().getItemMeta();
            if (itemMeta == null) {
                return;
            }
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            if (!pdc.has(new NamespacedKey(plugin, "player_damage_dealt"), PersistentDataType.DOUBLE)) {
                pdc.set(new NamespacedKey(plugin, "player_damage_dealt"), PersistentDataType.DOUBLE, event.getDamage());
            } else {
                pdc.set(new NamespacedKey(plugin, "player_damage_dealt"), PersistentDataType.DOUBLE, pdc.get(new NamespacedKey(plugin, "player_damage_dealt"), PersistentDataType.DOUBLE) + event.getDamage());
            }
            player.getActiveItem().setItemMeta(itemMeta);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            ItemMeta itemMeta = player.getActiveItem().getItemMeta();
            if (itemMeta == null) {
                return;
            }
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            if (!pdc.has(new NamespacedKey(plugin, "mobs_killed"), PersistentDataType.INTEGER)) {
                pdc.set(new NamespacedKey(plugin, "mobs_killed"), PersistentDataType.INTEGER, 1);
            } else {
                pdc.set(new NamespacedKey(plugin, "mobs_killed"), PersistentDataType.INTEGER, pdc.get(new NamespacedKey(plugin, "mobs_killed"), PersistentDataType.INTEGER) + 1);
            }
            player.getActiveItem().setItemMeta(itemMeta);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            ItemMeta itemMeta = player.getActiveItem().getItemMeta();
            if (itemMeta == null) {
                return;
            }
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            if (!pdc.has(new NamespacedKey(plugin, "players_killed"), PersistentDataType.INTEGER)) {
                pdc.set(new NamespacedKey(plugin, "players_killed"), PersistentDataType.INTEGER, 1);
            } else {
                pdc.set(new NamespacedKey(plugin, "players_killed"), PersistentDataType.INTEGER, pdc.get(new NamespacedKey(plugin, "players_killed"), PersistentDataType.INTEGER) + 1);
            }
            player.getActiveItem().setItemMeta(itemMeta);
        }
    }

}
