package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.item.ArmorItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.persistence.PersistentDataType;

public class netheirte implements Listener {

    private final DuckSMP plugin;

    public netheirte() {

        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void craftItem(SmithItemEvent event) {
        SmithingInventory inv = event.getInventory();
        for (ItemStack stack : inv.getStorageContents()) {
            if (stack == null) {
                continue;
            }
            if (CraftItemStack.asNMSCopy(stack).getItem() instanceof ArmorItem) {
                inv.setResult(null);
                event.setCancelled(true);
                return;
            }
        }
        if (inv.getResult() == null) {
            return;
        }
        if (inv.getResult().getType() == Material.NETHERITE_AXE || inv.getResult().getType() == Material.NETHERITE_SWORD) {
            inv.getResult().getItemMeta().getPersistentDataContainer().set(new NamespacedKey(plugin, "no_pvp_tool"), PersistentDataType.BYTE, (byte) 1);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (event.getEntity() instanceof Player) {
                if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "np_pvp_tool"))) {
                    event.setCancelled(true);
                    player.sendMessage(Component.text("You can't damage players with this tool.", NamedTextColor.RED));
                }
            }
        }
    }
}
