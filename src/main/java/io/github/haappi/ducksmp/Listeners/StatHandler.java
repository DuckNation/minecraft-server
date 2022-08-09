package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;

public class StatHandler implements Listener {

    private final DuckSMP plugin;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public StatHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private boolean cantBeUsedForStats(Material item) {
        String name = item.toString().toLowerCase();
        return !name.contains("shovel") &&
                !name.contains("pickaxe") &&
                !name.contains("axe") &&
                !name.contains("sword") &&
                !name.contains("hoe") &&
                !name.contains("shears") &&
                !name.contains("bow") &&
                !name.contains("fishing rod") &&
                !name.contains("trident") &&
                !name.contains("boots") &&
                !name.contains("leggings") &&
                !name.contains("chestplate") &&
                !name.contains("helmet");
    }

    private ItemMeta getItemMeta(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        return itemMeta;
    }

    private double getRounded(double input) {
        return Double.parseDouble(df.format(input));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (cantBeUsedForStats(event.getPlayer().getInventory().getItemInMainHand().getType())) {
            return;
        }
        ItemMeta itemMeta = getItemMeta(event.getPlayer().getInventory().getItemInMainHand());
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "broken_blocks");
        pdc.set(key, PersistentDataType.INTEGER, pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) + 1);
        event.getPlayer().getInventory().getItemInMainHand().setItemMeta(itemMeta);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (cantBeUsedForStats(player.getInventory().getItemInMainHand().getType())) {
                return;
            }
            ItemMeta itemMeta = getItemMeta(player.getInventory().getItemInMainHand());
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

            NamespacedKey key = new NamespacedKey(plugin, "damage_dealt");
            pdc.set(key, PersistentDataType.DOUBLE, getRounded(pdc.getOrDefault(key, PersistentDataType.DOUBLE, 0.0) + event.getFinalDamage()));

           player.getInventory().getItemInMainHand().setItemMeta(itemMeta);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            if (cantBeUsedForStats(player.getInventory().getItemInMainHand().getType())) {
                return;
            }
            ItemMeta itemMeta = getItemMeta(player.getInventory().getItemInMainHand());

            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "mobs_killed");

            pdc.set(key, PersistentDataType.INTEGER, pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) + 1);
           player.getInventory().getItemInMainHand().setItemMeta(itemMeta);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getEntity().getKiller() != null) {
            if (cantBeUsedForStats(player.getInventory().getItemInMainHand().getType())) {
                return;
            }
            ItemMeta itemMeta = getItemMeta(player.getInventory().getItemInMainHand());

            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "players_killed");

            pdc.set(key, PersistentDataType.INTEGER, pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) + 1);
           player.getInventory().getItemInMainHand().setItemMeta(itemMeta);
        }
    }

}
