package io.github.haappi.ducksmp.internals;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class nv extends BukkitCommand {
    public nv(String nv) {
        super(nv);
    }

    @Override
    public boolean execute(@NotNull org.bukkit.command.CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            ClientboundUpdateMobEffectPacket eff = new ClientboundUpdateMobEffectPacket(player.getEntityId(), new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, true));
            ((CraftPlayer) player).getHandle().connection.send(eff);
            player.sendMessage(Component.text("Applied night vision! Re-log to remove it.", NamedTextColor.GREEN));
        }
        return true;
    }
}
