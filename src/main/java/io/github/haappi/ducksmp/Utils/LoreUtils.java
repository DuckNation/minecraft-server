package io.github.haappi.ducksmp.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        List<Component> lore = new ArrayList<>();
//        if (!meta.hasLore()) {
//            lore = new ArrayList<>();
//            meta.lore(lore);
//        } else {
//            lore = meta.lore();
//        }
        Component enchant = noItalics("    ", NamedTextColor.YELLOW);

        boolean toAdd = true;

//        for (Iterator<Component> it = lore.iterator(); it.hasNext(); ) {
//            Component _lore = it.next();
//            if (_lore.equals(enchant)) {
//                // remove everything after this in the List
//                while (it.hasNext()) {
//                    it.next();
//                    it.remove();
//                }
//                toAdd = false;
//                // lore.subList(lore.indexOf(_lore), lore.size()).clear(); // If you're not using an Iterator, you can use this instead of the above code.
//            }
//        }

        if (toAdd) {
            lore.add(enchant);
        }

        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            lore.add(getLoreForEnchant(enchantment, meta.getEnchantLevel(enchantment)));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private static Component getLoreForEnchant(Enchantment enchantment, int level) {
        if (enchantment.equals(Enchantment.VANISHING_CURSE)) {
            return noItalics(WordUtils.capitalizeFully("Curse of Vanishing"), NamedTextColor.RED);
        }
        if (enchantment.equals(Enchantment.BINDING_CURSE)) {
            return noItalics(WordUtils.capitalizeFully("Curse of Binding"), NamedTextColor.RED);
        }
        return noItalics(WordUtils.capitalizeFully(enchantment.getKey().getKey().toLowerCase().replace("_", " ")), NamedTextColor.GRAY).append(noItalics(Component.text(" " + level, NamedTextColor.AQUA)));
    }


    public static ArrayList<Component> applyPDCLore(NamespacedKey key, ArrayList<Component> to) {
        switch (key.getKey()) {
            case "custom_home":
                break;
            default:
                break; // todo
        }

        return to;
    }
}
