package io.github.haappi.duckpaper.fun;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.haappi.duckpaper.DuckPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class DeathHead implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerHead(PlayerDeathEvent event) {
        event.getDrops().add(getHead(event.getPlayer()));
        event.getItemsToKeep().add(clown());
    }

    private ItemStack clown() {
        ItemStack stack = new ItemStack(Material.PAPER);

        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text("Imagine dying lmaoooo", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        stack.setItemMeta(meta);

        return stack;
    }

    private ItemStack getHead(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        PlayerProfile profile = player.getPlayerProfile();

        Property textureProperty = null;
        for (ProfileProperty profileProperty : profile.getProperties()) {
            if (profileProperty.getName().equals("textures")) {
                textureProperty = new Property(profileProperty.getName(), profileProperty.getValue(), profileProperty.getSignature());
                break;
            }
        }

        if (textureProperty != null) {
            GameProfile skullProfile = new GameProfile(UUID.randomUUID(), null);
            PropertyMap propertyMap = skullProfile.getProperties();
            propertyMap.put("textures", textureProperty);

            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, skullProfile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            skullMeta.displayName(player.name().append(Component.text("'s Head").color(NamedTextColor.GRAY)));

            NamespacedKey key = new NamespacedKey(DuckPaper.getInstance(), "death-head");
            skullMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            skull.setItemMeta(skullMeta);
        }

        return skull;

    }
}
