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
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class totem implements Listener {

    private final DuckSMP plugin;

    public static EntityType randomMob;

    public totem() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        EntityType[] mobs = Stream.of(EntityType.values())
//                .filter(EntityType::isSpawnable)
//                .filter(type -> type.getEntityClass() != null)
//                .filter(type -> LivingEntity.class.isAssignableFrom(type.getEntityClass()))
//                .toArray(EntityType[]::new);
        ArrayList<EntityType> mobs = new ArrayList<>();
        mobs.add(EntityType.AXOLOTL);
        mobs.add(EntityType.BEE);
        mobs.add(EntityType.BLAZE);
        mobs.add(EntityType.CAT);
        mobs.add(EntityType.ELDER_GUARDIAN);
        mobs.add(EntityType.ENDER_DRAGON);
        mobs.add(EntityType.ENDERMITE);
        mobs.add(EntityType.EVOKER);
        mobs.add(EntityType.GHAST);
        mobs.add(EntityType.HUSK);
        mobs.add(EntityType.ILLUSIONER);
        mobs.add(EntityType.RAVAGER);
        mobs.add(EntityType.SHULKER);
        mobs.add(EntityType.SKELETON_HORSE);
        mobs.add(EntityType.TADPOLE);
        mobs.add(EntityType.TURTLE);
        mobs.add(EntityType.VEX);
        mobs.add(EntityType.VILLAGER);
        mobs.add(EntityType.WITCH);
        mobs.add(EntityType.WITHER);
        mobs.add(EntityType.ZOGLIN);
        mobs.add(EntityType.WANDERING_TRADER);
        mobs.add(EntityType.GIANT);
        mobs.add(EntityType.BAT);
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
                if (randomNumber < 2) {
                    event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Pig entity) {
            if (PaperAdventure.asPlain(entity.customName(), Locale.US).equals("Technoblade")) {
                event.setCancelled(true);
            }
        }
    }

}
