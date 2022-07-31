package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlobalDeathHandler implements Listener {

    private final DuckSMP plugin;

    public GlobalDeathHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        org.bukkit.block.data.type.Chest chestData1;
        org.bukkit.block.data.type.Chest chestData2;

        if (event.getEntity() instanceof Player player) {
            org.bukkit.block.Chest bChest;

            Location loc = player.getLocation();
            final List<ItemStack> content = new ArrayList<>(event.getDrops());

            final ItemStack[] items = content.toArray(new ItemStack[0]);
            final double x = loc.getX();

            final Location x1 = loc.clone();
            final Location x2 = loc.clone();

            x2.setX(x + 1);

            Block block1 = x1.getBlock();
            Block block2 = x2.getBlock();

            int itemAmount = 0;
            for (final ItemStack ignored : items) {
                itemAmount++;
            }
            block1.setType(Material.CHEST);
            if (itemAmount >= 27) {
                block2.setType(Material.CHEST);

                Chest chest1 = (Chest) block1.getState();
                Chest chest2 = (Chest) block2.getState();

                chestData1 = (org.bukkit.block.data.type.Chest) chest1.getBlockData();
                chestData2 = (org.bukkit.block.data.type.Chest) chest2.getBlockData();

                chestData1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
                block1.setBlockData(chestData1, true);
                chestData2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
                block2.setBlockData(chestData2, true);
            }
            bChest = (org.bukkit.block.Chest) x1.getBlock().getState();
            bChest.customName(Component.text(player.getName() + "'s grave", NamedTextColor.AQUA));
            bChest.update();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (final ItemStack item : items) {
                    Chest bChest1 = (Chest) x1.getBlock().getState();
                    bChest1.getInventory().addItem(item);
                }
            }, 10);
            event.getDrops().clear();
        }
    }
}

