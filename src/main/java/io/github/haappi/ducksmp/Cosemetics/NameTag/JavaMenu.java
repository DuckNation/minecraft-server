package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.colorMapping;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.setStuff;

public class JavaMenu extends BukkitCommand implements Listener {

    public static final HashMap<UUID, String> responses = new HashMap<>();
    public static final HashMap<UUID, String> colors = new HashMap<>();

    private final Map<String, Integer> internalColorMapping = Map.ofEntries(
            Map.entry(NamedTextColor.DARK_BLUE.asHexString(), 0),
            Map.entry(NamedTextColor.DARK_GREEN.asHexString(), 1),
            Map.entry(NamedTextColor.DARK_AQUA.asHexString(), 2),
            Map.entry(NamedTextColor.DARK_RED.asHexString(), 3),
            Map.entry(NamedTextColor.DARK_PURPLE.asHexString(), 4),
            Map.entry(NamedTextColor.GOLD.asHexString(), 5),
            Map.entry(NamedTextColor.GRAY.asHexString(), 6),
            Map.entry(NamedTextColor.DARK_GRAY.asHexString(), 7),
            Map.entry(NamedTextColor.BLUE.asHexString(), 8),
            Map.entry(NamedTextColor.GREEN.asHexString(), 9),
            Map.entry(NamedTextColor.AQUA.asHexString(), 10),
            Map.entry(NamedTextColor.RED.asHexString(), 11),
            Map.entry(NamedTextColor.LIGHT_PURPLE.asHexString(), 12),
            Map.entry(NamedTextColor.YELLOW.asHexString(), 13)
    );


    public JavaMenu(String name) {
        super(name);
        Bukkit.getPluginManager().registerEvents(this, DuckSMP.getInstance());
    }

    public static void callback(Player player) {
        Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> {
            if (responses.containsKey(player.getUniqueId())) {
                String response = responses.get(player.getUniqueId());
                Bukkit.getServer().dispatchCommand(player, "menu withcolorname " + response);
            }
        });

    }

    private List<Component> getAllColors() {
        List<Component> colors = new ArrayList<>();
        int current = 0;
        for (NamedTextColor color : NamedTextColor.NAMES.values()) {
            if (current % 2 == 0) {
                colors.add(Component.newline());
            }
            String colorName = WordUtils.capitalizeFully(color.toString().toLowerCase().replace("_", " "));
            if (colorName.equals("White") || colorName.equals("Black")) {
               continue;
            }
            colors.add(Component.text(colorName, color).append(Component.text(" ", NamedTextColor.WHITE)).hoverEvent(HoverEvent.showText(Component.text("Click to set your name color to " + colorName))).clickEvent(ClickEvent.runCommand("/menu color " + color.asHexString())));
            current++;

        }
        return colors;
    }

    private Book getBook(@NotNull String hexString, @NotNull String name) {
        Component component = Component.text(name, NamedTextColor.nearestTo(TextColor.fromHexString(hexString))).decoration(TextDecoration.BOLD, true).hoverEvent(HoverEvent.showText(Component.text("Click to change your tag"))).clickEvent(ClickEvent.runCommand("/menu setname")).append(Component.newline());
        for (Component color : getAllColors()) {
            component = component.append(color);
        }
        component = component.append(Component.newline().append(Component.newline()));

        Component submitComponent = Component.text("Submit", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.showText(Component.text("Submit your changes"))).clickEvent(ClickEvent.runCommand("/menu done"));

        component = component.append(submitComponent);
        return Book.book(Component.text("Name Tag"), Component.text("ur mum"), List.of(component));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {

            if (args.length < 1) {
                Book book = getBook(NamedTextColor.RED.asHexString(), "Name Tag");
                player.openBook(book);
                colors.put(player.getUniqueId(), NamedTextColor.RED.asHexString());
            } else {
                List<String> _args = Arrays.stream(args).toList();

                switch (_args.get(0)) {
                    case "color":
                        responses.put(player.getUniqueId(), responses.getOrDefault(player.getUniqueId(), "Name Tag"));
                        player.openBook(getBook(_args.get(1), responses.get(player.getUniqueId())));
                        colors.put(player.getUniqueId(), _args.get(1));
                        break;
                    case "setname":
                        player.sendBlockChange(player.getLocation(), Material.ACACIA_SIGN.createBlockData());
                        ClientboundOpenSignEditorPacket packet = new ClientboundOpenSignEditorPacket(BlockPos.of(BlockPos.asLong(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())));
                        responses.put(player.getUniqueId(), responses.getOrDefault(player.getUniqueId(), "Name Tag"));
                        ((CraftPlayer) player).getHandle().connection.send(packet);
                        break;
                    case "withcolorname":
                        String color = colors.get(player.getUniqueId());
                        if (color == null) {
                            color = NamedTextColor.RED.asHexString();
                        }
                        player.openBook(getBook(color, responses.getOrDefault(player.getUniqueId(), "Name Tag")));
                        break;
                    case "done":
                        String finalColor = colors.get(player.getUniqueId());
                        if (finalColor == null) {
                            finalColor = NamedTextColor.RED.asHexString();
                        }
                        String prefix = "[" + responses.getOrDefault(player.getUniqueId(), "Name Tag") + "] ";

                        Common.teamPacket(player, String.valueOf(System.currentTimeMillis()), prefix, colorMapping.get(internalColorMapping.get(finalColor)));
                        setStuff(player, prefix, colorMapping.get(internalColorMapping.get(finalColor)));
                        player.sendMessage(Component.text("Your prefix is: " + prefix, NamedTextColor.nearestTo(TextColor.color(colorMapping.get(internalColorMapping.get(finalColor)).getColor()))));

                }
            }
        }

        return true;
    }
}
