package io.github.haappi.ducksmp.LifeSteal;

import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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



    public static ItemStack getHeart(int count) {
        ItemStack thing = new ItemStack(Material.NETHER_STAR, count);
        ItemMeta meta = thing.getItemMeta();
        meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(DuckSMP.getInstance(), "life_steal"), PersistentDataType.STRING, "true");

        List<Component> lore = Arrays.asList(
                Component.text(""),
                Component.text("Life Steal Heart.", NamedTextColor.GOLD),
                Component.text("Click to claim ", NamedTextColor.AQUA).append(Component.text("1 heart", NamedTextColor.YELLOW).append(Component.text(" ❤", NamedTextColor.RED)))
        );

        meta.lore(lore);
        meta.displayName(Component.text("LifeSteal Heart", NamedTextColor.YELLOW));
        thing.setItemMeta(meta);

        return thing;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            Integer claimed = player.getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER);
            if (claimed != null && claimed > 0) {
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() - 2);
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, claimed - 1);
            } else {
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, 0);
                return;
            }

            if (player.getKiller() != null) {
                Player killer = player.getKiller();
                Integer hearts = killer.getPersistentDataContainer().get(new NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER);
                if (hearts == null) {
                    hearts = 0;
                }
                if (hearts < 10) {
                    killer.getPersistentDataContainer().set(new NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, hearts + 1);
                    Objects.requireNonNull(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() + 2);
                    killer.sendMessage(Component.text("You have stolen a heart from " + player.getName() + ".", NamedTextColor.GREEN));
                    return;
                }
            }
            event.getDrops().add(getHeart(1));

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
                    event.getPlayer().sendMessage(Component.text("Claimed " , NamedTextColor.GREEN).append(Component.text("1", NamedTextColor.YELLOW).append(Component.text(" hearts.", NamedTextColor.GREEN))));
                } else {
                    event.getPlayer().sendMessage(Component.text("You have already claimed 10 hearts.", NamedTextColor.RED));
                }
            }
        } else {
            event.getPlayer().getInventory().addItem(getHeart(1));
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.getPlayer().getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "claimed_hearts"), PersistentDataType.INTEGER, 0);
    }


}
