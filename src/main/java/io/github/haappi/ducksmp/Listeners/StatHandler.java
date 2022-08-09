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

    /**
     * Automatically increments a stat. This handles all the boilerplate of checking for you, and doesn't do anything if it fails.
     * <p><b>This increases an integer value, not a double value.</b></p>

     @param stack The itemstack to increment the stat of.
     @param name The name of the stat to increment.
     **/
    @SuppressWarnings("SameParameterValue")
    private void incrementStat(ItemStack stack, String name) {
        if (cantBeUsedForStats(stack.getType())) {
            return;
        }

        ItemMeta itemMeta = getItemMeta(stack);
        NamespacedKey key = new NamespacedKey(plugin, name);

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(key, PersistentDataType.INTEGER, persistentDataContainer.getOrDefault(key, PersistentDataType.INTEGER, 0) + 1);

        stack.setItemMeta(itemMeta);
    }

    /**
     * Automatically increments a stat by the supplied double. This handles all the boilerplate of checking for you, and doesn't do anything if it fails.
     * <p><b>This increases a double value, not an integer value.</b></p>

     @param stack The itemstack to increment the stat of.
     @param name The name of the stat to increment.
     **/
    @SuppressWarnings("SameParameterValue")
    private void incrementStat(ItemStack stack, String name, Double amount) {
        if (cantBeUsedForStats(stack.getType())) {
            return;
        }

        ItemMeta itemMeta = getItemMeta(stack);
        NamespacedKey key = new NamespacedKey(plugin, name);

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(key, PersistentDataType.DOUBLE, getRounded(persistentDataContainer.getOrDefault(key, PersistentDataType.DOUBLE, 0.00) + amount));

        stack.setItemMeta(itemMeta);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        incrementStat(event.getPlayer().getInventory().getItemInMainHand(), "blocks_broken");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
         if (event.getDamager() instanceof Player player) {
            incrementStat(player.getInventory().getItemInMainHand(), "damage_dealt", event.getFinalDamage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (victim.getKiller() != null) {
                incrementStat(victim.getKiller().getInventory().getItemInMainHand(), "players_killed");
            }
        }
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            incrementStat(player.getInventory().getItemInMainHand(), "mobs_killed");
        }
    }

}
