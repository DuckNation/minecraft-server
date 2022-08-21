package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.Utils.CustomHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.haappi.ducksmp.Utils.LoreUtils.applyEnchantsToLore;
import static io.github.haappi.ducksmp.Utils.Utils.random;

public class Elytra implements Listener {

    private final DuckSMP plugin;

    public Elytra() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }
        ItemStack itemStack = event.getWhoClicked().getInventory().getChestplate();
        if (itemStack == null) {
            return;
        }
        if (itemStack.getType() == Material.ELYTRA) {
            itemStack.addEnchantment(Enchantment.VANISHING_CURSE, 1);
            itemStack.addEnchantment(Enchantment.BINDING_CURSE, 1);

            applyEnchantsToLore(itemStack);
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (event.getItem().getType() == Material.ELYTRA) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        if (event.getItem().getType() == Material.ELYTRA) {
            ItemStack itemStack = event.getItem();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() + random.nextInt(2, 5));
                itemStack.setItemMeta(meta);
            }
        }
    }

//    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.PHANTOM_MEMBRANE) {
            return;
        }
        if (event.getPlayer().getInventory().getChestplate() == null) {
            return;
        }
        if (event.getPlayer().getInventory().getChestplate().getType() != Material.ELYTRA) {
            return;
        }
        InventoryView view =  event.getPlayer().openAnvil(null, true);
        if (view == null) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getPlayer().closeInventory(), 1L);
            return;
        }
        AnvilInventory inventory = (AnvilInventory) view.getTopInventory();
        event.getPlayer().openInventory(inventory);
        inventory.setFirstItem(event.getPlayer().getInventory().getChestplate());
        event.getPlayer().getInventory().setChestplate(null);
        inventory.setSecondItem(event.getItem());
        event.getPlayer().getInventory().getItem(event.getHand()).setType(Material.AIR);
        event.getPlayer().updateInventory();
    }

//    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory inventory) {
            System.out.println(event.getClick());
            System.out.println(inventory.getFirstItem());
            System.out.println(event.getCurrentItem());// todo work on this
            if (event.getCurrentItem() == inventory.getFirstItem()) {
                event.setCancelled(true);
                return;
            }
            if (event.getCurrentItem() == inventory.getSecondItem()) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getWhoClicked().closeInventory(), 1L);
                return;
            }
            if (event.getCurrentItem() == inventory.getResult()) {
                System.out.println("they got elyra");
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getWhoClicked().closeInventory(), 1L);
                return;
            }
        }
    }
}
