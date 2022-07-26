package io.github.haappi.ducksmp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {

    public static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public Utils() {
        throw new UnsupportedOperationException("This class is not meant to be instantiated");
    }

    public static Component noItalics(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component noItalics(String content, NamedTextColor color) {
        return noItalics(Component.text(content, color));
    }

    public static Component noItalics(String content) {
        return noItalics(Component.text(content));
    }

    public static Component chain(Component... components) {
        Component thing = Component.empty();
        for (Component component : components) {
            thing = thing.append(component);
        }
        return thing;
    }

    public static String sFormat(Integer number) {
        if (number == 1) {
            return "";
        } else {
            return "s";
        }
    }

    public static TextComponent getCountdown(Integer countdown) {
        NamedTextColor color = switch (countdown) {
            case 0, 1, 2, 3 -> NamedTextColor.RED;
            case 4, 5, 6 -> NamedTextColor.YELLOW;
            default -> NamedTextColor.GREEN;
        };

        return Component.text(countdown + " second" + sFormat(countdown), color);
    }
}
