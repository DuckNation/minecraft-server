package io.github.haappi.duckpaper.nametag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.Nullable;

import static io.github.haappi.duckpaper.utils.Utils.scheduleNextTick;

public class Pets implements Listener {

    private TextComponent getPetName(@Nullable AnimalTamer ownerName, String petName, double petHP) {
        String owner = ownerName != null ? ownerName.getName() : "null";
        owner = owner != null ? owner : "null";
        if (petName.split("\\|").length != 1) {
            petName = petName.split(" \\|")[1].trim();
        }
        return Component.text()
                .append(Component.text(owner, NamedTextColor.GOLD))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text(petName, NamedTextColor.DARK_PURPLE))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text((int) petHP + "♥", NamedTextColor.RED))
                .build();
    }


    @EventHandler(ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (event.getEntity() instanceof Tameable tameable) {
            setName(tameable);
            tameable.setCustomNameVisible(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Tameable tameable) {
            setName(tameable);
        }
    }

    @EventHandler
    public void onRename(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Tameable tameable) {
            scheduleNextTick(() -> setName(tameable));
        }
    }

    private void setName(Tameable tameable) {
        if (!tameable.isTamed()) {
            return;
        }
        if (tameable.customName() == null) {
            tameable.customName(getPetName(tameable.getOwner(), tameable.getType().name(), tameable.getHealth()));
        } else {
            tameable.customName(getPetName(tameable.getOwner(), PlainTextComponentSerializer.plainText().serialize(tameable.customName()), tameable.getHealth()));
        }
    }
}
