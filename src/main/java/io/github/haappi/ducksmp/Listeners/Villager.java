package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class Villager implements Listener {
    private final DuckSMP plugin;

    public Villager() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (event.getEntity().getType() == org.bukkit.entity.EntityType.VILLAGER) {
            event.setCancelled(true);
            if (event.getBreeder() != null) {
                event.getBreeder().sendMessage(Component.text("You can't breed villagers.", NamedTextColor.RED));
            }
        }
    }
}
