package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalDeathHandler implements Listener {

    private final DuckSMP plugin;

    public GlobalDeathHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private String getDeathPositionFormatted(Location location) {
        return String.format("%s: %s, %s, %s", location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event) {
        AtomicReference<org.bukkit.block.data.type.Chest> chestData1 = new AtomicReference<>();
        AtomicReference<org.bukkit.block.data.type.Chest> chestData2 = new AtomicReference<>();

        if (event.getEntity() instanceof Player player) {
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "last_death_location"), PersistentDataType.STRING, getDeathPositionFormatted(player.getLocation()));
            AtomicReference<Chest> bChest = new AtomicReference<>();

            Location loc = player.getLocation();
            final List<ItemStack> content = new ArrayList<>(event.getDrops());

            final ItemStack[] items = content.toArray(new ItemStack[0]);
            event.getDrops().clear();
            final double x = loc.getX();

            final Location x1 = loc.clone();
            final Location x2 = loc.clone();

            x2.setX(x + 1);

            Block block1 = x1.getBlock();
            Block block2 = x2.getBlock();

            int itemAmount = items.length;


            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                block1.setType(Material.CHEST);
                if (itemAmount >= 27) {
                    block2.setType(Material.CHEST);

                    Chest chest1 = (Chest) block1.getState();
                    Chest chest2 = (Chest) block2.getState();

                    chestData1.set((org.bukkit.block.data.type.Chest) chest1.getBlockData());
                    chestData2.set((org.bukkit.block.data.type.Chest) chest2.getBlockData());

                    chestData1.get().setType(org.bukkit.block.data.type.Chest.Type.LEFT);
                    block1.setBlockData(chestData1.get(), true);
                    chestData2.get().setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
                    block2.setBlockData(chestData2.get(), true);
                }
                bChest.set((Chest) x1.getBlock().getState());
                bChest.get().customName(Component.text(player.getName() + "'s grave", NamedTextColor.AQUA));
                bChest.get().update();


                for (final ItemStack item : items) {
                    Chest bChest1 = (Chest) x1.getBlock().getState();
                    bChest1.getInventory().addItem(item);
                }
            }, 20 * 2L); // 2 seconds
        }
    }
}

