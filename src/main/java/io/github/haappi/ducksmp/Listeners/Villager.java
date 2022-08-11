package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import static io.github.haappi.ducksmp.Utils.Utils.random;

public class Villager implements Listener {
    private final DuckSMP plugin;

    public Villager() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Location loc = event.getEntity().getLocation();
        if (event.getEntity().getType() == EntityType.WARDEN) {
            if (event.getEntity().getPersistentDataContainer().has(new NamespacedKey(plugin, "vilager"), PersistentDataType.STRING)) {
                for (int i = 0; i < random.nextInt(6, 9); i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
                }
            }
        }
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (event.getEntity().getType() == org.bukkit.entity.EntityType.VILLAGER) {
            if (random.nextInt(4) != 0) {
                event.setCancelled(false);
                final Component yay = Component.text("Yay! Someone managed to breed a villager without dying!!!").color(NamedTextColor.GREEN);
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(yay));
            } else {
                event.setCancelled(true);
                Warden mob = (Warden) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.WARDEN);
                mob.getPersistentDataContainer().set(new NamespacedKey(plugin, "villager"), PersistentDataType.STRING, "true");
                mob.customName(Component.text("The FBI", NamedTextColor.BLUE));
                final Component boo = Component.text("Uh oh. Someone was trying to breed a villager, and got a warden sniffing them..").color(NamedTextColor.RED);
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(boo));

            }
        }
    }
}
