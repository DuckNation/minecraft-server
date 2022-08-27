package io.github.haappi.ducksmp.Biomes;

import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import static io.github.haappi.ducksmp.DuckSMP.secretKey;
import static io.github.haappi.ducksmp.Utils.Utils.random;

public class NetherNerf implements Listener {

    private final DuckSMP plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public NetherNerf() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Iterator<Recipe> iter = Bukkit.getServer().recipeIterator();
        while (iter.hasNext()) {
            Recipe r = iter.next();
            if (r.getResult().getType() == Material.NETHERITE_INGOT) {
                iter.remove();
            }
        }
        Bukkit.addRecipe(ingotRecipe());
        Bukkit.addRecipe(ingotRecipeV2());
    }

    private ShapedRecipe ingotRecipeV2() {
        NamespacedKey key = new NamespacedKey(plugin, "netherite_ingot_2");

        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.NETHERITE_INGOT, 3));

        recipe.shape(
                "ABA",
                "BCB",
                "ABA");
        recipe.setIngredient('A', Material.MANGROVE_PROPAGULE);
        recipe.setIngredient('B', Material.WAXED_WEATHERED_CUT_COPPER);
        recipe.setIngredient('C', Material.NETHERITE_SCRAP);

        return recipe;
    }

    private ShapedRecipe ingotRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "netherite_ingot");

        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.NETHERITE_INGOT, 1));

        recipe.shape(
                "ABC",
                "BDB",
                "CBA");
        recipe.setIngredient('A', Material.RAW_COPPER_BLOCK);
        recipe.setIngredient('B', Material.NETHERITE_SCRAP);
        recipe.setIngredient('C', Material.WARPED_FUNGUS_ON_A_STICK);
        recipe.setIngredient('D', Material.LAVA_BUCKET);

        return recipe;
    }

//    @EventHandler
//    public void onItemSpawn(ItemSpawnEvent event) {
//        if (event.getEntity().getItemStack().getType() == Material.ANCIENT_DEBRIS) {
//            event.getEntity().setInvulnerable(true);
//        }
//    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.getWorld().getEnvironment() != World.Environment.NETHER) return;
        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        if (container.has(new NamespacedKey(plugin, "nerfed"), PersistentDataType.STRING)) {
            return;
        }
        container.set(new NamespacedKey(plugin, "nerfed"), PersistentDataType.STRING, "true");
        // set the PDC
        if (!event.getChunk().contains(Material.ANCIENT_DEBRIS.createBlockData())) {
            return;
        }
        ChunkSnapshot snapshot = event.getChunk().getChunkSnapshot();
        // debris spawns from y values of 7 to 120
        int counter = 1;

        for (int y = 3; y < 130; y = y + 7) {
            int finalY = y;
            scheduler.runTaskLaterAsynchronously(this.plugin, () -> stoff(snapshot, event.getChunk(), finalY), counter * 4L - 3L * counter);
            counter++;
        }

    }

    private void stoff(ChunkSnapshot snapshot, Chunk chunk, int _y) {
        HashSet<Location> locations = new HashSet<>();
        final int chunkX = chunk.getX() << 4;
        final int chunkZ = chunk.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = _y; y < _y + 8; y++) {
                    if (snapshot.getBlockType(x, y, z) == Material.ANCIENT_DEBRIS) {
                        locations.add(new Location(chunk.getWorld(), x + chunkX, y, z + chunkZ));
                    }
                }
            }
        }

        for (Location _location : locations) {
            int x = _location.getBlockX();
            int z = _location.getBlockZ();
            int counter = 0;
            for (int y = _location.getBlockY() + random.nextInt(8, 25); y < 120; y++) {
                counter++;
                if (isSafeToPlaceBlock(snapshot, x, y, z)) {
                    int finalY = y;
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        _location.getBlock().setType(Material.NETHERRACK);
                        _location.subtract(0, _location.getBlockY(), 0).add(0, finalY, 0).getBlock().setType(Material.ANCIENT_DEBRIS);
                        locations.remove(_location);
                    }, 5L);
                    break;
                }
                if (counter > 40) {
                    break; // Give up after 40 tries
                }
            }
        }

    }

    private boolean isSafeToPlaceBlock(ChunkSnapshot snapshot, int newX, int y, int newZ) {
        int newXX = newX & 15;
        int newZZ = newZ & 15;
        if (newZZ + 1 >= 16 || newZZ - 1 < 0 || newXX + 1 >= 16 || newXX - 1 < 0) {
            return false;
        }
        return (snapshot.getBlockType(newXX, y, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y + 1, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y - 1, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.ANCIENT_DEBRIS)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.BEDROCK)
                && (snapshot.getBlockType(newXX + 1, y, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX - 1, y, newZZ) != Material.AIR)
                && (snapshot.getBlockType(newXX, y, newZZ + 1) != Material.AIR)
                && (snapshot.getBlockType(newXX, y, newZZ - 1) != Material.AIR)
                && y < 127
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.CHEST)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.GOLD_BLOCK)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.NETHER_PORTAL)
                && (snapshot.getBlockType(newXX, y, newZZ) != Material.OBSIDIAN);
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        Bukkit.getScheduler().runTaskLater(this.plugin, () -> sendQuestionForm(event.getPlayer()), 20L * 4L);
//    }

    private void sendQuestionForm(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(new NamespacedKey(plugin, "netherite_question"), PersistentDataType.INTEGER)) {
            return;
        }
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
            sendQuestionForm(floodgatePlayer);
            return;
        }
        player.sendRichMessage("<yellow>Do you believe that netherite is too over-powered currently?</yellow>");

        Component component = Component.text("Yeah it is...", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/poll " + secretKey + " 1")).hoverEvent(HoverEvent.showText(Component.text("Yeah netherite is too op")));
        Component component2 = Component.text("Nope, it's not", NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/poll " + secretKey + " 2")).hoverEvent(HoverEvent.showText(Component.text("Nope, it's not")));

        player.sendMessage(component);
        player.sendMessage(component2);
    }

    private void sendQuestionForm(FloodgatePlayer player) {
        SimpleForm.Builder form = SimpleForm.builder()
                .title("Netherite Question")
                .content("Do you believe that netherite is too over-powered currently?")
                .button("Yeah it is...") // id = 0
                .button("Not at all really..."); // id = 1;
        form.closedOrInvalidResultHandler(response -> {
            // no response was given
            Objects.requireNonNull(Bukkit.getPlayer(player.getJavaUniqueId())).sendMessage(Component.text("I couldn't understand your response", NamedTextColor.RED));
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            Player bukkitPlayer = Bukkit.getPlayer(player.getJavaUniqueId());
            assert bukkitPlayer != null;
            PersistentDataContainer pdc = bukkitPlayer.getPersistentDataContainer();

            if (response.clickedButtonId() == 0) {
                pdc.set(new NamespacedKey(DuckSMP.getInstance(), "netherite_question"), PersistentDataType.INTEGER, 1);
            } else {
                pdc.set(new NamespacedKey(DuckSMP.getInstance(), "netherite_question"), PersistentDataType.INTEGER, 0);
            }
            bukkitPlayer.sendMessage(Component.text("Thanks for your input!", NamedTextColor.GREEN));
        });

        player.sendForm(form);
    }

}
