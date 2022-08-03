package io.github.haappi.ducksmp.Commands;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.customblockdata.events.CustomBlockDataMoveEvent;
import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.haappi.ducksmp.Cosemetics.NameTag.Common;
import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.colorMapping;
import static io.github.haappi.ducksmp.Cosemetics.NameTag.Common.setStuff;
import static io.github.haappi.ducksmp.utils.Utils.*;

public class Home extends BukkitCommand implements Listener {
    private static final HashMap<UUID, Integer> tasks = new HashMap<>();
    private static final HashMap<UUID, Long> cooldowns = new HashMap<>();
    public static ConcurrentHashMap<UUID, Location> pickingName = new ConcurrentHashMap<>();
    private final DuckSMP plugin;

    public Home(@NotNull String name) {
        super(name);
        this.plugin = DuckSMP.getInstance();
        CustomBlockData.registerListener(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.addRecipe(homeRecipe(Material.BLACK_BED));
        Bukkit.addRecipe(homeRecipe(Material.BLUE_BED));
        Bukkit.addRecipe(homeRecipe(Material.BROWN_BED));
        Bukkit.addRecipe(homeRecipe(Material.CYAN_BED));
        Bukkit.addRecipe(homeRecipe(Material.GRAY_BED));
        Bukkit.addRecipe(homeRecipe(Material.GREEN_BED));
        Bukkit.addRecipe(homeRecipe(Material.LIGHT_BLUE_BED));
        Bukkit.addRecipe(homeRecipe(Material.LIGHT_GRAY_BED));
        Bukkit.addRecipe(homeRecipe(Material.LIME_BED));
        Bukkit.addRecipe(homeRecipe(Material.MAGENTA_BED));
        Bukkit.addRecipe(homeRecipe(Material.ORANGE_BED));
        Bukkit.addRecipe(homeRecipe(Material.PINK_BED));
        Bukkit.addRecipe(homeRecipe(Material.PURPLE_BED));
        Bukkit.addRecipe(homeRecipe(Material.RED_BED));
        Bukkit.addRecipe(homeRecipe(Material.WHITE_BED));
        Bukkit.addRecipe(homeRecipe(Material.YELLOW_BED));
    }

    private static ItemStack getHome(int count) {
        ItemStack item = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA, count);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(noItalics("Custom Home", NamedTextColor.GOLD));
        meta.lore(Arrays.asList(noItalics(""), chain(noItalics("Place this to add a ", NamedTextColor.GRAY), noItalics("home", NamedTextColor.AQUA), noItalics(".", NamedTextColor.GRAY))));
        meta.getPersistentDataContainer().set(new NamespacedKey(DuckSMP.getInstance(), "custom_home"), PersistentDataType.STRING, "home");
        item.setItemMeta(meta);

        return item;
    }

