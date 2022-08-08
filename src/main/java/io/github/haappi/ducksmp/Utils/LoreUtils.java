package io.github.haappi.ducksmp.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static io.github.haappi.ducksmp.Utils.Utils.noItalics;

public class LoreUtils {
    public static void applyEnchantsToLore(final ItemStack item) {
        if (item.getType() == Material.TOTEM_OF_UNDYING) {
            return;
        } // todo make it add the persistent data types to the item
        @NotNull ItemMeta meta = item.getItemMeta();
        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        ArrayList<Component> lore = new ArrayList<>();

        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            lore.add(noItalics(WordUtils.capitalizeFully(enchantment.getKey().getKey().toLowerCase().replace("_", " ")), NamedTextColor.GRAY).append(noItalics(Component.text(" " + meta.getEnchantLevel(enchantment), NamedTextColor.AQUA))));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }
}
