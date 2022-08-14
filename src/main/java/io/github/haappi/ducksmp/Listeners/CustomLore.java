package io.github.haappi.ducksmp.Listeners;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import static io.github.haappi.ducksmp.Listeners.StatHandler.cantBeUsedForStats;
import static io.github.haappi.ducksmp.Listeners.StatHandler.removeStatsFromItem;
import static io.github.haappi.ducksmp.Utils.LoreUtils.applyEnchantsToLore;

public class CustomLore implements Listener {

    private final DuckSMP plugin;

    public CustomLore() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

//    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (ItemStack item : event.getInventory().getContents()) {
                if (item == null) {
                    return;
                }
                if (item.getType() == Material.AIR) {
                    return;
                }
                if (cantBeUsedForStats(item.getType())) {
                    removeStatsFromItem(item);
                }
                 applyEnchantsToLore(item);
            }
        }, 1L);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (ItemStack item : event.getPlayer().getInventory().getContents()) {
                if (item == null) {
                    return;
                }
                if (item.getType() == Material.AIR) {
                    return;
                }
            }
        }, 1L);

    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().contains("enchant") && event.getMessage().contains("cant see")) {
            event.getPlayer().sendMessage(Component.text("Drop & pickup the item to view enchants.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onUse(PlayerItemDamageEvent event) {
        if (event.getItem().getType() == Material.AIR) {
            return;
        }
        applyEnchantsToLore(event.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.RESULT) {
            applyEnchantsToLore(event.getCurrentItem());
        }
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.AIR) {
            return;
        }
        if (cantBeUsedForStats(event.getEntity().getItemStack().getType())) {
            removeStatsFromItem(event.getEntity().getItemStack());
        }
        applyEnchantsToLore(event.getEntity().getItemStack());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        applyEnchantsToLore(event.getItem());
    }

    @EventHandler
    public void onGrindStone(PrepareResultEvent event) {
        if (event.getResult() == null) {
            return;
        }
        applyEnchantsToLore(event.getResult());
    }
}
