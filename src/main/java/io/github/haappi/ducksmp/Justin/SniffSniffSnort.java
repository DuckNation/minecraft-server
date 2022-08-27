package io.github.haappi.ducksmp.Justin;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;

public class SniffSniffSnort implements Goal<Warden> {
    private final GoalKey<Warden> key;
    private final Mob mob;
    private Player closestPlayer;
    private int cooldown;


    public SniffSniffSnort(DuckSMP plugin, Mob mob) {
        this.key = GoalKey.of(Warden.class, new NamespacedKey(plugin, "follow_player"));
        this.mob = mob;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK, GoalType.TARGET);
    }

    @Override
    public boolean shouldActivate() {
        if (cooldown > 0) {
            --cooldown;
            return false;
        }
        closestPlayer = getClosestPlayer();
        return closestPlayer != null;
    }

    @Override
    public void stop() {
        mob.getPathfinder().stopPathfinding();
        mob.setTarget(null);
        cooldown = 300;
    }

    @Override
    public void tick() {
        mob.setTarget(closestPlayer);
        if (mob.getLocation().distanceSquared(closestPlayer.getLocation()) < 15.25) {
            mob.getPathfinder().stopPathfinding();
        } else {
            mob.getPathfinder().moveTo(closestPlayer, 1.0D);
        }
    }

    @Override
    public @NotNull GoalKey<Warden> getKey() {
        return key;
    }

    private @Nullable Player getClosestPlayer() {
        Collection<Player> nearbyPlayers = mob.getWorld().getNearbyPlayers(mob.getLocation(), 20.0, player ->
                !player.isDead() && player.getGameMode() == GameMode.SURVIVAL && player.isValid());
        double closestDistance = -1.0;
        Player closestPlayer = null;
        for (Player player : nearbyPlayers) {
            double distance = player.getLocation().distanceSquared(mob.getLocation());
            if (closestDistance != -1.0 && !(distance < closestDistance)) {
                continue;
            }
            closestDistance = distance;
            closestPlayer = player;
        }
        return closestPlayer;
    }


}
