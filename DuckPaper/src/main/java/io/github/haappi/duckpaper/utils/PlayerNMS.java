package io.github.haappi.duckpaper.utils;

import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerNMS {
    public static ServerPlayer getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public static void applyNightVision(Player player) {
        ClientboundUpdateMobEffectPacket effect = new ClientboundUpdateMobEffectPacket(player.getEntityId(), new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
        getPlayer(player).connection.send(effect);
    }

    public static void removeNightVision(Player player) {
        ClientboundRemoveMobEffectPacket effect = new ClientboundRemoveMobEffectPacket(player.getEntityId(), MobEffects.NIGHT_VISION);
        getPlayer(player).connection.send(effect);
    }
}
