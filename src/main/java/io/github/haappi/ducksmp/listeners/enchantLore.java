package io.github.haappi.ducksmp.listeners;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static io.github.haappi.ducksmp.utils.Utils.noItalics;

public class enchantLore implements Listener {

    private final DuckSMP plugin;

    public enchantLore() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void applyEnchantsToLore(final ItemStack item) {
        @NotNull ItemMeta meta = item.getItemMeta();
        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        ArrayList<Component> lore = new ArrayList<>();

        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            lore.add(noItalics(WordUtils.capitalizeFully(enchantment.getKey().getKey().toLowerCase()), NamedTextColor.GRAY).append(noItalics(Component.text(" " + meta.getEnchantLevel(enchantment), NamedTextColor.AQUA))));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.AIR) {
            return;
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
