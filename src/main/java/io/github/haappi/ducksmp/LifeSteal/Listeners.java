package io.github.haappi.ducksmp.LifeSteal;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static io.github.haappi.ducksmp.utils.GUIUtils.sendOptInJava;
import static io.github.haappi.ducksmp.utils.Utils.*;

public class Listeners implements Listener {

    private final DuckSMP plugin;

//    private final PlayerTeam netherStar = new PlayerTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "Star");

    public Listeners() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        netherStar.setColor(Objects.requireNonNull(PaperAdventure.asVanilla(NamedTextColor.DARK_PURPLE))); // todo lifesteal nether star colors get glowing
        // & armor stand above head
//
//        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
//
//
//        }, 20 * 30L, 40L);
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        ((CraftPlayer) event.getPlayer()).getHandle().connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(netherStar, true));
//    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(new NamespacedKey(plugin, "life_steal"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            /* todo
            a GUI so people can see the location of their hearts
             */
        }
    }

    @EventHandler
    public void onSpawn(ItemSpawnEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(new NamespacedKey(plugin, "life_steal"), PersistentDataType.STRING)) {
            Entity entity = event.getEntity();
            entity.setPersistent(true);
            entity.setInvulnerable(true);
            entity.setGlowing(true);
            entity.setVisualFire(false);
            // todo armor stands above item.
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
        if (event.getItem() != null) {
            if (event.getItem().getItemMeta().getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "life_steal"), PersistentDataType.STRING) != null) {
                event.setCancelled(true);
                Integer claimed = event.getPlayer().getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER);
                if (claimed == null) {
                    claimed = 0;
                }
                if (claimed < 10) {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                    Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() + (2 * 1));
                    event.getPlayer().getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, claimed + 1);
                    event.getPlayer().sendMessage(Component.text("Claimed ", NamedTextColor.GREEN).append(Component.text("1", NamedTextColor.YELLOW).append(Component.text(" hearts.", NamedTextColor.GREEN))));
                } else {
                    event.getPlayer().sendMessage(Component.text("You have already claimed 10 hearts.", NamedTextColor.RED));
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        switch (event.getMessage()) {
            case "clear":
                Integer claimed = event.getPlayer().getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER);
                event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - (2 * claimed));
                event.getPlayer().getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, 0);
                break;
            case "heart":
                event.getPlayer().getInventory().addItem(getHeart(1, event.getPlayer()));
            case "fat":
                Bukkit.getScheduler().runTask(plugin, () -> sendOptInJava(event.getPlayer().getUniqueId()));
        }
    }


}
