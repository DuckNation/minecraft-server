package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.haappi.ducksmp.Utils.Serialization.itemStackArrayFromBase64;
import static io.github.haappi.ducksmp.Utils.Utils.chain;
import static io.github.haappi.ducksmp.Utils.Utils.random;

public class GlobalDeathHandler implements Listener {

    private final DuckSMP plugin;

    public GlobalDeathHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private String getDeathPositionFormatted(Location location) {
        return String.format("%s: %s, %s, %s", location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    //    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDeathEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    //    @EventHandler
    public void onSlap(EntityDamageByEntityEvent event) throws IOException {
        if (event.getEntity() instanceof ArmorStand armorStand) {
            if (!(event.getDamager() instanceof Player)) {
                return; // only players can slap armor stands
            }
            if (armorStand.getPersistentDataContainer().has(new NamespacedKey(plugin, "grave"), PersistentDataType.BYTE)) {
                event.setCancelled(true);

                armorStand.setHealth(0);
                armorStand.remove();

                ItemStack[] items = itemStackArrayFromBase64(armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "death_items"), PersistentDataType.STRING));

                for (ItemStack item : items) {
                    if (item == null) {
                        continue;
                    }
                    armorStand.getWorld().dropItemNaturally(armorStand.getLocation(), item);
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "last_death_location"), PersistentDataType.STRING, getDeathPositionFormatted(player.getLocation()));
            Location playerPosition = player.getLocation().clone();
            playerPosition.add(random.nextInt(-50, 50), 0, random.nextInt(-50, 50));
            player.sendMessage(Component.text("Ah so yes. Ahem, my magical sources tell me that you died somewhere around ", NamedTextColor.AQUA).append(Component.text(getDeathPositionFormatted(playerPosition), NamedTextColor.GOLD)));
            player.sendMessage(chain(Component.text("But my fuzzy memory forgot where it was exactly so ermmmm, it's within like ", NamedTextColor.RED), Component.text("100 blocks", NamedTextColor.GOLD).decorate(TextDecoration.BOLD), Component.text(" or so!!! Hope this helps heh", NamedTextColor.RED)));

            if (player.getKiller() != null) {
                return; // Don't drop a chest if the player was killed by another player
            }

            AtomicReference<Chest> chestData1 = new AtomicReference<>();
            AtomicReference<org.bukkit.block.data.type.Chest> chestData2 = new AtomicReference<>();
            AtomicReference<org.bukkit.block.Chest> bChest = new AtomicReference<>();

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

            if (itemAmount == 0) {
                return;
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                block1.setType(Material.CHEST);
                if (itemAmount >= 27) {
                    block2.setType(Material.CHEST);

                    org.bukkit.block.Chest chest1 = (org.bukkit.block.Chest) block1.getState();
                    org.bukkit.block.Chest chest2 = (org.bukkit.block.Chest) block2.getState();

                    chestData1.set((org.bukkit.block.data.type.Chest) chest1.getBlockData());
                    chestData2.set((org.bukkit.block.data.type.Chest) chest2.getBlockData());

                    chestData1.get().setType(org.bukkit.block.data.type.Chest.Type.LEFT);
                    block1.setBlockData(chestData1.get(), true);
                    chestData2.get().setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
                    block2.setBlockData(chestData2.get(), true);
                }
                bChest.set((org.bukkit.block.Chest) x1.getBlock().getState());
                bChest.get().customName(Component.text(player.getName() + "'s grave", NamedTextColor.AQUA));
                bChest.get().update();


                for (final ItemStack item : items) {
                    org.bukkit.block.Chest bChest1 = (org.bukkit.block.Chest) x1.getBlock().getState();
                    bChest1.getInventory().addItem(item);
                }
            }, 20 * 2L); // 2 seconds
        }
    }
}

