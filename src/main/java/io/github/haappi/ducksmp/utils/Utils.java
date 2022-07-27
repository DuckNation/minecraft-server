package io.github.haappi.ducksmp.utils;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public Utils() {
        throw new UnsupportedOperationException("This class is not meant to be instantiated");
    }

    public static Component noItalics(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component noItalics(String content, NamedTextColor color) {
        return noItalics(Component.text(content, color));
    }

    public static Component noItalics(String content) {
        return noItalics(Component.text(content));
    }

    public static Component chain(Component... components) {
        Component thing = Component.empty();
        for (Component component : components) {
            thing = thing.append(component);
        }
        return thing;
    }

    public static String sFormat(Integer number) {
        if (number == 1) {
            return "";
        } else {
            return "s";
        }
    }

    public static TextComponent getCountdown(Integer countdown) {
        NamedTextColor color = switch (countdown) {
            case 0, 1, 2, 3 -> NamedTextColor.RED;
            case 4, 5, 6 -> NamedTextColor.YELLOW;
            default -> NamedTextColor.GREEN;
        };

        return Component.text(countdown + " second" + sFormat(countdown), color);
    }

    public static Boolean registerNewCommand(Command command) {
        return ((CraftServer) Bukkit.getServer()).getCommandMap().register("duck", command);
    }

    public static @NotNull ItemStack getHeart(int count, Player owner) {
        ItemStack thing = new ItemStack(Material.NETHER_STAR, count);
        ItemMeta meta = thing.getItemMeta();
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(DuckSMP.getInstance(), "life_steal"), PersistentDataType.STRING, "true");
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(DuckSMP.getInstance(), "owner"), PersistentDataType.STRING, owner.getUniqueId().toString());

        List<Component> lore = Arrays.asList(
                noItalics(""),
                chain(noItalics("Click to claim a heart "), noItalics("❤", NamedTextColor.RED)),
//                noItalics(""),
                chain(noItalics(owner.getName(), NamedTextColor.YELLOW), noItalics("'s heart", NamedTextColor.GRAY))
        );

        meta.lore(lore);
        meta.displayName(miniMessage.deserialize("<rainbow>Life Steal Heart</rainbow>").decoration(TextDecoration.ITALIC, false));
        thing.setItemMeta(meta);

        return thing;
    }

    public static boolean isLifeStealItem(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getItemMeta() == null) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(DuckSMP.getInstance(), "life_steal"), PersistentDataType.STRING);
    }
}
