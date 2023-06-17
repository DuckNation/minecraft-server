package io.github.haappi.duckpaper.utils;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerNMS {
    public static ServerPlayer getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
