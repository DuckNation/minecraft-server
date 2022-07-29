package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JavaMenu extends BukkitCommand implements Listener {

    public JavaMenu(String name) {
        super(name);
        Bukkit.getPluginManager().registerEvents(this, DuckSMP.getInstance());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        System.out.println("Sign changed");
        System.out.println(event.getLines());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.sendBlockChange(player.getLocation(), Material.ACACIA_SIGN.createBlockData());
            ClientboundOpenSignEditorPacket packet = new ClientboundOpenSignEditorPacket(BlockPos.of(BlockPos.asLong(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())));
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }

        return true;
    }
    // all colors
    // make them input name on a sign or something
    // even better, use clicking on books like hypixel
    // https://www.spigotmc.org/resources/booknews-1-8-1-19-1.61163/
    // https://github.com/upperlevel/book-api/blob/master/src/main/java/xyz/upperlevel/spigot/book/BookUtil.java
    // https://www.spigotmc.org/threads/book-meta-with-click-event.268026/
    // https://www.spigotmc.org/threads/solved-open-sign-gui-get-input-from-player.380079/
}
