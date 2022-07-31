package io.github.haappi.ducksmp.LifeSteal;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import static io.github.haappi.ducksmp.utils.GUIUtils.sendOptInForm;
import static io.github.haappi.ducksmp.utils.Utils.*;

public class Listeners implements Listener {

    private final DuckSMP plugin;

    private final ConcurrentHashMap<UUID, UUID> armorMap = new ConcurrentHashMap<>();


    public Listeners() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::armorStandTask, 20L, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawn(ItemDespawnEvent event) {
        if (isLifeStealItem(event.getEntity().getItemStack())) {
            event.setCancelled(true);
            /* todo
            a GUI so people can see the location of their hearts
             */
        }
    }

        @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
//        event.setCancelled(true);
        removeStand(event.getItem().getUniqueId());
//        event.setCancelled(false);
    }

    private void removeStand(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (armorMap.get(armorMap.get(uuid)) == null) {
            armorMap.remove(uuid);
        }
        ArmorStand stand = (ArmorStand) Bukkit.getEntity(armorMap.get(uuid));
        if (stand != null) {
            stand.remove();
        }
    }

    @EventHandler
    public void entityMergeEvent(ItemMergeEvent event) {
        if (!isLifeStealItem(event.getEntity().getItemStack())) {
            return;
        }
        event.setCancelled(true);
//        removeStand(event.getEntity().getUniqueId());
//        removeStand(event.getTarget().getUniqueId());
    }

    private void armorStandTask() {
        for (Map.Entry<UUID, UUID> entry : this.armorMap.entrySet()) {
            ArmorStand stand = (ArmorStand) Bukkit.getEntity(entry.getValue());
            Entity entity = Bukkit.getEntity(entry.getKey());

            if (entity == null) { // todo check if armor stand is null -> make a new one
                removeStand(entry.getKey());
                continue;
            }

            if (stand == null) {
                stand = Utils.createStand(entity, 1);
            }

            if (stand.isDead()) {
                armorMap.remove(entry.getKey());
                stand.remove();
                continue;
            }


            if (stand.getLocation().add(0, 2.15, 0) != entity.getLocation()) {
                stand.teleport(entity.getLocation().add(0, 0.15, 0));
            }
        }


    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (!isLifeStealItem(item.getItemStack())) {
                return;
            }

            if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
//                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("There's a dropped heart at: " + item.getLocation().getBlockX() + ", " + item.getLocation().getBlockY() + ", " + item.getLocation().getBlockZ(), NamedTextColor.RED)));
            } else {
                item.teleport(item.getWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        ConcurrentLinkedDeque<Entity> entitiesList = new ConcurrentLinkedDeque<>();
        Collections.addAll(entitiesList, event.getChunk().getEntities());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Entity entity : entitiesList) {
                if (entity instanceof Item item) {
                    if (isLifeStealItem(item.getItemStack())) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> armorMap.put(entity.getUniqueId(), createStand(entity, 1).getUniqueId()), 1L);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        ConcurrentLinkedDeque<Entity> entitiesList = new ConcurrentLinkedDeque<>();
        Collections.addAll(entitiesList, event.getChunk().getEntities());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Entity entity : entitiesList) {
                if (entity instanceof Item item) {
                    if (isLifeStealItem(item.getItemStack())) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> removeStand(entity.getUniqueId()), 1L);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onSpawn(ItemSpawnEvent event) {
        if (isLifeStealItem(event.getEntity().getItemStack())) {
            Entity entity = event.getEntity();
            entity.setPersistent(true);
            entity.setInvulnerable(true);
            entity.setGlowing(true);
            entity.setVisualFire(false);
            entity.setVelocity(entity.getVelocity().setY(0.2));

            armorMap.put(entity.getUniqueId(), Utils.createStand(entity, 1).getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            Integer claimed = player.getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER);
            if (claimed == null) {
                if (player.getKiller() != null) {
                    player.getKiller().sendMessage(Component.text("Dang you just killed someone who isn't in life steal."));
                }
                return;
            }
            if (claimed > -6) { // they live with 3 hearts.
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() - 2);
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, claimed - 1);
            } else {
                return; // return, they might be in negative hearts & can't lose anymore.
            }

            if (player.getKiller() != null) {
                Player killer = player.getKiller();
                if (!killer.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER)) {
                    return; // don't drop hearts if killer isn't in lifesteal
                }
            }

            event.getDrops().add(getHeart(1, player));

        }
    }

    @EventHandler
    public void craftItem(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        for (ItemStack stack : inv.getStorageContents()) {
            if (isLifeStealItem(stack)) {
                inv.setResult(null);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction().isLeftClick() ||
                event.getAction().isRightClick())) {
            return;
        }
        if (event.getItem() != null) {
            if (isLifeStealItem(event.getItem())) {
                event.setCancelled(true);
                Integer claimed = event.getPlayer().getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER);
                if (claimed == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> sendOptInForm(event.getPlayer()));
                    return;
                }
                if (claimed < 10) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() + (2));
                    event.getPlayer().getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, claimed + 1);
                    event.getPlayer().sendMessage(Component.text("Claimed ", NamedTextColor.GREEN).append(Component.text("one", NamedTextColor.YELLOW).append(Component.text(" heart.", NamedTextColor.GREEN))));
                } else {
                    event.getPlayer().sendMessage(Component.text("You have already claimed 10 hearts.", NamedTextColor.RED));
                }
            }
        }
    }


}
