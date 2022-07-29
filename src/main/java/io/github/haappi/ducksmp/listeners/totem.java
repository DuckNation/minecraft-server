package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.utils.Utils;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class totem implements Listener {

    private final DuckSMP plugin;

    public static EntityType randomMob;
    public static final ArrayList<EntityType> mobs = new ArrayList<>(Arrays.asList(
            EntityType.AXOLOTL,
            EntityType.BEE,
            EntityType.CAT,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.GHAST,
            EntityType.HUSK,
            EntityType.ILLUSIONER,
            EntityType.RAVAGER,
            EntityType.SHULKER,
            EntityType.SKELETON_HORSE,
            EntityType.TADPOLE,
            EntityType.TURTLE,
            EntityType.VEX,
            EntityType.VILLAGER,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.ZOGLIN,
            EntityType.WANDERING_TRADER,
            EntityType.GIANT,
            EntityType.BAT
    ));


    public totem() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        EntityType[] mobs = Stream.of(EntityType.values())
//                .filter(EntityType::isSpawnable)
//                .filter(type -> type.getEntityClass() != null)
//                .filter(type -> LivingEntity.class.isAssignableFrom(type.getEntityClass()))
//                .toArray(EntityType[]::new);
        ArrayList<EntityType> mobs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mobs.add(EntityType.VILLAGER);
        }
        for (int i = 0; i < 4; i++) {
            mobs.add(EntityType.WITHER);
        }
        randomMob = mobs.get(Utils.random.nextInt(mobs.size()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Aaaaaand the random mob that drops totems this time is: ", NamedTextColor.YELLOW).append(Component.text(randomMob.name(), NamedTextColor.GREEN)));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity().getType() == randomMob) {
                int randomNumber = Utils.random.nextInt(1, 10);
//                event.getEntity().getKiller().sendMessage(String.valueOf(randomNumber));
                if (randomNumber < 5) {
                    event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
                }
            }
        }
    }

//    @EventHandler
//    public void onEntityDamage(EntityDamageEvent event) {
//        if (event.getEntity() instanceof Pig entity) {
//            if (PaperAdventure.asPlain(entity.customName(), Locale.US).equals("Technoblade")) {
//                event.setCancelled(true);
//            }
//        }
//    }

}
