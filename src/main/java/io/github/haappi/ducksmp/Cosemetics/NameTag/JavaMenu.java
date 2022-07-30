package io.github.haappi.ducksmp.Cosemetics.NameTag;

import io.github.haappi.ducksmp.DuckSMP;
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
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.colorMapping;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.setStuff;
import static io.github.haappi.ducksmp.DuckSMP.secretKey;

public class JavaMenu {

    public static final HashMap<UUID, String> responses = new HashMap<>();
    public static final HashMap<UUID, String> colors = new HashMap<>();

    private static final Map<String, Integer> internalColorMapping = Map.ofEntries(
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


    public static void callback(Player player) {
        Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> {
            if (responses.containsKey(player.getUniqueId())) {
                String response = responses.get(player.getUniqueId());
                Bukkit.getServer().dispatchCommand(player, "menu " + secretKey + " withcolorname " + response);
            }
        });

    }

    private static List<Component> getAllColors() {
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
            colors.add(Component.text(colorName, color).append(Component.text(" ", NamedTextColor.WHITE)).hoverEvent(HoverEvent.showText(Component.text("Click to set your name color to " + colorName))).clickEvent(ClickEvent.runCommand("/menu " + secretKey + " color " + color.asHexString())));
            current++;

        }
        return colors;
    }

    private static Book getBook(@NotNull String hexString, @NotNull String name) {
        Component component = Component.text(name, NamedTextColor.nearestTo(TextColor.fromHexString(hexString))).decoration(TextDecoration.BOLD, true).hoverEvent(HoverEvent.showText(Component.text("Click to change your tag"))).clickEvent(ClickEvent.runCommand("/menu " + secretKey + " setname")).append(Component.newline());
        for (Component color : getAllColors()) {
            component = component.append(color);
        }
        component = component.append(Component.newline().append(Component.newline()));

        Component submitComponent = Component.text("Submit", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.showText(Component.text("Submit your changes"))).clickEvent(ClickEvent.runCommand("/menu " + secretKey + " done"));

        component = component.append(submitComponent);
        return Book.book(Component.text("Name Tag"), Component.text("ur mum"), List.of(component));
    }

    @SuppressWarnings("ConstantConditions")
    public static void menu(Player player, @NotNull String[] args) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (args.length < 1) {
            String thing = container.get(new NamespacedKey(DuckSMP.getInstance(), "custom_prefix"), PersistentDataType.STRING);
            if (thing == null) {
                thing = "my awesome prefix";
            } else {
                thing = thing.replace("[", "").replace("] ", "");
            }
            // todo get color ig
            Book book = getBook(NamedTextColor.RED.asHexString(), thing);
            player.openBook(book);
            colors.put(player.getUniqueId(), NamedTextColor.RED.asHexString());
        } else {
            if (!args[0].equals(secretKey)) {
                player.sendMessage(Component.text("What do you think you're doing? Tryna look at my code and crack it?", NamedTextColor.RED));
                return;
            }
            List<String> _args = Arrays.stream(args).toList();

            switch (_args.get(1)) {
                case "color":
                    responses.put(player.getUniqueId(), responses.getOrDefault(player.getUniqueId(), "Name Tag"));
                    player.openBook(getBook(_args.get(2), responses.get(player.getUniqueId())));
                    colors.put(player.getUniqueId(), _args.get(2));
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
}
