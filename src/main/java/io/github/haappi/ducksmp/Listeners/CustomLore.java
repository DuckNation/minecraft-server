package io.github.haappi.ducksmp.Listeners;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.Utils.LoreUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class CustomLore implements Listener {

    private final DuckSMP plugin;

    public CustomLore() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

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
        LoreUtils.applyEnchantsToLore(event.getItem());
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.AIR) {
            return;
        }
        LoreUtils.applyEnchantsToLore(event.getEntity().getItemStack());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        LoreUtils.applyEnchantsToLore(event.getItem());
    }

    @EventHandler
    public void onGrindStone(PrepareResultEvent event) {
        if (event.getResult() == null) {
            return;
        }
        LoreUtils.applyEnchantsToLore(event.getResult());
    }
}
