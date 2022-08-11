package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import static io.github.haappi.ducksmp.Utils.Utils.random;

public class Villager implements Listener {
    private final DuckSMP plugin;

    public Villager() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (event.getEntity().getType() == org.bukkit.entity.EntityType.VILLAGER) {
            if (random.nextBoolean()) {
                event.setCancelled(false);
                final Component yay = Component.text("Yay! Someone managed to breed a villager without dying!!!").color(NamedTextColor.GREEN);
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(yay));
                for (int i = 0; i < random.nextInt(6, 12); i++) {
                    event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.VILLAGER, true);
                }
            } else {
                event.setCancelled(true);
                Warden mob = (Warden) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.WARDEN);
                mob.customName(Component.text("The FBI", NamedTextColor.BLUE));
                final Component boo = Component.text("Uh oh. Someone was trying to breed a villager, and got a warden sniffing them..").color(NamedTextColor.RED);
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(boo));

            }
        }
    }
}
