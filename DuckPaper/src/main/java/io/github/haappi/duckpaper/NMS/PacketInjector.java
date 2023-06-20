package io.github.haappi.duckpaper.NMS;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketInjector {

    public PacketInjector() {
        throw new RuntimeException("Unable to instantiate a static class");
    }

    public static void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
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
                if (packet instanceof ClientboundSetEntityDataPacket clientboundSetEntityDataPacket) {
                    Integer idxNameVisible = null;

                    for (int i = 0; i < clientboundSetEntityDataPacket.packedItems().size(); i++) {
                        final SynchedEntityData.DataValue<?> item = clientboundSetEntityDataPacket.packedItems().get(i);
//                        if(item.id() == 2) idxNameComponent = i;
                        if(item.id() == 3) idxNameVisible = i;
                    }

//                    for (SynchedEntityData.DataValue val : clientboundSetEntityDataPacket.packedItems()) {
//                        if(customNameVisible != null) {
                            SynchedEntityData.DataValue<Boolean> dataValueVisible =
                                    new SynchedEntityData.DataItem<>(
                                            new EntityDataAccessor<>(
                                                    3,
                                                    EntityDataSerializers.BOOLEAN
                                            ),
                                            true
                                    ).value();

                            if (idxNameVisible == null) {
                                clientboundSetEntityDataPacket.packedItems().add(dataValueVisible);
                            } else {
                                clientboundSetEntityDataPacket.packedItems().set(idxNameVisible, dataValueVisible);
                            }
//                    System.out.println("overrode name visible");
//                            super.write(channelHandlerContext, clientboundSetEntityDataPacket, channelPromise);
//                        }

//                    }
                } else {
                    super.write(channelHandlerContext, packet, channelPromise); // without this. the player can't do anything
                }
            }
        };

        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.pipeline().addAfter("packet_handler", player.getName(), channelDuplexHandler);
    }
}