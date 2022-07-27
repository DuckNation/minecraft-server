package io.github.haappi.ducksmp.utils;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static io.github.haappi.ducksmp.utils.Utils.chain;
import static io.github.haappi.ducksmp.utils.Utils.noItalics;



public class GUIUtils implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CustomHolder) {
            if (event.getCurrentItem() == null) {
                return;
            }
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> event.getWhoClicked().closeInventory());

            switch (event.getCurrentItem().getType()) {
                case GREEN_TERRACOTTA: // no
                    event.getWhoClicked().sendMessage(Component.text("Alright, you didn't join LifeSteal! Guess you live for another day", NamedTextColor.RED));
                    break;
                case RED_TERRACOTTA: // yes
                    event.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(DuckSMP.getInstance(), "claimed_hearts"), PersistentDataType.INTEGER, 0);
                    event.getWhoClicked().sendMessage(Component.text("You have joined LifeSteal! Now make sure you don't drop to zero hearts.", NamedTextColor.GREEN));
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("" + event.getWhoClicked().getName() + " has joined LifeSteal!", NamedTextColor.GREEN)));
                    break;
            }
        }

    }

    public static void sendOptInForm(FloodgatePlayer player) {
        SimpleForm.Builder form = SimpleForm.builder()
                .title("Life Steal Information")
                .content("Warning: You will be partaking in LifeSteal SMP. People will be allowed to kill you freely, and you will lose a heart ❤ upon death." +
                        " Regain hearts by killing other players. \n**You CANNOT leave LifeSteal SMP once you have joined.**")
                .button("Join LifeSteal") // id = 0
                .button("Never-mind..."); // id = 1;
        form.closedOrInvalidResultHandler(response -> {
            // no response was given
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            if (response.clickedButtonId() == 0) {
                Bukkit.getPlayer(player.getJavaUniqueId()).sendMessage("Thank you for your feedback!");
            } else {
                Bukkit.getPlayer(player.getJavaUniqueId()).sendMessage("We're sorry to hear that, we'll try to improve our server!");
            }
        });

        player.sendForm(form);
    }

    public static void sendOptInForm(Player player) {
        if (FloodgateApi.getInstance().isFloodgateId(player.getUniqueId())) {
            sendOptInForm(FloodgateApi.getInstance().getPlayer(player.getUniqueId()));
            return;
        }

        Inventory confirmationMenu = Bukkit.createInventory(new CustomHolder(), 45, Component.text("Life Steal Confirmation", NamedTextColor.RED));

        ItemStack readMe = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = readMe.getItemMeta();
        meta.displayName(Component.text("Info", NamedTextColor.AQUA));
        meta.lore(Arrays.asList(
                    chain(noItalics("Warning: You will be partaking in ", NamedTextColor.RED), noItalics("LifeSteal SMP.", NamedTextColor.YELLOW)),
                    noItalics("People will be allowed to kill you freely, and you will lose a heart ❤", NamedTextColor.RED),
                    noItalics(" upon death. Regain hearts by killing other players.", NamedTextColor.RED),
                    chain(noItalics(" You cannot leave "), noItalics("LifeSteal SMP ", NamedTextColor.YELLOW),
                noItalics("once you have joined.")).decoration(TextDecoration.BOLD, true)
                ));
        readMe.setItemMeta(meta);
        confirmationMenu.setItem(13, readMe);

        ItemStack accept = new ItemStack(Material.RED_TERRACOTTA, 1);
        meta = accept.getItemMeta();
        meta.displayName(noItalics("Join LifeSteal", NamedTextColor.RED));
        meta.lore(Collections.singletonList(
                chain(noItalics("I understand the risks. I'm willing to partake in ", NamedTextColor.RED), noItalics("LifeSteal SMP", NamedTextColor.YELLOW))
        ));
        accept.setItemMeta(meta);
        confirmationMenu.setItem(29, accept);

        ItemStack deny = new ItemStack(Material.GREEN_TERRACOTTA, 1);
        meta = deny.getItemMeta();
        meta.displayName(noItalics("Never mind...", NamedTextColor.GREEN));
        meta.lore(Collections.singletonList(
                chain(noItalics("I do not wish to partake in ", NamedTextColor.GREEN), noItalics("LifeSteal SMP", NamedTextColor.YELLOW))
        ));
        deny.setItemMeta(meta);
        confirmationMenu.setItem(33, deny);

        player.openInventory(confirmationMenu);
    }


}

