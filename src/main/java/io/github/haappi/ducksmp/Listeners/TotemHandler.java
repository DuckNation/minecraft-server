package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.haappi.ducksmp.Commands.ChangeMob.updateInDiscord;

public class TotemHandler implements Listener {

    public static final ArrayList<EntityType> mobs = new ArrayList<>(Arrays.asList(
            EntityType.AXOLOTL,
            EntityType.BEE,
            EntityType.CAT,
            EntityType.EVOKER,
            EntityType.GHAST,
            EntityType.SHULKER,
            EntityType.SKELETON_HORSE,
            EntityType.TADPOLE,
            EntityType.TURTLE,
            EntityType.WITCH,
            EntityType.ZOGLIN,
            EntityType.WANDERING_TRADER
    ));
    public static EntityType randomMob;
    private final DuckSMP plugin;


    public TotemHandler() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        EntityType[] mobs = Stream.of(EntityType.values())
//                .filter(EntityType::isSpawnable)
//                .filter(type -> type.getEntityClass() != null)
//                .filter(type -> LivingEntity.class.isAssignableFrom(type.getEntityClass()))
//                .toArray(EntityType[]::new);
//        ;

        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            randomMob = mobs.get(Utils.random.nextInt(mobs.size()));
            updateInDiscord();
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(
                    Component.text("Aaaaaand the random mob that drops totems this time is: ", NamedTextColor.YELLOW)
                            .append(Component.text(randomMob.name(), NamedTextColor.GREEN)
                                    .append(Component.text("! Come back 3 hours later for a new mob!", NamedTextColor.YELLOW))
                            )));
        }, 0, 20 * 60 * 60 * 2); // 3 hours
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Aaaaaand the random mob that drops totems this time is: ", NamedTextColor.YELLOW).append(Component.text(randomMob.name(), NamedTextColor.GREEN)));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity().getType() == randomMob) {
                int randomNumber = Utils.random.nextInt(1, 9);
                if (randomNumber < 5) {
                    ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.lore(List.of(Utils.chain(Utils.noItalics("Dropped from", NamedTextColor.YELLOW), Utils.noItalics(": ", NamedTextColor.GRAY), Utils.noItalics(randomMob.name(), NamedTextColor.GREEN))));
                    item.setItemMeta(meta);
                    event.getDrops().add(item);
                }
            }
        }
    }

}
