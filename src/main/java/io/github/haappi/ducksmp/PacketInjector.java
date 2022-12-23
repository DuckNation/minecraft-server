package io.github.haappi.ducksmp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketInjector {

    public PacketInjector() {
        throw new RuntimeException("Unable to instantiate a static class");
    }

    public static void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.getConnection().channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    public static void injectPlayer(@NotNull Player player) {

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet); // this sends stuff to the server. including keep alive packets.
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet == null) return;
                super.write(channelHandlerContext, packet, channelPromise); // without this. the player can't do anything
            }
        };

        Channel channel = ((CraftPlayer) player).getHandle().connection.getConnection().channel;
        channel.pipeline().addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}