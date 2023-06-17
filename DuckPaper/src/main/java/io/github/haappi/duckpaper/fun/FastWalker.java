package io.github.haappi.duckpaper.fun;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class FastWalker implements Listener {
    private final Set<Material> fastWalkingBlocks = new HashSet<>();
    private final PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 40, 0)
            .withAmbient(false)
            .withParticles(false)
            .withIcon(false);

    public FastWalker() {
        fastWalkingBlocks.add(Material.DIRT_PATH);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityMove(final EntityMoveEvent event) {
        final LivingEntity entity = event.getEntity();

        Block block = event.getTo().clone().subtract(0.0, 0.1, 0.0).getBlock();
        if (!fastWalkingBlocks.contains(block.getType())) {
            return;
        }

        // Apply potion effect
        entity.addPotionEffect(effect);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.getWalkSpeed() != 0.2F) { // prevents weird effects when player is already fast & i try removing
            return;
        }
        if (player.isGliding()) {
            return;
        }

        LivingEntity entity = player;
        if (player.isInsideVehicle() && player.getVehicle() instanceof LivingEntity vehicle) {
            entity = vehicle;
        }

        Block block = entity.getLocation().clone().subtract(0.0, 0.1, 0.0).getBlock();
        if (!fastWalkingBlocks.contains(block.getType())) {
            return;
        }

        entity.addPotionEffect(effect);
    }
}
