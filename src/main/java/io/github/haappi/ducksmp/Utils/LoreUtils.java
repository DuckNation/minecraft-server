package io.github.haappi.ducksmp.Utils;

import io.github.haappi.ducksmp.Listeners.StatHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static io.github.haappi.ducksmp.Listeners.FireballHandler.isFireBall;
import static io.github.haappi.ducksmp.Listeners.StatHandler.*;
import static io.github.haappi.ducksmp.Utils.Utils.chain;
import static io.github.haappi.ducksmp.Utils.Utils.noItalics;

public class LoreUtils {
    public static void applyEnchantsToLore(@Nullable final ItemStack item) {
        if (item == null) {
            return;
        }
        if (item.getType() == Material.AIR) {
            return;
        }
        if (item.getType() == Material.TOTEM_OF_UNDYING) {
            return;
        }
        @NotNull ItemMeta meta = getItemMeta(item);
        ArrayList<Component> lore = new ArrayList<>();

        if (isFireBall(item)) {
            lore.add(chain(noItalics("Right click", NamedTextColor.GOLD), noItalics(" to throw a fireball.", NamedTextColor.GREEN)));
        }

        if (lore.size() != 0) {
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        if (cantBeUsedForStats(item.getType())) {
            return;
        }

        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }


//        boolean toAdd = true;

//        for (Iterator<Component> it = lore.iterator(); it.hasNext(); ) {
//            Component _lore = it.next();
//            if (_lore.equals(enchant)) {
//               // remove everything after this in the List
//               while (it.hasNext()) {
//                    it.next();
//                    it.remove();
//               }
//                toAdd = false;
//                // lore.subList(lore.indexOf(_lore), lore.size()).clear(); // If you're not using an Iterator, you can use this instead of the above code.
//           }
//        }

        Component stat;
        stat = getStatsForItem(item, "Blocks Broken", String.valueOf(StatHandler.Stat.BLOCKS_BROKEN), PersistentDataType.INTEGER);
        if (stat != null) {
            lore.add(stat);
        }
        stat = getStatsForItem(item, "Mobs Killed", String.valueOf(StatHandler.Stat.MOBS_KILLED), PersistentDataType.INTEGER);
        if (stat != null) {
            lore.add(stat);
        }
        stat = getStatsForItem(item, "Players Killed", String.valueOf(StatHandler.Stat.PLAYERS_KILLED), PersistentDataType.INTEGER);
        if (stat != null) {
            lore.add(stat);
        }
        stat = getStatsForItem(item, "Damage Dealt", String.valueOf(StatHandler.Stat.DAMAGE_DEALT), PersistentDataType.DOUBLE);
        if (stat != null) {
            lore.add(stat);
        }
        stat = getStatsForItem(item, "Treasure Found", String.valueOf(StatHandler.Stat.TREASURE_CAUGHT), PersistentDataType.INTEGER);
        if (stat != null) {
            lore.add(stat);
        }

        if (item.getEnchantments().size() != 0) {
            lore.add(noItalics("    ", NamedTextColor.YELLOW));
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
}