    public static void callback(String homeName, Location blockLocation, Player player) {
        if (homeName.length() < 3) {
            player.sendMessage(noItalics("Home name must be at least 3 characters long.", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.length() > 16) {
            player.sendMessage(noItalics("Home name must be at most 16 characters long.", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.contains(";")) {
            player.sendMessage(noItalics("Home name cannot contain ';'", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.contains("\"")) {
            player.sendMessage(noItalics("Home name cannot contain \"", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.contains("\\")) {
            player.sendMessage(noItalics("Home name cannot contain \\", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.equalsIgnoreCase("list")) {
            player.sendMessage(noItalics("Home name cannot be 'list'", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.contains(" ")) {
            player.sendMessage(noItalics("Home name cannot contain spaces", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }
        if (homeName.contains(",")) {
            player.sendMessage(noItalics("Home name cannot contain commas", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
            return;
        }

        Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> {
            PersistentDataContainer container = player.getPersistentDataContainer();
            Map<String, String> homes = container.get(new NamespacedKey(DuckSMP.getInstance(), "custom_homes"), DataType.asMap(DataType.STRING, DataType.STRING));
            if (homes == null) {
                homes = new HashMap<>();
            }
            if (homes.size() > 5) {
                player.sendMessage(noItalics("You can only have 5 homes.", NamedTextColor.RED));
                Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
                return;
            }
            if (homes.containsKey(homeName)) {
                player.sendMessage(noItalics("Home ", NamedTextColor.RED).append(Component.text(homeName, NamedTextColor.GOLD)).append(Component.text(" already exists.", NamedTextColor.RED)));
                Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getWorld().dropItem(blockLocation, getHome(1)));
                return;
            }


            blockLocation.getBlock().setType(Material.MAGENTA_GLAZED_TERRACOTTA);
            PersistentDataContainer customBlockData = new CustomBlockData(blockLocation.getBlock(), DuckSMP.getInstance());
            customBlockData.set(new NamespacedKey(DuckSMP.getInstance(), "owner"), PersistentDataType.STRING, player.getUniqueId() + ";" + homeName);


            homes.put(homeName, formatLocation(blockLocation));

            container.set(new NamespacedKey(DuckSMP.getInstance(), "custom_homes"), DataType.asMap(DataType.STRING, DataType.STRING), homes);
            player.sendMessage(noItalics("Home " + homeName + " created.", NamedTextColor.GREEN));
        });

    }

    private ShapedRecipe homeRecipe(Material bedType) {
        NamespacedKey key = new NamespacedKey(plugin, "home_recipe_" + bedType.name().toLowerCase());

        ShapedRecipe recipe = new ShapedRecipe(key, getHome(2));

        recipe.shape(
                "ABA",
                "BCB",
                "ABA");
        recipe.setIngredient('A', bedType);
        recipe.setIngredient('B', Material.CRYING_OBSIDIAN);
        recipe.setIngredient('C', Material.COMPASS);

        return recipe;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.MAGENTA_GLAZED_TERRACOTTA) {
            PersistentDataContainer container = event.getItemInHand().getItemMeta().getPersistentDataContainer();
            if (container.has(new NamespacedKey(plugin, "custom_home"), PersistentDataType.STRING)) {
                event.getBlockPlaced().setType(Material.AIR);
                setNameOfHome(event.getPlayer(), event.getBlock().getLocation());
            }
        }
    }

    private void setNameOfHome(Player player, Location blockLocation) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            bedrockForm(player);
        } else {
            javaForm(player, blockLocation);
        }
    }

    private void bedrockForm(Player player) {
        CustomForm.Builder form = CustomForm.builder()
                .title("Home Name")
                .input("Home Name", "Enter the name of your home here.");

        form.closedOrInvalidResultHandler(response -> {
            Home.callback("", pickingName.remove(player.getUniqueId()), player);
        });

        form.validResultHandler(response -> {
            String responseInput = response.asInput();
            if (responseInput == null) {
                Home.callback("", pickingName.remove(player.getUniqueId()), player);
                return;
            }
            Home.callback(responseInput, pickingName.remove(player.getUniqueId()), player);

        });
        FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
    }

    private void javaForm(Player player, Location blockLocation) {
        pickingName.put(player.getUniqueId(), blockLocation);
        BlockData oldBlock = player.getLocation().getBlock().getBlockData();
        player.sendBlockChange(player.getLocation(), Material.ACACIA_SIGN.createBlockData());
        ClientboundOpenSignEditorPacket packet = new ClientboundOpenSignEditorPacket(BlockPos.of(BlockPos.asLong(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())));
        ((CraftPlayer) player).getHandle().connection.send(packet);
        player.sendBlockChange(player.getLocation(), oldBlock);
    }

    @EventHandler
    public void onBlockMove(CustomBlockDataMoveEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDelete(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.MAGENTA_GLAZED_TERRACOTTA) {
            return;
        }

        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), plugin);
        String data = customBlockData.getOrDefault(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING, "no_one");
        if (!data.equals("no_one")) {
            event.getBlock().setType(Material.AIR);
            event.setDropItems(false);
            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), getHome(1));
        }
    }

    @EventHandler
    public void onTap(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.MAGENTA_GLAZED_TERRACOTTA) {
            return;
        }
        PersistentDataContainer customBlockData = new CustomBlockData(event.getClickedBlock(), plugin);
        String data = customBlockData.getOrDefault(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING, "no_one");
        Component message;
        if (data.equals("no_one")) {
            message = Component.text("This home doesn't belong to anyone.", NamedTextColor.RED);
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(data.split(";")[0]));
            message = Component.text("This home belongs to ", NamedTextColor.GRAY).append(Component.text("" + player.getName(), NamedTextColor.GOLD)).append(Component.text(".", NamedTextColor.GRAY)).append(Component.text(" Its name is ", NamedTextColor.GRAY)).append(Component.text("" + data.split(";")[1], NamedTextColor.GOLD)).append(Component.text(".", NamedTextColor.GRAY));
        }
        event.getPlayer().sendMessage(message);
    }

    public static Component getHomesOfPlayer(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Map<String, String> homes = container.get(new NamespacedKey(DuckSMP.getInstance(), "custom_homes"), DataType.asMap(DataType.STRING, DataType.STRING));
        if (homes == null) {
            homes = new HashMap<>();
        }
        Component component = Component.text("", NamedTextColor.GRAY);
        for (Map.Entry<String, String> home : homes.entrySet()) {
            component = component.append(Component.newline()).append(Component.text(home.getKey() + " ", NamedTextColor.YELLOW).append(formattedLocation(getLocation(home.getValue()))));
        }
        return component;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(noItalics("You must be a player to use this command.", NamedTextColor.RED));
            return true;
        }
        if (player.isInLava() || player.isInPowderedSnow() || player.isInWaterOrRainOrBubbleColumn()) {
            player.sendMessage(noItalics("Hmmmm, it looks like you're in a rather sticky situation. I can't allow you to use this command right now.", NamedTextColor.RED));
            return true;
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(noItalics("Hmmmm, it looks like you're in a rather sticky situation. I can't allow you to use this command right now.", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(noItalics("Usage: /home <home_name | list>", NamedTextColor.RED));
            return true;
        }
        if (cooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            sender.sendMessage(noItalics("You must wait " + (cooldowns.getOrDefault(player.getUniqueId(), 0L) - System.currentTimeMillis()) / 1000 + " seconds before using this command again.", NamedTextColor.RED));
            return true;
        }
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 20000);

        String homeName = Arrays.toString(args).replace("[", "").replace("]", "").replace(",", "");
        PersistentDataContainer container = player.getPersistentDataContainer();
        Map<String, String> homes = container.get(new NamespacedKey(plugin, "custom_homes"), DataType.asMap(DataType.STRING, DataType.STRING));
        if (homes == null) {
            homes = new HashMap<>();
        }

        if (homeName.equalsIgnoreCase("list")) {
            Component component = Component.text("====== Your Homes ====== ", NamedTextColor.GRAY);

            player.sendMessage(component.append(getHomesOfPlayer(player)));
            return true;
        }


        if (!homes.containsKey(homeName)) {
            sender.sendMessage(noItalics("Home " + homeName + " doesn't exist.", NamedTextColor.RED));
            return true;
        }
        Location location = getLocation(homes.get(homeName));
        if (location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getType() != Material.MAGENTA_GLAZED_TERRACOTTA) {
            sender.sendMessage(noItalics("Home " + homeName + " was broken.", NamedTextColor.RED));
            homes.remove(homeName);
            container.set(new NamespacedKey(plugin, "custom_homes"), DataType.asMap(DataType.STRING, DataType.STRING), homes);
            return true;
        }

        if (location.getWorld().getUID() != player.getWorld().getUID()) {
            player.sendMessage(noItalics("Home " + homeName + " is in a different world.", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(noItalics("Teleporting to home " + homeName + ".", NamedTextColor.GREEN).append(Component.text(" Don't move for 10 seconds.", NamedTextColor.RED)));
        tasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(plugin, () -> teleport(player, location), 20L * 10).getTaskId()); // 10 seconds
        loadChunks(location, 6);

        return true;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Bukkit.getScheduler().cancelTask(tasks.remove(event.getPlayer().getUniqueId()));
    }

    private void teleport(Player player, Location location) {
        // todo add cool particles
        player.teleportAsync(location.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
        tasks.remove(player.getUniqueId());
        player.sendMessage(noItalics("Teleported to home.", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasExplicitlyChangedBlock()) {
            return;
        }
        if (tasks.containsKey(event.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().cancelTask(tasks.remove(event.getPlayer().getUniqueId()));
            event.getPlayer().sendMessage(Component.text("You moved! Teleport cancelled.", NamedTextColor.RED));
        }
    }
}
