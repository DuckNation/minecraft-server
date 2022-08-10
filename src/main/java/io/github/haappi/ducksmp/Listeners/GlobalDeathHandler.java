package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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

import static io.github.haappi.ducksmp.Utils.Serialization.itemStackArrayFromBase64;
import static io.github.haappi.ducksmp.Utils.Serialization.itemStackArrayToBase64;
import static io.github.haappi.ducksmp.Utils.Utils.*;

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
    public void onEntityDamage(EntityDeathEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler
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
            if (event.getDrops().size() == 0) {
                return;
            } // todo show their skin & a death lying position thingy
            Location loc = player.getLocation();
            ArmorStand stand = player.getLocation().getWorld().spawn(loc, ArmorStand.class);

            stand.setCanTick(true);
            stand.setPersistent(true);
            stand.customName(noItalics(player.getName() + "'s grave", NamedTextColor.RED));
            stand.setCustomNameVisible(true);
            stand.setGlowing(true);
            stand.setGravity(true);
            stand.setVisible(true);

            final String base64Items = itemStackArrayToBase64(event.getDrops().toArray(new ItemStack[0]));
            event.getDrops().clear();
            stand.getPersistentDataContainer().set(new NamespacedKey(plugin, "death_items"), PersistentDataType.STRING, base64Items);
            stand.getPersistentDataContainer().set(new NamespacedKey(plugin, "grave"), PersistentDataType.BYTE, (byte) 1);


        }
    }
}

