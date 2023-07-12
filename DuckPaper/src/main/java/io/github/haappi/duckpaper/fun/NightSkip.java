package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.DuckPaper;
import io.github.haappi.duckpaper.utils.PlayerNMS;
import io.github.haappi.duckpaper.utils.Utils;
import io.github.haappi.duckpaper.utils.WorldTime;
import io.papermc.paper.event.player.PlayerBedFailEnterEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NightSkip implements Listener {
    private final Set<UUID> sleepingNoobs = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        addToSleeping(event.getPlayer());
        Utils.scheduleNextTick(() -> worldCheckTask(event.getPlayer().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void rewriteMessage(PlayerBedFailEnterEvent event) {
        if (event.getFailReason() == PlayerBedFailEnterEvent.FailReason.NOT_SAFE) {
            event.setMessage(Component.text("You may not sleep now, there are hot singles in your area.", NamedTextColor.GRAY));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        sleepingNoobs.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        sleepingNoobs.remove(event.getPlayer().getUniqueId());
        worldCheckTask(event.getPlayer().getWorld());
    }


    private void addToSleeping(Player player) {
        sleepingNoobs.add(player.getUniqueId());

        int count = (int) Math.ceil(getPossibleSleepers(player.getWorld()) * player.getWorld().getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE));
        int sleepingCount = sleepingNoobs.size();

        Component message = Component.text()
                .append(Component.text("[" + sleepingCount + "/" + count + "] ", NamedTextColor.GRAY))
                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                .append(Component.text(" is sleeping", NamedTextColor.GRAY))
                .build();

        player.getServer().getOnlinePlayers().forEach(p -> p.sendActionBar(message));
    }

    private long getPossibleSleepers(final World world) {
        return world.getPlayers().stream().filter(p -> p.getGameMode() == GameMode.SURVIVAL).count();
    }

    private void worldCheckTask(World world) {
        if (enoughPlayersAsleep(world)) {
            Utils.runTaskLater(() -> doTask(world), (long) 1);
        }
    }

    private void doTask(World world) {
        if (!enoughPlayersAsleep(world)) {
            return;
        }
        if (world.getTime() > 0 && world.getTime() < 12541) {
            return;
        }
        WorldTime.changeTime(world, DuckPaper.getInstance(), 24000, 200);
        world.setStorm(false);
        world.setThundering(false);

        Utils.runTaskLater(() -> world.getPlayers().stream().filter(Player::isSleeping)
                .forEach(p -> PlayerNMS.getPlayer(p).stopSleepInBed(false, false)
                ), 100 - 5L); // mc takes 5s to naturally skip night

        resetSleepingNoobs();
    }

    private boolean enoughPlayersAsleep(World world) {
        return sleepingNoobs.size() >= Math.ceil(getPossibleSleepers(world) * world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE));
    }


    private void resetSleepingNoobs() {
        sleepingNoobs.clear();
    }
}
