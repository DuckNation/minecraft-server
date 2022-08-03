package io.github.haappi.ducksmp.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Flex extends BukkitCommand {

    public Flex(String name) {
        super(name);
        setAliases(List.of("brag"));
    }

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    private String toNumeral(int x) {
        return switch (x) {
            case 1 -> " I";
            case 2 -> " II";
            case 3 -> " III";
            case 4 -> " IV";
            case 5 -> " V";
            case 6 -> " VI";
            case 7 -> " VII";
            default -> " " + x;
        };
    }

    private String titleCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String formatStringAsTitleCase(String str) {
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(titleCase(word));
            sb.append(" ");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private Component getFlexMessage(ItemStack itemStack, String playerName, boolean isOp) {
        Component name;
        if (isOp) {
            name = Component.text("<" + playerName + "> ", NamedTextColor.GREEN).append(Component.text("Hey guys! Look at my item-  ", NamedTextColor.WHITE));
        } else {
            name = Component.text("<" + playerName + "> ", NamedTextColor.YELLOW).append(Component.text("Hey guys! Look at my item-  ", NamedTextColor.WHITE));
        }
        if (itemStack.getItemMeta().hasDisplayName()) {
            name = name.append(Component.newline()).append(Component.text("| ", NamedTextColor.GRAY)).append(itemStack.getItemMeta().displayName().color(itemStack.getRarity().getColor())).append(Component.text(" [" + formatStringAsTitleCase(itemStack.getType().name().toLowerCase().replace("_", " ")) + "] ", itemStack.getRarity().getColor()).append(Component.text("x " + itemStack.getAmount(), NamedTextColor.GRAY)));
        } else {
            name = name.append(Component.newline()).append(Component.text("| ", NamedTextColor.GRAY)).append(Component.text("[" + formatStringAsTitleCase(itemStack.getType().name().toLowerCase().replace("_", " ")) + "] ", itemStack.getRarity().getColor()).append(Component.text("x " + itemStack.getAmount(), NamedTextColor.GRAY)));
        }
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            name = name.append(
                    Component.newline()
                            .append(
                                    Component.text(
                                            "| " + titleCase(enchantment.getKey().toString().replace("minecraft:", ""))
                                                    + toNumeral(itemStack.getEnchantmentLevel(enchantment)),
                                            NamedTextColor.GRAY)));
        }
        return name;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(Component.text("You must be holding an item to use this command.", NamedTextColor.RED));
                return true;
            }
            if (cooldown.containsKey(player.getUniqueId())) {
                if (cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§cHey hey hey. You must wait " + (cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000 + " seconds before flexing to people. :D");
                    return true;
                } else {
                    cooldown.remove(player.getUniqueId());
                }
            }
            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
            Component flexMessage = getFlexMessage(player.getInventory().getItemInMainHand(), player.getName(), player.isOp());

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(flexMessage);
            }
        }
        return true;
    }

}
