package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class Crystal implements Listener {

    private final DuckSMP plugin;

    public Crystal() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamage(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
            event.blockList().clear();
            if (!event.getLocation().getWorld().getName().equals("world_the_end")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMoreDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.ENDER_CRYSTAL) {
            event.setCancelled(true);
        }
    }

}
