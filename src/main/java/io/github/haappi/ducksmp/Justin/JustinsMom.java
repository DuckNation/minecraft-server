package io.github.haappi.ducksmp.Justin;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

import static io.github.haappi.ducksmp.Utils.Utils.noItalics;
import static io.github.haappi.ducksmp.Utils.Utils.random;

public class JustinsMom implements Listener {

    private final DuckSMP plugin;

    public JustinsMom() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            Player killer = warden.getKiller();
            if (warden.getPersistentDataContainer().has(new NamespacedKey(this.plugin, "justins_mom"), PersistentDataType.INTEGER)) {
                ItemStack paper = new ItemStack(Material.PAPER, 1);
                ItemMeta meta = paper.getItemMeta();
                ArrayList<Component> lore = new ArrayList<>();
                lore.add(noItalics(Component.text("One ", NamedTextColor.GOLD).append(noItalics("free night with ", NamedTextColor.GRAY).append(noItalics("JuicetinPlayz's ", NamedTextColor.AQUA).append(noItalics("mother", NamedTextColor.GOLD)).append(noItalics(".", NamedTextColor.GRAY))))));
                meta.lore(lore);
                meta.displayName(noItalics("JustinPlayz's Mother Pass", NamedTextColor.GOLD));
                paper.setItemMeta(meta);
                event.getDrops().add(paper);

                if (killer != null) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(killer.displayName().append(noItalics(" has received a JustinPlayz's Mother Pass!", NamedTextColor.GOLD))));
                }
            }
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() != Material.COOKIE) {
            return;
        }
        if (event.getItemDrop().getItemStack().getAmount() > 1) {
            return;
        }
        if (random.nextInt(0, 100) > 70) {
            event.setCancelled(true);
            Warden cow = event.getPlayer().getWorld().spawn(event.getPlayer().getLocation().add(0, 0.5, 0), Warden.class);
            cow.setCustomNameVisible(true);
            cow.customName(Component.text("Justins... mom?", NamedTextColor.GOLD));
            cow.setGlowing(true);
            cow.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2048.0);
            cow.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0);
            cow.setHealth(420.69);
            cow.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            cow.setRemoveWhenFarAway(false);
            cow.getPersistentDataContainer().set(new NamespacedKey(plugin, "justins_mom"), PersistentDataType.INTEGER, 1);

            SniffSniffSnort goal = new SniffSniffSnort(this.plugin, cow);
            if (!Bukkit.getMobGoals().hasGoal(cow, goal.getKey())) {
                Bukkit.getMobGoals().addGoal(cow, 3, goal);
            }
        }
    }
}
