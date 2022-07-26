package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class totem implements Listener {

    private final DuckSMP plugin;

    private EntityType randomMob;

    public totem() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        EntityType[] mobs = Stream.of(EntityType.values())
                .filter(EntityType::isSpawnable)
                .filter(type -> type.getEntityClass() != null)
                .filter(type -> LivingEntity.class.isAssignableFrom(type.getEntityClass()))
                .toArray(EntityType[]::new);
        randomMob = mobs[ThreadLocalRandom.current().nextInt(mobs.length)];
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Aaaaaand the random mob that drops totems this reset is: ", NamedTextColor.YELLOW).append(Component.text(randomMob.name(), NamedTextColor.GREEN)));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity().getType() == randomMob) {
                event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // todo if pig named technoblade (cancel damage)
    }

}
