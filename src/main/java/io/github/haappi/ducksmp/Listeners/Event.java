package io.github.haappi.ducksmp.Listeners;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.Arrays;

import static io.github.haappi.ducksmp.Utils.Utils.chain;
import static io.github.haappi.ducksmp.Utils.Utils.noItalics;

public class Event implements Listener {

    private final DuckSMP plugin;
    private final NumberFormat numberFormat = NumberFormat.getInstance();

    public Event() {
        this.plugin = DuckSMP.getInstance();
        numberFormat.setGroupingUsed(true);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (System.currentTimeMillis() < 1661691600L * 1000) {
            Bukkit.addRecipe(eventItemRecipe());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static ItemStack getRecipe(int count) {
        ItemStack item = new ItemStack(Material.STRUCTURE_BLOCK, count);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(noItalics("Event Item", NamedTextColor.GOLD));
        meta.lore(Arrays.asList(noItalics(""), chain(noItalics("Drop this to claim an ", NamedTextColor.GRAY), noItalics("event item", NamedTextColor.AQUA), noItalics(".", NamedTextColor.GRAY))));
        meta.getPersistentDataContainer().set(new NamespacedKey(DuckSMP.getInstance(), "event_item"), PersistentDataType.STRING, "event");
        item.setItemMeta(meta);

        return item;
    }

    private ShapelessRecipe eventItemRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "event_recipe");

        ShapelessRecipe recipe = new ShapelessRecipe(key, getRecipe(1));

        recipe.addIngredient(Material.PODZOL);
        recipe.addIngredient(Material.CAKE);
        recipe.addIngredient(Material.MAGMA_CREAM);

        recipe.addIngredient(Material.TOTEM_OF_UNDYING);
        recipe.addIngredient(Material.BLUE_ORCHID);
        recipe.addIngredient(Material.TROPICAL_FISH);

        recipe.addIngredient(Material.ENDER_EYE);
        recipe.addIngredient(Material.POISONOUS_POTATO);
        recipe.addIngredient(Material.GREEN_WOOL);

        return recipe;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        PersistentDataContainer persistentDataContainer = event.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer();
        if (persistentDataContainer.has(new NamespacedKey(DuckSMP.getInstance(), "event_item"), PersistentDataType.STRING)) {
            if (System.currentTimeMillis() >= 1661691600L * 1000) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("Event items are no longer available."));
                return;
            }
            int amount = event.getItemDrop().getItemStack().getAmount();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> saveStuff(amount, event.getPlayer()), 5L);
                event.getItemDrop().remove();
            }, 20L);
        }
    }

    private void saveStuff(int amount, Player player) {
        final Bson filter = Filters.eq("_id", player.getUniqueId().toString().replaceAll("-", ""));
        Document previous = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("playerData")
                .find(
                        filter
                ).first();
        if (previous == null) {
            Bukkit.getScheduler().runTask(this.plugin, () -> player.kick(Component.text("You are not registered in the database.", NamedTextColor.RED)));
            return;
        }

        previous.put("event_item", (int) previous.getOrDefault("event_item", 0) + amount);


        DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("playerData")
                .updateOne(
                        filter,
                        new Document("$set", previous),
                        new UpdateOptions().upsert(true)
                );

        String count = numberFormat.format(previous.getOrDefault("event_item", amount));
        player.sendMessage(Component.text("You now have ", NamedTextColor.AQUA).append(Component.text(count + " ", NamedTextColor.GOLD)).append(Component.text("event items", NamedTextColor.AQUA)));
    }
}
