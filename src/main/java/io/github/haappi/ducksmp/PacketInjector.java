package io.github.haappi.ducksmp;

import io.github.haappi.ducksmp.Commands.Home;
import io.github.haappi.ducksmp.Cosemetics.NameTag.JavaMenu;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.print.Paper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

//import static io.github.haappi.ducksmp.Commands.TPClaim.teleports;
import static io.github.haappi.ducksmp.Commands.Home.pickingName;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.JavaMenu.currentlyInMenu;

// https://www.spigotmc.org/threads/advanced-minecraft-nms-packet-tutorial.538194/
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
                if (packet instanceof ServerboundSignUpdatePacket signUpdatePacket) {
                    if (JavaMenu.responses.containsKey(player.getUniqueId())) {
                        if (JavaMenu.currentlyInMenu.contains(player.getUniqueId())) {
                            JavaMenu.responses.replace(player.getUniqueId(), signUpdatePacket.getLines()[0]);
                            JavaMenu.callback(player);
                        }
                    }

                    if (Home.pickingName.containsKey(player.getUniqueId())) {
                        Home.callback(signUpdatePacket.getLines()[0], Home.pickingName.remove(player.getUniqueId()), player);
                    }
                }
//                Bukkit.getServer().getConsoleSender().sendMessage("Received packet: " + packet.getClass().getName() + " from " + player.getName() + ". " + packet.toString());
                super.channelRead(channelHandlerContext, packet); // this sends stuff to the server. including keep alive packets.
            }

            /*
            Can I play a part of a song to a player?
Ie only the first 30 seconds, or some 30 seconds in the middle of the song?
When I say song, I mean `Player#playSound`
             */

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet == null) return;
//                if (packet instanceof ClientboundSystemChatPacket systemChatPacket) {
//                    if (teleports.contains(player.getUniqueId())) {
//                        String message = systemChatPacket.content();
//                        if (message != null && message.contains("Region Info") && message.contains("{\"text\":\"teleport: \"")) {
//                            System.out.println(systemChatPacket.content());
//                            System.out.println("has permission to teleport: " + systemChatPacket.content().contains(player.getUniqueId().toString().toLowerCase(Locale.ROOT)));
//                        } else {
//                            System.out.println("no perms");
//                        }
//                    }
//                } // fixme randomly crashing because content is null
                // somehow see that worldguard r sending the messages -> decide from there
                // update to 1.19
                super.write(channelHandlerContext, packet, channelPromise); // without this. the player can't do anything
            }
        };

        Channel channel = ((CraftPlayer) player).getHandle().networkManager.channel;
        channel.pipeline().addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}
