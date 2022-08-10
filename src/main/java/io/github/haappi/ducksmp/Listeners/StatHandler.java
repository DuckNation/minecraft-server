package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static io.github.haappi.ducksmp.Utils.Utils.chain;
import static io.github.haappi.ducksmp.Utils.Utils.noItalics;

public class StatHandler implements Listener {

    private final DuckSMP plugin;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public StatHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static boolean cantBeUsedForStats(Material item) {
        String name = item.toString().toLowerCase();
        return !name.contains("shovel") &&
                !name.contains("pickaxe") &&
                !name.contains("axe") &&
                !name.contains("sword") &&
                !name.contains("hoe") &&
                !name.contains("shears") &&
                !name.contains("bow") &&
                !name.contains("fishing_rod") &&
                !name.contains("trident") &&
                !name.contains("boots") &&
                !name.contains("leggings") &&
                !name.contains("chestplate") &&
                !name.contains("helmet");
    }

    public static @NotNull ItemMeta getItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        return itemMeta;
    }

    public static void removeStatsFromItem(ItemStack itemStack) {
        ItemMeta itemMeta = getItemMeta(itemStack);
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        DuckSMP instance = DuckSMP.getInstance();

        pdc.remove(new NamespacedKey(instance, "blocks_broken"));
        pdc.remove(new NamespacedKey(instance, "damage_dealt"));
        pdc.remove(new NamespacedKey(instance, "players_killed"));
        pdc.remove(new NamespacedKey(instance, "mobs_killed"));

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Get stats for the supplied itemstack. If the itemstack is not a stat item, or it doesn't have stats, this returns null.
     *
     * @param stack        The itemstack to get the stats of.
     * @param nameToShow   The name of the stat to show on the lore.
     * @param internalName The name of the stat to get.
     * @return The stats of the itemstack.
     */
    public static Component getStatsForItem(ItemStack stack, String nameToShow, String internalName, PersistentDataType type) {
        ItemMeta meta = getItemMeta(stack);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        DuckSMP instance = DuckSMP.getInstance();
        NamespacedKey key = new NamespacedKey(instance, internalName);
        if (!pdc.has(key)) {
            return null;
        }
        return chain(noItalics(nameToShow, NamedTextColor.AQUA), noItalics(": ", NamedTextColor.GRAY), noItalics(NumberFormat.getIntegerInstance().format(pdc.get(key, type)), NamedTextColor.GOLD));
    }

    private double getRounded(double input) {
        return Double.parseDouble(df.format(input));
    }

    /**
     * Automatically increments a stat. This handles all the boilerplate of checking for you, and doesn't do anything if it fails.
     * <p><b>This increases an integer value, not a double value.</b></p>
     *
     * @param stack The itemstack to increment the stat of.
     * @param name  The name of the stat to increment.
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
     *
     * @param stack The itemstack to increment the stat of.
     * @param name  The name of the stat to increment.
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
        incrementStat(event.getPlayer().getInventory().getItemInMainHand(), String.valueOf(Stat.BLOCKS_BROKEN));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            incrementStat(player.getInventory().getItemInMainHand(), Stat.DAMAGE_DEALT.toString(), event.getFinalDamage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (victim.getKiller() != null) {
                incrementStat(victim.getKiller().getInventory().getItemInMainHand(), Stat.PLAYERS_KILLED.toString());
            }
        }
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            incrementStat(player.getInventory().getItemInMainHand(), Stat.MOBS_KILLED.toString());
        }
    }

    public enum Stat {
        BLOCKS_BROKEN("blocks_broken"),
        DAMAGE_DEALT("damage_dealt"),
        MOBS_KILLED("mobs_killed"),
        PLAYERS_KILLED("players_killed");

        private final String text;

        Stat(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

}
