package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

public class TotemHandler implements Listener {

    public static final ArrayList<EntityType> mobs = new ArrayList<>(Arrays.asList(
            EntityType.AXOLOTL,
            EntityType.BEE,
            EntityType.CAT,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.GHAST,
            EntityType.ILLUSIONER,
            EntityType.RAVAGER,
            EntityType.SHULKER,
            EntityType.SKELETON_HORSE,
            EntityType.TADPOLE,
            EntityType.TURTLE,
            EntityType.VEX,
            EntityType.VILLAGER,
//            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.ZOGLIN,
            EntityType.WANDERING_TRADER,
            EntityType.GIANT
//            EntityType.BAT
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
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(
                    Component.text("Aaaaaand the random mob that drops totems this time is: ", NamedTextColor.YELLOW)
                            .append(Component.text(randomMob.name(), NamedTextColor.GREEN)
                                    .append(Component.text("! Come back 3 hours later for a new mob!", NamedTextColor.YELLOW))
                            )));
        }, 0, 20 * 60 * 60 * 3); // 3 hours
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
                int randomNumber = Utils.random.nextInt(1, 10);
                if (randomNumber < 3) {
                    ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.lore(List.of(Component.text("Dropped from ", NamedTextColor.YELLOW).append(Component.text(randomMob.name(), NamedTextColor.GREEN)).decoration(TextDecoration.BOLD, false)));
                    item.setItemMeta(meta);
                    event.getDrops().add(item);
                }
            }
        }
    }

}
