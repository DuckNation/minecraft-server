package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.item.ArmorItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;
import java.util.Map;

public class Extra implements Listener {

    private final DuckSMP plugin;
    private final HashMap<String, String> claimPerms= new HashMap<>();
//    private boolean hasListenerLoaded = false;

    public Extra() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        claimPerms.put("tnt", "DENY");
        claimPerms.put("ghast-fireball", "DENY");
        claimPerms.put("wither-damage", "DENY");
        claimPerms.put("chorus-fruit-teleport", "DENY");

        claimPerms.put("other-explosion", "ALLOW");
        claimPerms.put("fire-spread", "ALLOW");
        claimPerms.put("lava-fire", "ALLOW");
        claimPerms.put("pistons", "ALLOW");
        claimPerms.put("enderman-grief", "ALLOW");
        claimPerms.put("creeper-explosion", "ALLOW");
        claimPerms.put("block-trampling", "ALLOW");
        claimPerms.put("firework-damage", "ALLOW");
        claimPerms.put("pvp", "ALLOW");
    }
//
//    //    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        if (event.getInventory().getHolder() instanceof CustomHolder) {
//            if (event.getCurrentItem() == null) {
//                return;
//            }
//            event.setCancelled(true);
//
//            switch (event.getCurrentItem().getType()) {
//                case GREEN_TERRACOTTA: // no
//                    event.getWhoClicked().sendMessage(Component.text("Alright, you didn't join LifeSteal. Guess you live for another day", NamedTextColor.RED));
//                    break;
//                case RED_TERRACOTTA: // yes
//                    event.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(DuckSMP.getInstance(), "claimed_hearts"), PersistentDataType.INTEGER, 0);
//                    event.getWhoClicked().sendMessage(Component.text("You have joined LifeSteal! Now make sure you don't drop to zero hearts.", NamedTextColor.GREEN));
//                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text("" + event.getWhoClicked().getName() + " has joined LifeSteal!", NamedTextColor.GREEN)));
//                    break;
//                default:
//                    return;
//            }
//            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> event.getWhoClicked().closeInventory());
//        }
//
//    }
//
////    @EventHandler
////    public void onWorldLoad(WorldLoadEvent event) {
////        if (!hasListenerLoaded) {
////            new Listeners();
////            hasListenerLoaded = true;
////        }
////    }

    @EventHandler
    public void onModerationCommand(PlayerCommandPreprocessEvent event) {
        Component displayName = event.getPlayer().displayName();
        if (displayName.contains(Component.text("haappi")) || displayName.contains(Component.text(".AlysDaDuck")) || displayName.contains(Component.text("AlysDaDuck"))) {
            return;
        }
        String message = event.getMessage().toLowerCase();
        if (message.contains("ban-ip") || message.contains("kick") || message.contains("ban") || message.contains("pardon") || message.contains("pardon-ip") || message.contains("banlist")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("You can't use this command.", NamedTextColor.RED));
        }

    }

    @EventHandler
    public void onCommandRun(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("rg claim")) {
            if (event.getMessage().split(" ").length != 3) {
                return;
            }
            String claimName = event.getMessage().split(" ")[2];
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                for (Map.Entry<String, String> perms : claimPerms.entrySet()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            String.format(
                                    "rg flag -w \"%s\" %s -g all %s %s", // world, claim, flag, permission
                                    event.getPlayer().getLocation().getWorld().getName(),
                                    claimName,
                                    perms.getKey(),
                                    perms.getValue()
                            )
                    );
                }
            }, 60L);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType().toString().toLowerCase().contains("elytra")) {
            ItemStack item = event.getPlayer().getInventory().getChestplate();
            event.getPlayer().getInventory().setChestplate(event.getItem());
            if (event.getHand() == EquipmentSlot.HAND) {
                event.getPlayer().getInventory().setItemInMainHand(item);
            } else {
                event.getPlayer().getInventory().setItemInOffHand(item);
            }
            return;
        }
        if (!(CraftItemStack.asNMSCopy(event.getItem()).getItem() instanceof ArmorItem)) {
            if (!FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId())) {
                return; // Java players right click to raise shield
            }
            if ((event.getItem().getType() == Material.SHIELD || event.getItem().getType() == Material.TOTEM_OF_UNDYING) && event.getHand() == EquipmentSlot.HAND) {
                ItemStack item = event.getPlayer().getInventory().getItemInOffHand();
                event.getPlayer().getInventory().setItemInMainHand(item);
                event.getPlayer().getInventory().setItemInOffHand(event.getItem());
            }
            return;
        }
        if (event.getItem().getType().toString().toLowerCase().contains("helmet")) {
            ItemStack item = event.getPlayer().getInventory().getHelmet();
            event.getPlayer().getInventory().setHelmet(event.getItem());
            if (event.getHand() == EquipmentSlot.HAND) {
                event.getPlayer().getInventory().setItemInMainHand(item);
            } else {
                event.getPlayer().getInventory().setItemInOffHand(item);
            }
        } else if (event.getItem().getType().toString().toLowerCase().contains("boots")) {
            ItemStack item = event.getPlayer().getInventory().getBoots();
            event.getPlayer().getInventory().setBoots(event.getItem());
            if (event.getHand() == EquipmentSlot.HAND) {
                event.getPlayer().getInventory().setItemInMainHand(item);
            } else {
                event.getPlayer().getInventory().setItemInOffHand(item);
            }
        } else if (event.getItem().getType().toString().toLowerCase().contains("leggings")) {
            ItemStack item = event.getPlayer().getInventory().getLeggings();
            event.getPlayer().getInventory().setLeggings(event.getItem());
            if (event.getHand() == EquipmentSlot.HAND) {
                event.getPlayer().getInventory().setItemInMainHand(item);
            } else {
                event.getPlayer().getInventory().setItemInOffHand(item);
            }
        } else if (event.getItem().getType().toString().toLowerCase().contains("chestplate")) {
            ItemStack item = event.getPlayer().getInventory().getChestplate();
            event.getPlayer().getInventory().setChestplate(event.getItem());
            if (event.getHand() == EquipmentSlot.HAND) {
                event.getPlayer().getInventory().setItemInMainHand(item);
            } else {
                event.getPlayer().getInventory().setItemInOffHand(item);
            }
        }
    }
}
